package com.criticalsoftware.certitools.presentation.action.plan;

import com.criticalsoftware.certitools.entities.jcr.Template12MeansResources;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;

/**
 * Template12 Means/Resources Action Bean
 *
 * @author miseabra
 * @version $Revision: $
 */
@SuppressWarnings("UnusedDeclaration")
public class PlanCMTemplate12MeansResourcesActionBean extends PlanCMTemplateActionBean {

    private Template12MeansResources template;

    @DefaultHandler
    public Resolution insertTemplate() {
        setTemplateToFolder(new Template12MeansResources());
        return new ForwardResolution(PlanCMOperationsActionBean.class, "insertFolderPrepareTree");
    }

    public Resolution updateTemplate() {
        setTemplateToFolder(new Template12MeansResources());
        return new ForwardResolution(PlanCMOperationsActionBean.class, "updateFolderPrepareTree");
    }

    public Template12MeansResources getTemplate() {
        return template;
    }

    public void setTemplate(Template12MeansResources template) {
        this.template = template;
    }
}
