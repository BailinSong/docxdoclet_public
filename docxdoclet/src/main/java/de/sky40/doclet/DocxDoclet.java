package de.sky40.doclet;

import com.sun.javadoc.Doclet;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;

/**
 * Microsoft Word 形式の Javadoc ドキュメントを作成するドックレットです。
 */
public class DocxDoclet extends Doclet {

  /**
   * Javadoc 生成処理を実行します。
   * <p>
   * 実行すると Javadoc 情報を Word 文書として生成します。 既に同名のファイルが存在する場合は上書きされます。
   *
   * @param rootDoc Javadoc のルートドキュメント
   * @return 実行結果を真偽値で返却します。
   */
  public static boolean start(RootDoc rootDoc) {
    DocumentBuilder creator = new DocumentBuilder();
    try {
      Options.options = rootDoc.options();
      for (String[] opt : Options.options) {
        for (String o : opt) {
          System.out.println(o);
        }
      }
      creator.create(rootDoc);
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  /**
   * オプションの引数の個数を返却します。
   *
   * @param option オプション名
   * @return 対応する引数自身を含むパラメタの個数
   */
  public static int optionLength(String option) {
    if (Options.isSupportedOption(option)) {
      return 2;
    }
    return 0;
  }

  /**
   * 対応する Java バージョンを指定します。
   *
   * @return 対応する Java バージョン
   */
  public static LanguageVersion languageVersion() {
    return LanguageVersion.JAVA_1_5;
  }
}