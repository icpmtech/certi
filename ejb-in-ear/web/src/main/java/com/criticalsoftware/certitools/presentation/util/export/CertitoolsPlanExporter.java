/*
 * $Id: CertitoolsPlanExporter.java,v 1.4 2010/07/01 19:12:04 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2010/07/01 19:12:04 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.util.export;

import com.criticalsoftware.certitools.business.exception.PDFException;
import com.criticalsoftware.certitools.entities.jcr.*;
import net.sourceforge.stripes.action.ActionBeanContext;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Certitools Plan exporter.
 * Exports a Plan with all his contents in a ZIP file
 *
 * @author : lt-rico
 */
public class CertitoolsPlanExporter {

    private boolean exportOnline = false;
    private String peiName, author, version;
    private Date verDate, simDate;
    private List<Folder> foldersToExport;
    private Resource companyLogo, installPhoto;


    public CertitoolsPlanExporter(boolean exportOnline, Plan pei) {
        this.exportOnline = exportOnline;
        this.peiName =
                exportOnline ? pei.getPlanNameOnline() == null ? "" : pei.getPlanNameOnline() : pei.getPlanName();
        this.author =
                exportOnline ? pei.getAuthorNameOnline() == null ? "" : pei.getAuthorNameOnline() : pei.getAuthorName();
        this.version = exportOnline ? pei.getVersionOnline() == null ? "" : pei.getVersionOnline() : pei.getVersion();
        this.verDate = exportOnline ? pei.getVersionDateOnline() == null ? null : pei.getVersionDateOnline() :
                pei.getVersionDate();
        this.simDate =
                exportOnline ? pei.getSimulationDateOnline() == null ? null : pei.getSimulationDateOnline() :
                        pei.getSimulationDate();
        this.foldersToExport = exportOnline ? pei.getOnline() : pei.getOffline();
        this.companyLogo = exportOnline ? pei.getCompanyLogoOnline() : pei.getCompanyLogo();
        this.installPhoto = exportOnline ? pei.getInstallationPhotoOnline() : pei.getInstallationPhoto();
    }

    /**
     * Zip all PEI
     *
     * @param context action bean context
     * @return Output stream containing the zipped PEI
     * @throws IOException  Some exception that may occour
     * @throws PDFException another execption that may occour
     */
    public ByteArrayOutputStream zipPEI(ActionBeanContext context) throws IOException, PDFException {
        Map<String, InputStream> files = new LinkedHashMap<String, InputStream>();

        String logoPath = context.getServletContext().getRealPath("/images/") + System.getProperty("file.separator") +
                "logopdf.png";

        findFiles(foldersToExport, files);

        byte[] logo = copyStream(companyLogo != null ? companyLogo.getData() : null);
        byte[] install = copyStream(installPhoto != null ? installPhoto.getData() : null);


        Plan2Pdf pdf = new Plan2Pdf(context.getLocale(), logoPath, peiName, author, version, verDate, simDate,
                logo != null ? new ByteArrayInputStream(logo) : null,
                install != null ? new ByteArrayInputStream(install) : null, foldersToExport);
        files.put(peiName.replaceAll("/", "_") + ".pdf", new ByteArrayInputStream(pdf.generatePDF().toByteArray()));

        Plan2Rtf rtf = new Plan2Rtf(context.getLocale(), logoPath, peiName, author, version, verDate, simDate,
                logo != null ? new ByteArrayInputStream(logo) : null,
                install != null ? new ByteArrayInputStream(install) : null, foldersToExport);
        files.put(peiName.replaceAll("/", "_") + ".rtf", new ByteArrayInputStream(rtf.generateRTF().toByteArray()));

        return PlanExportUtil.doZip(files);
    }


    private void findFiles(List<Folder> folders, Map<String, InputStream> resources) throws IOException {
        for (Folder f : folders) {
            Template t = f.getTemplate();
            if (t != null && (
                    t.getName().equals(Template.Type.TEMPLATE_RESOURCE.getName()) ||
                            t.getName().equals(Template.Type.TEMPLATE_DOCUMENTS_ELEMENT.getName()) ||
                            t.getName().equals(Template.Type.TEMPLATE_PLAN_CLICKABLE.getName()) ||
                            t.getName().equals(Template.Type.TEMPLATE_DIAGRAM.getName()))) {

                if (t instanceof TemplateResource) {
                    if (((TemplateResource) t).getResource() != null)
                        addResource(resources, ((TemplateResource) t).getResource(), false);
                } else if (t instanceof Template1Diagram) {
                    if (((Template1Diagram) t).getResource() != null)
                        addResource(resources, ((Template1Diagram) t).getResource(), true);
                } else if (t instanceof Template4PlanClickable) {
                    if (((Template4PlanClickable) t).getResource() != null)
                        addResource(resources, ((Template4PlanClickable) t).getResource(), true);
                } else if (t instanceof Template6DocumentsElement) {
                    if (((Template6DocumentsElement) t).getResources() != null) {
                        for (Resource aux : ((Template6DocumentsElement) t).getResources()) {
                            addResource(resources, aux, true);
                        }
                    }
                }
            }
            findFiles(f.getFolders(), resources);
        }
    }

    private void addResource(Map<String, InputStream> resources, Resource r, boolean putResourceName)
            throws IOException {
        if (r != null) {
            InputStream data = r.getData();
            byte[] buf = new byte[data.available()];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int len;
            while ((len = data.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            byte imagedata[] = out.toByteArray();
            out.close();

            r.setData(new ByteArrayInputStream(imagedata));
            if (putResourceName)
                resources
                        .put(substringPathToName(r.getPath()) + "_" + r.getName(), new ByteArrayInputStream(imagedata));
            else
                resources.put(substringPathToName(r.getPath()), new ByteArrayInputStream(imagedata));
        }
    }

    /**
     * Takes inputstream and saves the content to a byte array
     * @param data inputstream
     * @return bytearray with the data
     * @throws IOException IO error
     */
    private byte[] copyStream(InputStream data) throws IOException {
        if (data != null) {
            byte[] buf = new byte[data.available()];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int len;
            while ((len = data.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            byte imagedata[] = out.toByteArray();
            out.close();
            return imagedata;
        }
        return null;
    }

    private String substringPathToName(String path) {
        if (this.exportOnline) {
            path = path.substring(path.indexOf("/online/") + 8, path.indexOf("/template/resource"))
                    .replaceAll("/folders", "").replaceAll("/", "_");
        } else {
            path = path.substring(path.indexOf("/offline/") + 9, path.indexOf("/template/resource"))
                    .replaceAll("/folders", "").replaceAll("/", "_");
        }
        return "resources/" + path;
    }

}
