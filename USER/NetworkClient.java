import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.math.BigInteger;

public class NetworkClient 
{
    public static void main(String args[]) throws IOException
    {
        InetAddress address=InetAddress.getLocalHost();
        Socket s1=null;
        String line=null;
        String key[] = new String[2];
        BufferedReader br=null;
        BufferedReader is=null;
        PrintWriter os=null;
        BigInteger p=new BigInteger("1");
        BigInteger q=new BigInteger("2"),Key,b,B,Rkey;
        String choice;
        try 
        {
            s1=new Socket("10.1.24.29", 4445); // You can use static final constant PORT_NUM
            br= new BufferedReader(new InputStreamReader(System.in));
            is=new BufferedReader(new InputStreamReader(s1.getInputStream()));
            os= new PrintWriter(s1.getOutputStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.err.print("IO Exception");
        }

        System.out.println("Client Address : "+address);
        System.out.println("Enter Data to echo Server ( Enter QUIT to end):");

        String response=null;
        String accessTicket=null;
        String publisherKey=null;
        String kp=null;
        
        try
        {
            line=br.readLine(); 
            if(line.compareTo("QUIT")!=0)
            {
                os.println(line);
                os.flush();
                response=is.readLine();
                if(response.compareTo("ALREADY AUTHENTICATED")!=0)
                {
                    System.out.println("Server Response : "+response);
                    String[] split = response.split(" ");
    		        if(split.length == 2)
    		        {
                        p = new BigInteger(split[0]);
                        q = new BigInteger(split[1]);
                    }
                    System.out.println("p value"+p+"\nq Value"+q);
                    int min=0;
                    int ran=min+(int) (Math.random()*p.intValue());
                    b=new BigInteger(ran+"");
                    System.out.println("Random number chossed as: "+b);
                    B=q.modPow(b,p);
                    os.println(B);
                    os.flush();
                    System.out.println("Generated value is "+B);
                    response=is.readLine();
                    Rkey=new BigInteger(response);
                    System.out.println("Received value is "+Rkey);
                    //System.out.println("Random number chossed as111    : "+b);
                    Key=Rkey.modPow(b,p);
                    System.out.println("Generated key is "+Key);
                    response=is.readLine();
                    System.out.println(response);
                    choice=br.readLine(); 
                    os.println(choice);
                    os.flush();
                    publisherKey=is.readLine();
                    System.out.println(" KEY pi is "+publisherKey);
                    accessTicket=is.readLine();
                    System.out.println("Access Ticket: "+accessTicket);
        	    	if(choice.compareTo("P")==0)
                    {
        	        	Process pro=Runtime.getRuntime().exec("javac SessionKey.java");
                        Process pro1=Runtime.getRuntime().exec("java SessionKey "+publisherKey);
                        BufferedReader inp=new BufferedReader(new InputStreamReader(pro1.getInputStream()));
                        key = inp.readLine().split("\\.");
                        System.out.println("Session Key:"+key[1]);
                        System.out.println("Random :"+key[0]);
                        s1=new Socket("10.1.24.110", 4447);
                        os= new PrintWriter(s1.getOutputStream());
                        os.println(key[0]);
                        os.flush();
                        Process pro2=Runtime.getRuntime().exec("javac AESencrypt.java");
                        Process pro3=Runtime.getRuntime().exec("java AESencrypt "+key[1]);
                        BufferedReader inp1=new BufferedReader(new InputStreamReader(pro3.getInputStream()));
                        String cipher = inp1.readLine();
                        System.out.println("Cipher Text:"+cipher);
                        os.println(cipher);
                        os.flush();
                    }
    	           	else // Subscriber Generate the pi , sk and decrypt the cipher text using sk
    	           	{
						kp=is.readLine(); // Message broker's public key from the Authenticaton Server
						System.out.println("PUBLIC KEY:"+kp);
						s1=new Socket("10.1.24.110", 4447); // To establish the connection to the Message Broker 
						is=new BufferedReader(new InputStreamReader(s1.getInputStream()));
						String cip=is.readLine(); // Encrypted message from Message broker
						System.out.println("Cipher Text : "+cip);
						String rn=is.readLine(); // Random Number from the Message broker
						System.out.println("Random Number : "+rn);
						String pid=is.readLine(); // Publisher ID from the message broker
				   		System.out.println("Master Key : "+publisherKey); //Master key(pm)
						Process pro = Runtime.getRuntime().exec("javac PublisherKey.java");
						Process pro1 = Runtime.getRuntime().exec("java PublisherKey"+" "+pid+" "+publisherKey);
					   	BufferedReader in = new BufferedReader(new InputStreamReader(pro1.getInputStream()));
					   	String pk = in.readLine(); // Subscriber generated publisher key(pk)
					   	System.out.println("PublisherKey : "+pk);
					   	Process pro2 = Runtime.getRuntime().exec("javac SessionKeySub.java");
					   	Process pro3 = Runtime.getRuntime().exec("java SessionKeySub"+" "+pk+" "+rn);
					   	BufferedReader in1 = new BufferedReader(new InputStreamReader(pro3.getInputStream()));
					   	String sk = in1.readLine(); // Subscriber generated Session key(sk)
					   	System.out.println("SessionKey : "+sk);
						pro2=Runtime.getRuntime().exec("javac AESdecrypt.java");
						pro3=Runtime.getRuntime().exec("java AESdecrypt "+sk+" "+cip);
					  	BufferedReader inp1=new BufferedReader(new InputStreamReader(pro3.getInputStream()));
						String msg = inp1.readLine(); // Decrypted Message 
						System.out.println("Message : "+msg);
           	 		}
             	}
                else
                {
                    System.out.println("YOU ARE ALREADY AUTHENTICATED");
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        	System.out.println("Socket read Error");
        }
        finally
        {
            is.close();
            os.close();
            br.close();
            s1.close();
            System.out.println("Connection Closed");
        }
    }
}
