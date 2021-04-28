package com.criticalsoftware.certitools.entities.sm;

import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.sm.enums.WorkType;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Security Impact Work Entity
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
@Entity
@Table(name = "sm_securityimpactwork")
public class SecurityImpactWork {

    @Id
    @SequenceGenerator(name = "SECURITY_IMPACT_WORK_ID_GENERATOR", sequenceName = "sm_securityimpactwork_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECURITY_IMPACT_WORK_ID_GENERATOR")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Contract contract;
    @Enumerated(EnumType.STRING)
    private WorkType workType;
    private String code;
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @Lob
    private String description;
    private String name;
    private String responsible;
    private String qualifiedEntity;
    @ManyToOne
    private SecurityImpact securityImpact;
    private String duration;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "sm_securityimpactwork_risk",
            joinColumns = {@JoinColumn(name = "securityimpactwork_id")},
            inverseJoinColumns = {@JoinColumn(name = "risk_id")})
    private List<Risk> risks;
    @OneToMany(mappedBy = "securityImpactWork", fetch = FetchType.LAZY)
    private List<Document> documents;
    @OneToMany(mappedBy = "securityImpactWork", fetch = FetchType.LAZY)
    private List<CorrectiveAction> correctiveActions;
    @OneToMany(mappedBy = "securityImpactWork", fetch = FetchType.LAZY)
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

    public WorkType getWorkType() {
        return workType;
    }

    public void setWorkType(WorkType workType) {
        this.workType = workType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getDescription() { return description; }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() { return name; }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public String getQualifiedEntity() {
        return qualifiedEntity;
    }

    public void setQualifiedEntity(String qualifiedEntity) {
        this.qualifiedEntity = qualifiedEntity;
    }

    public SecurityImpact getSecurityImpact() {
        return securityImpact;
    }

    public void setSecurityImpact(SecurityImpact securityImpact) {
        this.securityImpact = securityImpact;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public List<Risk> getRisks() {
        return risks;
    }

    public void setRisks(List<Risk> risks) {
        this.risks = risks;
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
