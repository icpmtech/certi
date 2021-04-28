package com.criticalsoftware.certitools.business.sm;

/**
 * Sequence Generator
 *
 * @author miseabra
 * @version $Revision$
 */

import com.criticalsoftware.certitools.business.exception.InvalidSequenceException;
import com.criticalsoftware.certitools.entities.sm.CodeSequence;
import com.criticalsoftware.certitools.persistence.sm.SequenceGeneratorDAO;
import com.criticalsoftware.certitools.util.Configuration;
import com.criticalsoftware.certitools.util.enums.SequenceCode;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RunAs;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
@Stateless
@Local(SequenceGeneratorService.class)
@LocalBinding(jndiBinding = "certitools/SequenceGeneratorService")
@SecurityDomain("CertiToolsRealm")
@RunAs("private")
public class SequenceGeneratorServiceEJB implements SequenceGeneratorService {

    @EJB
    private SequenceGeneratorDAO sequenceGeneratorDAO;

    @PermitAll
    public String generateCode(SequenceCode sequenceCode, Long contractId) throws InvalidSequenceException {

        final List<Object> components = new ArrayList<Object>();
        int numberRetries = 0;
        int totalRetries = 10;
        boolean valueGenerated = false;
        String pattern;
        CodeSequence sequence;
        String code = null;

        while (!valueGenerated) {
            try {
                switch (sequenceCode) {
                    case ACTIVITY:
                        sequence = sequenceGeneratorDAO.getSequence(sequenceCode.toString(), contractId);
                        components.add(sequence.getValue());
                        components.add(new Date());
                        pattern = Configuration.getInstance().getSmActivityCodeSequencePattern();
                        break;
                    case ANOMALY:
                        sequence = sequenceGeneratorDAO.getSequence(sequenceCode.toString(), contractId);
                        components.add(sequence.getValue());
                        components.add(new Date());
                        pattern = Configuration.getInstance().getSmAnomalyCodeSequencePattern();
                        break;
                    case OCCURRENCE:
                        sequence = sequenceGeneratorDAO.getSequence(sequenceCode.toString(), contractId);
                        components.add(sequence.getValue());
                        components.add(new Date());
                        pattern = Configuration.getInstance().getSmOccurrenceCodeSequencePattern();
                        break;
                    case MODIFICATION:
                        sequence = sequenceGeneratorDAO.getSequence(sequenceCode.toString(), contractId);
                        components.add(sequence.getValue());
                        components.add(new Date());
                        pattern = Configuration.getInstance().getSmModificationCodeSequencePattern();
                        break;
                    case WORK_AUTHORIZATION:
                        sequence = sequenceGeneratorDAO.getSequence(sequenceCode.toString(), contractId);
                        components.add(sequence.getValue());
                        components.add(new Date());
                        pattern = Configuration.getInstance().getSmWorkAuthorizationCodeSequencePattern();
                        break;
                    case CORRECTIVE_ACTION:
                        sequence = sequenceGeneratorDAO.getSequence(sequenceCode.toString(), contractId);
                        components.add(sequence.getValue());
                        components.add(new Date());
                        pattern = Configuration.getInstance().getSmCorrectiveActionCodeSequencePattern();
                        break;
                    case MAINTENANCE:
                        sequence = sequenceGeneratorDAO.getSequence(sequenceCode.toString(), contractId);
                        components.add(sequence.getValue());
                        components.add(new Date());
                        pattern = Configuration.getInstance().getSmMaintenanceCodeSequencePattern();
                        break;
                    case EMERGENCY_ACTION:
                        sequence = sequenceGeneratorDAO.getSequence(sequenceCode.toString(), contractId);
                        components.add(sequence.getValue());
                        components.add(new Date());
                        pattern = Configuration.getInstance().getSmEmergencyActionCodeSequencePattern();
                        break;
                    default:
                        throw new UnsupportedOperationException(MessageFormat.format("Invalid sequence: {0}",
                                sequenceCode.toString()));
                }

                code = MessageFormat.format(pattern, components.toArray());

                valueGenerated = true;

            } catch (UnsupportedOperationException e) {
                throw new InvalidSequenceException(sequenceCode.toString(), e.getCause());

            } catch (Exception e) {
                if (numberRetries == totalRetries) {
                    throw new InvalidSequenceException(sequenceCode.toString(), e.getCause());
                }
                numberRetries++;

                try {
                    long waitTime = (long) Math.pow(2, numberRetries) * 10L;
                    Thread.sleep(waitTime);
                } catch (InterruptedException e1) {
                    throw new InvalidSequenceException(sequenceCode.toString(), e1.getCause());
                }
            }
        }

        return code;
    }
}
