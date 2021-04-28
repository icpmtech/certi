package com.criticalsoftware.certitools.persistence.sm;

import com.criticalsoftware.certitools.entities.sm.CodeSequence;
import com.criticalsoftware.certitools.persistence.GenericDAO;

/**
 * Sequence Generator DAO
 *
 * @author miseabra
 * @version $Revision$
 */
public interface SequenceGeneratorDAO extends GenericDAO<CodeSequence, Long> {

    CodeSequence getSequence(String sequenceCode, Long contractId) throws Exception;
}
