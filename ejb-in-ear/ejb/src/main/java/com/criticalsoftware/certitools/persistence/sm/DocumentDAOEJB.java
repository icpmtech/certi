package com.criticalsoftware.certitools.persistence.sm;

import com.criticalsoftware.certitools.entities.sm.Document;
import com.criticalsoftware.certitools.persistence.GenericDAOEJB;
import com.criticalsoftware.certitools.util.Logger;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.security.SecurityDomain;

import javax.annotation.Resource;
import javax.annotation.security.RolesAllowed;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DocumentDAOEJB
 *
 * @author miseabra
 * @version $Revision$
 */
@SuppressWarnings("UnusedDeclaration")
@Stateless
@Local(DocumentDAO.class)
@LocalBinding(jndiBinding = "certitools/DocumentDAO")
@SecurityDomain("CertiToolsRealm")
@RolesAllowed({"private"})
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DocumentDAOEJB extends GenericDAOEJB<Document, Long> implements DocumentDAO {

    private static final Logger LOGGER = Logger.getInstance(DocumentDAOEJB.class);

    @Resource(mappedName = "java:/CertiToolsDS")
    private DataSource dataSource;

    public Long getContractSpaceUsed(Long contractId) {
        Query query = manager.createQuery("select SUM(d.contentLength) from Document d " +
                "where d.contract.id = :contractId ");
        query.setParameter("contractId", contractId);
        return (Long) query.getSingleResult();
    }

    public Document getContractLogoPicture(Long contractId) {
        Query query = manager.createQuery("select c.smLogoPicture from Contract c " +
                "where c.id = :contractId ");
        query.setParameter("contractId", contractId);

        try {
            return (Document) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Document getContractCoverPicture(Long contractId) {
        Query query = manager.createQuery("select c.smCoverPicture from Contract c " +
                "where c.id = :contractId ");
        query.setParameter("contractId", contractId);

        try {
            return (Document) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Document findDocumentById(Long documentId) {
        Query query = manager.createQuery("select d from Document d where d.id = :documentId");
        query.setParameter("documentId", documentId);

        try {
            return (Document) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void deleteDocument(Long documentId) {
        Query query = manager.createQuery("delete from Document d where d.id = :documentId");
        query.setParameter("documentId", documentId);
        query.executeUpdate();
    }

    public byte[] findDocumentContent(Long documentId) {
        Query query = manager.createQuery("select d.content from Document d where d.id = :documentId");
        query.setParameter("documentId", documentId);

        try {
            return (byte[]) query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public InputStream findDocumentContentInputStream(Long documentId) {
        Connection conn = null;
        InputStream inputStream = null;
        try {
            conn = dataSource.getConnection();
            String sql = "SELECT content FROM sm_document WHERE id = ? ";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setLong(1, documentId);

            ResultSet result = statement.executeQuery();
            if (result.next()) {
                inputStream = result.getBinaryStream("content");
            }
        } catch (SQLException ex) {
            inputStream = null;
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignore) {
                }
            }
        }
        return inputStream;
    }
}
