package com.criticalsoftware.certitools.presentation.util.export.sm;

import com.opencsv.CSVWriter;
import com.criticalsoftware.certitools.business.exception.CSVException;
import com.criticalsoftware.certitools.entities.sm.Chat;
import com.criticalsoftware.certitools.util.Configuration;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * ReportCsv
 * Class that creates csv documents for security records.
 *
 * @author miseabra
 * @version $Revision: $
 */
@SuppressWarnings("UnusedDeclaration")
public class ReportCsv {

    private SimpleDateFormat dateHourFormat;
    public static final char SEPARATOR = ';';

    public ReportCsv(Locale l) {
        this.dateHourFormat = new SimpleDateFormat(Configuration.getInstance().getDateHourPattern(), l);
    }

    public ByteArrayOutputStream generateChatCSV(List<Chat> chatMessages) throws CSVException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            //old fix
            //CSVWriter writer = new CSVWriter( new OutputStreamWriter(baos), SEPARATOR);
            //new fix
            CSVWriter writer = new CSVWriter( new OutputStreamWriter(baos));


            for (Chat chat : chatMessages) {
                String[] message = new String[3];
                message[0] = chat.getUser() != null ? chat.getUser().getName() : chat.getEmergencyUser().getName();
                message[1] = dateHourFormat.format(chat.getDatetime());
                message[2] = chat.getMessage();
                writer.writeNext(message);
            }

            writer.close();

        } catch (Exception e) {
            throw new CSVException("Error while creating CSV " + e.getMessage(), e);
        }
        return baos;
    }
}
