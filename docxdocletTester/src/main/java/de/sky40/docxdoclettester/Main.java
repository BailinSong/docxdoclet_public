package de.sky40.docxdoclettester;

import de.sky40.docxdoclettester.foo.Foo;
import de.sky40.doclet.BuilderOptions;
import de.sky40.doclet.DocumentBuilder;
import de.sky40.docxreader.DocXReader;
import de.sky40.docxreader.domain.DocXReaderResult;
import de.sky40.docxreader.DocXWriter;
import java.io.File;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.docx4j.openpackaging.exceptions.Docx4JException;

/**
 * Project to test the binding of the doxdoclet and generating a javadoc like
 * docx documentation with templates.
 *
 * @author Hendrik Stilke <Hendrik.Stilke@sky40.de>
 */
public class Main {

  /**
   * private foo instance
   */
  private Foo foo;

  /**
   * This is a default java class to be started as main
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    Main app = new Main();
    try {
      app.run();
    } catch (Exception ex) {
      Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  /**
   * Inherit just from object and do nothing.
   */
  public Main() {
    super();
  }

  /**
   * Takes a {@link Foo} as parameter.
   * 
   * @param foo The Foo.
   */
  public Main(Foo foo) {
    this.foo = foo;
  }

  /**
   * The main method of the Doclet Tester
   * 
   * @throws Docx4JException In case the creation failed.
   */
  public void run() throws Docx4JException {
    DocXReader reader = new DocXReader();
    URL url = Main.class.getClassLoader().getResource("template.docx");
    File f = new File(url.getFile());

    // read in template and create a writer 
    DocXReaderResult readerResult = reader.read(f);
    DocXWriter writer = new DocXWriter(readerResult, "output.docx");

    DocumentBuilder docBuilder = new DocumentBuilder(new BuilderOptions(), writer);

  }
}
