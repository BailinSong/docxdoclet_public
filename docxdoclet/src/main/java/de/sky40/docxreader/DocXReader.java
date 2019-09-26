package de.sky40.docxreader;

import de.sky40.docxreader.domain.Style;
import de.sky40.docxreader.domain.StyleName;
import de.sky40.docxreader.domain.DocXReaderResult;
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
import org.docx4j.wml.Document;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;

/**
 * Reads docx files and matches styles and paragraphs with text to find members.
 *
 * @author Hendrik Stilke <Hendrik.Stilke@sky40.de>
 */
public class DocXReader {

  /**
   * Extracts the styles from the main document by searching for keywords.
   *
   * @param mainDoc
   * @return
   * @throws Docx4JException
   */
  private Map<StyleName, Style> extractStyles(MainDocumentPart mainDoc) throws Docx4JException {
    Map<StyleName, Style> styleMap = new HashMap<>();
    styleMap.put(StyleName.with(StyleName.PACKAGE), findStyleByText(StyleName.PACKAGE, mainDoc));
    styleMap.put(StyleName.with(StyleName.CLASS), findStyleByText(StyleName.CLASS, mainDoc));
    styleMap.put(StyleName.with(StyleName.METHOD), findStyleByText(StyleName.METHOD, mainDoc));
    styleMap.put(StyleName.with(StyleName.SIGNATURE), findStyleByText(StyleName.SIGNATURE, mainDoc));
    styleMap.put(StyleName.with(StyleName.COMMENT), findStyleByText(StyleName.COMMENT, mainDoc));
    styleMap.put(StyleName.with(StyleName.HEADING), findStyleByText(StyleName.HEADING, mainDoc));
    styleMap.put(StyleName.with(StyleName.MISSING), findStyleByText(StyleName.MISSING, mainDoc));
    return styleMap;
  }

  /**
   * Find the style in a document.
   *
   * @param findText
   * @param doc
   * @return
   * @throws Docx4JException
   */
  private Style findStyleByText(String findText, MainDocumentPart doc) throws Docx4JException {
    Document document = doc.getContents();
    log(document);
    List<Object> contents = document.getContent();
    for (Object content : contents) {
      // log(content.getClass().getCanonicalName());
      if (content instanceof org.docx4j.wml.P) {
        P paragraph = (org.docx4j.wml.P) content;
        Style style = findParagraphStyleByText(findText, paragraph);
        if (style != null) {
          return style;
        }
      }
    }
    return null;
  }

  /**
   * Find the style in a paragraph.
   *
   * @param findText
   * @param paragraph
   * @return
   */
  private Style findParagraphStyleByText(String findText, P paragraph) {
    String paragraphText = paragraph.toString();

    if (paragraphText.toLowerCase().equals(findText)) {
      log("text '" + paragraphText + "' matches template name");

      PPr paragraphProps = paragraph.getPPr();
      PPrBase.PStyle pStyle = paragraphProps.getPStyle();
      if (pStyle != null) {
        String styleId = pStyle.getVal();
        log("paragraph styleId:" + styleId);
        return new Style(styleId);
      } else {
        log("No styleId found. Searching for run properties instead.");
        List<Object> contents = paragraph.getContent();
        for (Object content : contents) {
          if (content instanceof org.docx4j.wml.R) {
            R run = (org.docx4j.wml.R) content;
            log("run found:" + run);
            if (run.getRPr() != null) {
              log("run has style properties:" + run.getRPr());
              RPr runProps = run.getRPr();
              return new Style(runProps);
            }
          }
        }
      }
    }
    return null;
  }

  /**
   * Writes the object to the log.
   *
   * @param obj The object to write.
   */
  private void log(Object obj) {
    System.out.println(obj);
  }

  /**
   * Logs the content of a map.
   *
   * @param map The map to log.
   */
  private void logMap(Map<?, ?> map) {
    for (Object entry : map.keySet()) {
      log(entry);
      log(map.get(entry));
    }
  }

  /**
   * Reads in the file and extract styles.
   *
   * @param file The file handle.
   * @return The styles in a reader result.
   *
   * @throws Docx4JException If an exception occured during file processing.
   */
  public DocXReaderResult read(File file) throws Docx4JException {
    log("Reading docx file.");
    WordprocessingMLPackage processingPackage = Docx4J.load(file);
    Parts parts = processingPackage.getParts();
    String contentType = processingPackage.getContentType();
    log("content type: " + contentType);
    MainDocumentPart mainDoc = processingPackage.getMainDocumentPart();

    Map<StyleName, Style> styleMap = extractStyles(mainDoc);

    log("----------------");
    log("parts found:");
    HashMap<PartName, Part> partsMap = parts.getParts();
    logMap(partsMap);
    log("----------------");

    return new DocXReaderResult(processingPackage, mainDoc, styleMap);
  }

}
