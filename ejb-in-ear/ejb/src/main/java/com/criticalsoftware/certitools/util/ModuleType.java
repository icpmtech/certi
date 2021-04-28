/*
 * $Id: ModuleType.java,v 1.3 2012/05/28 16:50:38 d-marques Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2012/05/28 16:50:38 $
 * Last changed by : $Author: d-marques $
 */
package com.criticalsoftware.certitools.util;

import java.io.Serializable;

/**
 * Application Modules Enum
 *
 * @author jp-gomes
 */
public enum ModuleType implements Serializable {
    LEGISLATION("module.legislation"),
    PEI("module.pei"),
    PRV("module.prv"),
    PSI("module.psi"),
    GSC("module.gsc"); //TODO-MODULE Add resource

    private final String key;

    ModuleType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getString() {
        return this.toString();
    }
}
