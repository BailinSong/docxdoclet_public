package de.sky40.doclet;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ExecutableMemberDoc;
import com.sun.javadoc.MemberDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ParamTag;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Tag;
import com.sun.javadoc.ThrowsTag;
import com.sun.javadoc.Type;
import de.sky40.docxreader.DocXWriter;
import de.sky40.docxreader.ParamsInfo;
import de.sky40.docxreader.domain.StyleName;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import org.docx4j.openpackaging.exceptions.Docx4JException;

/**
 * Builds Microsoft Word (docx Format) files from JavaDoc
 */
public class DocumentBuilder {

  /**
   * A sequence of spaces.
   */
  public static final String LONGSPACE = "   ";

  /**
   * The options for building the document.
   */
  private final BuilderOptions builderOptions;

  /**
   * A Writer to write styled paragraphs and runs into a docx document.
   */
  private final DocXWriter writer;

  /**
   * The Javadoc root node
   */
  private RootDoc root;

  /**
   * Array of the packages
   */
  private final List<PackageDoc> packages = new ArrayList<>();

  /**
   * Creates a document builder.
   *
   * @param builderOptions The options to use in the build process.
   * @param writer A document writer to write into the doc.
   */
  public DocumentBuilder(BuilderOptions builderOptions, DocXWriter writer) {
    this.builderOptions = builderOptions;
    this.writer = writer;
  }

  /**
   * Creates the doc.
   *
   * @param rootDoc Javadoc root node
   * @throws IOException
   * @throws org.docx4j.openpackaging.exceptions.Docx4JException
   */
  public void create(RootDoc rootDoc) throws IOException, Docx4JException {

    root = rootDoc;
    // create pages for all classes
    writeClassPages();
    // write to file
    writer.write();
  }

  /**
   * Find the comment of a method parameter by parameter name.
   *
   * @param doc The method/constructor to find the comment.
   * @param name the name of the parameter
   *
   * @return an info object on the parameter with name "name"
   */
  private ParamsInfo findCommentedParameterByName(ExecutableMemberDoc doc, String name) {

    if (doc == null) {
      return null;
    }

    // find parameters
    Parameter[] parameters = doc.parameters();
    if (0 < parameters.length) {
      // iterate all parameters of method/constructor
      for (Parameter parameter : parameters) {
        if (name.equals(parameter.name())) {
          // build comment
          String comment = getParamComment(doc.paramTags(), parameter.name());
          if (!comment.isEmpty()) {
            return new ParamsInfo(parameter, doc);
          }
        }
      }
    }

    // find in super type if possible
    if (!doc.isConstructor()) {
      // go to super method
      MethodDoc method = (MethodDoc) doc;
      return findCommentedParameterByName(method.overriddenMethod(), name);
    } else {
      // do not traverse constructor methods, because they are not inherited with @Override
    }

    // check the docs of the interfaces of the type
    ClassDoc classDoc = doc.containingClass();
    ClassDoc[] interfaces = classDoc.interfaces();
    // iterate all interfaces
    if (interfaces != null) {
      for (ClassDoc interfaceDoc : interfaces) {
        MethodDoc[] methods = interfaceDoc.methods();
        if (methods != null) // iterate methods of interfaces
        {
          for (MethodDoc ifcMethod : methods) {
            if (ifcMethod.name().equals(doc.name())) {
              if (ifcMethod.signature().equals(doc.signature())) {
                // method with same signature in interface was found
                return findCommentedParameterByName(ifcMethod, name);
              }
            }
          }
        }
      }
    }

    return null;
  }

  /**
   * Build a method signature from parameters. Replaces commons like java.lang,
   * java.io, etc.
   *
   * @param parameters The method parameters
   * @return a signature string
   */
  private String getParamSignature(Parameter[] parameters) {
    StringBuilder sb = new StringBuilder();
    for (Parameter parameter : parameters) {
      if (0 < sb.length()) {
        sb.append(", ");
      }
      String type = parameter.type().toString();
      type = type.replaceAll("java\\.(lang|util|io|nio)\\.", "");
      sb.append(type);
      sb.append(" ");
      sb.append(parameter.name());
    }
    return sb.toString();
  }

