/*
 * $Id: FCKActionImpl.java,v 1.4 2009/10/07 11:34:57 pjfsilva Exp $
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

import net.fckeditor.requestcycle.UserAction;

import javax.servlet.http.HttpServletRequest;

/**
 * <insert description here>
 *
 * @author : lt-rico
 */
public class FCKActionImpl implements UserAction{

    public boolean isEnabledForFileUpload(HttpServletRequest httpServletRequest) {
        return false;
    }

    public boolean isEnabledForFileBrowsing(HttpServletRequest httpServletRequest) {
        return true;
    }

    public boolean isCreateFolderEnabled(HttpServletRequest httpServletRequest) {
        return false;
    }
}
