package com.criticalsoftware.certitools.entities.sm;

import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.sm.enums.SubModuleType;

import javax.persistence.*;

/**
 * SubModule Entity
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
@Entity
@Table(name = "sm_submodule")
public class SubModule {

    @Id
    @SequenceGenerator(name = "SUBMODULE_ID_GENERATOR", sequenceName = "sm_submodule_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SUBMODULE_ID_GENERATOR")
    private Long id;
    @Enumerated(EnumType.STRING)
    private SubModuleType subModuleType;
    @ManyToOne(fetch = FetchType.LAZY)
    private Contract contract;

    @Transient
    private String name;

    public SubModule() {
    }

    public SubModule(SubModuleType subModuleType) {
        this.subModuleType = subModuleType;
    }

    public SubModule(SubModuleType subModuleType, String name) {
        this.subModuleType = subModuleType;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SubModuleType getSubModuleType() {
        return subModuleType;
    }

    public void setSubModuleType(SubModuleType subModuleType) {
        this.subModuleType = subModuleType;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