  /**
   * Get the comment of a parameter by param name.
   *
   * @param tags the tags to search in
   * @param name the name to find
   * @return the comment of the parameter with name "name", or empty String if
   * none was found.
   */
  private String getParamComment(ParamTag[] tags, String name) {
    for (ParamTag tag : tags) {
      if (tag.parameterName().equals(name)) {
        return tag.parameterComment();
      }
    }
    return "";
  }

  /**
   * Get the comment of a thrown exception by name.
   *
   * @param tags the tags to search in
   * @param name the name to find
   * @return the comment of the exception with name "name", or empty String if
   * none was found.
   */
  private String getThrowsComment(ThrowsTag[] tags, String name) {
    for (ThrowsTag tag : tags) {
      if (tag.exceptionName().equals(name)) {
        return tag.exceptionComment();
      }
    }
    return "";
  }

  /**
   * Write a doc on the implemented interfaces of a class.
   * 
   * @param classDoc The class documentation node.
   */
  private void writeClassImplementedInterfaces(ClassDoc classDoc) {
    // write implemented interfaces
    if (0 < classDoc.interfaces().length) {
      writer.addLineBreak();
      writer.addStyledRun(writer.findStyle(StyleName.HEADING), "implements:");
      
      for (int i = 0; i < classDoc.interfaces().length; i++) {
        if (0 < i) {
          writer.addRun(",");
        }
        writer.addStyledRun(writer.findStyle(StyleName.SIGNATURE), " " + classDoc.interfaces()[i].qualifiedName());
      }
    }
  }

  /**
   * Write the classes inheritance tree into the document.
   *
   * @param classDoc The class documentation node to use.
   */
  private void writeClassInheritanceTree(ClassDoc classDoc) {
    String str;
    // write class inheritance docs
    List<ClassDoc> classDocs = new ArrayList<>();
    classDocs.add(classDoc);
    ClassDoc d = classDoc.superclass();
    while (d != null) {
      classDocs.add(d);
      d = d.superclass();
    }
    Collections.reverse(classDocs);
    for (int i = 0; i < classDocs.size(); i++) {
      str = "";
      for (int j = 1; j < i; j++) {
        str += "　　 ";
      }
      if (0 < i) {
        str += "　└ ";
      }
      str += classDocs.get(i).qualifiedName();
      writer.addStyledRun(writer.findStyle(StyleName.SIGNATURE), str);
      writer.addLineBreak();
    }
  }

  /**
   * Writes the name of the class as a new chapter.
   *
   * @param classDoc the class documentation node.
   */
  private void writeClassNameParagraph(ClassDoc classDoc) {
    if (classDoc.isEnum()) {
      writer.addStyledParagraph(writer.findStyle(StyleName.CLASS), BuilderOptions.TEXT_ENUM + classDoc.name());
    } else if (classDoc.isInterface()) {
      writer.addStyledParagraph(writer.findStyle(StyleName.CLASS), BuilderOptions.TEXT_INTERFACE + classDoc.name());
    } else {
      writer.addStyledParagraph(writer.findStyle(StyleName.CLASS), BuilderOptions.TEXT_CLASS + classDoc.name());
    }
  }

  /**
   * Makes a new chapter for every package and a subchapter for every class and
   * writes to the document.
   */
  private void writeClassPages() {

    String str;

    // iterate all classes
    for (ClassDoc classDoc : root.classes()) {

      PackageDoc packageDoc = classDoc.containingPackage();

      // Create new chapter with package description if it does not exist yet.
      if (!packages.contains(packageDoc)) {

        writer.addPageBreak();
        str = BuilderOptions.TEXT_PACKAGE + packageDoc.name();
        writer.addStyledParagraph(writer.findStyle(StyleName.PACKAGE), str);

        writeComment(packageDoc.commentText());

        packages.add(packageDoc);
      }

      // begin a new page for every class
      writer.addPageBreak();
      writer.closeParagraph();

      // write class/interface name as a new chapter
      writeClassNameParagraph(classDoc);

      // write the class inheritance tree
      writeClassInheritanceTree(classDoc);

       // write implemented interfaces
      writeClassImplementedInterfaces(classDoc);
      
      writer.closeParagraph();

      // write modifiers and name
      writer.addStyledRun(writer.findStyle(StyleName.SIGNATURE), classDoc.modifiers() + " " + classDoc.name());
      writer.addLineBreak();
      // write comment on class
      if (!classDoc.commentText().isEmpty()) {
        writeComment(classDoc.commentText());
        writer.addLineBreak();
      } else {
        writer.addStyledRun(writer.findStyle(StyleName.MISSING), "[missing comment on class/interface]");
        writer.addLineBreak();
      }

      writer.addLineBreak();

      writeClassVersionsAndAuthors(classDoc);
      writeClassMembers(classDoc);

      writer.closeParagraph();
    }
  }

