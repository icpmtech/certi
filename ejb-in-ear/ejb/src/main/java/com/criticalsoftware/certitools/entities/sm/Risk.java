package com.criticalsoftware.certitools.entities.sm;

import javax.persistence.*;
import java.util.List;

/**
 * Risk Entity
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
@Entity
@Table(name = "sm_risk")
public class Risk {

    @Id
    @SequenceGenerator(name = "RISK_ID_GENERATOR", sequenceName = "sm_risk_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RISK_ID_GENERATOR")
    private Long id;
    private String name;
    @Lob
    private String preventiveMeasures;
    @ManyToMany(mappedBy = "risks")
    private List<SecurityImpactWork> securityImpactWorks;

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

    public String getPreventiveMeasures() {
        return preventiveMeasures;
    }

    public void setPreventiveMeasures(String preventiveMeasures) {
        this.preventiveMeasures = preventiveMeasures;
    }

    public List<SecurityImpactWork> getSecurityImpactWorks() {
        return securityImpactWorks;
    }

    public void setSecurityImpactWorks(List<SecurityImpactWork> securityImpactWorks) {
        this.securityImpactWorks = securityImpactWorks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Risk risk = (Risk) o;

        if (!id.equals(risk.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
