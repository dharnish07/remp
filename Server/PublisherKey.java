import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.util.UUID;
import java.security.SecureRandom;
public class PublisherKey
{
  	public static void main(String[] args) throws Exception 
  	{
	  /* Generate Random Number using Secure Random 
	  this random number master key pm */
		SecureRandom sr=new SecureRandom();
		byte[] ran = new byte[16];
		sr.nextBytes(ran);			

		/* To Change the IP address to integer number*/
		int[] ip = new int[4];
		long ipNumbers = 0;
		String[] parts = args[0].split("\\.");
		for (int i = 0; i < 4; i++) 
		{
			ip[i] = Integer.parseInt(parts[i]);
		}
		for (int i = 0; i < 4; i++) 
		{
			ipNumbers += ip[i] << (24 - (8 * i));
		}
		String str=ipNumbers+"";
		int l=str.length();
		
		/* change the Converted ipNumber into 16 byte length*/ 
		String num="";
		int j=16/l;
		
		for(int i=0;i<j;i++)
		{
			num+=str;
		}
		j=16%l;
		for(int i=0;i<j;i++)
		{
			num+=str.charAt(i);
		}
	    String key = DatatypeConverter.printHexBinary(ran); //random number generated (i.e) pm
	  	byte[] plaintext = num.getBytes(); 		// IP number of publisher
	  	String crp = encrypt(plaintext, key);
	  	System.out.println(key+"."+crp); 
	}
  
	private static final String ALGORITMO = "AES/ECB/NoPadding";
	private static final String CODIFICACION = "UTF-8";

	public static String encrypt(byte[] plaintext, String key)throws NoSuchAlgorithmException, NoSuchPaddingException,InvalidKeyException, IllegalBlockSizeException,BadPaddingException, IOException
	{
		//System.out.println("Inside encrypt - plaintext : "+plaintext+" Key :"+key);
		byte[] raw = DatatypeConverter.parseHexBinary(key);
		//System.out.println("Length of raw key :"+raw.length);
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance(ALGORITMO);
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] cipherText = cipher.doFinal(plaintext);
		byte[] iv ="lowpriceeditions".getBytes("UTF-8");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(iv);
		outputStream.write(cipherText);
		byte[] finalData = outputStream.toByteArray();
		String encodedFinalData = DatatypeConverter.printBase64Binary(finalData);
		return encodedFinalData;
	}
}

 

