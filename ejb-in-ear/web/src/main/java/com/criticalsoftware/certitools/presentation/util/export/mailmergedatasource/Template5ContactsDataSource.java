/*
 * $Id: Template5ContactsDataSource.java,v 1.1 2010/06/30 11:50:02 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 * 
 * Last changed on $Date: 2010/06/30 11:50:02 $
 * Last changed by $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.util.export.mailmergedatasource;

import com.aspose.words.IMailMergeDataSource;
import com.aspose.words.ref.Ref;
import com.criticalsoftware.certitools.entities.jcr.Template5ContactsElement;
import com.criticalsoftware.certitools.presentation.util.export.PlanExportUtil;

import java.util.HashMap;
import java.util.LinkedList;

//import com.aspose.words.MergeImageFieldEventArgs;
//import com.aspose.words.MergeImageFieldEventHandler;

/**
 * Description.
 *
 * @author :    pjfsilva
 * @version :   $Revision: 1.1 $
 */
public  class Template5ContactsDataSource implements IMailMergeDataSource {
    String tableName;
    HashMap<String, LinkedList<Template5ContactsElement>> contacts =
            new HashMap<String, LinkedList<Template5ContactsElement>>();
    int i = -1;
    String type;


    public Template5ContactsDataSource(LinkedList<Template5ContactsElement> external,
                                       LinkedList<Template5ContactsElement> internal,
                                       LinkedList<Template5ContactsElement> emergency,
                                       String name) {
        this.contacts.put("external", external);
        this.contacts.put("internal", internal);
        this.contacts.put("emergency", emergency);
        this.tableName = name;
    }

    public String getTableName() throws Exception {
        return tableName;
    }

    public boolean moveNext() throws Exception {
        i++;
        if (i > 0) {
            if (i >= contacts.get(type).size()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean getValue(String s, Ref<Object> ref) throws Exception {
        return false;
    }

    @Override
    public IMailMergeDataSource getChildDataSource(String s) throws Exception {
        return null;
    }

    public boolean getValue(String fieldName, Object[] fieldValue) throws Exception {
        String[] name = fieldName.split("\\.");
        type = name[0];
        String value = name[1];
        value = value.substring(0, 1).toUpperCase() + value.substring(1);

        LinkedList<Template5ContactsElement> list = contacts.get(type);
        if (i >= list.size()){
            fieldValue[0] = null;
            return false;
        }

        Template5ContactsElement contact = list.get(i);

        if ("Photo".equals(value)){
            if (contact.getPhoto() == null){
                fieldValue[0] = "";
                return true;
            }
            fieldValue[0] = PlanExportUtil.getImageByteArray(contact.getPhoto().getData());
            return true;
        }

        try {
            fieldValue[0] = Template5ContactsElement.class.getDeclaredMethod("get" + value).invoke(contact);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        fieldValue[0] = null;
        return false;
    }

    public void reset() {
        i = -1;
    }
}

/*
class HandleMergeImageFieldFromBlob implements MergeImageFieldEventHandler
{
    public void mergeImageField(Object sender, MergeImageFieldEventArgs e)
    {
        // The field value is a byte array, just cast it and create a stream on it.
        InputStream imageStream = new ByteArrayInputStream((byte[])e.getFieldValue());
        // Now the mail merge engine will retrieve the image from the stream.
        e.setImageStream(imageStream);
    }
} */