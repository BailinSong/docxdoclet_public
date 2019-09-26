package de.sky40.doclet;

/**
 * This class contains the options of the document creation process.
 */
public class BuilderOptions {

  /**
   * Options will be taken from the doclet configuration on start up. (General
   * usage: -option1 value1 -option2 value2 ...)
   *
   * @param options The options as provided in the doclet start
   */
  public BuilderOptions(String[][] options) {
    this.options = options;
  }

  /**
   * The dault value for the template file, if none supplied by options.
   */
  public static final String OPTION_DEFAULT_TEMPLATE_FILENAME = "template.docx";

  /**
   * The option name for the template file. (e.g. -template
   * "c:/mytemplate.docx")
   */
  public static final String OPTION_TEMPLATE_FILENAME = "template";

  /**
   * The option name for the output file. (e.g. -file "c:/output.docx");
   */
  public static final String OPTION_OUTPUT_FILENAME = "file";

    /**
   * The dault value for the template file, if none supplied by options.
   */
  public static final String OPTION_DEFAULT_OUTPUT_FILENAME = "javadocs.docx";

  /**
   * The option name for the documentation level (e.g. -level PRIVATE); Valid
   * values are PRIVATE and PUBLIC.
   */
  public static final String OPTION_ACCESS_LEVEL = "level";

  public static final String ACCESS_LEVEL_PUBLIC = "PUBLIC";
  public static final String ACCESS_LEVEL_PRIVATE = "PRIVATE";

  /**
   * The default option for the acces level if none is supplied.
   */
  public static final String OPTION_DEFAULT_ACCESS_LEVEL = ACCESS_LEVEL_PUBLIC;

  /**
   * CONSTS for modification of the created doc
   */
  public static final String TEXT_PACKAGE = "package ";
  public static final String TEXT_CLASS = "class ";
  public static final String TEXT_ENUM = "enum ";
  public static final String TEXT_INTERFACE = "interface ";
  
  public static final String HEADING_AUTHOR = "author:";
  public static final String HEADING_IMPLEMENTS = "implements:";
  public static final String HEADING_VERSION = "version:";
  
  public static final String NOTE_INHERITED_METHOD = "[JavaDocs: Inherited method. See super class.]";
  public static final String NOTE_MISSING_COMMENT_ON_CLASS = "[missing comment on class/interface]";
  public static final String NOTE_MISSING_COMMENT_ON_EXCEPTION = "[missing comment on exception]";
  public static final String NOTE_MISSING_COMMENT_ON_FIELD = "[missing comment on field]";
  public static final String NOTE_MISSING_COMMENT_ON_METHOD = "[missing comment on method]";
  public static final String NOTE_MISSING_COMMENT_ON_PARAMETER = "[missing comment on parameter]";
  public static final String NOTE_MISSING_COMMENT_ON_RETURN_VALUE = "[missing comment on return value]";


  private final String[][] options;

  static int SIZE_COVER = 10;
  static int SIZE_CHAPTER_TITLE = 20;
  static int SIZE_TITLE_PARAGRAPH = 14;
  static int SIZE_SUBTITLE_PARAGRAPH = 14;
  static int SIZE_SECTION_PARAGRAPH = 10;
  static int SIZE_DEFAULT = 10;

  public BuilderOptions() {
    this.options = null;
  }

  /**
   * Gets the option.
   *
   * @param name The name of the option to find.
   * @return the value of the option or an empty string if the option does not
   * exist.
   */
  public String getOption(String name) {
    return getOption(name, "");
  }

  /**
   * Gets the option.
   *
   * @param name The name of the option to find.
   * @param defaultValue The default value if no option with name exists.
   * @return the value of the option or a default value if the option does not
   * exist.
   */
  public String getOption(String name, String defaultValue) {
    for (String[] opt : options) {
      if (opt[0].equals("-" + name)) {
        return opt[1];
      }
    }
    return defaultValue;
  }

  /**
   * Gets all options as array of array of String.
   *
   * @return
   */
  public String[][] getOptions() {
    return options;
  }

  /**
   * Checks if the option is supported for the doclet.
   *
   * @param option
   * @return
   */
  public static boolean isSupportedOption(String option) {
    switch (option) {
      case "-" + OPTION_OUTPUT_FILENAME:
      case "-" + OPTION_TEMPLATE_FILENAME:
      case "-" + OPTION_ACCESS_LEVEL:
        return true;
    }
    return false;
  }

  /**
   * Indicates if the builder shall create a document with public members only.
   *
   * @return True, if level is public.
   */
  public boolean isAccessLevelPublic() {
    String level = getOption(OPTION_ACCESS_LEVEL, ACCESS_LEVEL_PUBLIC);
    return ACCESS_LEVEL_PUBLIC.equals(level);
  }

    /**
   * Indicates if the builder shall include private members in creating a document.
   *
   * @return true, if level is private.
   */
  public boolean isAccessLevelPrivate() {
    String level = getOption(OPTION_ACCESS_LEVEL, ACCESS_LEVEL_PUBLIC);
    return ACCESS_LEVEL_PRIVATE.equals(level);
  }

}
