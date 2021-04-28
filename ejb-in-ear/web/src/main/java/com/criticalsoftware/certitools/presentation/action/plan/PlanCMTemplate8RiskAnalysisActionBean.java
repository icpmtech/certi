/*
 * $Id: PlanCMTemplate8RiskAnalysisActionBean.java,v 1.3 2009/10/27 11:54:06 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/10/27 11:54:06 $
 * Last changed by : $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.presentation.action.plan;

import com.opencsv.CSVReader;
import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.business.exception.CertitoolsAuthorizationException;
import com.criticalsoftware.certitools.business.exception.JackrabbitException;
import com.criticalsoftware.certitools.business.exception.ObjectNotFoundException;
import com.criticalsoftware.certitools.business.plan.PlanService;
import com.criticalsoftware.certitools.entities.jcr.Folder;
import com.criticalsoftware.certitools.entities.jcr.RiskAnalysisElement;
import com.criticalsoftware.certitools.entities.jcr.Template;
import com.criticalsoftware.certitools.entities.jcr.Template8RiskAnalysis;
import com.criticalsoftware.certitools.presentation.util.PlanUtils;
import com.criticalsoftware.certitools.presentation.util.ValidationUtils;
import com.opencsv.exceptions.CsvException;
import com.samaxes.stripejb3.EJBBean;
import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.FileBean;
import net.sourceforge.stripes.action.ForwardResolution;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.validation.LocalizableError;
import net.sourceforge.stripes.validation.ValidationErrors;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Template 8 - Risk Analysis Action Bean
 *
 * @author jp-gomes
 */
public class PlanCMTemplate8RiskAnalysisActionBean extends PlanCMTemplateActionBean {

    private Template8RiskAnalysis template;
    private FileBean fileTemplate8RiskAnalysis;
    private Boolean insertFolderFlag;
    private String folderId;

    @EJBBean(value = "certitools/PlanService")
    private PlanService planService;

    @DefaultHandler
    public Resolution insertTemplate()
            throws BusinessException, ObjectNotFoundException, JackrabbitException, CertitoolsAuthorizationException, CsvException {
        setTemplateFile();
        return new ForwardResolution(PlanCMOperationsActionBean.class, "insertFolderPrepareTree");
    }

    public Resolution updateTemplate()
            throws BusinessException, ObjectNotFoundException, JackrabbitException, CertitoolsAuthorizationException, CsvException {
        setTemplateFile();
        return new ForwardResolution(PlanCMOperationsActionBean.class, "updateFolderPrepareTree");
    }

    public Resolution validateTemplate()
            throws BusinessException, ObjectNotFoundException, JackrabbitException, CertitoolsAuthorizationException, CsvException {
        Folder folder = planService.findFolder(folderId, false, getUserInSession(), getModuleTypeFromEnum());
        ValidationErrors errors = super.getValidationErrors();

        if (insertFolderFlag || !folder.getTemplate().getName()
                .equals(Template.Type.TEMPLATE_RISK_ANALYSIS.getName()) || fileTemplate8RiskAnalysis != null) {

            if (fileTemplate8RiskAnalysis != null) {
                if (!ValidationUtils
                        .validateCSVFileFormat(fileTemplate8RiskAnalysis.getContentType())) {
                    errors.addGlobalError(new LocalizableError("error.pei.template.8RiskAnalysis.contentType"));
                } else {
                    try {
                        String inputErrors = validateInput();
                        if (inputErrors == null) {
                            errors.addGlobalError(
                                    new LocalizableError("error.pei.template.8RiskAnalysis.invalidFile"));
                        } else if (!inputErrors.isEmpty()) {
                            errors.addGlobalError(
                                    new LocalizableError("error.pei.template.8RiskAnalysis.columnNumber",
                                            "\"" + inputErrors + "\""));
                        }
                    } catch (IOException e) {
                        throw new BusinessException("Error parsing file of Template8RiskAnalysis");
                    }
                }
            } else {
                errors.addGlobalError(new LocalizableError("error.pei.template.file.empty"));
            }
        }
        return new ForwardResolution(PlanCMOperationsActionBean.class, "insertFolder");
    }

