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

/**
 * Emergency Token Entity
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
@Entity
@Table(name = "sm_emergencytoken")
public class EmergencyToken {

    @Id
    @SequenceGenerator(name = "EMERGENCY_TOKEN_ID_GENERATOR", sequenceName = "sm_emergencytoken_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EMERGENCY_TOKEN_ID_GENERATOR")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private EmergencyAction emergencyAction;
    @ManyToOne
    private User user;
    @ManyToOne
    private EmergencyUser emergencyUser;
    private String accessToken;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EmergencyAction getEmergencyAction() {
        return emergencyAction;
    }

    public void setEmergencyAction(EmergencyAction emergencyAction) {
        this.emergencyAction = emergencyAction;
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

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
