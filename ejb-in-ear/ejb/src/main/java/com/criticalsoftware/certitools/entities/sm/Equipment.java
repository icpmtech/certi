package com.criticalsoftware.certitools.entities.sm;

import com.criticalsoftware.certitools.entities.Contract;

import javax.persistence.*;

/**
 * Equipment Entity
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
@Entity
@Table(name = "sm_equipment")
public class Equipment {

    @Id
    @SequenceGenerator(name = "EQUIPMENT_ID_GENERATOR", sequenceName = "sm_equipment_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EQUIPMENT_ID_GENERATOR")
    private Long id;
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    private Contract contract;
    @OneToOne(mappedBy = "equipment")
    private Document document;

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

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}
