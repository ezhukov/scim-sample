package eugene.zhukov.util;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import eugene.zhukov.ApplicationContextProvider;

/**
 * Some useful commands for public/private keys' generation.
 * 
 * # generate a 2048-bit RSA private key
 * $ openssl genrsa -out private_key.pem 2048
 *
 * # convert private Key to PKCS#8 format (so Java can read it)
 * $ openssl pkcs8 -topk8 -inform PEM -outform DER -in private_key.pem \
 *   -out private_key.der -nocrypt
 *
 * # output public key portion in DER format (so Java can read it)
 * $ openssl rsa -in private_key.pem -pubout -outform DER -out public_key.der
 * 
 * @author eugene
 *
 */
public class TestRSA {

	@BeforeClass
	public static void initAppContext() throws Exception {
		new ApplicationContextProvider().setApplicationContext(
				new ClassPathXmlApplicationContext("springConfiguration.xml"));
	}

	@Test
	public void check() throws Exception {
		String password = "pa$$word8888889!";
		long timestamp = System.currentTimeMillis();

	    S token = new S();
	    token.setPassword(password);
	    token.setTimestamp(timestamp);

		String encrypted = encrypt(token);

//        System.out.println(encrypted);

		SecureToken decrypted = Utils.decryptToken(encrypted);

		Assert.assertEquals(password, decrypted.getPassword());
		Assert.assertEquals(timestamp, decrypted.getTimestamp());
	}

	public static String encrypt(S token) throws Exception {
		return encrypt(token, getKeyBytes("public_key.der"));
	}
	
	public static String encrypt(S token, byte[] publicKey) throws Exception {
		String algorithm = "RSA";
        javax.crypto.Cipher cipher
                = javax.crypto.Cipher.getInstance(algorithm);

        java.security.spec.X509EncodedKeySpec keySpec
                = new java.security.spec.X509EncodedKeySpec(publicKey);

        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE,
                java.security.KeyFactory.getInstance(
                        algorithm).generatePublic(keySpec));

        java.io.ByteArrayOutputStream tokenByteStream
                = new java.io.ByteArrayOutputStream();

        java.io.ObjectOutputStream tokenObjectStream
                = new java.io.ObjectOutputStream(tokenByteStream);
        tokenObjectStream.writeObject(token);
        tokenObjectStream.close();
        // System.out.println("Byte array length before encryption: " + tokenByteStream.toByteArray().length);
        java.io.ByteArrayOutputStream encryptedTokenStream
                = new java.io.ByteArrayOutputStream();
        javax.crypto.CipherOutputStream cipherOutStream
                = new javax.crypto.CipherOutputStream(
                        encryptedTokenStream, cipher);
        cipherOutStream.write(tokenByteStream.toByteArray());
        cipherOutStream.close();

        return javax.xml.bind.DatatypeConverter
                .printBase64Binary(encryptedTokenStream.toByteArray());
    }

	private static byte[] getKeyBytes(String key) throws Exception {
		java.io.InputStream is = ClassLoader.getSystemResourceAsStream(key);
		byte[] keyBytes = new byte[is.available()];
		is.read(keyBytes);
		is.close();
		return keyBytes;
	}
}
