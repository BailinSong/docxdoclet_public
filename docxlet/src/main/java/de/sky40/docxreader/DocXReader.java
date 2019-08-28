/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import org.docx4j.openpackaging.parts.opendope.ComponentsPart;
import org.docx4j.wml.Document;
import org.docx4j.wml.Styles;

/**
 * Reads docx files and matches with templates.
 *
 * @author Hendrik Stilke <Hendrik.Stilke@sky40.de>
 */
public class DocXReader {

  private void findStyle(String templateName, MainDocumentPart doc) throws Docx4JException {
    Document document = doc.getContents();
    log(document);
    List<Object> content = document.getContent();
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

    findStyle("package_template", mainDoc);
    
    
    HashMap<PartName, Part> partsMap = parts.getParts();

    logMap(partsMap);
    log(contentType);
  }
}