    private void setTemplateFile()
            throws BusinessException, ObjectNotFoundException, JackrabbitException, CertitoolsAuthorizationException, CsvException {
        Folder folder = planService.findFolder(folderId, false, getUserInSession(), getModuleTypeFromEnum());

        if (insertFolderFlag || !folder.getTemplate().getName()
                .equals(Template.Type.TEMPLATE_RISK_ANALYSIS.getName()) || fileTemplate8RiskAnalysis != null) {
            try {
                parseFileAndBuidTemplate(template, PlanUtils.simplifyPath(folderId));
            } catch (IOException e) {
                throw new BusinessException("Error parsing file of Template8RiskAnalysis");
            }
        } else {
            Template8RiskAnalysis template8TemplateRiskAnalysis = (Template8RiskAnalysis) folder.getTemplate();
            template.setRiskAnalysis(template8TemplateRiskAnalysis.getRiskAnalysis());
        }

        super.setTemplateToFolder(template);
    }

    private void parseFileAndBuidTemplate(Template8RiskAnalysis template, String path) throws IOException, CsvException {

        List<RiskAnalysisElement> riskAnalysisList = new ArrayList<RiskAnalysisElement>();

        //old fix
        // CSVReader reader =
        //                new CSVReader(new InputStreamReader(fileTemplate8RiskAnalysis.getInputStream(), "ISO-8859-1"), ';');
        // new Fix
        CSVReader reader =
                new CSVReader(new InputStreamReader(fileTemplate8RiskAnalysis.getInputStream(), "ISO-8859-1"));

        List allEntries = reader.readAll();

        for (Object o : allEntries) {
            String[] line = (String[]) o;

            // if empty line just skip it
            if (line == null || StringUtils.isEmpty(line[0])) {
                continue;
            }

            RiskAnalysisElement riskAnalysisElement = new RiskAnalysisElement();
            riskAnalysisElement.setProduct(line[0].trim());
            riskAnalysisElement.setReleaseConditions(line[1].trim());
            riskAnalysisElement.setWeather(line[2].trim());
            riskAnalysisElement.setIgnitionPoint(line[3].trim());
            riskAnalysisElement.setRadiation(line[4].trim());
            riskAnalysisElement.setPressurized(line[5].trim());
            riskAnalysisElement.setToxicity(line[6].trim());
            riskAnalysisElement.setName(allEntries.indexOf(o) + 1 + "");

            if (line.length >= 8) {
                String folderLinks = "";
                for (int i = 7; i <= line.length - 1; i++) {
                    if (!StringUtils.isBlank(line[i])) {
                        folderLinks += "/" + line[i];
                        if (i != line.length - 1) {
                            folderLinks += ";";
                        }
                    }
                }
                riskAnalysisElement.setFileFolderLinks(folderLinks);
            } else {
                riskAnalysisElement.setFileFolderLinks("");
            }
            riskAnalysisList.add(riskAnalysisElement);
        }
        template.setRiskAnalysis(riskAnalysisList);
    }

    private String validateInput() throws IOException, CsvException {
        //old fix
        //CSVReader reader = new CSVReader(new InputStreamReader(fileTemplate8RiskAnalysis.getInputStream()), ';');
        //new fix
        CSVReader reader = new CSVReader(new InputStreamReader(fileTemplate8RiskAnalysis.getInputStream()));
        List allEntries = reader.readAll();
        StringBuilder sb = new StringBuilder();
        boolean existErrors = false;

        if (allEntries == null || allEntries.size() == 0) {
            return null;
        }
        for (Object lineObject : allEntries) {
            String[] line = (String[]) lineObject;

            // if empty line just skip it
            if (line == null || StringUtils.isEmpty(line[0])) {
                continue;
            }

            if (line.length < 7) {
                existErrors = true;
                sb.append(allEntries.indexOf(lineObject) + 1);
                sb.append(", ");
            }
        }
        if (existErrors) {
            return sb.substring(0, sb.length() - 2);
        }
        return sb.toString();
    }

    public Template8RiskAnalysis getTemplate() {
        return template;
    }

    public void setTemplate(Template8RiskAnalysis template) {
        this.template = template;
    }

    public FileBean getFileTemplate8RiskAnalysis() {
        return fileTemplate8RiskAnalysis;
    }

    public void setFileTemplate8RiskAnalysis(FileBean fileTemplate8RiskAnalysis) {
        this.fileTemplate8RiskAnalysis = fileTemplate8RiskAnalysis;
    }

    public Boolean getInsertFolderFlag() {
        return insertFolderFlag;
    }

    public void setInsertFolderFlag(Boolean insertFolderFlag) {
        this.insertFolderFlag = insertFolderFlag;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }

    public PlanService getPeiService() {
        return planService;
    }

    public void setPeiService(PlanService planService) {
        this.planService = planService;
    }
}
