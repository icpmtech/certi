package com.criticalsoftware.certitools.entities.sm;

import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.User;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Maintenance Entity
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
@Entity
@Table(name = "sm_maintenance")
public class Maintenance {

    @Id
    @SequenceGenerator(name = "MAINTENANCE_ID_GENERATOR", sequenceName = "sm_maintenance_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MAINTENANCE_ID_GENERATOR")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Contract contract;
    @ManyToOne
    private MaintenanceType maintenanceType;
    private String code;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateScheduled;
    @ManyToOne
    private Equipment equipment;
    private String designation;
    @Lob
    private String description;
    private String internalResponsible;
    private String externalEntity;
    @ManyToOne(fetch = FetchType.LAZY)
    private Recurrence recurrence;
    @OneToMany(mappedBy = "maintenance", fetch = FetchType.LAZY)
    private List<Document> documents;
    @OneToMany(mappedBy = "maintenance", fetch = FetchType.LAZY)
    private List<CorrectiveAction> correctiveActions;
    @OneToMany(mappedBy = "maintenance", fetch = FetchType.LAZY)
    private List<Chat> chatMessages;
    @Temporal(TemporalType.TIMESTAMP)
    private Date closedDate;
    private Boolean deleted;
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date changedDate;
    @ManyToOne(fetch = FetchType.LAZY)
    private User createdBy;
    @ManyToOne(fetch = FetchType.LAZY)
    private User changedBy;
    @Transient
    private Boolean hasChatMessages;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public MaintenanceType getMaintenanceType() {
        return maintenanceType;
    }

    public void setMaintenanceType(MaintenanceType maintenanceType) {
        this.maintenanceType = maintenanceType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getDateScheduled() {
        return dateScheduled;
    }

    public void setDateScheduled(Date dateScheduled) {
        this.dateScheduled = dateScheduled;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInternalResponsible() {
        return internalResponsible;
    }

    public void setInternalResponsible(String internalResponsible) {
        this.internalResponsible = internalResponsible;
    }

    public String getExternalEntity() {
        return externalEntity;
    }

    public void setExternalEntity(String externalEntity) {
        this.externalEntity = externalEntity;
    }

    public Recurrence getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(Recurrence recurrence) {
        this.recurrence = recurrence;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public List<CorrectiveAction> getCorrectiveActions() {
        return correctiveActions;
    }

    public void setCorrectiveActions(List<CorrectiveAction> correctiveActions) {
        this.correctiveActions = correctiveActions;
    }

    public List<Chat> getChatMessages() {
        return chatMessages;
    }

    public void setChatMessages(List<Chat> chatMessages) {
        this.chatMessages = chatMessages;
    }

    public Date getClosedDate() {
        return closedDate;
    }

    public void setClosedDate(Date closedDate) {
        this.closedDate = closedDate;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getChangedDate() {
        return changedDate;
    }

    public void setChangedDate(Date changedDate) {
        this.changedDate = changedDate;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(User changedBy) {
        this.changedBy = changedBy;
    }

    public boolean getClosed() {
        return closedDate != null && closedDate.before(new Date());
    }

    public Boolean getHasChatMessages() {
        if (hasChatMessages == null) {
            return false;
        }
        return hasChatMessages;
    }

    public void setHasChatMessages(Boolean hasChatMessages) {
        this.hasChatMessages = hasChatMessages;
    }
}
