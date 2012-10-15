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
		javax.crypto.Cipher c = javax.crypto.Cipher.getInstance("RSA");
		java.security.spec.X509EncodedKeySpec spec = new java.security.spec.X509EncodedKeySpec(getKeyBytes("public_key.der"));
	    c.init(javax.crypto.Cipher.ENCRYPT_MODE, java.security.KeyFactory.getInstance("RSA").generatePublic(spec));

	    java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
	    java.io.ObjectOutputStream out = new java.io.ObjectOutputStream(baos);
        out.writeObject(token);
        out.flush();

        byte[] tokenBytes = baos.toByteArray();
//        System.out.println("Byte array length before encryption: " + tokenBytes.length);

        baos = new java.io.ByteArrayOutputStream();
        javax.crypto.CipherOutputStream cout = new javax.crypto.CipherOutputStream(baos, c);
        cout.write(tokenBytes);
        cout.close();

        return javax.xml.bind.DatatypeConverter.printBase64Binary(baos.toByteArray());
	}

	private static byte[] getKeyBytes(String key) throws Exception {
		java.io.InputStream is = ClassLoader.getSystemResourceAsStream(key);
		byte[] keyBytes = new byte[is.available()];
		is.read(keyBytes);
		is.close();
		return keyBytes;
	}
}
