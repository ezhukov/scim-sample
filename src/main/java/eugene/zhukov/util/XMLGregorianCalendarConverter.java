package eugene.zhukov.util;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class XMLGregorianCalendarConverter {

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

        try {
        	DatatypeFactory df = DatatypeFactory.newInstance();
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTimeInMillis(date.getTime());
            return df.newXMLGregorianCalendar(gc);

	    } catch (DatatypeConfigurationException dce) {
	        throw new IllegalStateException(
	            "Exception while obtaining DatatypeFactory instance", dce);
	    }
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
