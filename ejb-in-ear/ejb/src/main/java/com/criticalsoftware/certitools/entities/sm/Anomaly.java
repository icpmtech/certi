package com.criticalsoftware.certitools.entities.sm;

import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.enums.AnomalyType;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Anomaly Entity
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
@Entity
@Table(name = "sm_anomaly")
public class Anomaly {

    @Id
    @SequenceGenerator(name = "ANOMALY_ID_GENERATOR", sequenceName = "sm_anomaly_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ANOMALY_ID_GENERATOR")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Contract contract;
    @Enumerated(EnumType.STRING)
    private AnomalyType anomalyType;
    private String code;
    @Temporal(TemporalType.TIMESTAMP)
    private Date datetime;
    @Lob
    private String description;
    private String name;
    private String whoDetected;
    private String internalActors;
    private String externalActors;
    @ManyToOne
    private SecurityImpact securityImpact;
    private String qualifiedEntity;
    @OneToMany(mappedBy = "anomaly", fetch = FetchType.LAZY)
    private List<Document> documents;
    @OneToMany(mappedBy = "anomaly", fetch = FetchType.LAZY)
    private List<CorrectiveAction> correctiveActions;
    @OneToMany(mappedBy = "anomaly", fetch = FetchType.LAZY)
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

    public AnomalyType getAnomalyType() {
        return anomalyType;
    }

    public void setAnomalyType(AnomalyType anomalyType) {
        this.anomalyType = anomalyType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWhoDetected() {
        return whoDetected;
    }

    public void setWhoDetected(String whoDetected) {
        this.whoDetected = whoDetected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInternalActors() {
        return internalActors;
    }

    public void setInternalActors(String internalActors) {
        this.internalActors = internalActors;
    }

    public String getExternalActors() {
        return externalActors;
    }

    public void setExternalActors(String externalActors) {
        this.externalActors = externalActors;
    }

    public SecurityImpact getSecurityImpact() {
        return securityImpact;
    }

    public void setSecurityImpact(SecurityImpact securityImpact) {
        this.securityImpact = securityImpact;
    }

    public String getQualifiedEntity() {
        return qualifiedEntity;
    }

    public void setQualifiedEntity(String qualifiedEntity) {
        this.qualifiedEntity = qualifiedEntity;
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
