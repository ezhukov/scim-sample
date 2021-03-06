package eugene.zhukov.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

public class TokenObjectInputStream extends ObjectInputStream {

    public TokenObjectInputStream(InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException {
    	super.readClassDescriptor();
    	return ObjectStreamClass.lookup(eugene.zhukov.util.SecureToken.class);
    }
}
