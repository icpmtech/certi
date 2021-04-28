package com.criticalsoftware.certitools.entities.sm;

import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.User;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Emergency Action Entity
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
@Entity
@Table(name = "sm_emergencyaction")
public class EmergencyAction {

    @Id
    @SequenceGenerator(name = "EMERGENCY_ACTION_ID_GENERATOR", sequenceName = "sm_emergencyaction_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "EMERGENCY_ACTION_ID_GENERATOR")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Contract contract;
    private String code;
    private String origin;
    @Lob
    private String description;
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @OneToMany(mappedBy = "emergencyAction", fetch = FetchType.LAZY)
    private List<Document> documents;
    @OneToMany(mappedBy = "emergencyAction", fetch = FetchType.LAZY)
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
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
