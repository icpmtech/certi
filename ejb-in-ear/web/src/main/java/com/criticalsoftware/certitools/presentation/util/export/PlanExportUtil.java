/*
 * $Id: PlanExportUtil.java,v 1.7 2010/07/09 11:09:17 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2010/07/09 11:09:17 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.util.export;

import com.criticalsoftware.certitools.entities.jcr.*;
import com.criticalsoftware.certitools.util.Logger;
import com.criticalsoftware.certitools.util.Utils;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import java.io.*;
import java.util.*;

/**
 * Utils common to all export options (PDF and DOCX)
 *
 * @author :    pjfsilva
 * @version :   $Revision: 1.7 $
 */
public class PlanExportUtil {
    private static final Logger LOGGER = Logger.getInstance(PlanExportUtil.class);


    /**
     * From the allPlanFolders list (that contains the full Plan loaded), loads the specified folder. path should include
     * "/folders/" folder also. path example: /organograma/folders/image.jpg
     *
     * @param path           full path of the folder
     * @param allPlanFolders all folders of the plan
     * @return folder object with the specified path or null if not found
     */
    protected static Folder findFolderFromPlanList(String path, List<Folder> allPlanFolders) {
        StringTokenizer st = new StringTokenizer(path, "/");
        if (st.countTokens() <= 0) {
            LOGGER.info("[PlanExportUtil - findFolderFromPlanList] - Invalid path: " + path);
            return null;
        }

        String currentFolderName = st.nextToken();
        ArrayList<Folder> currentFoldersList = new ArrayList<Folder>();
        currentFoldersList.addAll(allPlanFolders);
        Folder currentFolder = findFolderByName(currentFolderName, currentFoldersList);

        while (st.hasMoreTokens()) {
            st.nextToken(); // cut the "folders" from path
            currentFolderName = st.nextToken();
            currentFolder = findFolderByName(currentFolderName, currentFolder.getFolders());
            if (currentFolder == null) {
                return null;
            }
        }
        return currentFolder;
    }

    /**
     * Given a list of folders to search, returns the folder with the specified name
     *
     * @param name         name of the folder
     * @param listToSearch list of folders to search
     * @return folder with the specified name or null if not found
     */
    protected static Folder findFolderByName(String name, List<Folder> listToSearch) {
        for (Folder folder : listToSearch) {
            if (folder.getName().equals(name)) {
                return folder;
            }
        }
        return null;
    }

    /**
     * Finds a image from a template
     *
     * @param allPlanFolders a list of folders to search
     * @param folderName     the folder name to retrieve
     * @return Image object
     * @throws java.io.IOException when reading image
     * @throws com.lowagie.text.BadElementException
     *                             when construct a image
     */
    protected static ByteArrayInputStream findImage(List<Folder> allPlanFolders, String folderName)
            throws IOException, BadElementException {

        Folder folder = findFolderFromPlanList(folderName, allPlanFolders);

        if (folder == null) {
            return null;
        }

        Template t = folder.getTemplate();

        if (t != null) {
            Resource r = null;
            if (t instanceof TemplateResource) {
                r = ((TemplateResource) t).getResource();
            } else if (t instanceof Template1Diagram) {
                r = ((Template1Diagram) t).getResource();
            } else if (t instanceof Template4PlanClickable) {
                r = ((Template4PlanClickable) t).getResource();
            }
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

                return new ByteArrayInputStream(imagedata);
            } else {
                return null;
            }
        }

