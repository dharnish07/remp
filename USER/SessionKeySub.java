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
public class SessionKeySub
{
	public static void main(String[] args) throws Exception 
	{
		/* args[0] contain the Publisher Key(pi) for generating session key */
		/* args[1] contain the random number for generating session key */
		String key = args[0];
		byte[] plaintext = args[1].getBytes();
		String crp = encrypt(plaintext, key);
		System.out.println(crp);
	}

	private static final String ALGORITMO = "AES/ECB/NoPadding";
	private static final String CODIFICACION = "UTF-8";

	public static String encrypt(byte[] plaintext, String key)throws NoSuchAlgorithmException, NoSuchPaddingException,InvalidKeyException, IllegalBlockSizeException,BadPaddingException, IOException
	{
		byte[] raw = DatatypeConverter.parseBase64Binary(key);
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

 

