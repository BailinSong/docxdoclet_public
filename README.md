# DocxDoclet
Doclet which creates Javadoc as Microsoft Word document.
Ein Doclet das Javadoc als MS-Word Dokument generiert. z.B. für automatische Generierung von Schnittstellenbeschreibungen und APIs ;-) 

## Source / Quelle

Das Original findet man hier: https://github.com/cottonspace/docxdoclet

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
               
                    <doclet>doclet.docx.DocxDoclet</doclet>
                    <docletPath>${project.baseUri}libs\docxdoclet-1.0.jar</docletPath>
                    <useStandardDocletOptions>false</useStandardDocletOptions>
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
All the source code avaiable in this repository is licensed under the **[GPL, Version 3.0](http://www.gnu.org/licenses)**

This product includes software developed by [The Apache Software Foundation](http://www.apache.org/), under the Apache License 2.0
* Apache POI: Copyright 2003-2015 The Apache Software Foundation. This product includes software developed by
The Apache Software Foundation (http://www.apache.org/).
