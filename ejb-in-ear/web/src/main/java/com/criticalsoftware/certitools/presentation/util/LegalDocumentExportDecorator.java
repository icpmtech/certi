/*
 * $Id: LegalDocumentExportDecorator.java,v 1.3 2010/01/12 17:55:25 jp-gomes Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2010/01/12 17:55:25 $
 * Last changed by $Author: jp-gomes $
 */
package com.criticalsoftware.certitools.presentation.util;

import com.criticalsoftware.certitools.entities.LegalDocument;
import org.displaytag.decorator.TableDecorator;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.Locale;

import net.sourceforge.stripes.localization.LocalizationUtility;

/**
 * Deocrator for Legislation Export
 *
 * @author jp-gomes
 */
public class LegalDocumentExportDecorator extends TableDecorator {

    public String getFirstCategory() {
        if (this.getCurrentRowObject() instanceof LegalDocument) {
            LegalDocument legaDocument = (LegalDocument) this.getCurrentRowObject();
            if (legaDocument.getLegalDocumentCategories() != null
                    && legaDocument.getLegalDocumentCategories().size() >= 1) {
                return legaDocument.getLegalDocumentCategories().get(0).getName();
            } else {
                return "-";
            }
        }
        return "-";
    }

    public String getSecondCategory() {
        if (this.getCurrentRowObject() instanceof LegalDocument) {
            LegalDocument legaDocument = (LegalDocument) this.getCurrentRowObject();
            if (legaDocument.getLegalDocumentCategories() != null
                    && legaDocument.getLegalDocumentCategories().size() >= 2) {
                return legaDocument.getLegalDocumentCategories().get(1).getName();
            } else {
                return "-";
            }
        }
        return null;
    }

    public String getThirdCategory() {
        if (this.getCurrentRowObject() instanceof LegalDocument) {
            LegalDocument legaDocument = (LegalDocument) this.getCurrentRowObject();
            if (legaDocument.getLegalDocumentCategories() != null
                    && legaDocument.getLegalDocumentCategories().size() >= 3) {
                return legaDocument.getLegalDocumentCategories().get(2).getName();
            } else {
                return "-";
            }
        }
        return null;
    }

    public String getPublicationDate() {
        if (this.getCurrentRowObject() instanceof LegalDocument) {
            LegalDocument legaDocument = (LegalDocument) this.getCurrentRowObject();
            return legaDocument.getPublicationDate().toString();
        }
        return "-";
    }

    public String getKeywords() {
        if (this.getCurrentRowObject() instanceof LegalDocument) {
            LegalDocument legaDocument = (LegalDocument) this.getCurrentRowObject();
            if (legaDocument.getKeywords() != null && !legaDocument.getKeywords().isEmpty()) {
                return legaDocument.getKeywords();
            }
        }
        return "-";
    }

    public String getSummary() {
        if (this.getCurrentRowObject() instanceof LegalDocument) {
            LegalDocument legaDocument = (LegalDocument) this.getCurrentRowObject();
            if (legaDocument.getSummary() != null && !legaDocument.getSummary().isEmpty()) {
                String stripString = com.criticalsoftware.certitools.presentation.util.Utils.stripTags(legaDocument.getSummary());
                return StringEscapeUtils.unescapeHtml(stripString);
            }
        }
        return "-";
    }

    public String getCustomAbstract() {
        if (this.getCurrentRowObject() instanceof LegalDocument) {
            LegalDocument legaDocument = (LegalDocument) this.getCurrentRowObject();
            if (legaDocument.getCustomAbstract() != null && !legaDocument.getCustomAbstract().isEmpty()) {
                String stripString = com.criticalsoftware.certitools.presentation.util.Utils.stripTags(legaDocument.getCustomAbstract());
                return StringEscapeUtils.unescapeHtml(stripString);
            }
        }
        return "-";
    }

    public String getTransitoryProvisions() {
        if (this.getCurrentRowObject() instanceof LegalDocument) {
            LegalDocument legaDocument = (LegalDocument) this.getCurrentRowObject();
            if (legaDocument.getTransitoryProvisions() != null && !legaDocument.getTransitoryProvisions().isEmpty()) {
                String stripString = com.criticalsoftware.certitools.presentation.util.Utils.stripTags(legaDocument.getTransitoryProvisions());
                return StringEscapeUtils.unescapeHtml(stripString);
            }
        }
        return "-";
    }

    public String getLegalComplianceValidation() {
        if (this.getCurrentRowObject() instanceof LegalDocument) {
            LegalDocument legalDocument = (LegalDocument) this.getCurrentRowObject();
            if (legalDocument.getLegalComplianceValidation() != null && !legalDocument.getLegalComplianceValidation()
                    .isEmpty()) {
                String stripString = com.criticalsoftware.certitools.presentation.util.Utils.stripTags(legalDocument.getLegalComplianceValidation());
                return StringEscapeUtils.unescapeHtml(stripString);
            }
        }
        return "-";
    }

    public String getReferenceArticles() {
        if (this.getCurrentRowObject() instanceof LegalDocument) {
            LegalDocument legalDocument = (LegalDocument) this.getCurrentRowObject();
            if (legalDocument.getReferenceArticles() != null && !legalDocument.getReferenceArticles()
                    .isEmpty()) {
                String stripString = Utils.stripTags(legalDocument.getReferenceArticles());
                return StringEscapeUtils.unescapeHtml(stripString);
            }
        }
        return "-";
    }

    public String getPublished() {
        if (this.getCurrentRowObject() instanceof LegalDocument) {
            LegalDocument legaDocument = (LegalDocument) this.getCurrentRowObject();
            Locale locale = this.getPageContext().getRequest().getLocale();
            if (legaDocument.isPublished()) {
                return LocalizationUtility
                        .getLocalizedFieldName("common.yes", null, null, locale);
            } else {
                return LocalizationUtility
                        .getLocalizedFieldName("common.no", null, null, locale);
            }
        }
        return "-";
    }

    public String getAssociatedLegalDocuments() {
        if (this.getCurrentRowObject() instanceof LegalDocument) {
            StringBuilder sb = new StringBuilder();
            LegalDocument legaDocument = (LegalDocument) this.getCurrentRowObject();
            if (legaDocument.getAssociatedLegalDocuments() != null && !legaDocument.getAssociatedLegalDocuments()
                    .isEmpty()) {
                for (LegalDocument document : legaDocument.getAssociatedLegalDocuments()) {
                    sb.append(document.getFullDrTitle());

                    if (legaDocument.getAssociatedLegalDocuments().indexOf(document)
                            != legaDocument.getAssociatedLegalDocuments().size() - 1) {
                        sb.append(";");
                    }
                }
                return sb.toString();
            }
        }
        return "-";
    }
}