        return null;
    }

    /**
     * Return a Image element from a input stream
     *
     * @param data the input stream
     * @return Image element
     * @throws java.io.IOException some exception
     * @throws com.lowagie.text.BadElementException
     *                             another exception
     */
    protected static Image getImage(InputStream data) throws IOException, BadElementException {
        if (data != null) {
            byte[] buf = new byte[data.available()];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int len;
            while ((len = data.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            byte imagedata[] = out.toByteArray();
            out.close();

            return Image.getInstance(imagedata);
        }
        return null;
    }

    public static byte[] getImageByteArray(InputStream data) throws IOException {
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

    protected static void getContacts(Folder folder, LinkedList<Template5ContactsElement> external,
                                      LinkedList<Template5ContactsElement> internal,
                                      LinkedList<Template5ContactsElement> emergency) {
        Collections.sort(folder.getFolders());

        for (Folder f : folder.getFolders()) {
            if (f.getTemplate().getName().equals(Template.Type.TEMPLATE_CONTACTS_ELEMENT.getName()) &&
                    f.getActive().equals(Boolean.TRUE)) {
                Template5ContactsElement temp = ((Template5ContactsElement) f.getTemplate());
                if (temp.getContactType().equals(Template5ContactsElement.ContactType.EXTERNAL_ENTITY.toString())) {
                    external.add(temp);
                } else if (temp.getContactType()
                        .equals(Template5ContactsElement.ContactType.INTERNAL_PERSON.toString())) {
                    internal.add(temp);
                } else {
                    emergency.add(temp);
                }
            }
            getContacts(f, external, internal, emergency);
        }
    }

    protected static void getMeansResources(Folder folder, LinkedList<Template12MeansResourcesElement> list) {
        for (Folder f : folder.getFolders()) {
            if (f.getTemplate().getName().equals(Template.Type.TEMPLATE_MEANS_RESOURCES_ELEMENT.getName()) &&
                    f.getActive().equals(Boolean.TRUE)) {
                list.add(((Template12MeansResourcesElement) f.getTemplate()));
            }
            getMeansResources(f, list);
        }
    }

    protected static void getProceduresContent(Folder folder, ArrayList<Folder> result, ArrayList<Folder> filesAttached,
                                               int depth) {
        depth++;
        Collections.sort(folder.getFolders());

        for (Folder f : folder.getFolders()) {
            if ((f.getTemplate().getName().equals(Template.Type.TEMPLATE_RICH_TEXT.getName()) ||
                    f.getTemplate().getName().equals(Template.Type.TEMPLATE_RICH_TEXT_WITH_ATTACH.getName())) &&
                    f.getActive().equals(Boolean.TRUE)) {
                result.add(f);
                f.setDepth(depth);
            }
            if (filesAttached != null && (f.getTemplate().getName().equals(Template.Type.TEMPLATE_RESOURCE.getName()) &&
                    f.getActive().equals(Boolean.TRUE))) {
                filesAttached.add(f);
            }

            getProceduresContent(f, result, filesAttached, depth);
        }
    }

    protected static void getChildrenByType(Folder folder, ArrayList<Folder> result, String templateType) {
        Collections.sort(folder.getFolders());

        for (Folder f : folder.getFolders()) {
            if (f.getTemplate().getName().equals(templateType) &&
                    f.getActive().equals(Boolean.TRUE)) {
                result.add(f);
            }

            getChildrenByType(f, result, templateType);
        }
    }

    /**
     * Creates a byte array output stream with the zip files
     *
     * @param files A map containing a key with filename and a input stream
     * @return A ByteArrayOutputStream
     * @throws java.io.IOException throws a Exception while zipping the files
     */
    public static ByteArrayOutputStream doZip(Map<String, InputStream> files) throws IOException {
        byte[] buf = new byte[1024];

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Create the ZIP file
        ZipArchiveOutputStream zaos = new ZipArchiveOutputStream(baos);
        zaos.setFallbackToUTF8(true);
        zaos.setUseLanguageEncodingFlag(true);
        zaos.setCreateUnicodeExtraFields(ZipArchiveOutputStream.UnicodeExtraFieldPolicy.ALWAYS);

        // Compress the files
        Iterator it = files.keySet().iterator();
        String fileName;
        InputStream in;
        while (it.hasNext()) {
            fileName = (String) it.next();
            in = files.get(fileName);

            ZipArchiveEntry ze = new ZipArchiveEntry(fileName);
            zaos.putArchiveEntry(ze);

            // Transfer bytes from the file to the ZIP file
            int len;
            while ((len = in.read(buf)) > 0) {
                zaos.write(buf, 0, len);
            }

            // Complete the entry
            zaos.closeArchiveEntry();

            resetInputStream(in);
        }

        // after all work is done, close files
        while (it.hasNext()) {
            fileName = (String) it.next();
            in = files.get(fileName);
            in.close();
        }

        // Complete the ZIP file
        zaos.flush();
        zaos.close();

        return baos;
    }

    /**
     * Resets inputstream according to its type
     * reset the inputstream (in case it's file input - the one that comes from jackrabbit OCM, we need to
     * set it's channel to position 0. Reference: http://forums.sun.com/thread.jspa?threadID=552833&tstart=17
     *
     * @param in inputstream
     * @throws IOException something went wrong
     */
    public static void resetInputStream(InputStream in) throws IOException {
        if (in instanceof FileInputStream) {
            FileInputStream fis = (FileInputStream) in;
            fis.getChannel().position(0);
        } else if (in.markSupported()) { // if stream supports reset, just reset it
            in.reset();
        }
    }

    public static String sanitizeFilenameToExport(String s) {
        s = Utils.removeAccentedChars(s.replaceAll("/", "_"));
        s = s.replaceAll("[\\\\]", "");
        s = s.replaceAll("[/]", "");
        s = s.replaceAll("[?*|:\\\"<>]", "");
        s = s.replaceAll("[��]", ""); // word special chars
        s = s.replaceAll("[�]", "-"); // word special chars
        s = s.replaceAll("[�]", "..."); // word special chars
        s = s.replaceAll("[�]", "�"); // word special chars
        s = s.replaceAll("[�]", "�"); // word special chars
        return s;
    }
}