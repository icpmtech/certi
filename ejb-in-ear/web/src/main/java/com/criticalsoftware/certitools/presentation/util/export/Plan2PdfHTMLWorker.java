/*
 * $Id: Plan2PdfHTMLWorker.java,v 1.7 2010/06/23 17:09:54 pjfsilva Exp $
 *
 * Copyright (c) Critical Software S.A., All Rights Reserved.
 * (www.criticalsoftware.com)
 *
 * This software is the proprietary information of Critical Software S.A.
 * Use is subject to license terms.
 *
 * Last changed on : $Date: 2010/06/23 17:09:54 $
 * Last changed by : $Author: pjfsilva $
 */
package com.criticalsoftware.certitools.presentation.util.export;

import com.criticalsoftware.certitools.entities.jcr.Folder;
import com.criticalsoftware.certitools.entities.jcr.Template;
import com.criticalsoftware.certitools.util.Logger;
import com.lowagie.text.Chunk;
import com.lowagie.text.DocListener;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ElementTags;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.FontFactoryImp;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.TextElementArray;
import com.lowagie.text.html.HtmlTags;
import com.lowagie.text.html.Markup;
import com.lowagie.text.html.simpleparser.ALink;
import com.lowagie.text.html.simpleparser.ChainedProperties;
import com.lowagie.text.html.simpleparser.FactoryProperties;
import com.lowagie.text.html.simpleparser.ImageProvider;
import com.lowagie.text.html.simpleparser.Img;
import com.lowagie.text.html.simpleparser.IncCell;
import com.lowagie.text.html.simpleparser.IncTable;
import com.lowagie.text.html.simpleparser.StyleSheet;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfFileSpecification;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.lowagie.text.xml.simpleparser.SimpleXMLDocHandler;
import com.lowagie.text.xml.simpleparser.SimpleXMLParser;
import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * This class parses the HTML and return a list of PDF elements This class has some parts of code from the default
 * parser
 *
 * @author : lt-rico
 */
@SuppressWarnings({"unchecked"})
public class Plan2PdfHTMLWorker implements SimpleXMLDocHandler, DocListener {

    Logger LOG = Logger.getInstance(Plan2PdfHTMLWorker.class);

    protected java.util.List<Folder> folders;

    protected PdfWriter writer;

    protected ArrayList objectList;

    protected DocListener document;

    private Paragraph currentParagraph;

    private ChainedProperties cprops = new ChainedProperties();

    private Stack stack = new Stack();

    private boolean pendingTR = false;

    private boolean pendingTD = false;

    private boolean pendingLI = false;

    private StyleSheet style = new StyleSheet();

    private boolean isPRE = false;

    private Stack tableState = new Stack();

    private boolean skipText = false;

    private HashMap interfaceProps;

    private FactoryProperties factoryProperties = new FactoryProperties();

    boolean firstTime = true;

    private boolean isPDF;

    /**
     * Creates a new instance of HTMLWorker
     *
     * @param document A class that implements <CODE>DocListener</CODE>
     */
    public Plan2PdfHTMLWorker(DocListener document, boolean isPDF) {
        this.document = document;
        this.isPDF = isPDF;
    }

    public void setStyleSheet(StyleSheet style) {
        this.style = style;
    }

    public StyleSheet getStyleSheet() {
        return style;
    }

    public void setInterfaceProps(HashMap interfaceProps) {
        this.interfaceProps = interfaceProps;
        FontFactoryImp ff = null;
        if (interfaceProps != null) {
            ff = (FontFactoryImp) interfaceProps.get("font_factory");
        }
        if (ff != null) {
            factoryProperties.setFontImp(ff);
        }
    }

    public HashMap getInterfaceProps() {
        return interfaceProps;
    }

    public void parse(Reader reader) throws IOException {
        SimpleXMLParser.parse(this, null, reader, true);
    }

    public static ArrayList parseToList(PdfWriter writer, Reader reader, StyleSheet style,
                                        java.util.List<Folder> resources, boolean isPDF)
            throws IOException {
        return parseToList(writer, reader, style, null, resources, isPDF);
    }

