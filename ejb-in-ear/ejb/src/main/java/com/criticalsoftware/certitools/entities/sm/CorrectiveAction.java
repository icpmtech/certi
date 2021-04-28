package com.criticalsoftware.certitools.entities.sm;

import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.util.Configuration;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Corrective Action Entity
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
@Entity
@Table(name = "sm_correctiveaction")
public class CorrectiveAction {

    @Id
    @SequenceGenerator(name = "CORRECTIVE_ACTION_ID_GENERATOR", sequenceName = "sm_correctiveaction_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CORRECTIVE_ACTION_ID_GENERATOR")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Contract contract;
    private String code;
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @Lob
    private String description;
    private String name;
    private String duration;
    private String executionResponsible;
    @Lob
    private String notes;
    @ManyToOne(fetch = FetchType.LAZY)
    private Activity activity;
    @ManyToOne(fetch = FetchType.LAZY)
    private Anomaly anomaly;
    @ManyToOne(fetch = FetchType.LAZY)
    private SecurityImpactWork securityImpactWork;
    @ManyToOne(fetch = FetchType.LAZY)
    private Maintenance maintenance;
    @OneToMany(mappedBy = "correctiveAction", fetch = FetchType.LAZY)
    private List<Document> documents;
    @OneToMany(mappedBy = "correctiveAction", fetch = FetchType.LAZY)
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public String getStartDateFormatted() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Configuration.getInstance().getDatePattern());
        return dateFormat.format(this.startDate);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getExecutionResponsible() {
        return executionResponsible;
    }

    public void setExecutionResponsible(String executionResponsible) {
        this.executionResponsible = executionResponsible;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
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
