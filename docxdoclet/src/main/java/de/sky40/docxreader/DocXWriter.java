package de.sky40.docxreader;

import de.sky40.docxreader.domain.Style;
import de.sky40.docxreader.domain.StyleName;
import de.sky40.docxreader.domain.DocXReaderResult;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.Br;
import org.docx4j.wml.CTBorder;
import org.docx4j.wml.ObjectFactory;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase;
import org.docx4j.wml.R;
import org.docx4j.wml.RPr;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.Text;

/**
 * A class to write into docX documents.
 *
 * @author Hendrik Stilke <Hendrik.Stilke@sky40.de>
 */
public class DocXWriter {

  /**
   * The WML object creating factory.
   */
  private final ObjectFactory factory;

  /**
   * The name of the file to write to.
   */
  private final String fileName;

  /**
   * The main document part to process.
   */
  private final MainDocumentPart mainDoc;

  /**
   * Runs to be written to a paragraph.
   */
  private final ArrayList<Object> pendingRuns = new ArrayList<>();

  /**
   * The result of the template reading process.
   */
  private final DocXReaderResult reader;

  /**
   * The package representing object.
   */
  private final WordprocessingMLPackage packageML;

  /**
   * Create a new writer with styles in the ReaderResult.
   *
   * @param readerResult The result of the template reading process (styles
   * mainly).
   * @param fileName The file name of teh file to write to.
   */
  public DocXWriter(DocXReaderResult readerResult, String fileName) {
    this.factory = Context.getWmlObjectFactory();
    this.reader = readerResult;
    this.mainDoc = readerResult.getMainDoc();
    this.packageML = readerResult.getPackageML();

    this.fileName = fileName;
  }

  /**
   * Add a line break in the current paragraph.
   */
  public void addLineBreak() {
    Br linebreak = factory.createBr();
    pendingRuns.add(linebreak);
  }

  /**
   * Adds a pagebreak to current paragraph.
   */
  public void addPageBreak() {
    Br pageBreak = factory.createBr();
    pageBreak.setType(STBrType.PAGE);
    pendingRuns.add(pageBreak);
  }

  /**
   * Add an unstyled paragraph with text to the document.
   *
   * @param text The text inside the paragraph.
   */
  public void addParagraph(String text) {
    addStyledParagraph(null, text);
  }

  /**
   * Closes the current paragraph if any exists.
   */
  public void closeParagraph() {
    flushRunQueue();
  }

  /**
   * Add a paragraph with style to the document and inserts the text into the
   * only run of the paragraph. (Closes the previous paragraph).
   *
   * @param style The style to use.
   * @param text The text inside the paragraph.
   */
  public void addStyledParagraph(Style style, String text) {
    flushRunQueue();
    mainDoc.getContent().add(createStyledParagraph(style, text));
  }

  /**
   * Adds a run to the current (pending) paragraph.
   *
   * @param text text of paragraph
   */
  public void addRun(String text) {
    pendingRuns.add(createStyledRun(null, text));
  }

  /**
   * Adds a styled run to the current (pending) paragraph.
   *
   * @param style the style
   * @param text text of paragraph
   */
  public void addStyledRun(Style style, String text) {
    pendingRuns.add(createStyledRun(style, text));
  }

  /**
   * Create a styled run with text.
   *
   * @param style The style to use
   * @param text The text to add.
   * @return the created run element.
   */
  public R createStyledRun(Style style, String text) {
    R newRun = factory.createR();
    Text newText = factory.createText();
    newText.setValue(text);
    newText.setSpace("preserve");
    newRun.getContent().add(newText);
    if (style != null) {
      RPr runStyle = style.getRunStyle();
      newRun.setRPr(runStyle);
    }
    return newRun;
  }

  /**
   * Find style with name "name".
   *
   * @param name The name to find.
   * @return The found style or null if not found.
   */
  public Style findStyle(StyleName name) {
    return reader.findStyle(name);
  }

  /**
   * Find style with name "name".
   *
   * @param name The name to find.
   * @return The found style or null if not found.
   */
  public Style findStyle(String name) {
    return reader.findStyle(name);
  }

  /**
   * Flushes the queue of pending runs to be written to the current open paragraph.
   */
  private void flushRunQueue() {
    if (!pendingRuns.isEmpty()) {
      P paragraph = createStyledParagraph(null, pendingRuns);
      mainDoc.getContent().add(paragraph);
      pendingRuns.clear();
    }
  }

  /**
   * Writes the docx file to disk.
   *
   * @throws Docx4JException
   */
  public void write() throws Docx4JException {
    File exportFile = new File(fileName);
    packageML.setName(fileName);
    packageML.save(exportFile);
  }

  /**
   * Creates a new paragraph with given style attached to the paragrpah and text
   * in a single run.
   *
   * @param style The style to use.
   * @param text The text of the paragraph (text is created in one run)
   * @return Returns a style paragraph. If style is NULL, returns an unstyled
   * paragraph.
   */
  public P createStyledParagraph(Style style, String text) {
    R newRun = createStyledRun(null, text);

    return createStyledParagraph(style, newRun);
  }

  /**
   * Creates a new paragraph with given style and runs.
   *
   * @param style The style to use.
   * @param runs The runs to add to the paragraph, if any
   * @return Returns a style paragraph. If style is NULL, returns an unstyled
   * paragraph.
   */
  public P createStyledParagraph(Style style, Object... runs) {
    return createStyledParagraph(style, Arrays.asList((Object[]) runs));
  }

  /**
   * Creates a new paragraph with given style and runs.
   *
   * @param style The style to use.
   * @param runs The runs/objects to add to the paragraph, if any
   * @return Returns a style paragraph. If style is NULL, returns an unstyled
   * paragraph.
   */
  public P createStyledParagraph(Style style, Collection<Object> runs) {
    P paragraph = factory.createP();

    if (style != null) {
      PPr paragraphProps = factory.createPPr();
      PPrBase.PStyle paragraphStyle = factory.createPPrBasePStyle();
      paragraphStyle.setVal(style.getStyleId());
      paragraphProps.setPStyle(paragraphStyle);
      paragraph.setPPr(paragraphProps);
    }
    if (runs != null) {
      paragraph.getContent().addAll(runs);
    }

    return paragraph;
  }

  /**
   * Creates a an empty paragraph with given style.
   *
   * @param style The Id of the style to use.
   * @return Returns a styled paragraph without runs. If style is NULL, returns
   * an unstyled paragraph.
   */
  public P createStyledParagraph(Style style) {

    return createStyledParagraph(style, (Object) null);
  }

  /**
   * Adds a horizontal line.
   */
  public void addHorizontalLine() {
    P p = factory.createP();
    PPr pPr = factory.createPPr();
    p.setPPr(pPr);
    PPrBase.PBdr bdr = factory.createPPrBasePBdr();
    CTBorder bottom = factory.createCTBorder();
    pPr.setPBdr(bdr);
    bdr.setBottom(bottom);
    bottom.setVal(org.docx4j.wml.STBorder.SINGLE);
    bottom.setSz(new java.math.BigInteger("6")); //yuck
    bottom.setSpace(new java.math.BigInteger("1"));
    bottom.setColor("auto");
    flushRunQueue();
    mainDoc.getContent().add(p);
  }
}
