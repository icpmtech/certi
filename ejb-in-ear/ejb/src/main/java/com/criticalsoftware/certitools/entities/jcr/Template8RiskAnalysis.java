/*
 * $Id: Template8RiskAnalysis.java,v 1.2 2009/06/03 18:28:02 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/06/03 18:28:02 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.entities.jcr;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

import java.util.List;


/**
 * Template 8 - Risk Analysis
 *
 * @author jp-gomes
 */
@Node(extend = Template.class)
public class Template8RiskAnalysis extends Template {

    @Collection
    private List<RiskAnalysisElement> riskAnalysis;

    public Template8RiskAnalysis() {
        super(Type.TEMPLATE_RISK_ANALYSIS.getName());
    }

    public List<RiskAnalysisElement> getRiskAnalysis() {
        return riskAnalysis;
    }

    public void setRiskAnalysis(List<RiskAnalysisElement> riskAnalysis) {
        this.riskAnalysis = riskAnalysis;
    }


}
