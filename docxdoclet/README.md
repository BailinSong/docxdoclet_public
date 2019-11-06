# DocxDoclet
Doclet which creates Javadoc as Microsoft Word document.
Ein Doclet das Javadoc als MS-Word Dokument generiert. z.B. für automatische Generierung von Schnittstellenbeschreibungen und APIs ;-) 

see / siehe auch: https://maven.apache.org/plugins/maven-javadoc-plugin/index.html

## Source / Quelle

The original project is here: https://github.com/cottonspace/docxdoclet

Das Original findet man hier: https://github.com/cottonspace/docxdoclet

## Changes / Änderungen

v2.0
+ Reads in a docx file ("template.docx" as default) and extracts the styles within the document.
+ Produces a docx documentation with the same style as in template. Adjust the template or
  take your company's standard docx template.
+ uses docx4j (Apache License 2.0) instead of POI now

v1.0
+ Maven based build scripts.
+ Translated to english and locale to en-US. 
+ Style changes to support Headings.
+ Text refactored to constants (so you can translate it into the language of your choice.)  

## How to use in Maven POM / Wie man das in Maven einbindet.

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-javadoc-plugin</artifactId>
    <version>3.0.0</version>
    <configuration>
        <show>public</show>
        <nohelp>false</nohelp>
        <detectLinks>true</detectLinks>
        <detectOfflineLinks>true</detectOfflineLinks>
        <links>   
            <link>target\apidocs</link>
        </links>
   
        <doclet>de.sky40.doclet.DocxDoclet</doclet>
        <docletPath>...\docxdoclet\dist\docxdoclet-2.0-jar-with-dependencies.jar</docletPath>
        <useStandardDocletOptions>false</useStandardDocletOptions>
        <additionalOptions>
            <additionalOption>-level</additionalOption>
            <additionalOption>PRIVATE or PUBLIC</additionalOption>
            <additionalOption>-missing</additionalOption>
            <additionalOption>SHOW or HIDE</additionalOption>
            <additionalOption>-template</additionalOption>
            <additionalOption>path to the template like : ...\docxdoclet\dist\template.docx</additionalOption>
            <additionalOption>-file</additionalOption>
            <additionalOption>name of output file like : myoutput.docx</additionalOption>
        </additionalOptions>                
    </configuration>
    <executions>
        <execution>
            <id>attach-javadocs</id>
            <goals>
                <goal>jar</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## Copyright and License
All the source code avaiable in this repository is licensed under the **[Apache License 2.0] (https://www.apache.org/licenses/)**

This product includes software developed by [The Apache Software Foundation](http://www.apache.org/), under the Apache License 2.0
* Apache POI: Copyright 2003-2015 The Apache Software Foundation. This product includes software developed by
The Apache Software Foundation (http://www.apache.org/).
