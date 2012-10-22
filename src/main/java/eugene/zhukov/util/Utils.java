package eugene.zhukov.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.xml.bind.DatatypeConverter;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import eugene.zhukov.ApplicationContextProvider;

public class Utils {

	private static Logger logger = Logger.getLogger(Utils.class.getName());

	private final static String ALGORITHM = "RSA";
	private final static String TIME_ZONE = "UTC";
	private final static String CHARSET_NAME = "UTF-8";

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
			logger.fine(e.toString());
		}

		try {
			datatypeFactory = DatatypeFactory.newInstance();

		} catch (DatatypeConfigurationException e) {
			logger.fine(e.toString());
		}
	}

	/**
	 * Decrypts given token and casts to <code>SecureToken</code> object.
	 *
	 * @param token to decrypt
	 * @return SecureToken object
	 */
	public static SecureToken decryptToken(String token) {
		try {
			cipher.init(Cipher.DECRYPT_MODE, privateKey);

			return (SecureToken) new TokenObjectInputStream(new CipherInputStream(
		    		new ByteArrayInputStream(DatatypeConverter.parseBase64Binary(token)), cipher)).readObject();

		} catch (IOException | ClassNotFoundException | InvalidKeyException | ArrayIndexOutOfBoundsException e) {
			logger.fine(e.toString());
			return null;
		}
	}

	/**
     * Converts a java.sql.Timestamp into an instance of XMLGregorianCalendar
     *
     * @param timestamp Instance of java.sql.Timestamp from database
     * @return XMLGregorianCalendar instance
     */
	public static XMLGregorianCalendar asXMLGregorianCalendar(java.sql.Timestamp timestamp) {

    	if (timestamp == null) {
            return null;
        }
    	GregorianCalendar gregorianCalendar = new GregorianCalendar(TimeZone.getTimeZone(TIME_ZONE));
    	gregorianCalendar.setTime(timestamp);
        XMLGregorianCalendar calendar = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
        calendar.setFractionalSecond(null);
        return calendar;
    }

	/**
	 * Checks if given locale is valid according to SCIM schema.
	 *
	 * @param locale String to parse and check
	 * @return boolean true if valid, false otherwise
	 */
	public static boolean isLocaleValid(String locale) {
		String[] split = locale.split("_");

		return split.length == 2
				&& Arrays.binarySearch(Locale.getISOLanguages(), split[0]) > 0
				&& isCountryValid(split[1]);
	}

	/**
	 * Checks if given 2-letter country code is valid according to ISO 3166.
	 *
	 * @param twoLetterCountryCode String to check
	 * @return boolean true if valid, false otherwise
	 */
	public static boolean isCountryValid(String twoLetterCountryCode) {
		return Arrays.binarySearch(Locale.getISOCountries(), twoLetterCountryCode) > 0;
	}

	/**
	 * Trims this string if it is not null.
	 *
	 * @param s string to trim
	 * @return string trimmed or null
	 */
	public static String trimOrNull(String s) {
		s = s != null ? s.trim() : s;
		return s != null && s.length() > 0 ? s : null;
	}

	public static String toSHA1(java.util.Date dateTime) {
		try {
		    MessageDigest md = MessageDigest.getInstance("SHA-1");
		    byte[] digested = md.digest(dateTime.toString().getBytes(CHARSET_NAME));

		    return DatatypeConverter.printBase64Binary(digested);

		} catch(NoSuchAlgorithmException | UnsupportedEncodingException e) {
			logger.fine(e.toString());
	        return null;
	    }
	}

	public static String createVersion(java.util.Date dateTime) {
		    return "\"" + toSHA1(dateTime) + "\"";
	}
}
