/*
 * $Id: CertitoolsCsvWorker.java,v 1.9 2013/12/18 05:13:47 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2013/12/18 05:13:47 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.util;

import com.criticalsoftware.certitools.business.exception.ImportException;
import com.criticalsoftware.certitools.entities.Permission;
import com.criticalsoftware.certitools.entities.Role;
import com.criticalsoftware.certitools.entities.User;
import com.criticalsoftware.certitools.entities.UserContract;
import org.apache.commons.lang.StringUtils;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CertitoolsCsvWorker
 *
 * @author : lt-rico
 */
@SuppressWarnings({"unchecked"})
public class CertitoolsCsvWorker {
    private BufferedReader br;

    private boolean hasNext = true;

    private char separator;

    private char quotechar;

    private int skipLines;

    private boolean linesSkiped;

    /** The default separator to use if none is supplied to the constructor. */
    public static final char DEFAULT_SEPARATOR = ';';

    /** The default quote character to use if none is supplied to the constructor. */
    public static final char DEFAULT_QUOTE_CHARACTER = '"';

    /** The default line to start reading. */
    public static final int DEFAULT_SKIP_LINES = 0;

    /**
     * Constructs CSVReader using a comma for the separator.
     *
     * @param reader the reader to an underlying CSV source.
     */
    private CertitoolsCsvWorker(Reader reader) {
        this(reader, DEFAULT_SEPARATOR);
    }

    /**
     * Constructs CSVReader with supplied separator.
     *
     * @param reader    the reader to an underlying CSV source.
     * @param separator the delimiter to use for separating entries.
     */
    private CertitoolsCsvWorker(Reader reader, char separator) {
        this(reader, separator, DEFAULT_QUOTE_CHARACTER);
    }


    /**
     * Constructs CSVReader with supplied separator and quote char.
     *
     * @param reader    the reader to an underlying CSV source.
     * @param separator the delimiter to use for separating entries
     * @param quotechar the character to use for quoted elements
     */
    private CertitoolsCsvWorker(Reader reader, char separator, char quotechar) {
        this(reader, separator, quotechar, DEFAULT_SKIP_LINES);
    }

    /**
     * Constructs CSVReader with supplied separator and quote char.
     *
     * @param reader    the reader to an underlying CSV source.
     * @param separator the delimiter to use for separating entries
     * @param quotechar the character to use for quoted elements
     * @param line      the line number to skip for start reading
     */
    private CertitoolsCsvWorker(Reader reader, char separator, char quotechar, int line) {
        this.br = new BufferedReader(reader);
        this.separator = separator;
        this.quotechar = quotechar;
        this.skipLines = line;
    }

    /**
     * Reads the entire file into a List with each element being a String[] of tokens.
     *
     * @return a List of String[], with each String[] representing a line of the file.
     *
     * @throws IOException if bad things happen during the read
     */
    public List readAll() throws IOException {

        List allElements = new ArrayList();
        while (hasNext) {
            String[] nextLineAsTokens = readNext();
            if (nextLineAsTokens != null) {
                allElements.add(nextLineAsTokens);
            }
        }
        return allElements;

    }

    /**
     * Reads the next line from the buffer and converts to a string array.
     *
     * @return a string array with each comma-separated element as a separate entry.
     *
     * @throws IOException if bad things happen during the read
     */
    public String[] readNext() throws IOException {

        String nextLine = getNextLine();
        return hasNext ? parseLine(nextLine) : null;
    }

    /**
     * Reads the next line from the file.
     *
     * @return the next line from the file without trailing newline
     *
     * @throws IOException if bad things happen during the read
     */
    private String getNextLine() throws IOException {
        if (!this.linesSkiped) {
            for (int i = 0; i < skipLines; i++) {
                br.readLine();
            }
            this.linesSkiped = true;
        }
        String nextLine = br.readLine();
        if (nextLine == null) {
            hasNext = false;
        }
        return hasNext ? nextLine : null;
    }

