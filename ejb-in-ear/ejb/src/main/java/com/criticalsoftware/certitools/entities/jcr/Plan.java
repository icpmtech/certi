/*
 * $Id: Plan.java,v 1.7 2012/06/14 00:18:31 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2012/06/14 00:18:31 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.entities.jcr;

import com.criticalsoftware.certitools.util.ModuleType;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Bean;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * PEI folder object
 *
 * @author : lt-rico
 */
@Node(extend = HierarchyNode.class)
public class Plan extends HierarchyNode implements Comparable<Plan> {

    @Field
    private String planName;

    @Field
    private String planNameOnline;

    @Field
    private String authorName;

    @Field
    private String authorNameOnline;

    @Field
    private String version;

    @Field
    private String versionOnline;

    @Field
    private Date versionDate;

    @Field
    private Date versionDateOnline;

    @Field
    private Date simulationDate;

    @Field
    private Date simulationDateOnline;

    @Field
    private Date publishedDate;

    @Field
    private String publishedAuthor;

    @Field
    private Date lastSaveDate;

    @Field
    private Date lastParcialPublished;

    @Field
    private String moduleType;

    @Bean
    private Resource installationPhoto;

    @Bean
    private Resource installationPhotoOnline;

    @Bean
    private Resource companyLogo;

    @Bean
    private Resource companyLogoOnline;

    @Collection(proxy = true, autoUpdate = false)
    private List<Folder> online;

    @Collection(proxy = true, autoUpdate = false)
    private List<Folder> offline;

    private boolean userCanAccess = true;

    public Plan() {
    }

    public Plan(Long contractId, String planName, String authorName,
                Date versionDate, Date simulationDate, Resource installationPhoto, ModuleType moduleType,
                String moduleName) {
        super("/" + moduleType + contractId, "" + contractId);

        this.planName = planName;
        this.authorName = authorName;
        this.versionDate = versionDate;
        this.simulationDate = simulationDate;
        this.installationPhoto = installationPhoto;
        this.online = new ArrayList<Folder>();
        this.moduleType = moduleType.toString();

        this.offline = getEmptyOfflineFolder(moduleType, moduleName);
    }


