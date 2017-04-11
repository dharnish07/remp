import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
 
public class MessageBroker
{
	public static boolean flag=false;
	public static String cipher=null;
	public static String rnd=null;
	public static String pid=null;
	
	public static void main(String args[])
	{
	    Socket s1=null;
		Socket s2=null;
		ServerSocket ss2=null;
		String tk=null,kp=null,kr=null;
		BufferedReader br=null;
		BufferedReader is=null;
		PrintWriter os=null;
		Process pro=null,pro1=null;
		try
		{
			s1=new Socket("10.1.24.29", 4446); // You can use static final constant PORT_NUM
		 	//br= new BufferedReader(new InputStreamReader(System.in));
	        is=new BufferedReader(new InputStreamReader(s1.getInputStream()));
	        os= new PrintWriter(s1.getOutputStream());	
	        //System.out.println("Client Address : "+address);
		    pro = Runtime.getRuntime().exec("javac GeneratePublicPrivateKeys.java");
	 		pro1 = Runtime.getRuntime().exec("java GeneratePublicPrivateKeys");
			String line = null;
			String line1[]= new String[2];
			BufferedReader in = new BufferedReader(new InputStreamReader(pro1.getInputStream()));
			line = in.readLine();
			line1=line.split("\\.");
			kr=line1[0];
			kp=line1[1];
			System.out.println(kr);
			System.out.println(kp);
			tk=is.readLine();
			System.out.println("Ticket tk:"+tk);
			os.println(kp);
			os.flush();
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		}
		try
		{
			ss2 = new ServerSocket(4447); // can also use static final PORT_NUM , when defined
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.out.println("Server error");
		}
		while(true)
		{
			try
			{
				s1= ss2.accept();
				System.out.println("connection Established"+s1);
				MBThread st=new MBThread(s1);
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
class MBThread extends Thread
{
	Socket s=null;
	public MBThread(Socket s)
	{
		this.s = s;
	}
	public void run()
	{
	 	BufferedReader is=null;
		PrintWriter os= null;
		try
		{
		 	is=new BufferedReader(new InputStreamReader(s.getInputStream()));
		 	os=new PrintWriter(s.getOutputStream());
			if(!MessageBroker.flag)
			{
				MessageBroker.flag=true;  
				MessageBroker.pid = s.getInetAddress().getHostAddress(); 
				System.out.println("established connection "+s+" Publisher ID : "+MessageBroker.pid);
				MessageBroker.rnd=is.readLine();
				System.out.println("Random Number : "+MessageBroker.rnd);
				MessageBroker.cipher= is.readLine();
				System.out.println("Cipher Text :"+MessageBroker.cipher);	
			}
			else
			{
	         	os.println(MessageBroker.cipher);
	         	os.flush();
	         	os.println(MessageBroker.rnd);
	         	os.flush();
	         	os.println(MessageBroker.pid);
	         	os.flush();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
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
