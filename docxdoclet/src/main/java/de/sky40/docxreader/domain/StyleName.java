package de.sky40.docxreader.domain;

import java.util.Objects;

/**
 * Strong typing for a string that represents a name with a style. To be used by
 * maps with a {@link Style}.
 *
 * Immutable type.
 *
 * @author Hendrik Stilke <Hendrik.Stilke@sky40.de>
 */
public final class StyleName {

  public static final String CLASS = "classtemplate";
  public static final String METHOD = "methodtemplate";
  public static final String PACKAGE = "packagetemplate";
  public static final String SIGNATURE = "signaturetemplate";
  public static final String COMMENT = "commenttemplate";
  public static final String HEADING = "headingtemplate";
  public static final String MISSING = "missingtemplate";

  private final String name;

  /**
   * Creates a template with the name.
   *
   * @param name
   */
  public StyleName(String name) {
    this.name = name;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final StyleName other = (StyleName) obj;
    if (!Objects.equals(this.name, other.name)) {
      return false;
    }
    return true;
  }

  public String getName() {
    return name;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 79 * hash + Objects.hashCode(this.name);
    return hash;
  }

  public static StyleName with(String name) {
    return new StyleName(name);
  }

}
