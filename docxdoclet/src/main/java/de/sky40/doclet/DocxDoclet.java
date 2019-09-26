package de.sky40.doclet;

import com.sun.javadoc.Doclet;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;
import de.sky40.docxreader.DocXReader;
import de.sky40.docxreader.domain.DocXReaderResult;
import de.sky40.docxreader.DocXWriter;
import java.io.File;

/**
 * Creates Microsoft Word docx files from JavaDocs and uses a custom template
 * file for styling.
 */
public class DocxDoclet extends Doclet implements Runnable {

  private static DocxDoclet theDoclet;
  private BuilderOptions builderOptions;

  private final RootDoc rootDoc;

  private DocxDoclet(RootDoc rootDoc) {
    this.rootDoc = rootDoc;
  }

  /**
   * Indicates if the last run was successfull. This is set in the run() method.
   */
  private boolean successfull = false;

  public boolean isSuccessfull() {
    return successfull;
  }

  @Override
  public void run() {

    try {
      // read options first
      this.builderOptions = new BuilderOptions(rootDoc.options());
      System.out.println("Running doclet with options:");
      for (String[] opt : this.builderOptions.getOptions()) {
        for (String o : opt) {
          System.out.print(o + " ");
        }
        System.out.println();
      }

      // get template file location from doclet options
      DocXReader templateReader = new DocXReader();
      String templateFileName = this.builderOptions.getOption(BuilderOptions.OPTION_TEMPLATE_FILENAME, BuilderOptions.OPTION_DEFAULT_TEMPLATE_FILENAME); 
      File f = new File(templateFileName);

      // read in template and create a writer 
      DocXReaderResult readerResult = templateReader.read(f);
      String outputFileName = this.builderOptions.getOption(BuilderOptions.OPTION_OUTPUT_FILENAME, BuilderOptions.OPTION_DEFAULT_OUTPUT_FILENAME);
      DocXWriter writer = new DocXWriter(readerResult, outputFileName);

      DocumentBuilder docBuilder = new DocumentBuilder(builderOptions, writer);

      docBuilder.create(rootDoc);
    } catch (Exception e) {
      this.successfull = false;
      return;
    }
    this.successfull = true;
  }

  /**
   * The entry point into the JavaDocs creation.
   *
   * This method is required for all inherited classes of
   * {@link com.sun.javadoc.Doclet}
   *
   * @param rootDoc the JavaDocs root node.
   * @return true on success.
   */
  public static boolean start(RootDoc rootDoc) {
    System.out.println("DocXdoclet started.");

    theDoclet = new DocxDoclet(rootDoc);
    theDoclet.run();

    return theDoclet.isSuccessfull();
  }

  /**
   * This method is required for inherited classes of
   * {@link com.sun.javadoc.Doclet}
   *
   * @param option
   * @return
   */
  public static int optionLength(String option) {
    System.out.println("checking availability on option " + option);

    if (BuilderOptions.isSupportedOption(option)) {
      return 2;
    }
    return 0;
  }

  /**
   * This method is required for inherited classes of
   * {@link com.sun.javadoc.Doclet}
   *
   * @return supported JavaDocs language version
   */
  public static LanguageVersion languageVersion() {
    return LanguageVersion.JAVA_1_5;
  }
}