  /**
   * Write all methods, fields, constructors etc of class or interface.
   *
   * @param classDoc The class to document.
   */
  private void writeClassMembers(ClassDoc classDoc) {
    // public enum constants
    if (0 < classDoc.enumConstants().length) {
      for (int i = 0; i < classDoc.enumConstants().length; i++) {
        if (classDoc.enumConstants()[i].isPublic() || builderOptions.isAccessLevelPrivate()) {
          writeFieldDoc(classDoc.enumConstants()[i]);
        }
      }
    }

    // fields
    if (0 < classDoc.fields().length) {
      for (int i = 0; i < classDoc.fields().length; i++) {
        // public fields
        if (classDoc.fields()[i].isPublic() || builderOptions.isAccessLevelPrivate()) {
          writeFieldDoc(classDoc.fields()[i]);
        }
      }
    }

    // constructors
    if (!classDoc.isAbstract()) {
      if (0 < classDoc.constructors().length) {
        for (int i = 0; i < classDoc.constructors().length; i++) {
          if (classDoc.constructors()[i].isPublic() || builderOptions.isAccessLevelPrivate()) {
            if (0 < i) {
              writer.addLineBreak();
            }
            writeMemberDoc(classDoc.constructors()[i]);
          }
        }
      }
    }

    // write methods
    if (0 < classDoc.methods().length) {
      for (int i = 0; i < classDoc.methods().length; i++) {
        if (classDoc.methods()[i].isPublic() || builderOptions.isAccessLevelPrivate()) {
          writeMemberDoc(classDoc.methods()[i]);
        }
      }
    }
  }

  private void writeClassVersionsAndAuthors(ClassDoc classDoc) {
    // write version info
    Tag[] versionTags = classDoc.tags("version");
    if (0 < versionTags.length) {
      writer.addStyledRun(writer.findStyle(StyleName.HEADING), "version:");
      for (int i = 0; i < versionTags.length; i++) {
        String text = " " + versionTags[i].text();
        if (0 < i) {
          text = "," + text;
        }
        writer.addRun(text);
      }
      writer.addLineBreak();
    }

    // write authors info
    Tag[] authorTags = classDoc.tags("author");
    if (0 < authorTags.length) {
      writer.addStyledRun(writer.findStyle(StyleName.HEADING), "author:");
      for (int i = 0; i < authorTags.length; i++) {
        String text = " " + authorTags[i].text();
        if (0 < i) {
          text = "," + text;
        }
        writer.addRun(text);
      }
      writer.addLineBreak();
    }
  }

  private void writeFieldDoc(MemberDoc doc) {

    String fieldType;
    if (doc.isEnumConstant()) {
      fieldType = "Enum constant";
    } else if (doc.isEnum()) {
      fieldType = "Enumeration type";
    } else {
      fieldType = "Field";
    }

    writer.addStyledParagraph(writer.findStyle(StyleName.METHOD), doc.name() + " " + fieldType);

    writer.addStyledRun(writer.findStyle(StyleName.SIGNATURE), doc.modifiers() + " " + doc.name());
    writer.addLineBreak();

    if (!doc.commentText().isEmpty()) {
      writeComment(doc.commentText());
    } else {
      writer.addStyledRun(writer.findStyle(StyleName.MISSING), "[missing comment on field]");
    }
    writer.addLineBreak();
  }

