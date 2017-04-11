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


public class accessticket
{
  	public static void main(String[] args) throws Exception 
  	{
		/*args[3] is given to the key i/p of AES algm.
		arg[3] contain the tk is the one time single access ticket key. */
		
		byte[] ran = new byte[16];
	  	ran = DatatypeConverter.parseBase64Binary(args[3]);
		
		/* arg[0] contain the IP address of the publiser  
		Here we remove the . in the IP address and convert it into long */

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

		/*arg[1] contain the authentication key and 
		arg[2] contain the access write(i.e 0 for publiser, 1 for subscriber) 
		here append the ip_addres,authentication_key and access_right 
		this appended data is given as plain_text for AES algm*/

		String str=ipNumbers+args[1]+args[2];
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
		byte[] key = ran;
	  	String plaintext = num;
	  	String crp = encrypt(plaintext, key);
	  	//String dec = decrypt(crp, key);
  		System.out.println(crp);
  	}
  
	private static final String ALGORITMO = "AES/CBC/PKCS5Padding";
	private static final String CODIFICACION = "UTF-8";

	public static String encrypt(String plaintext, byte[] key)throws NoSuchAlgorithmException, NoSuchPaddingException,InvalidKeyException, IllegalBlockSizeException,BadPaddingException, IOException
	{
		//byte[] raw = DatatypeConverter.parseHexBinary(key);
		SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance(ALGORITMO);
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] cipherText = cipher.doFinal(plaintext.getBytes(CODIFICACION));
		byte[] iv = cipher.getIV();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(iv);
		outputStream.write(cipherText);
		byte[] finalData = outputStream.toByteArray();
		String encodedFinalData = DatatypeConverter.printBase64Binary(finalData);
		return encodedFinalData;
	}

/*public static String decrypt(String encodedInitialData, String key)throws InvalidKeyException, IllegalBlockSizeException,BadPaddingException, UnsupportedEncodingException,NoSuchAlgorithmException, NoSuchPaddingException,InvalidAlgorithmParameterException{
	byte[] encryptedData = DatatypeConverter.parseBase64Binary(encodedInitialData);
	byte[] raw = DatatypeConverter.parseHexBinary(key);
	SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	Cipher cipher = Cipher.getInstance(ALGORITMO);
	byte[] iv = Arrays.copyOfRange(encryptedData, 0, 16);
	byte[] cipherText = Arrays.copyOfRange(encryptedData, 16, encryptedData.length);
	IvParameterSpec iv_specs = new IvParameterSpec(iv);
	cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv_specs);
	byte[] plainTextBytes = cipher.doFinal(cipherText);
	String plainText = new String(plainTextBytes);
	return plainText;
	}*/
}