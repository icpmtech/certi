/*
 * $Id: RiskAnalysisElement.java,v 1.4 2009/10/27 11:55:41 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/10/27 11:55:41 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.entities.jcr;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

import java.util.List;

/**
 * <description>
 *
 * @author jp-gomes
 */
@Node(extend = HierarchyNode.class)
public class RiskAnalysisElement extends HierarchyNode implements Comparable<RiskAnalysisElement> {
    @Field
    private String product;

    @Field
    private String releaseConditions;

    @Field
    private String weather;

    @Field
    private String ignitionPoint;

    @Field
    private String radiation;

    @Field
    private String pressurized;

    @Field
    private String toxicity;

    @Field
    private String fileFolderLinks;

    private List<String> fileFolderLinksLists;

    public RiskAnalysisElement() {

    }

    public List<String> getFileFolderLinksLists() {
        return fileFolderLinksLists;
    }

    public void setFileFolderLinksLists(List<String> fileFolderLinksLists) {
        this.fileFolderLinksLists = fileFolderLinksLists;
    }

    public String getFileFolderLinks() {
        return fileFolderLinks;
    }

    public void setFileFolderLinks(String fileFolderLinks) {
        this.fileFolderLinks = fileFolderLinks;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getReleaseConditions() {
        return releaseConditions;
    }

    public void setReleaseConditions(String releaseConditions) {
        this.releaseConditions = releaseConditions;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getIgnitionPoint() {
        return ignitionPoint;
    }

    public void setIgnitionPoint(String ignitionPoint) {
        this.ignitionPoint = ignitionPoint;
    }

    public String getRadiation() {
        return radiation;
    }

    public void setRadiation(String radiation) {
        this.radiation = radiation;
    }

    public String getPressurized() {
        return pressurized;
    }

    public void setPressurized(String pressurized) {
        this.pressurized = pressurized;
    }

    public String getToxicity() {
        return toxicity;
    }

    public void setToxicity(String toxicity) {
        this.toxicity = toxicity;
    }

    public int compareTo(RiskAnalysisElement o) {
        if (product.compareTo(o.getProduct()) == 0) {
            if (releaseConditions.compareTo(o.getReleaseConditions()) == 0) {
                return weather.compareTo(o.getWeather());
            }
            return releaseConditions.compareTo(o.getReleaseConditions());
        }
        return product.compareTo(o.getProduct());
    }
}
