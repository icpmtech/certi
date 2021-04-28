package com.criticalsoftware.certitools.business.exception;

/**
 * Exception while generating a csv file
 *
 * @author miseabra
 * @version $Revision: $
 */
@SuppressWarnings("UnusedDeclaration")
public class CSVException extends Exception {

    public CSVException() {
        super("Error while generating a CSV file");
    }

    public CSVException(String message) {
        super(message);
    }

    public CSVException(String message, Throwable cause) {
        super(message, cause);
    }
}
