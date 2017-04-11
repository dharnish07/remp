import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.math.BigInteger;
import java.security.SecureRandom;
import javax.xml.bind.DatatypeConverter;

public class AuthenticationServer 
{
	
    public static int row=0;
    public static int col=0;
	public static int flag2=0;
	public static String tk="";  	//Key for Access ticket Generation
	public static String kp=null; 	// Message Broker's Public key 
	public static String pm="";   	// Master Key
	public static String [][] table = new String[20][4]; // Used to store the connected subscriber and publiser ID and Authentication key
	public static void main(String args[])
	{
		
		Socket s=null;
		Socket mb=null;
		ServerSocket ss2=null;
		ServerSocket msgbroker=null;	
		/* Generate the random number using securerandom method. 
		this number is given to the key i/p of AES algm.
		tk is the one time single access ticket key. 
	  	it will given to the Message boraker*/
	  	byte[] ran = new byte[16];
		SecureRandom sr=new SecureRandom();
	  	sr.nextBytes(ran);
	  	AuthenticationServer.tk=DatatypeConverter.printBase64Binary(ran);

		System.out.println("Server Listening......");
		try
		{
			ss2 = new ServerSocket(4445); 		// Authentication Server running on this port
			msgbroker = new ServerSocket(4446);	// Message Broker connecting with this port
			mb= msgbroker.accept();
			try
			{ 
				BufferedReader  is= new BufferedReader(new InputStreamReader(mb.getInputStream()));
				PrintWriter os=new PrintWriter(mb.getOutputStream());
				System.out.println("One time acces token :"+AuthenticationServer.tk);
				os.println(AuthenticationServer.tk);
				os.flush();	
				AuthenticationServer.kp=is.readLine();
				System.out.println("MB Public key : "+kp);
				mb.close();
			}
			catch(IOException e)
			{
				System.out.println("IO error in server thread");
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.out.println("Server error");
		}
		/* the server is running continously 
		 to accept the connection request from the users*/
		while(true)
		{
			try
			{
				s= ss2.accept();
				System.out.println("connection Established"+s);
				ServerThread st=new ServerThread(s);
				st.start();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				System.out.println("Connection Error");
			}
		}
		
	}
}

class ServerThread extends Thread
{  
	int flag=0,flag1=0;
	String line=null,ka=null;
	String option=null;
	String lin=null;
	BufferedReader  is = null;
	PrintWriter os=null;
	Socket s=null;
	String host = null;

