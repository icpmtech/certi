/*
 * $Id: Template8RiskAnalysisDataSource.java,v 1.2 2010/07/27 18:43:58 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2010/07/27 18:43:58 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.util.export.mailmergedatasource;

import com.aspose.words.IMailMergeDataSource;
import com.aspose.words.ref.Ref;
import com.criticalsoftware.certitools.entities.jcr.RiskAnalysisElement;
import com.criticalsoftware.certitools.util.Logger;

import java.util.List;

/**
 * Aspose data source for RiskAnalysys template
 *
 * @author :    pjfsilva
 * @version :   $Revision: 1.2 $
 */
public class Template8RiskAnalysisDataSource implements IMailMergeDataSource{
    private String tableName;
    private List<RiskAnalysisElement> riskAnalysis;
    private int i = -1;
    private static final Logger LOGGER = Logger.getInstance(Template8RiskAnalysisDataSource.class);

    public Template8RiskAnalysisDataSource(String tableName, List<RiskAnalysisElement> riskAnalysis) {
        this.tableName = tableName;
        this.riskAnalysis = riskAnalysis;
    }

    public String getTableName() throws Exception {
        return tableName;
    }

    public boolean moveNext() throws Exception {
        i++;
        if (i > 0) {
            if (i >= riskAnalysis.size()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean getValue(String s, Ref<Object> ref) throws Exception {
        return false;
    }

    @Override
    public IMailMergeDataSource getChildDataSource(String s) throws Exception {
        return null;
    }

    public boolean getValue(String fieldName, Object[] fieldValue) throws Exception {
        fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        RiskAnalysisElement riskElement = riskAnalysis.get(i);

        try {
            fieldValue[0] = RiskAnalysisElement.class.getDeclaredMethod("get" + fieldName).invoke(riskElement);
            return true;
        } catch (Exception e) {
            LOGGER.error(e);
        }

        fieldValue[0] = null;
        return false;
    }
}