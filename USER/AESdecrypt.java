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
public class AESdecrypt
{
	public static void main(String args[])
	{
		/* args[0] - Session key */
		/* args[1] - Cipher text */
		String key=args[0];
		String cipher=args[1];
		try
		{
			String crypt=decrypt(cipher,key);
			System.out.println(crypt);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private static final String ALGORITMO = "AES/CBC/NoPadding";
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
