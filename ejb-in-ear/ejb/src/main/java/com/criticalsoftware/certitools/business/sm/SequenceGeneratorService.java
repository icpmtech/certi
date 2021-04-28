package com.criticalsoftware.certitools.business.sm;

import com.criticalsoftware.certitools.business.exception.InvalidSequenceException;
import com.criticalsoftware.certitools.util.enums.SequenceCode;

/**
 * Sequence Generator Service
 *
 * @author miseabra
 * @version $Revision$
 */
public interface SequenceGeneratorService {

    String generateCode(SequenceCode sequenceCode, Long contractId) throws InvalidSequenceException;
}
