package com.criticalsoftware.certitools.business.exception;

/**
 * Excel Exception
 *
 * @author miseabra
 * @version $Revision: $
 */
@SuppressWarnings("UnusedDeclaration")
public class ExcelException extends Exception {

    public ExcelException() {
        super("Error while generating an excel file.");
    }

    public ExcelException(String message) {
        super(message);
    }

    public ExcelException(String message, Throwable cause) {
        super(message, cause);
    }
}
