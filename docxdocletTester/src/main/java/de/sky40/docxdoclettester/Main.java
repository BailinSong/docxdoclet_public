package de.sky40.docxdoclettester;

import de.sky40.docxreader.DocXReader;
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

  public Main(Foo foo) {
    this.foo = foo;
  }
  
  public void run() throws Docx4JException {
    DocXReader reader = new DocXReader();
    URL url = Main.class.getClassLoader().getResource("template.docx");
    File f = new File(url.getFile());
    reader.read(f); 
  }
}
