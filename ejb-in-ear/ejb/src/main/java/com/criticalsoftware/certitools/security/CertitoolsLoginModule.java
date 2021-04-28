package com.criticalsoftware.certitools.security;

import com.criticalsoftware.certitools.util.Logger;
import org.jboss.security.auth.spi.DatabaseServerLoginModule;
import org.jboss.tm.TransactionDemarcationSupport;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.sql.DataSource;
import javax.transaction.Transaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CertitoolsLoginModule extends DatabaseServerLoginModule {

    Logger log = Logger.getInstance(CertitoolsLoginModule.class);

    /**
     * Get the expected password for the current username available via the getUsername() method. This is called from
     * within the login() method after the CallbackHandler has returned the username and candidate password.
     *
     * @return the valid password String
     */
    protected String getUsersPassword() throws LoginException {
        boolean trace = log.isTraceEnabled();
        String username = getUsername();
        String password = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        Transaction tx = null;
        if (suspendResume) {
            tx = TransactionDemarcationSupport.suspendAnyTransaction();
            if (trace) {
                log.trace("suspendAnyTransaction");
            }
        }

        try {
            String[] temp = getUsernameAndPassword();
            String tempPassword = temp[1];

            InitialContext ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup(dsJndiName);
            conn = ds.getConnection();
            // Get the password
            if (trace) {
                log.trace("Excuting query: " + principalsQuery + ", with username: " + username);
            }
            ps = conn.prepareStatement(principalsQuery);
            ps.setString(2, username);
            ps.setString(1, tempPassword);

            log.debug("Certitools Login Module: " + principalsQuery + " | " + username + " | " + tempPassword);

            rs = ps.executeQuery();
            if (rs.next() == false) {
                if (trace) {
                    log.trace("Query returned no matches from db");
                }
                throw new FailedLoginException("No matching username found in Principals");
            }

            password = rs.getString(1);
            password = convertRawPassword(password);

            log.debug("Certitools Login Module, returning password: " + password);

            if (trace) {
                log.trace("Obtained user password");
            }
        }
        catch (NamingException ex) {
            LoginException le = new LoginException("Error looking up DataSource from: " + dsJndiName);
            le.initCause(ex);
            throw le;
        }
        catch (SQLException ex) {
            LoginException le = new LoginException("Query failed");
            le.initCause(ex);
            throw le;
        }
        finally {
            if (rs != null) {
                try {
                    rs.close();
                }
                catch (SQLException e) {
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                }
                catch (SQLException e) {
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                }
                catch (SQLException ex) {
                }
            }
            if (suspendResume) {
                TransactionDemarcationSupport.resumeAnyTransaction(tx);
                if (log.isTraceEnabled()) {
                    log.trace("resumeAnyTransaction");
                }
            }
        }
        return password;
    }

}