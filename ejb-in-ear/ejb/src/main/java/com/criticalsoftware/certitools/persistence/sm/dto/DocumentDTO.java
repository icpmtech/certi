package com.criticalsoftware.certitools.persistence.sm.dto;

import java.io.InputStream;

/**
 * DocumentDTO
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
public class DocumentDTO {

    private String name;
    private String displayName;
    private String contentType;
    private InputStream inputStream;

    public DocumentDTO(String name, String displayName, String contentType, InputStream inputStream) {
        this.name = name;
        this.displayName = displayName;
        this.contentType = contentType;
        this.inputStream = inputStream;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
