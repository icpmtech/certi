/*
 * $Id: HierarchyNode.java,v 1.5 2009/05/13 14:02:26 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/05/13 14:02:26 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.entities.jcr;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

/**
 * Hierachy Node
 *
 * @author : lt-rico
 */

@Node(isAbstract=true)
public class HierarchyNode {

    @Field(path = true)
    protected String path;

    @Field(jcrMandatory = true, id = true)
    protected String name;

    public HierarchyNode() {
    }

    public HierarchyNode(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
