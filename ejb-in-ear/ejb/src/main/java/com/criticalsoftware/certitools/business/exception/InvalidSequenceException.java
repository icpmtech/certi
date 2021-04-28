package com.criticalsoftware.certitools.business.exception;

/**
 * Invalid Sequence Exception
 *
 * @author miseabra
 * @version $Revision$
 */
public class InvalidSequenceException extends Exception {

    public InvalidSequenceException(String sequenceCode, Throwable cause) {
        super("Could not generate a new value for sequence " + sequenceCode, cause);
    }
}
