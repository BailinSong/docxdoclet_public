package doclet.docx;

import java.math.BigInteger;

import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.LineSpacingRule;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing;

/**
 * ドキュメントの書式を設定するためのクラスです。
 */
public class DocumentStyle {

  /**
   * 段落の行間を設定します。
   *
   * @param paragraph 段落
   * @param space 行間
   */
  private static void setLineSpacing(XWPFParagraph paragraph, int space) {
    paragraph.setSpacingLineRule(LineSpacingRule.AUTO);
    CTPPr ppr = paragraph.getCTP().getPPr();
    if (ppr == null) {
      ppr = paragraph.getCTP().addNewPPr();
    }
    CTSpacing spacing = ppr.isSetSpacing() ? ppr.getSpacing() : ppr.addNewSpacing();
    spacing.setLine(BigInteger.valueOf(space));
  }

  /**
   * 標準の段落を設定します。
   *
   * @param paragraph 段落
   * @param indent set indent spacing from left
   * @param fontsize the font size to set
   * @return 文字出力ハンドル
   */
  public static XWPFRun getDefaultRun(XWPFParagraph paragraph, int indent, int fontsize) {

    // 段落を設定
    paragraph.setAlignment(ParagraphAlignment.LEFT);
    if (0 <= indent) {
      paragraph.setIndentFromLeft(indent);
    }

    // 行間を設定
    setLineSpacing(paragraph, 276);

    // 文字を設定
    XWPFRun run = paragraph.createRun();
    run.setFontFamily(Options.getOption("font1", Options.FONT_DEFAULT_TEXT));
    run.setFontSize(fontsize);
    run.setBold(false);
    run.setItalic(false);

    // 出力ハンドルを返却
    return run;
  }

  public static XWPFRun getDefaultRun(XWPFParagraph paragraph, int indent) {
    return getDefaultRun(paragraph, indent, Options.SIZE_DEFAULT);
  }

  /**
   * 表紙用段落を設定します。
   *
   * @param paragraph 段落
   * @param spaces 段落の前の空白行
   * @return 文字出力ハンドル
   */
  public static XWPFRun setCoverParagraph(XWPFParagraph paragraph, int spaces) {

    // デフォルト設定
    XWPFRun run = getDefaultRun(paragraph, 0, Options.SIZE_COVER);

    // 段落を設定
    paragraph.setAlignment(ParagraphAlignment.CENTER);
    paragraph.setSpacingBeforeLines(spaces);

    // 出力ハンドルを返却
    return run;
  }

  /**
   * 章タイトル用段落を設定します。
   *
   * @param paragraph 段落
   * @param spaces 段落の前の空白行
   * @return 文字出力ハンドル
   */
  public static XWPFRun setChapterTitleParagraph(XWPFParagraph paragraph, int spaces, int depth) {

    // デフォルト設定
    XWPFRun run = getDefaultRun(paragraph, 0, Options.SIZE_CHAPTER_TITLE);

    // 文字を設定
    run.setBold(true);

    // 段落を設定
    paragraph.setAlignment(ParagraphAlignment.LEFT);
    paragraph.setSpacingBeforeLines(spaces);
    paragraph.setStyle("Heading "+depth);

    // 出力ハンドルを返却
    return run;
  }

  /**
   * タイトル用段落を設定します。
   *
   * @param paragraph 段落
   * @param spaces 段落の前の空白行
   * @return 文字出力ハンドル
   */
  public static XWPFRun setTitleParagraph(XWPFParagraph paragraph, int spaces) {

    // デフォルト設定
    XWPFRun run = getDefaultRun(paragraph, 0, Options.SIZE_TITLE_PARAGRAPH);

    // 段落を設定
    paragraph.setAlignment(ParagraphAlignment.LEFT);
    paragraph.setSpacingBeforeLines(spaces);
    paragraph.setFirstLineIndent(100);
    paragraph.setBorderTop(Borders.BASIC_BLACK_DASHES);
    paragraph.setBorderBottom(Borders.BASIC_BLACK_DASHES);
    paragraph.setBorderLeft(Borders.BASIC_BLACK_DASHES);
    paragraph.setBorderRight(Borders.BASIC_BLACK_DASHES);

    // 行間を設定
    setLineSpacing(paragraph, 240);

    // 出力ハンドルを返却
    return run;
  }

  /**
   * サブタイトル用段落を設定します。
   *
   * @param paragraph 段落
   * @param spaces 段落の前の空白行
   * @return 文字出力ハンドル
   */
  public static XWPFRun setSubTitleParagraph(XWPFParagraph paragraph, int spaces) {

    // デフォルト設定
    XWPFRun run = getDefaultRun(paragraph, 0, Options.SIZE_SUBTITLE_PARAGRAPH);

    // 段落を設定
    paragraph.setAlignment(ParagraphAlignment.LEFT);
    paragraph.setSpacingBeforeLines(spaces);

    // 出力ハンドルを返却
    return run;
  }

  /**
   * セクション用段落を設定します。
   *
   * @param paragraph 段落
   * @param spaces 段落の前の空白行
   * @return 文字出力ハンドル
   */
  public static XWPFRun setSectionParagraph(XWPFParagraph paragraph, int spaces) {

    // デフォルト設定
    XWPFRun run = getDefaultRun(paragraph, 0, Options.SIZE_SECTION_PARAGRAPH);

    // 段落を設定
    paragraph.setAlignment(ParagraphAlignment.LEFT);
    paragraph.setSpacingBeforeLines(spaces);

    // 出力ハンドルを返却
    return run;
  }

  /**
   * Add aseparator line (border line) below the paragraph
   *
   * @param paragraph 段落
   */
  public static void setSeparatorParagraph(XWPFParagraph paragraph) {
    paragraph.setBorderBottom(Borders.BASIC_BLACK_DASHES);
  }
}
