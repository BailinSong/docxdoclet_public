package de.sky40.docxreader;

import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.Parameter;

/**
 * Info about a parameter.
 *
 * Immutable type.
 *
 * Result type.
 *
 * @author Hendrik Stilke <Hendrik.Stilke@sky40.de>
 */
public class ParamsInfo {

  private final Parameter parameter;
  private final ExecutableMemberDoc doc;

  public ParamsInfo(Parameter parameter, ExecutableMemberDoc doc) {
    this.parameter = parameter;
    this.doc = doc;
  }

  public ExecutableMemberDoc getDoc() {
    return doc;
  }

  public Parameter getParameter() {
    return parameter;
  }

}
