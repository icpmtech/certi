package com.criticalsoftware.certitools.entities.jcr;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

/**
 * One individual mean/resource
 *
 * @author miseabra
 * @version $Revision: $
 */
@SuppressWarnings("UnusedDeclaration")
@Node(extend = Template.class)
public class Template12MeansResourcesElement extends Template {

    @Field(jcrMandatory = true)
    private String resourceType;
    @Field(jcrMandatory = true)
    private String resourceName;
    @Field
    private String entityName;
    @Field
    private String characteristics;
    @Field
    private String quantity;

    public Template12MeansResourcesElement() {
        super(Type.TEMPLATE_MEANS_RESOURCES_ELEMENT.getName());
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(String characteristics) {
        this.characteristics = characteristics;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
