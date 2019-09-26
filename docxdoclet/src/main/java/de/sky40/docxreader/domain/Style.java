package de.sky40.docxreader.domain;

import org.docx4j.wml.RPr;

/**
 * Holds a style object. This can be either a simple ID or an object of docx4j
 * with style information.
 *
 * @author Hendrik Stilke <Hendrik.Stilke@sky40.de>
 */
public class Style {

  private final String styleId;
  private final RPr runStyle;

  public Style(String styleId) {
    this.styleId = styleId;
    this.runStyle = null;
  }

  public Style(RPr runStyle) {
    this.styleId = null;
    this.runStyle = runStyle;
  }

  /**
   * The style object of a run.
   *
   * @return
   */
  public RPr getRunStyle() {
    return runStyle;
  }

  /**
   * The style id of a paragraph style.
   *
   * @return
   */
  public String getStyleId() {
    return styleId;
  }

}
