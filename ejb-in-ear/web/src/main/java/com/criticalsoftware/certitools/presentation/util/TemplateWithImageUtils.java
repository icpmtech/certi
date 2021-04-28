/*
 * $Id: TemplateWithImageUtils.java,v 1.5 2012/10/12 16:39:03 d-marques Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2012/10/12 16:39:03 $
 * Last changed by : $Author: d-marques $
 */
package com.criticalsoftware.certitools.presentation.util;
import com.criticalsoftware.certitools.util.Logger;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Image;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

/**
 * Description.
 *
 * @author :    Daniel Marques
 * @version :   $Revision: 1.5 $
 */
public class TemplateWithImageUtils {
    private static final Logger LOGGER = Logger.getInstance(TemplateWithImageUtils.class);

    // indentation constant: 2 spaces
    private static final String SINGLE_INDENT = " ";

    public static String getAttribute(Node element, String attName) {
        NamedNodeMap attrs = element.getAttributes();
        if (attrs == null) {
            return null;
        }
        Node attN = attrs.getNamedItem(attName);
        if (attN == null) {
            return null;
        }
        return attN.getNodeValue();
    }

    public static Image getImage(InputStream data) {
        if (data != null) {
            ByteArrayOutputStream out = null;
            try {
                byte[] buf = new byte[data.available()];
                out = new ByteArrayOutputStream();
                int len;
                while ((len = data.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                byte imageData[] = out.toByteArray();
                out.close();
                return Image.getInstance(imageData);
            } catch (BadElementException e) {
                LOGGER.error(e);
            } catch (IOException e) {
                LOGGER.error(e);
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e1) {
                    }
                }
            }
        }
        return null;
    }

    public static void writeNodeToStream(Node node, Writer out) throws IOException {
        serializeNode(node, out, "");
        out.write('\n');
        out.flush();
    }

    public static void writeDocToStream(Document doc, Writer out)
            throws IOException {
        serializeNode(doc, out, "");
        out.write('\n');
        out.flush();
    }

    private static void serializeNode(Node node, Writer out, String indent) throws IOException {
        switch (node.getNodeType()) {
            case Node.DOCUMENT_NODE:
                writeNode(((Document) node).getDocumentElement(), out, indent);
                break;
            case Node.ELEMENT_NODE:
                writeNode(node, out, indent);
                break;
            case Node.TEXT_NODE:
                out.write(node.getNodeValue());
                break;
            case Node.COMMENT_NODE:
                out.write( /*indent + */ "<!--" + node.getNodeValue() + "-->");
                break;
            default:
                System.out.println("Got node: " + node.getNodeName());
                break;
        }
    }

    private static void writeNode(Node node, Writer out, String indent)
            throws IOException {
        out.write(nodeWithAttrs(node, indent));
        NodeList kids = node.getChildNodes();
        if (kids != null) {
            if ((kids.item(0) != null) &&
                    (kids.item(0).getNodeType() == Node.ELEMENT_NODE)) {
                out.write('\n');
            }
            for (int i = 0; i < kids.getLength(); i++) {
                serializeNode(kids.item(i), out, indent + SINGLE_INDENT);
            }
        }
        if (node.hasChildNodes()) {
            out.write("</" + node.getNodeName() + ">");
        }
    }

    private static String nodeWithAttrs(Node node, String indent) {
        StringBuffer sb = new StringBuffer();
        sb.append("<");
        sb.append(node.getNodeName());

        NamedNodeMap attrs = node.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            sb.append(" ");
            Node attrNode = attrs.item(i);
            sb.append(attrNode.getNodeName());
            sb.append("=\"");
            String attrValue = attrNode.getNodeValue();
            sb.append(attrNode.getNodeValue());
            sb.append("\"");
        }

        if (!node.hasChildNodes()) {
            sb.append("/>");
        } else {
            sb.append(">");
        }

