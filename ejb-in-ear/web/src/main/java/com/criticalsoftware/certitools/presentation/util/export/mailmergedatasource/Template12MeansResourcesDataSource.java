package com.criticalsoftware.certitools.presentation.util.export.mailmergedatasource;

import com.aspose.words.IMailMergeDataSource;
import com.aspose.words.ref.Ref;
import com.criticalsoftware.certitools.entities.jcr.Template12MeansResourcesElement;
import com.criticalsoftware.certitools.util.Logger;

import java.util.List;

/**
 * Aspose data source for Template12MeansResources template
 *
 * @author miseabra
 * @version $Revision: $
 */
public class Template12MeansResourcesDataSource implements IMailMergeDataSource {

    private static final Logger LOGGER = Logger.getInstance(Template12MeansResourcesDataSource.class);

    private String tableName;
    private List<Template12MeansResourcesElement> meansResourcesElements;
    private int i = -1;

    public Template12MeansResourcesDataSource(String tableName, List<Template12MeansResourcesElement> meansResourcesElements) {
        this.tableName = tableName;
        this.meansResourcesElements = meansResourcesElements;
    }

    @Override
    public String getTableName() {
        return tableName;
    }

    public boolean moveNext() throws Exception {
        i++;
        if (i > 0) {
            if (i >= meansResourcesElements.size()) {
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
        fieldName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        Template12MeansResourcesElement resourcesElement = meansResourcesElements.get(i);

        try {
            fieldValue[0] = Template12MeansResourcesElement.class.getDeclaredMethod("get" + fieldName).invoke(resourcesElement);
            return true;
        } catch (Exception e) {
            LOGGER.error(e);
        }

        fieldValue[0] = null;
        return false;
    }
}
