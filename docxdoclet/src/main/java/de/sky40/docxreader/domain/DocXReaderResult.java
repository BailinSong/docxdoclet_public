package de.sky40.docxreader.domain;

import java.util.Map;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;

/**
 * Class contains all necessary information to process a docx (template) file after the read in process.
 *
 * @author Hendrik Stilke <Hendrik.Stilke@sky40.de>
 */
public class DocXReaderResult {

  private final Map<StyleName, Style> styles;

  private final MainDocumentPart mainDoc;
  private final WordprocessingMLPackage packageML;


  public DocXReaderResult(WordprocessingMLPackage packageML, MainDocumentPart mainDoc, Map<StyleName, Style> styles) {
    this.packageML = packageML;
    this.mainDoc = mainDoc;
    this.styles = styles;}

  public Style findStyle(StyleName name) {
    return styles.get(name);
  } 
  
  public Style findStyle(String name) {
    return styles.get(StyleName.with(name));
  }

  public MainDocumentPart getMainDoc() {
    return mainDoc;
  }

  public WordprocessingMLPackage getPackageML() {
    return packageML;
  }

}
