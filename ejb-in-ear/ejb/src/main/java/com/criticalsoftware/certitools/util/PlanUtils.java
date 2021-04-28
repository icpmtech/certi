/*
 * $Id: PlanUtils.java,v 1.8 2013/10/20 02:02:48 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2013/10/20 02:02:48 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.util;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.entities.jcr.Folder;
import com.criticalsoftware.certitools.entities.jcr.Template;
import com.criticalsoftware.certitools.entities.jcr.TemplateResource;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * PEI Util methods used in services/DAOs
 *
 * @author pjfsilva
 */
public class PlanUtils {
    public static final String[] IMAGE_MEDIA_TYPES =
            {"image/bmp", "image/cgm", "image/cmu-raster", "image/g3fax", "image/gif", "image/ief",
                    "image/jpeg", "image/jpg", "image/x-png", "image/pjpeg", "image/jpe", "image/naplps", "image/png",
                    "image/targa",
                    "image/tiff", "image/tif", "image/tiff", "image/vnd.dwg", "image/vnd.dxf", "image/vnd.fpx",
                    "image/vnd.net.fpx", "image/vnd.svf", "image/x-xbitmap", "image/x-cmu-raster", "image/x-pict",
                    "image/x-portable-anymap", "image/x-portable-bitmap", "image/x-portable-graymap",
                    "image/x-portable-pixmap",
                    "image/x-rgb", "image/x-tiff", "image/x-win-bmp", "image/x-xbitmap", "image/x-xbm",
                    "image/x-xpixmap",
                    "image/x-windowdump"};

    public static final String ROOT_PLAN_FOLDER = "certitools_plan_root";

    // used in plan migration export/import
    public static final String IMPORT_PROPERTIES_FILE = "properties.ini";
    public static final String IMPORT_PLAN_FILE = "plan.xml";

    /**
     * Calculates the depth of a Folder based on the path (counts '/') /certitools_pei_root/PEI16/offline/Sec��o 1
     * returns 1 /certitools_pei_root/PEI16/offline/Sec��o 1/folders/sub returns 2 /certitools_pei_root/PEI16/offline/Sec��o
     * 1/folders/sub/folders/sub sub  returns 3
     *
     * @param path path to analyse
     * @return depth of the path
     */
    public static int calculateDepth(String path) {
        int matches = StringUtils.countMatches(path, "/");
        matches = (matches - 4);
        return matches / 2 + 1;
    }

    public static String getOnlineOrOfflineFromPath(String path) throws BusinessException {
        if (path != null) {
            String[] split = path.split("/");
            if (split.length < 3) {
                throw new BusinessException("Invalid path");
            }
            if (split[3].equals("online")) {
                return "online";
            } else {
                return "offline";
            }
        }
        return null;
    }

    // copied from com.criticalsoftware.certitools.presentation.util.PlanUtils
    public static Long getContractNumberByPath(String path) throws BusinessException {
        if (path != null) {
            String[] split = path.split("/");
            if (split.length < 2) {
                throw new BusinessException("Invalid path");
            }
            try {
                return Long.parseLong(split[2].substring(3, split[2].length()));
            } catch (NumberFormatException e) {
                throw new BusinessException("Error converting folder path to contract Id", e);
            }
        }
        return null;
    }

    public static List<String> findAllPathsToFolder(String path) {
        List<String> paths = new ArrayList<String>();
        String[] split = path.split("/");
        String parcialPath;

        if (split.length == 3) {
            paths.add("/" + split[1] + "/" + split[2]);
            return paths;
        }
        parcialPath = "/" + split[1] + "/" + split[2] + "/" + split[3];

        for (int i = 4; i < split.length; i++) {
            parcialPath += "/" + split[i];

            if (i % 2 == 0) {
                paths.add(parcialPath);
            }
        }
        return paths;
    }

    public static String simplifyPath(String path) {
        String[] split = path.split("/");

        String newPath = "";
        for (int i = 4; i < split.length; i++) {
            newPath += "/" + split[i];
        }
        return newPath;
    }

    public static String getParentPath(String path) throws BusinessException {
        String result = "";
        if (path != null) {
            String[] split = path.split("/");
            if (split.length <= 2) {
                throw new BusinessException("Invalid path");
            }
            for (int i = 1; i < split.length - 2; i++) {
                result += "/" + split[i];
            }
        }
        return result;
    }

    public static boolean isFolderTemplateImage(Folder folder, boolean isImage) {
        if (folder.getTemplate() != null && folder.getTemplate().getName()
                .equals(Template.Type.TEMPLATE_RESOURCE.getName())) {
            List<String> s = Arrays.asList(IMAGE_MEDIA_TYPES);
            if (!isImage || s.contains(((TemplateResource) folder.getTemplate()).getResource().getMimeType())) {
                if (folder.getActive()) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String getPathAfterOffline(String path) {
        StringBuilder sb = new StringBuilder();
        String[] split = path.split("/");

        for (int i = 4; i < split.length; i++) {
            sb.append("/");
            sb.append(split[i]);
        }
        return sb.toString();
    }

    public static String buildFolderMirrorLink(String contractDesignation, String companyName, String completePath) {
        StringBuilder sb = new StringBuilder();

        sb.append("<strong>");
        sb.append(companyName);
        sb.append("</strong>");
        sb.append(" > ");
        sb.append("<strong>");
        sb.append(contractDesignation);
        sb.append("</strong>");
        sb.append(": ");
        String pathToFolder = getPathAfterOffline(completePath);
        pathToFolder = pathToFolder.replaceAll("/folders", "");
        sb.append(pathToFolder);

        return sb.toString();
    }

    public static ModuleType getModuleTypeFromPath(String path) throws BusinessException {
        if (path != null) {
            String[] split = path.split("/");
            if (split.length < 2) {
                throw new BusinessException("Invalid path");
            }
            return ModuleType.valueOf(split[2].substring(0, 3));
        }
        return null;
    }
}