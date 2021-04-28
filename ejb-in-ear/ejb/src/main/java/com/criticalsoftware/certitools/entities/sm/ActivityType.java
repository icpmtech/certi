package com.criticalsoftware.certitools.entities.sm;

import com.criticalsoftware.certitools.entities.Contract;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Activity Type Entity
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
@Entity
@Table(name = "sm_activitytype")
public class ActivityType {

    @Id
    @SequenceGenerator(name = "ACTIVITY_TYPE_ID_GENERATOR", sequenceName = "sm_activitytype_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ACTIVITY_TYPE_ID_GENERATOR")
    private Long id;
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    private Contract contract;

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

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }
}
