/*
 * Copyright (c) 2006-2007 Julias R. Shaw
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
package org.panopticode.doclet;

import com.sun.javadoc.*;
import org.panopticode.*;
import org.dom4j.io.*;

import java.io.*;
import java.util.List;
import java.util.LinkedList;

public class PanopticodeDoclet extends Doclet {
    private static PanopticodeDoclet doclet = new PanopticodeDoclet();

    private PanopticodeProject project;
    private RootDoc rootDoc;

    public static LanguageVersion languageVersion() {
        return LanguageVersion.JAVA_1_5;
    }

    public static int optionLength(final String option) {
        if (option.equals("-debug")
          || option.equals("-outputFile")
          || option.equals("-projectName")
          || option.equals("-projectVersion")) {
          return 2;
        }

        return 0;
    }

    public static boolean start(final RootDoc rootDoc) {
        try {
            doclet.process(rootDoc);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static PanopticodeProject getProject() {
        return doclet.project;
    }

    void dumpXMLReport(PanopticodeProject theProject) throws IOException {
        PrintStream output;
        String outputFile;
        XMLWriter xmlWriter;

        outputFile = readOption("outputFile");

        if (outputFile == null) {
            output = System.out;
        } else {
            output = new PrintStream(outputFile);
        }

        xmlWriter = new XMLWriter(output, OutputFormat.createPrettyPrint());
        xmlWriter.write(theProject.generateXMLDocument());
    }

    void gatherStructure(RootDoc theRootDoc, PanopticodeProject theProject) throws IOException {
        for (ClassDoc classDoc : theRootDoc.classes()) {
            loadClassInfo(classDoc, theProject);
        }
    }

    private void loadClassInfo(ClassDoc classDoc, PanopticodeProject theProject) throws IOException {
        String packageName = classDoc.containingPackage().name();

        PanopticodePackage panopticodePackage = theProject.createAndAddPackage(packageName);

        SourcePosition classPosition = classDoc.position();

        String filePath = classPosition.file().getCanonicalPath();
        String fileName = classPosition.file().getName();

        PanopticodeFile panopticodeFile = panopticodePackage.createAndAddFile(filePath, fileName);
        PanopticodeClass panopticodeClass = panopticodeFile.createAndAddClass(classDoc.name(),
                classPosition.line(),
                classPosition.column());

        panopticodeClass.setEnum(classDoc.isEnum());
        panopticodeClass.setInterface(classDoc.isInterface());
        panopticodeClass.setStatic(classDoc.isStatic());
        panopticodeClass.setAbstract(classDoc.isAbstract());

        addMethods(panopticodeClass, classDoc.constructors(true), true);
        addMethods(panopticodeClass, classDoc.methods(true), false);

        for (ClassDoc innerClassDoc : classDoc.innerClasses(false)) {
            loadClassInfo(innerClassDoc, theProject);
        }
    }

    /*
     * The isSynthetic call seems to be broken for default contructors.  This seems to fix it.
     */
    private boolean syntheticConstructor(ExecutableMemberDoc executableMemberDoc,
                                         PanopticodeClass panopticodeClass,
                                         boolean isConstructor) {
        SourcePosition methodPosition = executableMemberDoc.position();
        int line = methodPosition.line();
        int column = methodPosition.column();

        return (isConstructor
                && line == panopticodeClass.getPositionLine()
                && column == panopticodeClass.getPositionColumn());
    }

    private void addMethods(PanopticodeClass panopticodeClass,
                            ExecutableMemberDoc[] executableMemberDocs,
                            boolean areConstructors) {
        for (ExecutableMemberDoc executableMemberDoc : executableMemberDocs) {
            if (!syntheticConstructor(executableMemberDoc, panopticodeClass, areConstructors)) {
                int column;
                int line;
                List <PanopticodeArgument> arguments;
                PanopticodeMethod panopticodeMethod;
                String methodName;

                methodName = executableMemberDoc.name();
                arguments = new LinkedList < PanopticodeArgument >();
                PanopticodeArgument lastArgument = null;
                for (Parameter parameter : executableMemberDoc.parameters()) {
                    PanopticodeArgument panopticodeArgument;
                    String parameterName;
                    String parameterQualifiedType;
                    String parameterType;

                    parameterName = parameter.name();
                    parameterQualifiedType = parameter.type().qualifiedTypeName() + parameter.type().dimension();
                    parameterType = parameter.type().simpleTypeName() + parameter.type().dimension();
                    panopticodeArgument = new PanopticodeArgument(parameterName, parameterQualifiedType, parameterType);
                    arguments.add(panopticodeArgument);

                    if (parameter.type().asParameterizedType() != null) {
                        panopticodeArgument.setParameterizedType(true);
                    }

                    lastArgument = panopticodeArgument;
                }

                if (lastArgument != null && executableMemberDoc.isVarArgs()) {
                    lastArgument.setVarArg(true);
                }

                SourcePosition methodPosition = executableMemberDoc.position();
                line = methodPosition.line();
                column = methodPosition.column();

                panopticodeMethod = panopticodeClass.createAndAddMethod(methodName, arguments, line, column);
                panopticodeMethod.setParentClass(panopticodeClass);
                if (!areConstructors && ((MethodDoc) executableMemberDoc).isAbstract()) {
                    panopticodeMethod.setAbstract(true);
                }
                panopticodeMethod.setConstructor(areConstructors);
            }
        }
    }

    private void process(RootDoc theRootDoc) throws IOException {
        boolean debug;
        long dumpXMLTime;
        long gatherStructureTime;
        long startTime;

        startTime = System.currentTimeMillis();

        rootDoc = theRootDoc;
        debug = "true".equalsIgnoreCase(readOption("debug"));
        project = new PanopticodeProject(readOption("projectName"), System.getProperty("user.dir"));
        project.setVersion(readOption("projectVersion"));

        gatherStructure(rootDoc, project);
        gatherStructureTime = System.currentTimeMillis();

        dumpXMLReport(project);
        dumpXMLTime = System.currentTimeMillis();

        if (debug) {
            System.out.println("Doclet Time: " + (dumpXMLTime - startTime) + "ms");
            System.out.println("\tTime to gather structure: " + (gatherStructureTime - startTime) + "ms");
            System.out.println("\tTime to dump XML: " + (dumpXMLTime - gatherStructureTime) + "ms");
        }
    }

    private String readOption(String tagName) {
        String[] optionArray = readOptionArray(tagName);

        if (optionArray != null) {
            return optionArray[1];
        }

        return null;
    }

    private String[] readOptionArray(String tagName) {
      String[][] options = rootDoc.options();

        for (String[] opt : options) {
            if (opt[0].equals("-" + tagName)) {
                return opt;
            }
        }

        return null;
    }
}
