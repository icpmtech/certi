/*
 * $Id: Template11Mirror.java,v 1.4 2009/10/19 15:52:05 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/10/19 15:52:05 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.entities.jcr;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import com.criticalsoftware.certitools.util.ModuleType;
import com.criticalsoftware.certitools.util.PlanUtils;
import com.criticalsoftware.certitools.business.exception.BusinessException;

/**
 * <description>
 *
 * @author jp-gomes
 */
@Node(extend = Template.class)
public class Template11Mirror extends Template {

    @Field(jcrMandatory = true)
    private String sourcePath;

    @Field(jcrMandatory = true)
    private Long sourceContractId;

    @Field(jcrMandatory = true)
    private String sourceTemplateName;

    private String parentPath;

    private ModuleType moduleType;

    public Template11Mirror() {
        super(Type.TEMPLATE_MIRROR.getName());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Template11Mirror that = (Template11Mirror) o;
        return sourceContractId.equals(that.sourceContractId) && sourcePath.equals(that.sourcePath);
    }

    public int hashCode() {
        int result;
        result = sourcePath.hashCode();
        result = 31 * result + sourceContractId.hashCode();
        return result;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public Long getSourceContractId() {
        return sourceContractId;
    }

    public void setSourceContractId(Long sourceContractId) {
        this.sourceContractId = sourceContractId;
    }

    public ModuleType getModuleType() throws BusinessException {
        if (moduleType == null && sourcePath != null) {
            return PlanUtils.getModuleTypeFromPath(sourcePath);
        }
        return moduleType;
    }

    public void setModuleType(ModuleType moduleType) {
        this.moduleType = moduleType;
    }

    public String getSourceTemplateName() {
        return sourceTemplateName;
    }

    public void setSourceTemplateName(String sourceTemplateName) {
        this.sourceTemplateName = sourceTemplateName;
    }
}

