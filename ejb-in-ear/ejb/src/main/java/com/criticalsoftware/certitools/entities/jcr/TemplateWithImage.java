/*
 * $Id: TemplateWithImage.java,v 1.1 2012/06/01 13:51:51 d-marques Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2012/06/01 13:51:51 $
 * Last changed by : $Author: d-marques $
 */
package com.criticalsoftware.certitools.entities.jcr;

/**
 * Description.
 *
 * @author :    Daniel Marques
 * @version :   $Revision: 1.1 $
 */
public interface TemplateWithImage {
    Resource getResource();

    String getImageMap();
}