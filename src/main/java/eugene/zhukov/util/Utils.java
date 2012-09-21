package eugene.zhukov.util;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;

import org.apache.commons.codec.binary.Base64;
import org.springframework.core.io.Resource;

import eugene.zhukov.SecureToken;

public class Utils {

	private static PrivateKey getPrivateKey(Resource keyResource) throws Exception {
		java.io.InputStream is = keyResource.getInputStream();
		byte[] keyBytes = new byte[is.available()];
		is.read(keyBytes);
	    is.close();

	    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
	    return KeyFactory.getInstance("RSA").generatePrivate(spec);
	}
	
	public static SecureToken decryptToken(String token, Resource keyResource) {

		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(keyResource));
		    
		    ObjectInputStream ois = new ObjectInputStream(new CipherInputStream(
		    		new ByteArrayInputStream(Base64.decodeBase64(token)), cipher));
		    return (SecureToken) ois.readObject();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
