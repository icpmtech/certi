package com.criticalsoftware.certitools.persistence.sm;

import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.sm.CodeSequence;
import com.criticalsoftware.certitools.persistence.GenericDAOEJB;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Calendar;

/**
 * Sequence Generator DAO EJB
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
@Stateless
@Local(SequenceGeneratorDAO.class)
@LocalBinding(jndiBinding = "certitools/SequenceGeneratorDAO")
@SecurityDomain("CertiToolsRealm")
@RolesAllowed({"private"})
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class SequenceGeneratorDAOEJB extends GenericDAOEJB<CodeSequence, Long> implements SequenceGeneratorDAO {

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public CodeSequence getSequence(String sequenceCode, Long contractId) throws Exception {

        Integer actualYear = Calendar.getInstance().get(Calendar.YEAR);
        Integer value = 1;
        Integer year;

        CodeSequence sequence;

        try {
            // throws a NoResultException if the sequence is not found
            Query query = manager.createQuery("SELECT s FROM CodeSequence s " +
                    "WHERE s.code = :code AND s.contract.id = :contractId");
            query.setParameter("code", sequenceCode);
            query.setParameter("contractId", contractId);
            sequence = (CodeSequence) query.getSingleResult();

            year = sequence.getYear();
            if (year == null || !year.equals(actualYear)) {
                // reset value and set year to current year.
                year = actualYear;
                value = 1;
            } else {
                value = sequence.getValue() + 1;
            }

            //update sequence
            sequence.setValue(value);
            sequence.setYear(year);

        } catch (NoResultException e) {
            sequence = new CodeSequence();
            sequence.setCode(sequenceCode);
            sequence.setValue(value);
            sequence.setYear(actualYear);
            sequence.setContract(manager.getReference(Contract.class, contractId));
            insert(sequence);
        }

        return sequence;
    }
}
