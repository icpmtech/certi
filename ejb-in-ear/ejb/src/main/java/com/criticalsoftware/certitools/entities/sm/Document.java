package com.criticalsoftware.certitools.entities.sm;

import com.criticalsoftware.certitools.entities.Contract;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Document Entity
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
@Entity
@Table(name = "sm_document")
public class Document {

    @Id
    @SequenceGenerator(name = "DOCUMENT_ID_GENERATOR", sequenceName = "sm_document_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DOCUMENT_ID_GENERATOR")
    private Long id;
    private String name;
    private String displayName;
    private String contentType;
    private Integer contentLength;
    private byte[] content;
    @ManyToOne(fetch = FetchType.LAZY)
    private Contract contract;
    @ManyToOne(fetch = FetchType.LAZY)
    private Activity activity;
    @ManyToOne(fetch = FetchType.LAZY)
    private CorrectiveAction correctiveAction;
    @ManyToOne(fetch = FetchType.LAZY)
    private Anomaly anomaly;
    @ManyToOne(fetch = FetchType.LAZY)
    private SecurityImpactWork securityImpactWork;
    @ManyToOne(fetch = FetchType.LAZY)
    private Maintenance maintenance;
    @ManyToOne(fetch = FetchType.LAZY)
    private EmergencyAction emergencyAction;
    @OneToOne(fetch = FetchType.LAZY)
    private Equipment equipment;

    public Document() {
    }

    public Document(Long id, String displayName, String name, String contentType, Integer contentLength) {
        this.id = id;
        this.displayName = displayName;
        this.name = name;
        this.contentType = contentType;
        this.contentLength = contentLength;
    }

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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Integer getContentLength() {
        return contentLength;
    }

    public void setContentLength(Integer contentLength) {
        this.contentLength = contentLength;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public CorrectiveAction getCorrectiveAction() {
        return correctiveAction;
    }

    public void setCorrectiveAction(CorrectiveAction correctiveAction) {
        this.correctiveAction = correctiveAction;
    }

    public Anomaly getAnomaly() {
        return anomaly;
    }

    public void setAnomaly(Anomaly anomaly) {
        this.anomaly = anomaly;
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

    public EmergencyAction getEmergencyAction() {
        return emergencyAction;
    }

    public void setEmergencyAction(EmergencyAction emergencyAction) {
        this.emergencyAction = emergencyAction;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }
}