  private void writeMemberDoc(ExecutableMemberDoc doc) {

    String str;
    boolean isOverriddenMethod = false;

    String memberType;
    if (doc.isConstructor()) {
      memberType = "Constructor";
    } else if (doc.isMethod()) {
      memberType = "Method";
    } else {
      memberType = "Member";
    }

    writer.addStyledParagraph(writer.findStyle(StyleName.METHOD), doc.name() + " " + memberType);

    // Method annotations
    if (0 < doc.annotations().length) {
      AnnotationDesc[] annotations = doc.annotations();
      for (int i = 0; i < annotations.length; i++) {
        AnnotationDesc annotation = annotations[i];
        AnnotationTypeDoc annoType = annotation.annotationType();
        String name = annoType.simpleTypeName();
        if ("Override".equals(name)) {
          str = "@Override";
          isOverriddenMethod = true;
          writer.addStyledRun(writer.findStyle(StyleName.SIGNATURE), str);
          writer.addLineBreak();
        }
      }
    }

    // Method signature 
    str = doc.modifiers();
    if (doc instanceof MethodDoc) {
      MethodDoc method = (MethodDoc) doc;
      str += " " + method.returnType().simpleTypeName();
    }
    str += " " + doc.name();
    str += " (" + getParamSignature(doc.parameters()) + ")";
    writer.addStyledRun(writer.findStyle(StyleName.SIGNATURE), str);
    writer.addLineBreak();

    // writes the comment field
    boolean hasComment = writeMethodComment(doc, false);
    if (!hasComment) {
      writer.addStyledRun(writer.findStyle(StyleName.MISSING), "[missing comment on method]");
      writer.addLineBreak();
    }

    writer.addLineBreak();

    // Exceptions
    Type[] exceptions = doc.thrownExceptionTypes();
    if (0 < exceptions.length) {
      writer.addStyledRun(writer.findStyle(StyleName.HEADING), "throws:");
      writer.addLineBreak();
      for (int i = 0; i < exceptions.length; i++) {
        str = LONGSPACE + exceptions[i].simpleTypeName();
        writer.addStyledRun(writer.findStyle(StyleName.SIGNATURE), str);
        String comment = " " + getThrowsComment(doc.throwsTags(), exceptions[i].typeName());
        if (!comment.isEmpty()) {
          str = " - " + comment;
          writeComment(str);
        } else {
          writer.addStyledRun(writer.findStyle(StyleName.MISSING), "[missing comment on exception reason]");
        }
        writer.addLineBreak();
      }
      writer.addLineBreak();
    }

    Parameter[] parameters = doc.parameters();
    if (0 < parameters.length) {
      writer.addStyledRun(writer.findStyle(StyleName.HEADING), "Parameters:");
      writer.addLineBreak();
      for (int i = 0; i < parameters.length; i++) {
        boolean isFound = false;
        ParamsInfo paramInfo = findCommentedParameterByName(doc, parameters[i].name());
        if (paramInfo != null) {
          str = String.format(LONGSPACE + "(%d) ", i + 1) + paramInfo.getParameter().name();
          String comment = getParamComment(paramInfo.getDoc().paramTags(), paramInfo.getParameter().name());
          if (!comment.isEmpty()) {
            str += " - " + comment;
            writeComment(str);
            isFound = true;
          }
        }

        if (!isFound) {
          if (!isOverriddenMethod) {
            writer.addStyledRun(writer.findStyle(StyleName.MISSING), "[missing comment on parameter]");
          } else {
            writer.addStyledRun(writer.findStyle(StyleName.COMMENT), "[JavaDocs: Inherited method. See super class.]");
          }
        }
        writer.addLineBreak();
      }
      writer.addLineBreak();
    }

    if (doc instanceof MethodDoc) {
      MethodDoc method = (MethodDoc) doc;
      if (!method.returnType().simpleTypeName().equals("void")) {
        writer.addStyledRun(writer.findStyle(StyleName.HEADING), "returns:");
        writer.addLineBreak();
        str = LONGSPACE + method.returnType().simpleTypeName();
        boolean isFound = false;
        do {
          Tag[] tags = method.tags("return");
          if (0 < tags.length) {
            String comment = tags[0].text();
            if (!comment.isEmpty()) {
              str += " - " + comment;
              writeComment(str);
              writer.addLineBreak();
              isFound = true;
              break;
            }
          }
          method = method.overriddenMethod();

        } while (method != null);

        if (!isFound) {
          if (!isOverriddenMethod) {
            writer.addStyledRun(writer.findStyle(StyleName.MISSING), "[missing comment on return value]");
          } else {
            writer.addStyledRun(writer.findStyle(StyleName.COMMENT), "[JavaDocs: Inherited method. See super class.]");
          }
          writer.addLineBreak();
        }
      }
    }
  }

