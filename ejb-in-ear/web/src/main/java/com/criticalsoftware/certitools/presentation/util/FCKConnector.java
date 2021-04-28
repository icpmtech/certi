/*
 * $Id: FCKConnector.java,v 1.18 2009/10/07 11:34:57 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2009/10/07 11:34:57 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.util;

import com.criticalsoftware.certitools.business.plan.PlanService;
import com.criticalsoftware.certitools.entities.jcr.Folder;
import com.criticalsoftware.certitools.entities.jcr.Resource;
import com.criticalsoftware.certitools.entities.jcr.Template4PlanClickable;
import com.criticalsoftware.certitools.entities.jcr.TemplateResource;
import com.criticalsoftware.certitools.util.Logger;
import com.criticalsoftware.certitools.util.ModuleType;
import net.fckeditor.connector.Connector;
import net.fckeditor.connector.exception.FolderAlreadyExistsException;
import net.fckeditor.connector.exception.InvalidCurrentFolderException;
import net.fckeditor.connector.exception.InvalidNewFolderNameException;
import net.fckeditor.connector.exception.ReadException;
import net.fckeditor.connector.exception.WriteException;
import net.fckeditor.handlers.ResourceType;
import net.fckeditor.requestcycle.ThreadLocalData;
import org.apache.commons.lang.StringEscapeUtils;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <insert description here>
 *
 * @author : lt-rico
 */
public class FCKConnector implements Connector {

    private static final Logger LOGGER = Logger.getInstance(FCKConnector.class);

    private PlanService planService;

    public void init(ServletContext servletContext) throws Exception {
        try {
            InitialContext ctx = new InitialContext();
            planService = (PlanService) ctx.lookup("certitools/PlanService");
        } catch (NamingException e) {
            LOGGER.error("Error while getting initial context", e);
        }
        LOGGER.info("Certitools FCK Connector Initialized");
    }

    public List<Map<String, Object>> getFiles(ResourceType resourceType, String s)
            throws InvalidCurrentFolderException, ReadException {
        String planModuleType = ThreadLocalData.getRequest().getParameter("planModuleType");
        String folderPath = ThreadLocalData.getRequest().getParameter("folder");
        folderPath = StringEscapeUtils.unescapeJavaScript(folderPath);

        String isTemplate4Clickable = ThreadLocalData.getRequest().getParameter("isTemplate4PlanClickable");
        Boolean insertFolderFlag = new Boolean(ThreadLocalData.getRequest().getParameter("insertFolderFlag"));
        List<Map<String, Object>> files = new ArrayList<Map<String, Object>>();
        String type = ThreadLocalData.getContext().getTypeStr();
        Map<String, Object> fileMap;
        String pathName;

        try {
            if (isTemplate4Clickable != null) {
                List<Folder> folderList = planService.findPlanClickableAndResourcesFolders(folderPath, ModuleType.valueOf(planModuleType));
                for (Folder f : folderList) {
                    fileMap = new HashMap<String, Object>(2);
                    pathName = PlanUtils.getPathAfterOffline(f.getPath());
                    pathName = pathName.substring(1, pathName.length());

                    if (f.getTemplate() instanceof Template4PlanClickable) {
                        fileMap.put(Connector.KEY_NAME, pathName);
                        fileMap.put(Connector.KEY_SIZE, 0L);
                    } else if (f.getTemplate() instanceof TemplateResource) {
                        TemplateResource templateResource = (TemplateResource) f.getTemplate();
                        fileMap.put(Connector.KEY_NAME, pathName);
                        fileMap.put(Connector.KEY_SIZE, new Long(templateResource.getResource().getSize()));
                    }
                    files.add(fileMap);
                }
            } else {
                List<Resource> fileList = planService.findFolderResources(folderPath, type.equals("Image"), !insertFolderFlag,
                        ModuleType.valueOf(planModuleType));
                for (Resource file : fileList) {
                    pathName = file.getPath();
                    fileMap = new HashMap<String, Object>(2);
                    pathName = pathName.substring(pathName.indexOf("/offline/") + 9,
                            pathName.indexOf("/template/resource"));
                    fileMap.put(Connector.KEY_NAME, pathName);
                    fileMap.put(Connector.KEY_SIZE, new Long(file.getSize()));
                    files.add(fileMap);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return files;
    }

    public List<String> getFolders(ResourceType resourceType, String s)
            throws InvalidCurrentFolderException, ReadException {


        return new ArrayList<String>();
    }

    public void createFolder(ResourceType resourceType, String s, String s1)
            throws InvalidCurrentFolderException, InvalidNewFolderNameException, FolderAlreadyExistsException,
            WriteException {
        //DO NOTHING
    }

    public String fileUpload(ResourceType resourceType, String s, String s1, InputStream inputStream)
            throws InvalidCurrentFolderException, WriteException {
        //NO CAN DO
        return null;
    }
}
