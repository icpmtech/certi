package com.criticalsoftware.certitools.entities.sm;

import com.criticalsoftware.certitools.entities.Contract;

import javax.persistence.*;

/**
 * Emergency User Entity
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
@Entity
@Table(name = "sm_emergencyuser")
public class EmergencyUser {

    @Id
    @SequenceGenerator(name = "EMERGENCY_USER_ID_GENERATOR", sequenceName = "sm_emergencyuser_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EMERGENCY_USER_ID_GENERATOR")
    private Long id;
    private String name;
    private String email;
    @ManyToOne(fetch = FetchType.LAZY)
    private Contract contract;
    private Boolean deleted = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