  /**
   * Pretty print comment as a run to writer.
   *
   */
  private void writeComment(String str) {

    String[] paragraphs = str.split("\\s*<(p|P)>\\s*");
    for (int i = 0; i < paragraphs.length; i++) {
      paragraphs[i] = paragraphs[i].replaceAll("\\s*[\\r\\n]+\\s*", " ");
      paragraphs[i] = paragraphs[i].replaceAll("\\.\\s+", ".\n");
      paragraphs[i] = paragraphs[i].replaceAll("。\\s*", "。\n");

      String[] lines = paragraphs[i].split("\n");
      for (int j = 0; j < lines.length; j++) {

        String line = lines[j];

        line = line.replaceAll("\\s*</?([a-z]+|[A-Z]+)>\\s*", " ");

        line = line.replaceAll("&lt;", "<");
        line = line.replaceAll("&gt;", ">");
        line = line.replaceAll("&quot;", "\"");
        line = line.replaceAll("&apos;", "'");
        line = line.replaceAll("&nbsp;", " ");
        line = line.replaceAll("&amp;", "&");

        // Javadoc special comments like "{@link}"
        Pattern p = Pattern.compile("\\{@([a-z]+)\\s*([^\\}]*)\\}");
        Matcher m = p.matcher(line);
        int pos = 0;
        while (m.find()) {
          writer.addStyledRun(writer.findStyle(StyleName.COMMENT), line.substring(pos, m.start()));
          pos = m.end();
          String value = m.group(2);

          if (!value.isEmpty()) {
            writer.addStyledRun(writer.findStyle(StyleName.SIGNATURE), value);
          }
        }

        line = line.substring(pos);
        if (!line.isEmpty()) {
          writer.addStyledRun(writer.findStyle(StyleName.COMMENT), line);
        }
      }
    }
  }

  /**
   * Write the comment of the member (method or constructor) or delegate to
   * supertype and write comment if empty.
   *
   * @param doc the member to take the comment from.
   */
  private boolean writeMethodComment(ExecutableMemberDoc doc, boolean isSuperType) {
    if (null == doc) {
      return false;
    }

    if (!doc.commentText().isEmpty()) {
      writeComment(doc.commentText());
      if (isSuperType) {
        writeComment(" [JavaDocs note: This comment is inherited from super type " + doc.containingClass().qualifiedTypeName() + ".]");
      }
      writer.addLineBreak();
      return true;
    } else {
      boolean isPrinted = false;
      if (doc instanceof MethodDoc) {
        MethodDoc superMethod = ((MethodDoc) doc).overriddenMethod();
        isPrinted = writeMethodComment(superMethod, true);
      }

      if (!isPrinted) {
        // check the docs of the interfaces of the type
        ClassDoc classDoc = doc.containingClass();
        ClassDoc[] interfaces = classDoc.interfaces();
        // iterate interfaces
        if (interfaces != null) {
          for (ClassDoc interfaceDoc : interfaces) {
            MethodDoc[] methods = interfaceDoc.methods();
            if (methods != null) // iterate methods of interfaces
            {
              for (MethodDoc ifcMethod : methods) {
                if (ifcMethod.name().equals(doc.name())) {
                  if (ifcMethod.signature().equals(doc.signature())) {
                    isPrinted = writeMethodComment(ifcMethod, true);
                    if (isPrinted) {
                      return true;
                    }
                  }
                }
              }
            }
          }
        }
      }

      return isPrinted;
    }
  }

  public static byte[] hexToBytes(String hexString) {
    HexBinaryAdapter adapter = new HexBinaryAdapter();
    byte[] bytes = adapter.unmarshal(hexString);
    return bytes;
  }
}
