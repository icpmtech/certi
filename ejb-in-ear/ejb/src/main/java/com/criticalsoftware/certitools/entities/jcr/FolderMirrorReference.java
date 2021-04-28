/*
 * $Id: FolderMirrorReference.java,v 1.8 2009/11/02 20:08:14 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/11/02 20:08:14 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.entities.jcr;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.util.ModuleType;
import com.criticalsoftware.certitools.util.PlanUtils;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

/**
 * Folder Mirror Reference
 *
 * @author jp-gomes
 */
@Node(extend = HierarchyNode.class)
public class FolderMirrorReference extends HierarchyNode {

    @Field(jcrMandatory = true)
    private String referenceContractDesignation;

    @Field(jcrMandatory = true)
    private String referenceCompanyName;

    @Field(jcrMandatory = true)
    private String referencePath;

    private String pathToShow;

    private Long referenceContractId;

    private ModuleType moduleType;

    public FolderMirrorReference() {
    }

    public FolderMirrorReference(String referencePath) {
        this.referencePath = referencePath;
    }

    public FolderMirrorReference(long index, String contractDesignation, String companyName,
                                 String referencePath) {
        super("/folderMirrorReference" + index, "folderMirrorReference" + index);
        this.referencePath = referencePath;
        this.referenceContractDesignation = contractDesignation;
        this.referenceCompanyName = companyName;
    }

    public String getReferenceContractDesignation() {
        return referenceContractDesignation;
    }

    public void setReferenceContractDesignation(String referenceContractDesignation) {
        this.referenceContractDesignation = referenceContractDesignation;
    }

    public String getReferenceCompanyName() {
        return referenceCompanyName;
    }

    public void setReferenceCompanyName(String referenceCompanyName) {
        this.referenceCompanyName = referenceCompanyName;
    }

    public String getReferencePath() {
        return referencePath;
    }

    public void setReferencePath(String referencePath) {
        this.referencePath = referencePath;
    }

    public ModuleType getModuleType() throws BusinessException {
        if (referencePath != null) {
            return PlanUtils.getModuleTypeFromPath(referencePath);
        }
        return moduleType;
    }

    public void setModuleType(ModuleType moduleType) {
        this.moduleType = moduleType;
    }

    public String getPathToShow() {
        if (pathToShow == null) {
            return PlanUtils.buildFolderMirrorLink(referenceContractDesignation, referenceCompanyName, referencePath);
        }
        return pathToShow;
    }

    public void setPathToShow(String pathToShow) {
        this.pathToShow = pathToShow;
    }

    public Long getReferenceContractId() throws BusinessException {
        if (referenceContractId == null) {
            return PlanUtils.getContractNumberByPath(referencePath);
        }
        return referenceContractId;
    }

    public void setReferenceContractId(Long referenceContractId) {
        this.referenceContractId = referenceContractId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FolderMirrorReference that = (FolderMirrorReference) o;
        return referencePath.equals(that.referencePath);
    }

    public int hashCode() {
        int result;
        result = referenceContractDesignation.hashCode();
        result = 31 * result + referenceCompanyName.hashCode();
        result = 31 * result + referencePath.hashCode();
        return result;
    }
}
