package eugene.zhukov.util;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.xml.bind.DatatypeConverter;

import org.junit.Assert;
import org.junit.Test;

public class TestRSA {

	public static PrivateKey getPrivate() throws Exception {
	    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(getKeyBytes("private_key.der"));
	    return KeyFactory.getInstance("RSA").generatePrivate(spec);
	}

	public static PublicKey getPublic() throws Exception {
	    X509EncodedKeySpec spec = new X509EncodedKeySpec(getKeyBytes("public_key.der"));
	    return KeyFactory.getInstance("RSA").generatePublic(spec);
	}

	private static byte[] getKeyBytes(String key) throws Exception {
		java.io.InputStream is = ClassLoader.getSystemResourceAsStream(key);
		byte[] keyBytes = new byte[is.available()];
		is.read(keyBytes);
		is.close();
		return keyBytes;
	}

	@Test
	public void check() throws Exception {
	    S token = new S();
	    token.setPassword("pa$$word8888889!");
	    token.setTimestamp(System.currentTimeMillis());

		String encrypted = encrypt(token);

//		System.out.println(encrypted);

		S decrypted = decrypt(encrypted);

//		System.out.println(decrypted);
		Assert.assertEquals("pa$$word8888889!", decrypted.getPassword());
	}
	
	private static String encrypt(S token) throws Exception {
		Cipher c = Cipher.getInstance("RSA");
	    c.init(Cipher.ENCRYPT_MODE, getPublic());

	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(baos);
        out.writeObject(token);
        out.flush();
        
        byte[] tokenBytes = baos.toByteArray();
//        System.out.println("Byte array length before encryption: " + tokenBytes.length);
        
        baos = new ByteArrayOutputStream();
        CipherOutputStream cout = new CipherOutputStream(baos, c);
        cout.write(tokenBytes);
        cout.close();

        return DatatypeConverter.printBase64Binary(baos.toByteArray());
	}
	
	private static S decrypt(String token) throws Exception {
		Cipher c = Cipher.getInstance("RSA");
		c.init(Cipher.DECRYPT_MODE, getPrivate());

		ByteArrayInputStream in2 = new ByteArrayInputStream(DatatypeConverter.parseBase64Binary(token));
	    ObjectInputStream ois = new ObjectInputStream(new CipherInputStream(in2, c));

	    return (S) ois.readObject();
	}
}
