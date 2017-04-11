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
		/* To Change the IP address to integer number */
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
		/* To change the converted IP number into 16 byte length */
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
		String key = args[1];
		byte[] plaintext = num.getBytes();
		String crp = encrypt(plaintext, key);
		System.out.println(crp);
	}

	private static final String ALGORITMO = "AES/ECB/NoPadding";
	private static final String CODIFICACION = "UTF-8";

	/* To find the Publisher key pi from publisher ID as plaintext and master key(pm) as key */
	public static String encrypt(byte[] plaintext, String key)throws NoSuchAlgorithmException, NoSuchPaddingException,InvalidKeyException, IllegalBlockSizeException,BadPaddingException, IOException
	{
		byte[] raw = DatatypeConverter.parseHexBinary(key);
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance(ALGORITMO);
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] cipherText = cipher.doFinal(plaintext);
		byte[] iv = "lowpriceeditions".getBytes("UTF-8");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(iv);
		outputStream.write(cipherText);
		byte[] finalData = outputStream.toByteArray();
		String encodedFinalData = DatatypeConverter.printBase64Binary(finalData);
		return encodedFinalData;
	}
}

 

