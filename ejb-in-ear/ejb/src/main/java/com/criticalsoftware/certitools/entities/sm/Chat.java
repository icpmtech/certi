package com.criticalsoftware.certitools.entities.sm;

import com.criticalsoftware.certitools.entities.User;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Chat Entity
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
@Entity
@Table(name = "sm_chat")
public class Chat {

    @Id
    @SequenceGenerator(name = "CHAT_ID_GENERATOR", sequenceName = "sm_chat_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CHAT_ID_GENERATOR")
    private Long id;
    @ManyToOne
    private User user;
    @ManyToOne
    private EmergencyUser emergencyUser;
    private String message;
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetime;
    @ManyToOne(fetch = FetchType.LAZY)
    private EmergencyAction emergencyAction;
    @ManyToOne(fetch = FetchType.LAZY)
    private Activity activity;
    @ManyToOne(fetch = FetchType.LAZY)
    private SecurityImpactWork securityImpactWork;
    @ManyToOne(fetch = FetchType.LAZY)
    private Maintenance maintenance;
    @ManyToOne(fetch = FetchType.LAZY)
    private Anomaly anomaly;
    @ManyToOne(fetch = FetchType.LAZY)
    private CorrectiveAction correctiveAction;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public EmergencyUser getEmergencyUser() {
        return emergencyUser;
    }

    public void setEmergencyUser(EmergencyUser emergencyUser) {
        this.emergencyUser = emergencyUser;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public EmergencyAction getEmergencyAction() {
        return emergencyAction;
    }

    public void setEmergencyAction(EmergencyAction emergencyAction) {
        this.emergencyAction = emergencyAction;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public SecurityImpactWork getSecurityImpactWork() {
        return securityImpactWork;
    }

    public void setSecurityImpactWork(SecurityImpactWork securityImpactWork) {
        this.securityImpactWork = securityImpactWork;
    }

    public Maintenance getMaintenance() {
        return maintenance;
    }

    public void setMaintenance(Maintenance maintenance) {
        this.maintenance = maintenance;
    }

    public Anomaly getAnomaly() {
        return anomaly;
    }

    public void setAnomaly(Anomaly anomaly) {
        this.anomaly = anomaly;
    }

    public CorrectiveAction getCorrectiveAction() {
        return correctiveAction;
    }

    public void setCorrectiveAction(CorrectiveAction correctiveAction) {
        this.correctiveAction = correctiveAction;
    }
}