    public List<Folder> getEmptyOfflineFolder(ModuleType moduleType, String moduleName) {
        List<Folder> folders = new ArrayList<Folder>();

        //TODO-MODULE
        if (moduleType.equals(ModuleType.PRV)) {
            folders.add(
                    new Folder("/Organograma Funcional", "Organograma Funcional", null, true, 1, new Template2Index(),
                            null, null));
            folders.add(new Folder(
                    "/Procedimentos" , "Procedimentos", null, true, 2, new Template2Index(), null, null));
            folders.add(new Folder(
                    "/Registos de Seguran\u00E7a", "Registos de Seguran\u00E7a", null, true, 3, new Template2Index(), null,
                    null));
            folders.add(new Folder("/Plantas", "Plantas", null, true, 4, new Template2Index(), null, null));
            folders.add(new Folder("/Documenta\u00E7\u00E3o", "Documenta\u00E7\u00E3o", null, true, 5, new Template2Index(), null, null));
            folders.add(new Folder("/FAQ", "FAQ", null, true, 6, new Template2Index(), null, null));
            folders.add(new Folder("/Contactos", "Contactos", null, true, 7, new Template2Index(), null, null));
            folders.add(new Folder(
                    "/Sobre a Instala\u00E7\u00E3o", "Sobre a Instala\u00E7\u00E3o", null, true, 8, new Template2Index(), null, null));
            folders.add(new Folder("/Extra", "Extra", null, true, 9, new Template2Index(), null, null));
        } else if (moduleType.equals(ModuleType.PEI)) {
            folders.add(new Folder("/Organograma", "Organograma", null, true, 1, new Template2Index(), null, null));
            folders.add(new Folder(
                    "/Alarme e Alerta", "Alarme e Alerta", null, true, 2, new Template2Index(), null, null));
            folders.add(new Folder(
                    "/Plano de Emerg\u00EAncia", "Plano de Emerg\u00EAncia", null, true, 3, new Template2Index(), null, null));
            folders.add(new Folder(
                    "/Procedimentos", "Procedimentos", null, true, 4, new Template2Index(), null, null));
            folders.add(new Folder("/Comunica\u00E7\u00F5es", "Comunica\u00E7\u00F5es", null, true, 5, new Template2Index(), null, null));
            folders.add(new Folder(
                    "/Sobre a Instala\u00E7\u00E3o", "Sobre a Instala\u00E7\u00E3o", null, true, 6, new Template2Index(), null, null));
            folders.add(new Folder("/Documenta\u00E7\u00E3o", "Documenta\u00E7\u00E3o", null, true, 7, new Template2Index(), null, null));
            folders.add(new Folder("/FAQ", "FAQ", null, true, 8, new Template2Index(), null, null));
            folders.add(new Folder("/Extra", "Extra", null, true, 9, new Template2Index(), null, null));
        } else if (moduleType.equals(ModuleType.PSI)) {
            folders.add(new Folder("/Organiza\u00E7\u00E3o em Emerg\u00EAncia", "Organiza\u00E7\u00E3o em Emerg\u00EAncia", null, true, 1, new Template2Index(), null, null));
            folders.add(new Folder(
                    "/Planos de Atua\u00E7\u00E3o", "Planos de Atua\u00E7\u00E3o", null, true, 2, new Template2Index(), null, null));
            folders.add(new Folder(
                    "/Plano de Evacua\u00E7\u00E3o", "Plano de Evacua\u00E7\u00E3o", null, true, 3, new Template2Index(), null, null));
            folders.add(new Folder(
                    "/Instru\u00E7\u00F5es e Procedimentos", "Instru\u00E7\u00F5es e Procedimentos", null, true, 4, new Template2Index(), null, null));
            folders.add(new Folder("/Comunica\u00E7\u00F5es e Contactos", "Comunica\u00E7\u00F5es e Contactos", null, true, 5, new Template2Index(), null, null));
            folders.add(new Folder(
                    "/Sobre a Instala\u00E7\u00E3o", "Sobre a Instala\u00E7\u00E3o", null, true, 6, new Template2Index(), null, null));
            folders.add(new Folder("/Registos de Seguran\u00E7a", "Registos de Seguran\u00E7a", null, true, 7, new Template2Index(), null, null));
            folders.add(new Folder("/Documenta\u00E7\u00E3o", "Documenta\u00E7\u00E3o", null, true, 8, new Template2Index(), null, null));
            folders.add(new Folder("/FAQ", "FAQ", null, true, 9, new Template2Index(), null, null));
        }
        return folders;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public Date getVersionDate() {
        return versionDate;
    }

    public void setVersionDate(Date versionDate) {
        this.versionDate = versionDate;
    }

    public Resource getInstallationPhoto() {
        return installationPhoto;
    }

    public void setInstallationPhoto(Resource installationPhoto) {
        this.installationPhoto = installationPhoto;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Date getSimulationDate() {
        return simulationDate;
    }

    public void setSimulationDate(Date simulationDate) {
        this.simulationDate = simulationDate;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Folder> getOnline() {
        return online;
    }

    public void setOnline(List<Folder> online) {
        this.online = online;
    }

    public List<Folder> getOffline() {
        return offline;
    }

    public void setOffline(List<Folder> offline) {
        this.offline = offline;
    }

    public Resource getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(Resource companyLogo) {
        this.companyLogo = companyLogo;
    }

    public String getPlanNameOnline() {
        return planNameOnline;
    }

    public void setPlanNameOnline(String planNameOnline) {
        this.planNameOnline = planNameOnline;
    }

    public String getAuthorNameOnline() {
        return authorNameOnline;
    }

    public void setAuthorNameOnline(String authorNameOnline) {
        this.authorNameOnline = authorNameOnline;
    }

    public String getVersionOnline() {
        return versionOnline;
    }

    public void setVersionOnline(String versionOnline) {
        this.versionOnline = versionOnline;
    }

    public Date getVersionDateOnline() {
        return versionDateOnline;
    }

    public void setVersionDateOnline(Date versionDateOnline) {
        this.versionDateOnline = versionDateOnline;
    }

    public Date getSimulationDateOnline() {
        return simulationDateOnline;
    }

    public void setSimulationDateOnline(Date simulationDateOnline) {
        this.simulationDateOnline = simulationDateOnline;
    }

    public Resource getInstallationPhotoOnline() {
        return installationPhotoOnline;
    }

    public void setInstallationPhotoOnline(Resource installationPhotoOnline) {
        this.installationPhotoOnline = installationPhotoOnline;
    }

    public Resource getCompanyLogoOnline() {
        return companyLogoOnline;
    }

    public void setCompanyLogoOnline(Resource companyLogoOnline) {
        this.companyLogoOnline = companyLogoOnline;
    }

    public int compareTo(Plan o) {
        return this.getPlanName().compareTo(o.getPlanName());
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getPublishedAuthor() {
        return publishedAuthor;
    }

    public void setPublishedAuthor(String publishedAuthor) {
        this.publishedAuthor = publishedAuthor;
    }

    public boolean isUserCanAccess() {
        return userCanAccess;
    }

    public void setUserCanAccess(boolean userCanAccess) {
        this.userCanAccess = userCanAccess;
    }

    public Date getLastSaveDate() {
        return lastSaveDate;
    }

    public void setLastSaveDate(Date lastSaveDate) {
        this.lastSaveDate = lastSaveDate;
    }

    public Date getLastParcialPublished() {
        return lastParcialPublished;
    }

    public void setLastParcialPublished(Date lastParcialPublished) {
        this.lastParcialPublished = lastParcialPublished;
    }

    public String getModuleType() {
        return moduleType;
    }

    public void setModuleType(String moduleType) {
        this.moduleType = moduleType;
    }
}
