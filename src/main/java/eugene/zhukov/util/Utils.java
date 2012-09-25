package eugene.zhukov.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.GregorianCalendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import eugene.zhukov.ApplicationContextProvider;

public class Utils {

	private final static String ALGORITHM = "RSA";

	private static Cipher cipher;
	private static DatatypeFactory datatypeFactory;
	private static PrivateKey privateKey;

	static {

		try {
			java.io.InputStream is = ((SecurityConfig) ApplicationContextProvider
					.getContext().getBean(ApplicationContextProvider.SECURITY_CONFIG)).getPrivateKey().getInputStream();
			byte[] privateKeyBinary = new byte[is.available()];
			is.read(privateKeyBinary);
		    is.close();

		    cipher = Cipher.getInstance(ALGORITHM);
		    privateKey = KeyFactory.getInstance(ALGORITHM)
					.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBinary));

		} catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException e) {
			e.printStackTrace();
		}
		
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Decrypts given token and casts to <code>SecureToken</code> object.
	 *
	 * @param token do decrypt
	 * @return SecureToken object
	 */
	public static SecureToken decryptToken(String token) {
		try {
			cipher.init(Cipher.DECRYPT_MODE, privateKey);

			return (SecureToken) new LocalObjectInputStream(new CipherInputStream(
		    		new ByteArrayInputStream(DatatypeConverter.parseBase64Binary(token)), cipher)).readObject();

		} catch (IOException | ClassNotFoundException | InvalidKeyException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
     * Converts a java.util.Date into an instance of XMLGregorianCalendar
     *
     * @param date Instance of java.util.Date
     * @return XMLGregorianCalendar instance
     */
	public static XMLGregorianCalendar asXMLGregorianCalendar(java.util.Date date) {

    	if (date == null) {
            return null;
        }
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(date.getTime());
        return datatypeFactory.newXMLGregorianCalendar(gc);
    }

	/**
     * Converts an XMLGregorianCalendar to an instance of java.util.Date
     *
     * @param xgc Instance of XMLGregorianCalendar
     * @return java.util.Date instance
     */
    public static java.util.Date asDate(XMLGregorianCalendar xgc) {
    	return xgc == null ? null : xgc.toGregorianCalendar().getTime();
    }
}