        return sb.toString();
    }

    // TODO CERTOOL-537 check if this parsing could be done with HTMLCleaner
    /*
    String html = "<a href=\"linha1\nlinha2\">linha1\nlinha2\nlinha3</a>";

            final CleanerProperties props = new CleanerProperties();
            props.setOmitHtmlEnvelope(true);
            props.setOmitDoctypeDeclaration(true);
            props.setOmitXmlDeclaration(true);
            props.setOmitUnknownTags(false);
            HtmlCleaner cleaner = new HtmlCleaner(props);

            TagNode allNodes = cleaner.clean(html);

            final SimpleHtmlSerializer htmlSerializer =
                new SimpleHtmlSerializer(props);

            try {
                htmlSerializer.writeToFile(allNodes, "ola.txt", "utf-8");
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
     */




    public static String resizeImageMap(String htmlContent, Image oldImage, Image newImage) throws IOException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = null;
            db = dbf.newDocumentBuilder();
            dbf.setValidating(false);
            dbf.setIgnoringElementContentWhitespace(false);
            if (htmlContent != null) {
                int start = htmlContent.indexOf("<map");
                int end = htmlContent.indexOf("</map>") + 6;
                if (start >= 0 && end >= 0) {
                    String goodPart = htmlContent.substring(start, end);
                    goodPart = HTMLEntities.htmlentities(goodPart);
                    goodPart = goodPart.replace("\r\n", "-BREAK-");
                    goodPart = goodPart.replace("\n", "-BREAK-");
                    Document doc = db.parse(new InputSource(new StringReader(goodPart)));
                    NodeList mapList = doc.getElementsByTagName("map");
                    if (mapList != null && mapList.getLength() >= 1) {
                        float newHeight = newImage.getHeight();
                        float newWidth = newImage.getWidth();
                        Node map = mapList.item(0);
                        NodeList areaList = map.getChildNodes();
                        for (int i = 0; i < areaList.getLength(); i++) {
                            Node node = areaList.item(i);
                            if (node.getNodeName().equalsIgnoreCase("area")) {
                                String shape = TemplateWithImageUtils.getAttribute(node, "shape");
                                if (shape.equalsIgnoreCase("rect")) {
                                    TemplateWithImageUtils.calculateNewRectArea(node, oldImage, newImage);
                                } else if (shape.equalsIgnoreCase("circle")) {
                                    TemplateWithImageUtils.calculateNewCircleArea(node, oldImage, newImage);
                                } else if (shape.equalsIgnoreCase("poly")) {
                                    TemplateWithImageUtils.calculateNewPolyArea(node, oldImage, newImage);
                                }
                            }
                        }
                        StringWriter output = new StringWriter();
                        TemplateWithImageUtils.writeDocToStream(doc, output);
                        output.close();
                        String imagePart = output.toString();
                        imagePart = imagePart.replace("-BREAK-", "\r\n");
                        String result = htmlContent.substring(0, start) + imagePart + htmlContent.substring(end);
                        return result;
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            LOGGER.error(e);
            throw new IOException("Error calculating image map!");
        } catch (SAXException e) {
            LOGGER.error(e);
            throw new IOException("Error calculating image map!");
        } catch (IOException e) {
            LOGGER.error(e);
            throw new IOException("Error calculating image map!");
        }
        return null;
    }

    private static void calculateNewRectArea(Node areaNode, Image oldImage, Image newImage) {
        //Obtain the coordinates of the area and calculate the values
        String coordinates = TemplateWithImageUtils.getAttribute(areaNode, "coords");
        if (coordinates == null) {
            return;
        }
        //Coordinates are in the format (X1,Y1,X2,Y2)=(top,left,right,bottom)
        String[] parts = coordinates.split(",");
        if (parts.length != 4) {
            LOGGER.error("Wrong values on attribute coords for area rect! - [" + coordinates + "]");
        } else {
            float newImageWidth = newImage.getWidth();
            float newImageHeight = newImage.getHeight();
            float oldX1 = Float.parseFloat(parts[0].trim());
            float oldY1 = Float.parseFloat(parts[1].trim());
            float oldX2 = Float.parseFloat(parts[2].trim());
            float oldY2 = Float.parseFloat(parts[3].trim());
            float diffX = oldX2 - newImageWidth;
            float diffY = oldY2 - newImageHeight;
            float newX1 = oldX1;
            float newY1 = oldY1;
            float newX2 = oldX2;
            float newY2 = oldY2;
            if (diffX > 0) {
                newX1 = (newX1 - diffX < 0) ? 1 : newX1 - diffX;
                newX2 = (newX2 - diffX < 0) ? 1 : newX2 - diffX;
            }
            if (diffY > 0) {
                newY1 = (newY1 - diffY < 0) ? 1 : newY1 - diffY;
                newY2 = (newY2 - diffY < 0) ? 1 : newY2 - diffY;
            }
            areaNode.getAttributes().getNamedItem("coords")
                    .setNodeValue(new Float(newX1).intValue() + "," +
                            new Float(newY1).intValue() + "," +
                            new Float(newX2).intValue() + "," +
                            new Float(newY2).intValue());
        }
    }

    private static void calculateNewCircleArea(Node areaNode, Image oldImage, Image newImage) {
        //Obtain the coordinates of the area and calculate the values
        String coordinates = TemplateWithImageUtils.getAttribute(areaNode, "coords");
        if (coordinates == null) {
            return;
        }
        //Coordinates are in the format (X0,Y0,radix)
        String[] parts = coordinates.split(",");
        if (parts.length != 3) {
            LOGGER.error("Wrong values on attribute coords for area circle! - [" + coordinates + "]");
        } else {
            float newImageWidth = newImage.getWidth();
            float newImageHeight = newImage.getHeight();
            float oldX = Float.parseFloat(parts[0].trim());
            float oldY = Float.parseFloat(parts[1].trim());
            float oldRadix = Float.parseFloat(parts[2].trim());
            float newRadix = oldRadix;
            float newX = oldX;
            float newY = oldY;
            float halfHeight = newImageHeight / 2;
            float halfWidth = newImageWidth / 2;
            //Reduce the radix to fit in the smallest dimension
            if (oldRadix > halfWidth || oldRadix > halfHeight) {
                newRadix = (newImageWidth > newImageHeight) ? halfHeight : halfWidth;
            }
            float diffX = newRadix + (oldX - newImageWidth);
            float diffY = newRadix + (oldY - newImageHeight);
            if (diffX > 0) {
                newX = oldX - diffX;
            }
            if (diffY > 0) {
                newY = oldY - diffY;
            }
            areaNode.getAttributes().getNamedItem("coords")
                    .setNodeValue(new Float(newX).intValue() + "," +
                            new Float(newY).intValue() + "," +
                            new Float(newRadix).intValue());
        }
    }

    private static void calculateNewPolyArea(Node areaNode, Image oldImage, Image newImage) {
        //Obtain the coordinates of the area and calculate the values
        String coordinates = TemplateWithImageUtils.getAttribute(areaNode, "coords");
        if (coordinates == null) {
            return;
        }
        String[] parts = coordinates.split(",");
        float[] values = new float[parts.length];
        float biggestX = -1f;
        float biggestY = -1f;
        //Coordinates are in pairs (X1,Y1,X2,Y2,...,Xn,Yn)
        for (int pos = 0; pos < values.length; pos += 2) {
            values[pos] = Float.parseFloat(parts[pos].trim());
            values[pos + 1] = Float.parseFloat(parts[pos + 1].trim());
            if (biggestX < values[pos]) {
                biggestX = values[pos];
            }
            if (biggestY < values[pos + 1]) {
                biggestY = values[pos + 1];
            }
        }
        float newImageWidth = newImage.getWidth();
        float newImageHeight = newImage.getHeight();
        //If the biggest coordinate is out of the new image boundaries we resize the whole polygon
        if (biggestX > newImage.getWidth() || biggestY > newImage.getHeight()) {
            float ratioX = newImageWidth / oldImage.getWidth();
            float ratioY = newImageHeight / oldImage.getHeight();
            StringBuilder sb = new StringBuilder();
            for (int pos = 0; pos < values.length; pos += 2) {
                values[pos] = values[pos] * ratioX;
                values[pos + 1] = values[pos + 1] * ratioY;
                sb.append(new Float(values[pos]).intValue()).append(",");
                sb.append(new Float(values[pos + 1]).intValue());
                if (pos + 1 < parts.length - 1) {
                    sb.append(",");
                }
            }
            areaNode.getAttributes().getNamedItem("coords")
                    .setNodeValue(sb.toString());
        }
    }
}