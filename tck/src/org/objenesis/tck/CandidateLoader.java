package org.objenesis.tck;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Properties;

/**
 * Loads a set of candidate classes from a properties file into the TCK.
 * <p/>
 * The properties file takes the form of candidateClassName=shortDescription.
 *
 * @author Joe Walnes
 * @see TCK
 */
public class CandidateLoader {

    private final TCK tck;
    private final ClassLoader classloader;
    private final ErrorHandler errorHandler;

    /**
     * Handler for reporting errors from the CandidateLoader.
     */
    public static interface ErrorHandler {
        void classNotFound(String name);
    }

    /**
     * Error handler that logs errors to a text stream.
     */
    public static class LoggingErrorHandler implements CandidateLoader.ErrorHandler {

        private final PrintStream out;

        public LoggingErrorHandler(PrintStream out) {
            this.out = out;
        }

        public void classNotFound(String name) {
            out.println("Class not found : " + name);
        }

    }

    public CandidateLoader(TCK tck, ClassLoader classloader, ErrorHandler errorHandler) {
        this.tck = tck;
        this.classloader = classloader;
        this.errorHandler = errorHandler;
    }

    public void loadFrom(InputStream inputStream) throws IOException {
        // Properties contains a convenient key=value parser, however it stores
        // the entries in a Hashtable which loses the original order.
        // So, we create a special Properties instance that writes its
        // entries directly to the TCK (which retains order).
        Properties properties = new Properties() {
            public Object put(Object key, Object value) {
                handlePropertyEntry((String) key, (String) value);
                return null;
            }
        };
        properties.load(inputStream);
    }

    public void loadFromResource(Class cls, String resource) throws IOException {
        InputStream candidatesConfig = cls.getResourceAsStream(resource);
        if (candidatesConfig == null) {
            throw new IOException("Resource '" + resource + "' not found relative to " + cls.getName());
        }
        try {
            loadFrom(candidatesConfig);
        } finally {
            candidatesConfig.close();
        }
    }

    private void handlePropertyEntry(String key, String value) {
        try {
            Class candidate = Class.forName(key, true, classloader);
            tck.registerCandidate(candidate, value);
        } catch (ClassNotFoundException e) {
            errorHandler.classNotFound(key);
        }
    }

}
