package com.criticalsoftware.certitools.persistence.sm;

import com.criticalsoftware.certitools.entities.sm.Document;
import com.criticalsoftware.certitools.persistence.GenericDAO;

import java.io.InputStream;

/**
 * DocumentDAO
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("unused")
public interface DocumentDAO extends GenericDAO<Document, Long> {

    Long getContractSpaceUsed(Long contractId);

    Document getContractLogoPicture(Long contractId);

    Document getContractCoverPicture(Long contractId);

    Document findDocumentById(Long documentId);

    void deleteDocument(Long documentId);

    byte[] findDocumentContent(Long documentId);

    InputStream findDocumentContentInputStream(Long documentId);
}
