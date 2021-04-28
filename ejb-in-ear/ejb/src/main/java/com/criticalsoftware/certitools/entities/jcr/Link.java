/*
 * $Id: Link.java,v 1.2 2009/10/16 10:27:27 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2009/10/16 10:27:27 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.entities.jcr;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;

/**
 * Link Class
 *
 * @author jp-gomes
 */
@Node(extend = HierarchyNode.class)
public class Link extends HierarchyNode {
    @Field
    private String href;
    @Field
    private String alias;

    public Link() {
    }

    public Link(String path, String name, String href, String alias) {
        super(path, name);
        this.href = href;
        this.alias = alias;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}

