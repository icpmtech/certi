package com.criticalsoftware.certitools.presentation.action.plan;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.JackrabbitException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.plan.PlanService;
import com.criticalsoftware.certitools.entities.jcr.Template12MeansResourcesElement;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.ValidationErrors;

import java.io.IOException;

/**
 * Template12 Mean/Resource Element Action Bean
 *
 * @author miseabra
 * @version $Revision: $
 */
public class PlanCMTemplate12MeansResourcesElementActionBean extends PlanCMTemplateActionBean {

    private Template12MeansResourcesElement template;
    private Boolean insertFolderFlag;

    private String folderId;

    @EJBBean(value = "certitools/PlanService")
    private PlanService planService;

    @DefaultHandler
    public Resolution insertTemplate()
            throws IOException, BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException,
            JackrabbitException {
        setTemplateToFolder(template);
        return new ForwardResolution(PlanCMOperationsActionBean.class, "insertFolderPrepareTree");
    }

    public Resolution updateTemplate()
            throws IOException, BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException,
            JackrabbitException {
        setTemplateToFolder(template);
        return new ForwardResolution(PlanCMOperationsActionBean.class, "updateFolderPrepareTree");
    }

    public Resolution validateTemplate()
            throws IOException, BusinessException, ObjectNotFoundException, CertitoolsAuthorizationException,
            JackrabbitException {
        ValidationErrors errors = super.getValidationErrors();

        if (template == null) {
            errors.add("template.resourceName", new LocalizableError("error.pei.template.12MeansResourcesElement.resourceName"));
            errors.add("template.resourceType", new LocalizableError("error.pei.template.12MeansResourcesElement.resourceType"));
        } else {
            if (template.getResourceName() == null) {
                errors.add("template.resourceName", new LocalizableError("error.pei.template.12MeansResourcesElement.resourceName"));
            }
            if (template.getResourceType() == null) {
                errors.add("template.resourceType", new LocalizableError("error.pei.template.12MeansResourcesElement.resourceType"));
            }
        }

        super.setTemplateToFolder(template);
        return new ForwardResolution(PlanCMOperationsActionBean.class, "insertFolder");
    }

    public Template12MeansResourcesElement getTemplate() {
        return template;
    }

    public void setTemplate(Template12MeansResourcesElement template) {
        this.template = template;
    }

    public Boolean getInsertFolderFlag() {
        return insertFolderFlag;
    }

    public void setInsertFolderFlag(Boolean insertFolderFlag) {
        this.insertFolderFlag = insertFolderFlag;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public PlanService getPlanService() {
        return planService;
    }

    public void setPlanService(PlanService planService) {
        this.planService = planService;
    }
}
