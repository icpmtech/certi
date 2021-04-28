/*
 * $Id: Resource.java,v 1.11 2009/10/20 21:02:29 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/10/20 21:02:29 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.entities.jcr;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

import java.io.InputStream;

/**
 * Resource object
 *
 * @author : lt-rico
 */
@Node(extend = HierarchyNode.class)
public class Resource extends HierarchyNode implements Comparable<Resource> {

    @Field
    private String mimeType;
    @Field
    private InputStream data;
    @Field
    private String size;
    @Field
    private Boolean photo; // true if it is a photo, false if other content type
    @Field
    private String alias;

    public Resource() {
    }

    public Resource(InputStream data){
        this.data = data;        
    }

    public Resource(String path, String name, String mimeType, InputStream data, String size) {
        super(path, name);

        this.mimeType = mimeType;
        this.data = data;
        this.setSize(size);

        photo = mimeType.startsWith("image");
    }

    public Resource(String path, String name, String mimeType, InputStream data, String size, Boolean photo) {
        super(path, name);
        this.mimeType = mimeType;
        this.data = data;
        this.size = size;
        this.photo = photo;
    }

    public Resource(String path, String name, String mimeType, InputStream data, String size, Boolean photo,
                    String alias) {
        super(path, name);
        this.mimeType = mimeType;
        this.data = data;
        this.size = size;
        this.photo = photo;
        this.alias = alias;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public InputStream getData() {
        return data;
    }

    public void setData(InputStream data) {
        this.data = data;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Boolean getPhoto() {
        return photo;
    }

    public void setPhoto(Boolean photo) {
        this.photo = photo;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override public boolean equals(Object obj) {
        return super.equals(obj);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public int compareTo(Resource o) {
        return this.path.compareTo(o.getPath());
    }
}