    /**
     * Parses an incoming String and returns an array of elements.
     *
     * @param nextLine the string to parse
     *
     * @return the comma-tokenized list of elements, or null if nextLine is null
     *
     * @throws IOException if bad things happen during the read
     */
    private String[] parseLine(String nextLine) throws IOException {

        if (nextLine == null) {
            return null;
        }

        List tokensOnThisLine = new ArrayList();
        StringBuffer sb = new StringBuffer();
        boolean inQuotes = false;
        do {
            if (inQuotes) {
                // continuing a quoted section, reappend newline
                sb.append("\n");
                nextLine = getNextLine();
                if (nextLine == null) {
                    break;
                }
            }
            for (int i = 0; i < nextLine.length(); i++) {

                char c = nextLine.charAt(i);
                if (c == quotechar || c == '#') {
                    // this gets complex... the quote may end a quoted block, or escape another quote.
                    // do a 1-char lookahead:
                    if (inQuotes  // we are in quotes, therefore there can be escaped quotes in here.
                            && nextLine.length() > (i + 1)  // there is indeed another character to check.
                            && (nextLine.charAt(i + 1) == '#' ||
                            nextLine.charAt(i + 1) == quotechar)) { // ..and that char. is a quote also.
                        // we have two quote chars in a row == one quote char, so consume them both and
                        // put one on the token. we do *not* exit the quoted text.
                        sb.append(nextLine.charAt(i + 1));
                        i++;
                    } else {
                        inQuotes = !inQuotes;
                        // the tricky case of an embedded quote in the middle: a,bc"d"ef,g
                        if (i > 2 //not on the begining of the line
                                && nextLine.charAt(i - 1) != this.separator //not at the begining of an escape sequence
                                && nextLine.length() > (i + 1) &&
                                nextLine.charAt(i + 1) != this.separator //not at the	end of an escape sequence
                                ) {
                            sb.append(c);
                        }
                    }
                } else if (c == separator && !inQuotes) {
                    tokensOnThisLine.add(sb.toString());
                    sb = new StringBuffer(); // start work on next token
                } else {
                    sb.append(c);
                }
            }
        } while (inQuotes);
        tokensOnThisLine.add(sb.toString());
        return (String[]) tokensOnThisLine.toArray(new String[0]);

    }

    /**
     * Closes the underlying reader.
     *
     * @throws IOException if the close fails
     */
    public void close() throws IOException {
        br.close();
    }

    public static StringBuilder parseUserToCSV(User u) {
        DateFormat df = new SimpleDateFormat(Configuration.getInstance().getDatePattern());
        StringBuilder builder = new StringBuilder();
        builder.append(u.getName().contains(";") ? "\"" + u.getName() + "\";" : u.getName() + ";");
        builder.append(u.getEmail()).append(";");
        builder.append(u.getEmailContact()).append(";");
        builder.append(u.getFiscalNumber()).append(";");
        builder.append(u.getPhone() != null ?
                (u.getPhone().contains(";") ? "\"" + u.getPhone() + "\";" : u.getPhone() + ";") : ";");
        builder.append(u.getExternalUser() != null ?
                (u.getExternalUser().contains(";") ? "\"" + u.getExternalUser() + "\";" : u.getExternalUser() + ";") :
                ";");
        builder.append(u.isActive()).append(";");
        builder.append(u.isUniqueSession()).append(";");
        builder.append(u.isDeleted()).append(";");
        int x = 1;

        if (u.getRoles().size() == 1) {
            builder.append("##");
        } else {
            for (Role r : u.getRoles()) {
                if (!r.getRole().equals("user")) {
                    builder.append(x == 1 ? "#" : "").append(r.getRole())
                            .append(++x == u.getRoles().size() ? "#" : ";");
                }
            }
        }
        builder.append(";");

        if (u.getUserContract() == null || u.getUserContract().isEmpty()) {
            builder.append("##");
        } else {
            for (UserContract uc : u.getUserContract()) {
                builder.append("#");
                builder.append(uc.getUserContractPK().getIdContract());
                if (uc.getValidityStartDate() != null) {
                    builder.append(";");
                    builder.append(df.format(uc.getValidityStartDate()));
                } else {
                    builder.append(";");
                }

                if (uc.getValidityEndDate() != null) {
                    builder.append(";");
                    builder.append(df.format(uc.getValidityEndDate()));
                } else {
                    builder.append(";");
                }

                x = 0;
                for (Permission p : uc.getPermissions()) {
                    builder.append(x == 0 ? ";" : "").append(p.getName())
                            .append(++x == uc.getPermissions().size() ? "" : ";");
                }
                builder.append("#;");
            }
        }
        return builder;
    }