	public ServerThread(Socket s)
	{
		this.s=s;
	}
	public void run()
	{
		BigInteger  pRandom;
		BigInteger qRandom=new BigInteger("2");
		BigInteger a,A,Ka,Ya,Yb;
		int i;
		String dup = s.getInetAddress().getHostAddress(); //
		/* To check the user is alredy authenticated or not */
		for(i=0;i<AuthenticationServer.row;i++)
		{
			if(AuthenticationServer.table[i][1].equals(dup))
			{
				flag1=1;
				break;
			}
		}
		/* if User is not authenticated previously do the following*/
		if(flag1!=1)
		{
			try
			{
				is= new BufferedReader(new InputStreamReader(s.getInputStream()));
				os=new PrintWriter(s.getOutputStream());

			}
			catch(IOException e)
			{
				System.out.println("IO error in server thread");
			}

			try 
			{
				line=is.readLine();
				if(line.compareTo("QUIT")!=0)
				{
					/*Generate the two RANDOM PRIME NUMBERS P and Q*/
					while (true) 
					{
						flag=0;
						int p = (int) (Math.random() * (127 - 2) + 2);
						for(i=2;i<=Math.sqrt(p);i++)
						{
						   if(p % i == 0)
						   {
							   flag=1;
						   }
					   }
					   if(flag==0)
					   {
								System.out.println("Got Random Prime P :"+p);
								pRandom= new BigInteger(p+"");
								break;
					   }
					
					}
					/* to check whether Q is not common factor of P*/
					while(true)
					{
						flag=0;
						int q = (int) (Math.random() * (127 - 2) + 2);
						int t,a1,b1;
						a1=pRandom.intValue();
						b1=q;
						while(b1 != 0)
						{
							t = a1;
							a1 = b1;
							b1 = t%b1;
						}
						if(a1==1)
						{
							System.out.println("NO FACTOR");
							qRandom= new BigInteger(q+"");
							System.out.println("Got Random Q :"+qRandom);
							os.println(pRandom+" "+qRandom);
							os.flush();
							break;
						}
						else
						{
							System.out.println("COMMON FACTOR");
						}
					}
					 
					/* Key Exchange algorithm Using Deffi Helman Key X-Change
					storing client IP and Key in table array*/
					int minimum=0;
					int a2 = minimum + (int)(Math.random() * pRandom.intValue());
					a=new BigInteger(a2+"");
					System.out.println("A is"+a);
					
					Ya=qRandom.modPow(a,pRandom);
					System.out.println("Generated Value"+Ya);
					os.println(Ya);
					os.flush();
					
					line=is.readLine();
					Yb=new BigInteger(line);
					System.out.println("RESULT FROM CLIENT"+Yb);
									
					Ka=Yb.modPow(a,pRandom);
					System.out.println("Got Key :"+Ka);
					
					ka=Ka+"";
					AuthenticationServer.table[AuthenticationServer.row][AuthenticationServer.col++]=ka;
					host = s.getInetAddress().getHostAddress();
					AuthenticationServer.table[AuthenticationServer.row++][AuthenticationServer.col]=host;
					
					for(i=0;i<AuthenticationServer.row;i++)
					{
						System.out.println("\nTABLE:\nKEY:"+AuthenticationServer.table[i][0]+"\nID:"+AuthenticationServer.table[i][1]);
					}
					AuthenticationServer.col=0;
				}
					
				try
				{
					/* Public master key(pm) and Publiser key(pi) generation
					the variable key[0] contain publiser key pi
					key[1] contain the master key pm 
					The program HelloWorld.Java Generate the pi and pm using AES*/
					String s4=host;
					String key[]= new String[2];
				  	if(AuthenticationServer.pm.compareTo("") == 0)
				  	{
				   		Process pro = Runtime.getRuntime().exec("javac PublisherKey.java");
				   		Process pro1 = Runtime.getRuntime().exec("java PublisherKey"+" "+s4);
				   		String line = null;
				   		String line1="";
				   		BufferedReader in = new BufferedReader(new InputStreamReader(pro1.getInputStream()));
				   		while ((line = in.readLine()) != null)
						{
							line1+=line;
						}
						key=line1.split("\\.");
						AuthenticationServer.pm = key[0];
					}
					/* Access ticket generation  
					variable s4 contain the IP address of the user
					variable ka contain the authentication key of the particular user
					thrid argument specifies the access right of the user
					tk is the one time access ticket key*/
					Process pro2;
					Process pro3;
					BufferedReader in1;

					if(AuthenticationServer.flag2!=1)
					{
						os.println("Want to continue as Publisher or Subscriber(P/S) :" );
						os.flush();
						option=is.readLine();
						/* if user is publiser to send publiser key pi and Access ticket ti*/
						if(option.compareTo("P")==0)
						{
							pro2 = Runtime.getRuntime().exec("javac accessticket.java");
							pro3 = Runtime.getRuntime().exec("java accessticket"+" "+s4+" "+ka+" "+"0"+" "+AuthenticationServer.tk);
							System.out.println("Publisher key pi:"+key[1]);
							os.println(key[1]);
							os.flush();
							AuthenticationServer.flag2=1;
							String line2 = "";
							String line3[]= new String[2];
							in1 = new BufferedReader(new InputStreamReader(pro3.getInputStream()));
							line2=in1.readLine();
							System.out.println("ACCESS Ticket Ti:"+line2);
							os.println(line2);
							os.flush();
						} 
						/* if user is subscriber then send Master key pm
						Access ticket ti and public key of meassge borker kp*/
						else
						{
							pro2 = Runtime.getRuntime().exec("javac accessticket.java");
							pro3 = Runtime.getRuntime().exec("java accessticket"+" "+s4+" "+ka+" "+"1"+" "+AuthenticationServer.tk);
							System.out.println("master key :"+AuthenticationServer.pm);
							os.println(AuthenticationServer.pm);
							os.flush();
							String line2 = null;
							String line3[]= new String[2];
							in1 = new BufferedReader(new InputStreamReader(pro3.getInputStream()));
							line2=in1.readLine();
							System.out.println("ACCESS Ticket Ti:"+line2);
							System.out.println("Public key of MB :"+AuthenticationServer.kp);
							os.println(line2);
							os.flush();
							os.println(AuthenticationServer.kp);
							os.flush();
						}
					}
					else
					{
						pro2 = Runtime.getRuntime().exec("javac accessticket.java");
						pro3 = Runtime.getRuntime().exec("java accessticket"+" "+s4+" "+ka+" "+"1"+" "+AuthenticationServer.tk);
						os.println("You are Connected as Subscriber:Want to continue");
						os.flush();
						option=is.readLine();
						System.out.println("master key :"+AuthenticationServer.pm);
						os.println(AuthenticationServer.pm);
						os.flush();
						String line2 = null;
						String line3[]=new String[2];
						BufferedReader in2 = new BufferedReader(new InputStreamReader(pro3.getInputStream()));
						line2=in2.readLine();
						System.out.println("ACCESS Ticket Ti:"+line2);
						System.out.println("Public key of MB :"+AuthenticationServer.kp);
						os.println(line2);
						os.flush();
						os.println(AuthenticationServer.kp);
						os.flush();
					}
				}
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			} 
			catch (IOException e) 
			{
			 line=this.getName(); //reused String line for getting thread name
			 System.out.println("IO Error/ Client "+line+" terminated abruptly");
			}
			catch(NullPointerException e)
			{
			 line=this.getName(); //reused String line for getting thread name
			 System.out.println("Client "+line+" Closed");
			}
		}
		else
		{
			try
			{
				is= new BufferedReader(new InputStreamReader(s.getInputStream()));
				os=new PrintWriter(s.getOutputStream());
				line=is.readLine();
				String qw="ALREADY AUTHENTICATED";
				os.println(qw);
				os.flush();
				System.out.println(qw);
			}
			catch(IOException e)
			{
				System.out.println("IO error in server thread");
			}
		}
		/* To close the open sockets*/
		try
		{
			System.out.println("Connection Closed.."+s);
			if (is!=null)
			{
				is.close(); 
			}
			if(os!=null)
			{
				os.close();
			}
			if (s!=null)
			{
				s.close();
			}
		}
		catch(IOException ie)
		{
			System.out.println("Socket Close Error");
		}
	}
}