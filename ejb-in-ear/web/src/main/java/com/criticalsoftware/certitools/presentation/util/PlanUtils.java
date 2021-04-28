/*
 * $Id: PlanUtils.java,v 1.6 2010/06/21 10:45:57 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2010/06/21 10:45:57 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.util;

import com.criticalsoftware.certitools.business.exception.BusinessException;
import com.criticalsoftware.certitools.entities.Contract;
import com.criticalsoftware.certitools.entities.jcr.Folder;
import com.criticalsoftware.certitools.util.ModuleType;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

/**
 * <description>
 *
 * @author jp-gomes
 */
public class PlanUtils {

    public static final String ROOT_PLAN_FOLDER = "certitools_plan_root";

    public static String getOnlineOfflineFromPath(String path) throws BusinessException {
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

    // copied from com.criticalsoftware.certitools.util.PlanUtils
    public static int calculateDepth(String path) {
        int matches = StringUtils.countMatches(path, "/");
        matches = (matches - 4);
        return matches / 2 + 1;
    }

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

    /**
     * Breaks a path by their / and returns only the folder in the specified index. Index starts at 0. Example, if path
     * is /a/b/c and index is 1 this method returns 'b'
     *
     * @param path  path to analyse
     * @param index index of the folder to return
     * @return folder in the specified index
     *
     * @throws BusinessException invalid path
     */
    public static String getFolderInPathByIndex(String path, int index) throws BusinessException {
        if (!path.contains("/")) {
            throw new BusinessException("Invalid path");
        }

        StringTokenizer st = new StringTokenizer(path, "/");
        for (int i = 0; i < index; i++) {
            st.nextToken();
        }
        return st.nextToken();
    }

    /**
     * Given a folder path returns the path of the section
     *
     * @param path folder path to analyse
     * @return path of the section
     *
     * @throws BusinessException invalid path
     */
    public static String getSectionPath(String path) throws BusinessException {
        if (!path.contains("/")) {
            throw new BusinessException("Invalid path");
        }

        StringTokenizer st = new StringTokenizer(path, "/");
        String result = "";
        for (int i = 0; i <= 3; i++) {
            result += "/" + st.nextToken();
        }
        return result;
    }

    /**
     * Returns the parent path of the specified path (path minus 2 folders)
     *
     * @param path path of the folder
     * @return parent path of the specified path
     *
     * @throws BusinessException invalid path
     */
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

    public static String getPathAfterOffline(String path) {
        StringBuilder sb = new StringBuilder();
        String[] split = path.split("/");

        for (int i = 4; i < split.length; i++) {
            sb.append("/");
            sb.append(split[i]);
        }
        return sb.toString();
    }

    public static String convertResourceToHTML(String folderId, String contextPath, ModuleType moduleType)
            throws BusinessException {
        StringBuilder sb = new StringBuilder();

        sb.append("<p>");
        sb.append("<img src=\"");
        sb.append(contextPath);
        sb.append("/plan/Plan.action?planModuleType=").append(moduleType).append("&viewResource=");
        sb.append("&peiId=");
        sb.append(PlanUtils.getContractNumberByPath(folderId));
        sb.append("&peiViewOffline=true&selfImage=true");
        sb.append("&path=");
        sb.append(PlanUtils.getPathAfterOffline(folderId));
        sb.append("\"/>");
        sb.append("</p>");
        return sb.toString();
    }

    public static String simplifyPath(String path) {
        String[] split = path.split("/");

        String newPath = "";
        for (int i = 4; i < split.length; i++) {
            newPath += "/" + split[i];
        }
        return newPath;
    }

    public static List<Folder> simplifyFolderPath(List<Folder> folders) {
        for (Folder f : folders) {

            String[] split = f.getPath().split("/");
            if (split.length >= 5) {
                String newPath = "";
                for (int i = 4; i < split.length; i++) {
                    newPath += "/" + split[i];
                }
                f.setPath(newPath);
            }
        }
        return folders;
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

    /**
     * Finds path parent path ex: path=/certitools_pei_root/PEI12/offline/Organograna returns
     * /certitools_pei_root/PEI12
     *
     * @param path - path
     * @return - parent root path
     *
     * @throws BusinessException - path is invalid
     */
    public static String findRootPath(String path) throws BusinessException {
        if (path != null) {
            String[] split = path.split("/");

            if (split.length <= 2) {
                throw new BusinessException("Invalid path");
            }
            return "/" + split[1] + "/" + split[2];
        }
        return null;
    }

    public static Collection<Contract> cleanContractsForJavascriptResolution(Collection<Contract> contracts){
        if (contracts != null){
            for (Contract contract : contracts) {
                contract.setTemplatesDocx(null);
            }
        }
        return contracts;
    }
}