/*
 * $Id: PlanExportAnnex.java,v 1.6 2010/07/12 12:09:12 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2010/07/12 12:09:12 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.util.export;

import com.aspose.words.*;
import com.criticalsoftware.certitools.util.Logger;

import java.util.ArrayList;

/**
 * Description.
 *
 * @author :    pjfsilva
 * @version :   $Revision: 1.6 $
 */
public class PlanExportAnnex {
    private static final Logger LOGGER = Logger.getInstance(PlanExportAnnex.class);
    private Document templateDoc;
    private String title = null;
    private boolean contentWritten = false;
    private ArrayList<String> sectionOptions;

    public PlanExportAnnex(String title, Document sourceDoc, int sectionIndex) throws Exception {
        // get sectionOptions
        String[] titleSplitted = title.split(":");
        if (titleSplitted.length > 1) {
            sectionOptions = new ArrayList<String>();
            for (int i = 1; i < titleSplitted.length; i++) {
                sectionOptions.add(titleSplitted[i].toLowerCase().trim());
            }
        }
        // title
        this.title = titleSplitted[0];
        
        try {
            // copy section to new doc
            this.templateDoc = sourceDoc.deepClone();
            SectionCollection sections = this.templateDoc.getSections();
            for (int i = sectionIndex + 1; i < sections.getCount(); ) {
                sections.removeAt(i);
            }
            while (sections.getCount() > 1) {
                sections.removeAt(0);
            }

            // remove first empty paragraph
            DocumentBuilder db = new DocumentBuilder(templateDoc);
            db.moveToDocumentStart();
            Paragraph currentParagraph = db.getCurrentParagraph();
            if (currentParagraph.getText().trim().isEmpty()) {
                currentParagraph.remove();
            }

        } catch (Exception e) {
            LOGGER.error(
                    "[PlanExportAnnex] Error initializing object. title:" + title + " section index:" + sectionIndex);
            LOGGER.error(e);
            throw e;
        }
    }

    public Document getTemplateDoc() {
        return templateDoc;
    }

    public void setTemplateDoc(Document templateDoc) {
        this.templateDoc = templateDoc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isContentWritten() {
        return contentWritten;
    }

    public void setContentWritten(boolean contentWritten) {
        this.contentWritten = contentWritten;
    }

    public ArrayList<String> getSectionOptions() {
        return sectionOptions;
    }

    public void setSectionOptions(ArrayList<String> sectionOptions) {
        this.sectionOptions = sectionOptions;
    }
}