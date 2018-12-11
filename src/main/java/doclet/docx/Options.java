package doclet.docx;

/**
 * 実行時オプションを格納するクラスです。
 */
public class Options {

  /**
   * CONSTS for modification of the created doc
   */
  public static final String FONT_ARIAL = "Calibri";
  public static final String FONT_CONSOLAS = "Consolas";
  public static final String TEXT_PACKAGE = "package";
  public static final String TEXT_CLASS = "class";
  public static final String TEXT_INTERFACE = "interface";
  public static final String TEXT_ALL_IMPLEMENTED_INTERFACES = "Implemented Interfaces:";
  public static final String TEXT_VERSION = "version:";
  public static final String TEXT_AUTHOR = "author:";
  public static final String TEXT_CONSTANT_DETAILS = "Constant details";
  public static final String TEXT_FIELD_DETAILS = "Field details";
  public static final String TEXT_CONSTRUCTOR_DETAILS = "Constructor details";
  public static final String TEXT_METHOD_DETAIL = "Method details";

  
  /**
   * Javadoc オプションの配列
   */
  public static String[][] options;

  /**
   * オプション文字列を取得します。
   * <p>
   * 該当するオプションが指定されていない場合は空文字列を返却します。
   *
   * @param name オプション名
   * @return オプションの値
   */
  public static String getOption(String name) {
    return getOption(name, "");
  }

  /**
   * オプション文字列を取得します。
   * <p>
   * 該当するオプションが指定されていない場合はデフォルト値を返却します。
   *
   * @param name オプション名
   * @param defaultValue オプションが指定されていない場合に使用する値
   * @return オプションの値
   */
  public static String getOption(String name, String defaultValue) {
    for (int i = 0; i < options.length; i++) {
      String[] opt = options[i];
      if (opt[0].equals("-" + name)) {
        return opt[1];
      }
    }
    return defaultValue;
  }

  /**
   * 対応しているオプション名であるか判定します。
   *
   * @param option オプション名
   * @return 対応しているオプション名の場合は true を返却します。
   */
  public static boolean isSupportedOption(String option) {
    switch (option) {
      case "-file":
      case "-font1":
      case "-font2":
      case "-title":
      case "-subtitle":
      case "-version":
      case "-company":
      case "-copyright":
        return true;
    }
    return false;
  }
}
