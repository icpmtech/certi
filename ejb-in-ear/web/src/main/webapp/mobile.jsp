<%@ page import="java.io.BufferedWriter" %>
<%@ page import="java.io.FileWriter" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.TreeMap" %>
<%@ page import="java.util.Iterator" %>

<%
    /*
    DISABLED as this is not needed anymore

    try {
        BufferedWriter bw = new BufferedWriter(new FileWriter("mobile_log.txt", true));
        bw.write(request.getHeader("user-agent"));
        bw.newLine();

        bw.close();
    } catch (IOException e) {
        e.printStackTrace();
    }


    Enumeration enames;
    Map map;
    String title;

    // Print the request headers

    map = new TreeMap();
    enames = request.getHeaderNames();
    while (enames.hasMoreElements()) {
        String name = (String) enames.nextElement();
        String value = request.getHeader(name);
        map.put(name, value);
    }
    out.println(createTable(map, "Request Headers"));

    // Print the session attributes

    map = new TreeMap();
    enames = session.getAttributeNames();
    while (enames.hasMoreElements()) {
        String name = (String) enames.nextElement();
        String value = "" + session.getAttribute(name);
        map.put(name, value);
    }
    out.println(createTable(map, "Session Attributes"));
    */
%>

<%-- Define a method to create an HTML table --%>

<%!
    private static String createTable(Map map, String title) {
        StringBuffer sb = new StringBuffer();

        // Generate the header lines

        sb.append("<table border='1' cellpadding='3'>");
        sb.append("<tr>");
        sb.append("<th colspan='2'>");
        sb.append(title);
        sb.append("</th>");
        sb.append("</tr>");

        // Generate the table rows

        Iterator imap = map.entrySet().iterator();
        while (imap.hasNext()) {
            Map.Entry entry = (Map.Entry) imap.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            sb.append("<tr>");
            sb.append("<td>");
            sb.append(key);
            sb.append("</td>");
            sb.append("<td>");
            sb.append(value);
            sb.append("</td>");
            sb.append("</tr>");
        }

        // Generate the footer lines

        sb.append("</table><p></p>");

        // Return the generated HTML

        return sb.toString();
    }
%>