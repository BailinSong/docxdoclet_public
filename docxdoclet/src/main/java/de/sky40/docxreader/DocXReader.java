package de.sky40.docxreader;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.docx4j.Docx4J;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.Part;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.Parts;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart;
import org.docx4j.wml.Document;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase;
import org.docx4j.wml.Style;

/**
 * Reads docx files and matches with templates.
 *
 * @author Hendrik Stilke <Hendrik.Stilke@sky40.de>
 */
public class DocXReader {

  private String findStyleIdInDocument(String templateName, MainDocumentPart doc) throws Docx4JException {
    Document document = doc.getContents();
    log(document);
    List<Object> contents = document.getContent();
    for (Object content : contents) {
      // log(content.getClass().getCanonicalName());
      if (content instanceof org.docx4j.wml.P) {
        P paragraph = (org.docx4j.wml.P) content;
        String styleId = findStyleIdInParagraph(paragraph, templateName);
        if (styleId != null) {
          return styleId;
        }
      }
    }
    return null;
  }

  private String findStyleIdInParagraph(P paragraph, String templateName) {
    String text = paragraph.toString();
    //log(paragraph.getClass().getCanonicalName());
    /*log("paraId " + paragraph.getParaId());
    log("RsidDel " + paragraph.getRsidDel());
    log("RsidP " + paragraph.getRsidP());
    log("RsidR " + paragraph.getRsidR());
    log("RsidDefault " + paragraph.getRsidRDefault());
    log("RPr " + paragraph.getRsidRPr());*/
    if (text.toLowerCase().equals(templateName)) {
      log("text '" + text + "' matches template name");

      PPr paragraphProps = paragraph.getPPr();
      PPrBase.PStyle pStyle = paragraphProps.getPStyle();
      String styleId = pStyle.getVal();
      log("paragraph styleId:" + styleId);
      List<Object> contents = paragraph.getContent();
      for (Object content : contents) {
        if (content instanceof org.docx4j.wml.P) {
        }
        if (content instanceof org.docx4j.wml.PPr) {
        }
      }
      return styleId;
    }
    return null;
  }

  private void log(Object obj) {
    System.out.println(obj);
  }

  private void logMap(Map<?, ?> map) {
    for (Object entry : map.keySet()) {
      log(entry);
      log(map.get(entry));
    }
  }

  public void read(File f) throws Docx4JException {
    WordprocessingMLPackage processingPackage = Docx4J.load(f);
    Parts parts = processingPackage.getParts();
    String contentType = processingPackage.getContentType();
    MainDocumentPart mainDoc = processingPackage.getMainDocumentPart();
    String packageStyleId = findStyleIdInDocument("packagetemplate", mainDoc);
    String classStyleId = findStyleIdInDocument("classtemplate", mainDoc);
    String methodStyleId = findStyleIdInDocument("methodtemplate", mainDoc);

    HashMap<PartName, Part> partsMap = parts.getParts();

    StyleDefinitionsPart stylePart = (StyleDefinitionsPart) partsMap.get(new PartName("/word/styles.xml"));
    logMap(partsMap);

    Style packageStyle = stylePart.getStyleById(packageStyleId);

    log(packageStyle.toString());

    log(contentType);
  }
}