    public static Map<String, Object[]> readImportFile(InputStream stream) throws ImportException {

        DateFormat df = new SimpleDateFormat(Configuration.getInstance().getDatePattern());
        Map<String, Object[]> users = new HashMap<String, Object[]>();

        CertitoolsCsvWorker worker = new CertitoolsCsvWorker(new BufferedReader(new InputStreamReader(stream)), ';');

        int lineNumber = 1;
        try {
            String[] fields;
            while ((fields = worker.readNext()) != null) {

                // if empty line, just skip
                if (fields == null || StringUtils.isEmpty(fields[0])) {
                    lineNumber++;
                    continue;
                }

                //Validations
                if (fields.length < 8) { //Validate fields length
                    throw new ImportException(
                            "Error reading line from file. Not all fields filled. Line: " + lineNumber,
                            "error.users.import.file.csv.exception.read.not.filled", lineNumber, "",
                            ImportException.Type.READ);
                }

                String email = "";
                Object[] attributes = new Object[fields.length];
                for (int i = 0; i < fields.length; i++) {

                    if (i == 0) {
                        if (fields[i].length() == 0) { // Validate user name (not empty)
                            throw new ImportException(
                                    "Error reading line from file. User name cannot be empty. Line: " + lineNumber,
                                    "error.users.import.file.csv.exception.read.name.empty",
                                    lineNumber, "", ImportException.Type.READ);
                        }
                        attributes[i] = fields[i];
                    } else if (i == 1) {
                        if (fields[i].length() == 0) { // Validate user email (not empty and valid email)
                            throw new ImportException(
                                    "Error reading line from file. User email cannot be empty. Line: " + lineNumber,
                                    "error.users.import.file.csv.exception.read.email.empty",
                                    lineNumber, "", ImportException.Type.READ);
                        }
                        if (Configuration.getInstance().getLocalInstallation()) {
                            // if we are doing BPI installation, ignore email validation
                            email = fields[i];
                        } else {
                            try {
                                InternetAddress address = new InternetAddress(fields[i]);
                                email = address.getAddress();
                                if (!email.contains("@")) {
                                    throw new AddressException();
                                }
                            } catch (AddressException e) {
                                throw new ImportException(
                                        "Error reading line from file. User email is malformed. Line: " + lineNumber,
                                        "error.users.import.file.csv.exception.parse.email.malformed",
                                        lineNumber, fields[i], fields[i], ImportException.Type.PARSE);
                            }
                        }
                        attributes[i] = email;
                    } else if (i == 2) {
                        if (fields[i].length() == 0) { // Validate user emailcontact (not empty and valid email)
                            throw new ImportException(
                                    "Error reading line from file. User emailcontact cannot be empty. Line: " + lineNumber,
                                    "error.users.import.file.csv.exception.read.email.empty",
                                    lineNumber, "", ImportException.Type.READ);
                        }
                        String emailContact = "";
                        try {
                            InternetAddress address = new InternetAddress(fields[i]);
                            emailContact = address.getAddress();
                            if (!emailContact.contains("@")) {
                                throw new AddressException();
                            }
                        } catch (AddressException e) {
                            throw new ImportException(
                                    "Error reading line from file. User emailcontact is malformed. Line: " + lineNumber,
                                    "error.users.import.file.csv.exception.parse.email.malformed",
                                    lineNumber, fields[i], fields[i], ImportException.Type.PARSE);
                        }
                        attributes[i] = emailContact;
                    } else if (i == 3) {
                        if (fields[i].length() == 0) {// Validate user fiscal number (not empty)
                            throw new ImportException(
                                    "Error reading line from file. User fiscal number cannot be empty. Line: "
                                            + lineNumber,
                                    "error.users.import.file.csv.exception.read.fiscal.empty",
                                    lineNumber, "",
                                    ImportException.Type.READ);

                        }
                        try {
                            attributes[i] = new Long(fields[i]);
                        } catch (NumberFormatException e) {
                            throw new ImportException(
                                    "Error reading line from file. Cannot parse number field. Line: " + lineNumber,
                                    "error.users.import.file.csv.exception.parse.fiscal.malformed",
                                    lineNumber, email, fields[i], ImportException.Type.PARSE);
                        }
                    } else if (i == 6 || i == 7 || i == 8) {
                        try {
                            attributes[i] = new Boolean(fields[i]);
                        } catch (NumberFormatException e) {
                            throw new ImportException(
                                    "Error reading line from file. Cannot parse boolean field. Line: " + lineNumber,
                                    "error.users.import.file.csv.exception.parse.boolean.malformed" + i,
                                    lineNumber, email, fields[i], ImportException.Type.PARSE);
                        }
                    } else if (i >= 9) {
                        String aux = fields[i];
                        if (aux.length() > 0) {
                            if (aux.contains("#")) {
                                throw new ImportException(
                                        "Error reading line from file. Invalid line. Line: " + lineNumber,
                                        "error.users.import.file.csv.exception.read", lineNumber, "",
                                        ImportException.Type.READ);
                            }
                            String[] list = aux.split(";");
                            //Roles
                            if (i == 9) {
                                attributes[i] = list;
                            } else {//contracts
                                Object[] contract = new Object[list.length];
                                try {
                                    contract[0] = new Long(list[0]);
                                    if (list.length > 1) {
                                        try {
                                            contract[1] = df.parse(list[1]);
                                        } catch (ParseException e) {
                                            contract[1] = null;
                                        }
                                        try {
                                            contract[2] = df.parse(list[2]);
                                        } catch (ParseException e) {
                                            contract[2] = null;
                                        }
                                    }
                                    if (list.length > 3) {
                                        for (int z = 3; z < list.length; z++) {
                                            contract[z] = list[z];
                                        }
                                    }
                                } catch (Exception e) {
                                    throw new ImportException(
                                            "Error reading line from file. Cannot parse contract fields. Line: "
                                                    + lineNumber,
                                            "error.users.import.file.csv.exception.parse.contract.malformed",
                                            lineNumber, email, "", ImportException.Type.PARSE);
                                }
                                attributes[i] = contract;
                            }
                        }
                    } else {
                        attributes[i] = fields[i];
                    }
                }
                // check if user has the permission peimanager and is not a clientpeimanager
                if (attributes != null && attributes.length > 9) {
                    boolean clientpeimanager = false;
                    if (attributes[9] != null) {
                        String[] roles = (String[]) attributes[9];
                        for (String role : roles) {
                            if (role.equalsIgnoreCase("clientpeimanager")) {
                                clientpeimanager = true;
                            }
                        }
                    }

                    // if user isn't clientpeimanager, he can't have the permission pei manager
                    if (!clientpeimanager) {
                        String permission = "";
                        Object[] contract;
                        for (int i = 10; i < attributes.length; i++) {
                            contract = (Object[]) attributes[i];
                            if (contract != null && contract.length > 3) {
                                for (int j = 3; j < contract.length; j++) {
                                    permission = (String) contract[j];
                                    if (permission
                                            .equalsIgnoreCase(
                                                    ConfigurationProperties.PERMISSION_PEI_MANAGER.getKey())) {
                                        throw new ImportException(
                                                "Error reading line from file. User has permission peimanager withouth "
                                                        + "the role clientpeimanager. Line: " + lineNumber,
                                                "error.users.import.file.csv.exception.parse.clientpeimanagerpermission.missing",
                                                lineNumber, email, "", ImportException.Type.PARSE);
                                    }
                                }
                            }
                        }
                    }
                }
                users.put(email, attributes);
                lineNumber++;
            }
        } catch (IOException e) {
            throw new ImportException("Error while reading csv file", "error.users.import.file.csv.exception.read.io",
                    lineNumber, "", ImportException.Type.READ);
        } finally {
            try {
                worker.close();
            } catch (IOException e) {
                throw new ImportException("Error while reading csv file",
                        "error.users.import.file.csv.exception.read.io", lineNumber, "", ImportException.Type.READ);
            }
        }
        return users;
    }


}