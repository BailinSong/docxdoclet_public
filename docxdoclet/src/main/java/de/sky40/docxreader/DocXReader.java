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

/**
 * Reads docx files and matches with templates.
 *
 * @author Hendrik Stilke <Hendrik.Stilke@sky40.de>
 */
public class DocXReader {

  private void findStyle(String templateName, MainDocumentPart doc) throws Docx4JException {
    Document document = doc.getContents();
    log(document);
    List<Object> contents = document.getContent();
    for (Object content : contents) {
      log(content.getClass().getCanonicalName());
      if (content instanceof org.docx4j.wml.P) {
        P paragraph = (org.docx4j.wml.P) content;
        String text = paragraph.toString();
        log(paragraph.getClass().getCanonicalName());
        log("text:" + text);
        /*log("paraId " + paragraph.getParaId());
        log("RsidDel " + paragraph.getRsidDel());
        log("RsidP " + paragraph.getRsidP());
        log("RsidR " + paragraph.getRsidR());
        log("RsidDefault " + paragraph.getRsidRDefault());
        log("RPr " + paragraph.getRsidRPr());*/
        if (text.toLowerCase().equals(templateName)) {
          List<Object> paragraphContents = paragraph.getContent();
          for (Object pr : paragraphContents) {
            log(pr.getClass().getCanonicalName());
            log(pr);
          }
        }
      }
    }
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
    findStyle("packagetemplate", mainDoc);

    HashMap<PartName, Part> partsMap = parts.getParts();

    StyleDefinitionsPart stylePart = (StyleDefinitionsPart) partsMap.get(new PartName("/word/styles.xml"));
    logMap(partsMap);
    log(contentType);
  }
}
