package com.criticalsoftware.certitools.entities.jcr;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

/**
 * Means/Resources index
 *
 * @author miseabra
 * @version $Revision: $
 */
@Node(extend = Template.class)
public class Template12MeansResources extends Template {

    public Template12MeansResources() {
        super(Type.TEMPLATE_MEANS_RESOURCES.getName());
    }
}