    public static ArrayList parseToList(PdfWriter writer, Reader reader, StyleSheet style, HashMap interfaceProps,
                                        java.util.List<Folder> resources, boolean isPDF) throws IOException {
        Plan2PdfHTMLWorker worker = new Plan2PdfHTMLWorker(null, isPDF);
        worker.folders = resources;
        worker.writer = writer;
        if (style != null) {
            worker.style = style;
        }
        worker.document = worker;
        worker.setInterfaceProps(interfaceProps);
        worker.objectList = new ArrayList();
        worker.parse(reader);
        return worker.objectList;
    }

    public void endDocument() {
        try {
            for (int k = 0; k < stack.size(); ++k) {
                document.add((Element) stack.elementAt(k));
            }
            if (currentParagraph != null) {
                document.add(currentParagraph);
            }
            currentParagraph = null;
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public void startDocument() {
        HashMap h = new HashMap();
        style.applyStyle("body", h);
        cprops.addToChain("body", h);
    }

    public void startElement(String tag, HashMap h) {
        if (!tagsSupported.containsKey(tag)) {
            return;
        }
        try {
            style.applyStyle(tag, h);
            String follow = (String) FactoryProperties.followTags.get(tag);
            if (follow != null) {
                HashMap prop = new HashMap();
                prop.put(follow, null);
                cprops.addToChain(follow, prop);
                return;
            }
            FactoryProperties.insertStyle(h, cprops);
            if (tag.equals(HtmlTags.ANCHOR)) {
                cprops.addToChain(tag, h);

                if (currentParagraph == null) {
                    currentParagraph = new Paragraph();
                }
                stack.push(currentParagraph);
                currentParagraph = new Paragraph();
                return;
            }
            if (tag.equals(HtmlTags.NEWLINE)) {
                if (currentParagraph == null) {
                    currentParagraph = new Paragraph();
                }
                currentParagraph.add(factoryProperties
                        .createChunk("\n", cprops));
                return;
            }
            if (tag.equals(HtmlTags.HORIZONTALRULE)) {
                // Attempting to duplicate the behavior seen on Firefox with
                // http://www.w3schools.com/tags/tryit.asp?filename=tryhtml_hr_test
                // where an initial break is only inserted when the preceding element doesn't
                // end with a break, but a trailing break is always inserted.
                boolean addLeadingBreak = true;
                if (currentParagraph == null) {
                    currentParagraph = new Paragraph();
                    addLeadingBreak = false;
                }
                if (addLeadingBreak) { // Not a new paragraph
                    int numChunks = currentParagraph.getChunks().size();
                    if (numChunks == 0 ||
                            ((Chunk) (currentParagraph.getChunks().get(numChunks - 1))).getContent().endsWith("\n")) {
                        addLeadingBreak = false;
                    }
                }
                String align = (String) h.get("align");
                int hrAlign = Element.ALIGN_CENTER;
                if (align != null) {
                    if (align.equalsIgnoreCase("left")) {
                        hrAlign = Element.ALIGN_LEFT;
                    }
                    if (align.equalsIgnoreCase("right")) {
                        hrAlign = Element.ALIGN_RIGHT;
                    }
                }
                String width = (String) h.get("width");
                float hrWidth = 1;
                if (width != null) {
                    float tmpWidth = Markup.parseLength(width, Markup.DEFAULT_FONT_SIZE);
                    if (tmpWidth > 0) {
                        hrWidth = tmpWidth;
                    }
                    if (!width.endsWith("%")) {
                        hrWidth = 100; // Treat a pixel width as 100% for now.
                    }
                }
                String size = (String) h.get("size");
                float hrSize = 1;
                if (size != null) {
                    float tmpSize = Markup.parseLength(size, Markup.DEFAULT_FONT_SIZE);
                    if (tmpSize > 0) {
                        hrSize = tmpSize;
                    }
                }
                if (addLeadingBreak) {
                    currentParagraph.add(Chunk.NEWLINE);
                }
                currentParagraph
                        .add(new LineSeparator(hrSize, hrWidth, null, hrAlign, currentParagraph.getLeading() / 2));
                currentParagraph.add(Chunk.NEWLINE);
                return;
            }
            if (tag.equals(HtmlTags.CHUNK) || tag.equals(HtmlTags.SPAN)) {

                if (h.containsKey("style")) {
                    String size = null;
                    float actualFontSize =
                            Markup.parseLength(cprops.getProperty(ElementTags.SIZE), Markup.DEFAULT_FONT_SIZE);
                    if (actualFontSize <= 0f) {
                        actualFontSize = Markup.DEFAULT_FONT_SIZE;
                    }
                    StringTokenizer st = new StringTokenizer((String) h.get("style"), ";");
                    while (st.hasMoreTokens()) {
                        String style = st.nextToken().trim();
                        if (style.equals("font-size: smaller")) {
                            size = "" + Markup.parseLength("8", actualFontSize);
                        } else if (style.equals("font-size: x-small")) {
                            size = "" + Markup.parseLength("9", actualFontSize);
                        } else if (style.equals("font-size: xx-small")) {
                            size = "" + Markup.parseLength("10", actualFontSize);
                        } else if (style.equals("font-size: small")) {
                            size = "" + Markup.parseLength("11", actualFontSize);
                        } else if (style.equals("font-size: medium")) {
                            size = "" + Markup.parseLength("12", actualFontSize);
                        } else if (style.equals("font-size: larger")) {
                            size = "" + Markup.parseLength("13", actualFontSize);
                        } else if (style.equals("font-size: large")) {
                            size = "" + Markup.parseLength("14", actualFontSize);
                        } else if (style.equals("font-size: x-large")) {
                            size = "" + Markup.parseLength("15", actualFontSize);
                        } else if (style.equals("font-size: xx-large")) {
                            size = "" + Markup.parseLength("16", actualFontSize);
                        }
                        if (size != null) {
                            h.put("size", size);
                        }
                    }
                }

                cprops.addToChain(tag, h);
                return;
            }

            if (tag.equals(HtmlTags.IMAGE)) {
                String src = (String) h.get(ElementTags.SRC);
                if (src == null) {
                    return;
                }
                src = URLDecoder.decode(src, "UTF-8");
                cprops.addToChain(tag, h);
                Image img = null;
                if (interfaceProps != null) {
                    ImageProvider ip = (ImageProvider) interfaceProps.get("img_provider");
                    if (ip != null) {
                        img = ip.getImage(src, h, cprops, document);
                    }
                    if (img == null) {
                        HashMap images = (HashMap) interfaceProps.get("img_static");
                        if (images != null) {
                            Image tim = (Image) images.get(src);
                            if (tim != null) {
                                img = Image.getInstance(tim);
                            }
                        } else {
                            if (!src.startsWith("http")) { // relative src references only
                                String baseurl = (String) interfaceProps.get("img_baseurl");
                                if (baseurl != null) {
                                    src = baseurl + src;
                                    img = Image.getInstance(src);
                                }
                            }
                        }
                    }
                }
                if (img == null) {
                    if (src.contains("path=")) {
                        //Plan2PdfUtil.findFolderFromPEIList(src.substring(src.indexOf("path=") + 5));
                        //src = src.substring(src.lastIndexOf("path=") + 5, src.length());
                        //src = src.substring(src.lastIndexOf("/") + 1, src.length());
                        src = src.substring(src.indexOf("path=") + 5);
                        img = PlanExportUtil.getImage(Plan2PdfUtil.findImage(src));
                    } else {
                        if (!src.startsWith("http") && !src.startsWith("https")) {
                            String path = cprops.getProperty("image_path");
                            if (path == null) {
                                path = "";
                            }
                            src = new File(path, src).getPath();
                        }
                        try {
                            img = Image.getInstance(src);
                        } catch (Exception e) {
                            LOG.error(e);
                            return;
                        }
                    }
                }
                if (img != null) {
                    String align = (String) h.get("align");
                    if (align == null) {
                        align = "middle";
                    }
                    String width = (String) h.get("width");
                    if (width == null) {
                        width = "" + img.getWidth();
                    }
                    String height = (String) h.get("height");
                    if (height == null) {
                        height = "" + img.getHeight();
                    }
                    String before = cprops.getProperty("before");
                    String after = cprops.getProperty("after");
                    if (before != null) {
                        img.setSpacingBefore(Float.parseFloat(before));
                    }
                    if (after != null) {
                        img.setSpacingAfter(Float.parseFloat(after));
                    }
                    float actualFontSize =
                            Markup.parseLength(cprops.getProperty(ElementTags.SIZE), Markup.DEFAULT_FONT_SIZE);
                    if (actualFontSize <= 0f) {
                        actualFontSize = Markup.DEFAULT_FONT_SIZE;
                    }
                    float widthInPoints = Markup.parseLength(width, actualFontSize);
                    float heightInPoints = Markup.parseLength(height, actualFontSize);
                    Plan2PdfUtil.resizeImage(img, actualFontSize, widthInPoints, heightInPoints);
                    img.setWidthPercentage(0);
                    if (align != null) {
                        endElement("p");
                        int ralign = Image.MIDDLE;
                        if (align.equalsIgnoreCase("left")) {
                            ralign = Image.LEFT;
                        } else if (align.equalsIgnoreCase("right")) {
                            ralign = Image.RIGHT;
                        }
                        img.setAlignment(ralign);
                        Img i;
                        boolean skip = false;
                        if (interfaceProps != null) {
                            i = (Img) interfaceProps.get("img_interface");
                            if (i != null) {
                                skip = i.process(img, h, cprops, document);
                            }
                        }
                        if (!skip) {
                            document.add(img);
                        }
                        cprops.removeChain(tag);
                    } else {
                        img.setAlignment(Image.ALIGN_MIDDLE);
                        //img.set
                        document.add(img);
                        cprops.removeChain(tag);
                    }
                }
                return;
            }
            endElement("p");
            if (tag.equals("area")) {

                //For TEMPLATE_PLAN_CLICKABLE do not show image area comments
                if (folders != null && !folders.isEmpty()) {
                    if (!folders.get(0).getTemplate().getName()
                            .equals(Template.Type.TEMPLATE_PLAN_CLICKABLE.getName())) {

                        String title = (String) h.get(ElementTags.TITLE);
                        if (title != null) {
                            if (currentParagraph == null) {
                                currentParagraph = new Paragraph(new Chunk(Chunk.NEWLINE));
                            } else {
                                currentParagraph.add(new Chunk(Chunk.NEWLINE));
                            }
                            Font font = Plan2PdfUtil.getFontSmaller();
                            font.setStyle(Font.BOLD);
                            if (firstTime) {
                                currentParagraph.add(new Phrase((String) interfaceProps.get("message"), font));
                                currentParagraph.add(new Chunk(Chunk.NEWLINE));
                                firstTime = false;
                            }

                            Chunk titleChunk = new Chunk(title, Plan2PdfUtil.getFontSmaller());
                            String href = (String) h.get("href");
                            if (href != null && href.contains("path=")) {
                                href = URLDecoder.decode(href, "UTF-8");
                                String file = href.substring(href.lastIndexOf("path=/") + 6, href.length())
                                        .replaceAll("/folders", "").replaceAll("/", "_");
                                String text = " [resources\\" + file + "] ";
                                Chunk pathToFileChunk = new Chunk(text, Plan2PdfUtil.getFontSmaller());
                                // create pdf links to attachments
                                if (writer != null) {
                                    PdfAction action = new PdfAction();
                                    action.put(PdfName.S, PdfName.LAUNCH);
                                    PdfFileSpecification pfs =
                                            PdfFileSpecification.fileExtern(writer, "resources\\" + file);
                                    action.put(PdfName.F, pfs.getReference());
                                    pathToFileChunk.setAction(action);
                                }
                                currentParagraph.add(titleChunk);
                                currentParagraph.add(new Chunk(" -- ", Plan2PdfUtil.getFontSmaller()));
                                currentParagraph.add(pathToFileChunk);
                            } else if (href != null && (href.startsWith("http") || href.startsWith("https"))) {
                                Chunk hrefChunk = new Chunk(href, Plan2PdfUtil.getFontSmaller());
                                if (writer != null) {
                                    hrefChunk.setAnchor(href);
                                }
                                currentParagraph
                                        .add(new Paragraph(titleChunk + " -- " + hrefChunk,
                                                Plan2PdfUtil.getFontSmaller()));
                            } else {

                                String[] split = title.split("<line_break>");
                                if (split.length > 1) {
                                    title = StringUtils.remove(title, split[0]);
                                    currentParagraph.add(new Chunk(Plan2PdfUtil.escapeAndRemoveHMTLTags(split[0]),
                                            Plan2PdfUtil.getFontBold()));
                                }
                                currentParagraph.add(new Chunk(Plan2PdfUtil.escapeAndRemoveHMTLTags(title),
                                        Plan2PdfUtil.getFontSmaller()));
                            }
                        }
                    }

                }
                return;
            }
            if (tag.equals("h1") || tag.equals("h2") || tag.equals("h3")
                    || tag.equals("h4") || tag.equals("h5") || tag.equals("h6")) {
                if (!h.containsKey(ElementTags.SIZE)) {
                    int v = 7 - Integer.parseInt(tag.substring(1));
                    if (tag.equals("h4") || tag.equals("h5") || tag.equals("h6")){
                        v = 4;
                    }
                    h.put(ElementTags.SIZE, Integer.toString(v));
                }
                cprops.addToChain(tag, h);
                return;
            }
            if (tag.equals(HtmlTags.UNORDEREDLIST)) {
                if (pendingLI) {
                    endElement(HtmlTags.LISTITEM);
                }
                skipText = true;
                cprops.addToChain(tag, h);
                com.lowagie.text.List list = new com.lowagie.text.List(false, 20);
                try {
                    list.setIndentationLeft(new Float(cprops.getProperty("indent")).floatValue());
                } catch (Exception e) {
                    list.setAutoindent(true);
                }
                list.setListSymbol("\u2022");
                stack.push(list);
                return;
            }
            if (tag.equals(HtmlTags.ORDEREDLIST)) {
                if (pendingLI) {
                    endElement(HtmlTags.LISTITEM);
                }
                skipText = true;
                cprops.addToChain(tag, h);
                com.lowagie.text.List list = new com.lowagie.text.List(true, 20);
                try {
                    list.setIndentationLeft(new Float(cprops.getProperty("indent")).floatValue());
                } catch (Exception e) {
                    list.setAutoindent(true);
                }
                stack.push(list);
                return;
            }
            if (tag.equals(HtmlTags.LISTITEM)) {
                if (pendingLI) {
                    endElement(HtmlTags.LISTITEM);
                }
                skipText = false;
                pendingLI = true;
                cprops.addToChain(tag, h);
                ListItem item = FactoryProperties.createListItem(cprops);
                stack.push(item);
                return;
            }
            if (tag.equals(HtmlTags.DIV) || tag.equals(HtmlTags.BODY) || tag.equals(HtmlTags.PARAGRAPH)) {
                cprops.addToChain(tag, h);
                return;
            }
            if (tag.equals(HtmlTags.PRE)) {
                if (!h.containsKey(ElementTags.FACE)) {
                    h.put(ElementTags.FACE, Font.HELVETICA);
                }
                cprops.addToChain(tag, h);
                isPRE = true;
                return;
            }
            if (tag.equals("tr")) {
                if (pendingTR) {
                    endElement("tr");
                }
                skipText = true;
                pendingTR = true;
                cprops.addToChain("tr", h);
                return;
            }
            if (tag.equals("td") || tag.equals("th")) {
                if (pendingTD) {
                    endElement(tag);
                }
                skipText = false;
                pendingTD = true;
                cprops.addToChain("td", h);
                stack.push(new IncCell(tag, cprops));
                return;
            }
            if (tag.equals("table")) {
                cprops.addToChain("table", h);
                IncTable table = new IncTable(h);
                stack.push(table);
                tableState.push(new boolean[]{pendingTR, pendingTD});
                pendingTR = pendingTD = false;
                skipText = true;
                return;
            }
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public void endElement(String tag) {
        if (!tagsSupported.containsKey(tag)) {
            return;
        }
        try {
            String follow = (String) FactoryProperties.followTags.get(tag);
            if (follow != null) {
                cprops.removeChain(follow);
                return;
            }
            if (tag.equals("font") || tag.equals("span")) {
                cprops.removeChain(tag);
                return;
            }
            if (tag.equals("a")) {
                if (currentParagraph == null) {
                    currentParagraph = new Paragraph();
                }
                boolean skip = false;
                if (interfaceProps != null) {
                    ALink i = (ALink) interfaceProps.get("alink_interface");
                    if (i != null) {
                        skip = i.process(currentParagraph, cprops);
                    }
                }
                if (!skip) {
                    String href = cprops.getProperty("href");
                    if (href != null) {
                        if (href.contains("path=")) {
                            String file =
                                    href.substring(href.lastIndexOf("path=/") + 6, href.length())
                                            .replaceAll("/folders", "").replaceAll("/", "_");
                            file = URLDecoder.decode(file, "UTF-8");
                            //String text = " [resources\\" + file + "] ";
                            if (writer != null) {
                                PdfAction action = new PdfAction();
                                action.put(PdfName.S, PdfName.LAUNCH);
                                PdfFileSpecification pfs =
                                        PdfFileSpecification.fileExtern(writer, "resources\\" + file);
                                action.put(PdfName.F, pfs.getReference());
                                ArrayList chunks = currentParagraph.getChunks();
                                int size = chunks.size();
                                for (int k = 0; k < size; ++k) {
                                    Chunk ck = (Chunk) chunks.get(k);
                                    ck.setAction(action);
                                }
                            }
                        } else {
                            ArrayList chunks = currentParagraph.getChunks();
                            int size = chunks.size();
                            for (int k = 0; k < size; ++k) {
                                Chunk ck = (Chunk) chunks.get(k);
                                ck.setAnchor(href);
                            }
                        }
                    }
                }
                Paragraph tmp = (Paragraph) stack.pop();

                if (isPDF) {
                    for (Object chunkObject : currentParagraph.getChunks()) {
                        Chunk chunk = (Chunk) chunkObject;
                        chunk.setFont(
                                FontFactory.getFont(FontFactory.HELVETICA, Markup.DEFAULT_FONT_SIZE, Font.UNDERLINE,
                                        new Color(0, 0, 255)));
                    }
                }
                Phrase tmp2 = new Phrase(currentParagraph);
                tmp.add(tmp2);
                currentParagraph = tmp;
                cprops.removeChain("a");
                return;
            }
            if (tag.equals("br")) {
                return;
            }
            if (currentParagraph != null) {
                if (stack.empty()) {
                    document.add(currentParagraph);
                } else {
                    Object obj = stack.pop();
                    if (obj instanceof TextElementArray) {
                        TextElementArray current = (TextElementArray) obj;
                        current.add(currentParagraph);
                    }
                    stack.push(obj);
                }
            }
            currentParagraph = null;
            if (tag.equals(HtmlTags.UNORDEREDLIST)
                    || tag.equals(HtmlTags.ORDEREDLIST)) {
                if (pendingLI) {
                    endElement(HtmlTags.LISTITEM);
                }
                skipText = false;
                cprops.removeChain(tag);
                if (stack.empty()) {
                    return;
                }
                Object obj = stack.pop();
                if (!(obj instanceof com.lowagie.text.List)) {
                    stack.push(obj);
                    return;
                }
                if (stack.empty()) {
                    document.add((Element) obj);
                } else {
                    ((TextElementArray) stack.peek()).add(obj);
                }
                return;
            }
            if (tag.equals(HtmlTags.LISTITEM)) {
                pendingLI = false;
                skipText = true;
                cprops.removeChain(tag);
                if (stack.empty()) {
                    return;
                }
                Object obj = stack.pop();
                if (!(obj instanceof ListItem)) {
                    stack.push(obj);
                    return;
                }
                if (stack.empty()) {
                    document.add((Element) obj);
                    return;
                }
                Object list = stack.pop();
                if (!(list instanceof com.lowagie.text.List)) {
                    stack.push(list);
                    return;
                }
                ListItem item = (ListItem) obj;
                ((com.lowagie.text.List) list).add(item);
                ArrayList cks = item.getChunks();
                if (!cks.isEmpty()) {
                    item.getListSymbol()
                            .setFont(((Chunk) cks.get(0)).getFont());
                }
                stack.push(list);
                return;
            }
            if (tag.equals("div") || tag.equals("body")) {
                cprops.removeChain(tag);
                return;
            }
            if (tag.equals(HtmlTags.PRE)) {
                cprops.removeChain(tag);
                isPRE = false;
                return;
            }
            if (tag.equals("p")) {
                cprops.removeChain(tag);
                return;
            }
            if (tag.equals("h1") || tag.equals("h2") || tag.equals("h3")
                    || tag.equals("h4") || tag.equals("h5") || tag.equals("h6")) {
                cprops.removeChain(tag);
                return;
            }
            if (tag.equals("table")) {
                if (pendingTR) {
                    endElement("tr");
                }
                cprops.removeChain("table");
                IncTable table = (IncTable) stack.pop();
                PdfPTable tb = table.buildTable();
                tb.setSpacingBefore(6);
                tb.setSpacingAfter(6);
                tb.setSplitRows(true);
                if (stack.empty()) {
                    document.add(tb);
                } else {
                    ((TextElementArray) stack.peek()).add(tb);
                }
                boolean state[] = (boolean[]) tableState.pop();
                pendingTR = state[0];
                pendingTD = state[1];
                skipText = false;
                return;
            }
            if (tag.equals("tr")) {
                if (pendingTD) {
                    endElement("td");
                }
                pendingTR = false;
                cprops.removeChain("tr");
                ArrayList cells = new ArrayList();
                IncTable table;
                while (true) {
                    Object obj = stack.pop();
                    if (obj instanceof IncCell) {
                        cells.add(((IncCell) obj).getCell());
                    }
                    if (obj instanceof IncTable) {
                        table = (IncTable) obj;
                        break;
                    }
                }
                table.addCols(cells);
                table.endRow();
                stack.push(table);
                skipText = true;
                return;
            }
            if (tag.equals("td") || tag.equals("th")) {
                pendingTD = false;
                cprops.removeChain("td");
                skipText = true;
                return;
            }
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public void text(String str) {
        if (skipText) {
            return;
        }
        String content = str;
        if (isPRE) {
            if (currentParagraph == null) {
                currentParagraph = FactoryProperties.createParagraph(cprops);
            }
            Chunk chunk = factoryProperties.createChunk(content, cprops);
            currentParagraph.add(chunk);
            return;
        }
        if (content.trim().length() == 0 && content.indexOf(' ') < 0) {
            return;
        }

        StringBuffer buf = new StringBuffer();
        int len = content.length();
        char character;
        boolean newline = false;
        for (int i = 0; i < len; i++) {
            switch (character = content.charAt(i)) {
                case ' ':
                    if (!newline) {
                        buf.append(character);
                    }
                    break;
                case '\n':
                    if (i > 0) {
                        newline = true;
                        buf.append(' ');
                    }
                    break;
                case '\r':
                    break;
                case '\t':
                    break;
                default:
                    newline = false;
                    buf.append(character);
            }
        }
        if (currentParagraph == null) {
            currentParagraph = FactoryProperties.createParagraph(cprops);
        }
        Chunk chunk = factoryProperties.createChunk(buf.toString(), cprops);
        currentParagraph.add(chunk);
    }

    public boolean add(Element element) throws DocumentException {
        objectList.add(element);
        return true;
    }

    public void clearTextWrap() throws DocumentException {
    }

    public void close() {
    }

    public boolean newPage() {
        return true;
    }

    public void open() {
    }

    public void resetFooter() {
    }

    public void resetHeader() {
    }

    public void resetPageCount() {
    }

    public void setFooter(HeaderFooter footer) {
    }

    public void setHeader(HeaderFooter header) {
    }

    public boolean setMarginMirroring(boolean marginMirroring) {
        return true;
    }

    public boolean setMarginMirroringTopBottom(boolean marginMirroring) {
        return false;
    }

    public boolean setMargins(float marginLeft, float marginRight, float marginTop, float marginBottom) {
        return false;
    }

    public void setPageCount(int pageN) {
    }

    public boolean setPageSize(Rectangle pageSize) {
        return true;
    }

    public static final String tagsSupportedString =
            "ol ul li a pre font span br p div body table td th tr i b u sub sup em strong s strike"
                    + " h1 h2 h3 h4 h5 h6 img hr area";

    public static final HashMap tagsSupported = new HashMap();

    static {
        StringTokenizer tok = new StringTokenizer(tagsSupportedString);
        while (tok.hasMoreTokens()) {
            tagsSupported.put(tok.nextToken(), null);
        }
    }
}

