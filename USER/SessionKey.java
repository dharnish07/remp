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
public class SessionKey
{
	public static void main(String[] args) throws Exception 
	{
		/* Generate Random Number using Secure Random.
		this random number is used as plian text for AES*/
		SecureRandom sr=new SecureRandom();
		byte[] ran = new byte[8];
		sr.nextBytes(ran);

		/* args[0] contain the pi. 
		this pi is used as key for AES
		*/
	  		
    	String key = args[0];
  		byte[] plaintext = ran;
  	  	String crp = encrypt(DatatypeConverter.printHexBinary(ran), key);
  	  	System.out.println(DatatypeConverter.printHexBinary(ran)+"."+crp);
  		//System.out.println("Hex string size : "+.getBytes("UTF-8").length);
  	}
  
  	private static final String ALGORITMO = "AES/ECB/NoPadding";
	private static final String CODIFICACION = "UTF-8";

	public static String encrypt(String plaintext, String key)throws NoSuchAlgorithmException, NoSuchPaddingException,InvalidKeyException, IllegalBlockSizeException,BadPaddingException, IOException
	{
		byte[] raw = DatatypeConverter.parseBase64Binary(key);
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance(ALGORITMO);
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] cipherText = cipher.doFinal(plaintext.getBytes("UTF-8"));
		byte[] iv = "lowpriceeditions".getBytes("UTF-8");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(iv);
		outputStream.write(cipherText);
		byte[] finalData = outputStream.toByteArray();
		//String finalData = outputStream.toString();
		//System.out.println("final data"+finalData);
		//System.out.println("length of final data"+finalData.length());
		String encodedFinalData = DatatypeConverter.printBase64Binary(finalData);
		return encodedFinalData;
	}
}

 

