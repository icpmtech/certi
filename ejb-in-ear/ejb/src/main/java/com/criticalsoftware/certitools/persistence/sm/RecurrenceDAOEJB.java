package com.criticalsoftware.certitools.persistence.sm;

import com.criticalsoftware.certitools.entities.sm.Recurrence;
import com.criticalsoftware.certitools.entities.sm.RecurrenceNotification;
import com.criticalsoftware.certitools.entities.sm.RecurrenceType;
import com.criticalsoftware.certitools.persistence.GenericDAOEJB;
import com.criticalsoftware.certitools.util.Logger;
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
import java.util.List;

/**
 * Recurrence DAO EJB
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
@Stateless
@Local(RecurrenceDAO.class)
@LocalBinding(jndiBinding = "certitools/RecurrenceDAO")
@SecurityDomain("CertiToolsRealm")
@RolesAllowed({"private"})
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class RecurrenceDAOEJB extends GenericDAOEJB<Recurrence, Long> implements RecurrenceDAO {

    private static final Logger LOGGER = Logger.getInstance(RecurrenceDAOEJB.class);

    public RecurrenceNotification insertRecurrenceNotification(RecurrenceNotification notification) {
        manager.persist(notification);
        return notification;
    }

    public void deleteRecurrenceNotifications(Long recurrenceId) {
        Query query = manager.createQuery("delete from RecurrenceNotification rn where rn.recurrence.id = :recurrenceId");
        query.setParameter("recurrenceId", recurrenceId);
        query.executeUpdate();
    }

    @SuppressWarnings({"unchecked"})
    public List<Recurrence> findScheduledRecurrences() {
        Query query = manager.createQuery("select distinct r from Recurrence r left join fetch r.notifications " +
                "where r.nextScheduledDate <= :nextDate and r.active = true");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        query.setParameter("nextDate", calendar.getTime());
        return query.getResultList();
    }

    @SuppressWarnings({"unchecked"})
    public List<RecurrenceType> findRecurrenceTypes() {
        Query query = manager.createQuery("select rt from RecurrenceType rt ");
        return query.getResultList();
    }

    public RecurrenceType findRecurrenceType(Long recurrenceTypeId) {
        Query query = manager.createQuery("select rt from RecurrenceType rt where rt.id = :recurrenceTypeId");
        query.setParameter("recurrenceTypeId", recurrenceTypeId);
        try {
            return (RecurrenceType) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }
}
