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
public class AESencrypt
{
	public static void main(String args[])
	{
		/* The args[0] contain the session key for Encryption
		the message tobe encrypted is given to the input of variable pliantext
		*/
		String key=args[0];
		String plaintext="Resilient End To End Message Protection for CPS by our Team";
		try
		{
		String crypt=encrypt(plaintext,key);
		String decrypt=decrypt(crypt,key);
		System.out.println(crypt);
		System.out.println(decrypt);
		}
		catch(Exception e)
		{
		e.printStackTrace();
		}
	}
	public static String encrypt(String plaintext, String key)throws NoSuchAlgorithmException, NoSuchPaddingException,InvalidKeyException, IllegalBlockSizeException,BadPaddingException, IOException
	{
		byte[] raw = DatatypeConverter.parseBase64Binary(key);
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance(ALGORITMO);
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] cipherText = cipher.doFinal(plaintext.getBytes());
		byte[] iv = cipher.getIV();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		outputStream.write(iv);
		outputStream.write(cipherText);
		byte[] finalData = outputStream.toByteArray();
		String encodedFinalData = DatatypeConverter.printBase64Binary(finalData);
		return encodedFinalData;
	}
	private static final String ALGORITMO = "AES/CBC/PKCS5Padding";
	private static final String CODIFICACION = "UTF-8";
	public static String decrypt(String encodedInitialData, String key)throws InvalidKeyException, IllegalBlockSizeException,BadPaddingException, UnsupportedEncodingException,NoSuchAlgorithmException, NoSuchPaddingException,InvalidAlgorithmParameterException
	{
		byte[] encryptedData = DatatypeConverter.parseBase64Binary(encodedInitialData);
		byte[] raw = DatatypeConverter.parseBase64Binary(key);
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance(ALGORITMO);
		byte[] iv = Arrays.copyOfRange(encryptedData, 0, 16);
		byte[] cipherText = Arrays.copyOfRange(encryptedData, 16, encryptedData.length);
		IvParameterSpec iv_specs = new IvParameterSpec(iv);
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv_specs);
		byte[] plainTextBytes = cipher.doFinal(cipherText);
		String plainText = new String(plainTextBytes);
		return plainText;
	}
}
