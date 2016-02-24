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
package org.panopticode.supplement.emma;

import org.panopticode.*;
import org.dom4j.io.SAXReader;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.util.LinkedList;
import java.util.List;

public class EmmaSupplement implements Supplement {
    private SupplementDeclaration declaration;
    private RatioMetricDeclaration blockCoverageDeclaration;
    private RatioMetricDeclaration methodCoverageDeclaration;
    private RatioMetricDeclaration lineCoverageDeclaration;

    void addCoverageToClass(Element classElement, PanopticodeClass panopticodeClass) {
        for (Object objCoverageElement : classElement.elements("coverage")) {
            Metric metric = createClassCoverageMetric((Element) objCoverageElement);

            if(metric != null) {
                panopticodeClass.addMetric(metric);
            }
        }
    }

    void addCoverageToMethod(Element methodElement, PanopticodeMethod panopticodeMethod) {
        for (Object objCoverageElement : methodElement.elements("coverage")) {
            Metric metric = createMethodCoverageMetric((Element) objCoverageElement);

            if(metric != null) {
                panopticodeMethod.addMetric(metric);
            }
        }
    }

    private Metric createMethodCoverageMetric(Element coverageElement) {
        double numerator;
        double denominator;
        Metric metric = null;
        String type;
        String value;

        type = coverageElement.attributeValue("type");
        value = coverageElement.attributeValue("value");
        numerator = getCovered(value);
        denominator = getTotal(value);

        if ("block, %".equals(type)) {
            metric = blockCoverageDeclaration.createMetric(numerator, denominator);
        } else if ("line, %".equals(type)) {
            metric = lineCoverageDeclaration.createMetric(numerator, denominator);
        }

        return metric;
    }

    private Metric createClassCoverageMetric(Element coverageElement) {
        double numerator;
        double denominator;
        Metric metric = null;
        String type;
        String value;

        type = coverageElement.attributeValue("type");
        value = coverageElement.attributeValue("value");
        numerator = getCovered(value);
        denominator = getTotal(value);

        if ("method, %".equals(type)) {
            metric = methodCoverageDeclaration.createMetric(numerator, denominator);
        } else if ("block, %".equals(type)) {
            metric = blockCoverageDeclaration.createMetric(numerator, denominator);
        } else if ("line, %".equals(type)) {
            metric = lineCoverageDeclaration.createMetric(numerator, denominator);
        }

        return metric;
    }

    double getCovered(String attributeValue) {
        int openParenIndex = attributeValue.indexOf("(") + 1;
        int slashIndex = attributeValue.indexOf("/");
        String covered = attributeValue.substring(openParenIndex, slashIndex);
        return Double.valueOf(covered);
    }

    public SupplementDeclaration getDeclaration() {
        if (declaration == null) {
            methodCoverageDeclaration = new RatioMetricDeclaration(this, "Method Coverage");
            methodCoverageDeclaration.addLevel(Level.CLASS);

            blockCoverageDeclaration = new RatioMetricDeclaration(this, "Block Coverage");
            blockCoverageDeclaration.addLevel(Level.CLASS);
            blockCoverageDeclaration.addLevel(Level.METHOD);

            lineCoverageDeclaration = new RatioMetricDeclaration(this, "Line Coverage");
            lineCoverageDeclaration.addLevel(Level.CLASS);
            lineCoverageDeclaration.addLevel(Level.METHOD);

            declaration = new SupplementDeclaration(this.getClass().getName());
            declaration.addMetricDeclaration(methodCoverageDeclaration);
            declaration.addMetricDeclaration(blockCoverageDeclaration);
            declaration.addMetricDeclaration(lineCoverageDeclaration);
        }

        return declaration;
    }

    String formatClassName(PanopticodeClass panopticodeClass) {
        String name = panopticodeClass.getName();

        while (name.indexOf(".") != -1) {
            name = name.replace('.', '$');
        }

        return name;
    }

    String formatMethodName(PanopticodeMethod panopticodeMethod) {
        StringBuffer sb = new StringBuffer();
        String name = panopticodeMethod.getName();

        while (name.indexOf(".") != -1) {
            name = name.replace('.', '$');
        }

        sb.append(name);
        sb.append(" (");

        return sb.toString();
    }

    String formatType(PanopticodeArgument panopticodeArgument) {
        String typeName = panopticodeArgument.getSimpleType();

        if ("T".equals(typeName)) {
            typeName = "Object";
        } else if (typeName.startsWith("T[")) {
            typeName = typeName.replace("T[", "Object[");
        }

        typeName = replaceAll(typeName, '.', '$');

        // We need to insert a space before the first bracket in array types
        int bracketIndex = typeName.indexOf("[");
        if (bracketIndex == -1) {
            return typeName;
        }

        String prefix = typeName.substring(0, bracketIndex);
        String suffix = typeName.substring(bracketIndex);

        return prefix + " " + suffix;
    }

    private String replaceAll(String replaceIn, char replace, char with) {
        String replaced = replaceIn;

        while (replaced.indexOf(replace) != -1) {
            replaced = replaced.replace(replace, with);
        }

        return replaced;
    }

    Element getElementByMethod(Document document,
                               PanopticodeMethod panopticodeMethod)
        throws CouldntFindElementException, CouldntNarrowMethodMatchesException {
        Element classElement = getElementByClass(document, panopticodeMethod.getParentClass());

        LinkedList<Element> possibleElements = getPossibleMethodMatches(panopticodeMethod, classElement);

        if (possibleElements.size() == 1) {
            return possibleElements.get(0);
        } else {
            return narrowUsingStrictArgumentMatch(panopticodeMethod, possibleElements);
        }
    }

    private LinkedList<Element> getPossibleMethodMatches(PanopticodeMethod panopticodeMethod,
                                                         Element classElement)
        throws CouldntFindElementException {
        String methodName = formatMethodName(panopticodeMethod);
        LinkedList<Element> possibleElements = new LinkedList<Element>();

        for (Object objMethodElement : classElement.elements("method")) {
            Element methodElement = (Element) objMethodElement;
            String elementMethodName = methodElement.attributeValue("name");
            if (elementMethodName.startsWith(methodName)) {
                if (argumentsCouldMatch(panopticodeMethod, elementMethodName)) {
                    possibleElements.add(methodElement);
                }
            }
        }

        if (possibleElements.size() == 0) {
            throw new CouldntFindElementException("Couldn't find method '"
                    + methodName + "' in class '" + panopticodeMethod.getParentClass().getFullyQualifiedName() + "'");
        }

        return possibleElements;
    }

    private Element narrowUsingStrictArgumentMatch(PanopticodeMethod panopticodeMethod,
                                                   LinkedList<Element> possibleElements)
        throws CouldntNarrowMethodMatchesException {
        LinkedList<Element> newList = new LinkedList<Element>();

        for (Element methodElement : possibleElements) {
            if (argumentsExactlyMatch(panopticodeMethod, methodElement.attributeValue("name"))) {
                newList.add(methodElement);
            }
        }

        if (newList.size() == 1) {
            return newList.get(0);
        } else {
            throw new CouldntNarrowMethodMatchesException(panopticodeMethod, possibleElements);
        }
    }

    private boolean argumentsExactlyMatch(PanopticodeMethod panopticodeMethod, String elementMethodName) {
        List<PanopticodeArgument> methodArguments;
        String[] arguments;

        arguments = parseArguments(elementMethodName);
        methodArguments = panopticodeMethod.getArguments();

        for (int i = 0; i < arguments.length; i++) {
            // filter out any with impossible argument types
            String typeName = formatType(methodArguments.get(i));
            if (!arguments[i].trim().equals(typeName)) {
                return false;
            }

        }

        return true;
    }

    boolean argumentsCouldMatch(PanopticodeMethod panopticodeMethod, String elementMethodName) {
        List<PanopticodeArgument> methodArguments;
        PanopticodeClass parentClass;
        String[] arguments;

        arguments = parseArguments(elementMethodName);
        methodArguments = panopticodeMethod.getArguments();

        parentClass = panopticodeMethod.getParentClass();
        if(panopticodeMethod.isConstructor() && parentClass.isInnerClass() && !parentClass.isStatic()) {
            // Pull the one synthetic argument off the beginning of the list
            String[] originalArguments = arguments;
            arguments = new String[originalArguments.length - 1];
            System.arraycopy(originalArguments, 1, arguments, 0, originalArguments.length - 1);
        }

        if(parentClass.isEnum() && panopticodeMethod.isConstructor()) {
            // Pull the two synthetic args offthe beginning of the list
            String[] originalArguments = arguments;
            arguments = new String[originalArguments.length - 2];
            System.arraycopy(originalArguments, 2, arguments, 0, originalArguments.length - 2);
        }

        if (arguments.length != methodArguments.size()) {
            // filter out any with the wrong number of arguments
            return false;
        }

        for (int i = 0; i < arguments.length; i++) {
            // filter out any with impossible argument types
            String typeName = formatType(methodArguments.get(i));
            if (!arguments[i].trim().endsWith(typeName)) {
                return false;
            }
        }

        return true;
    }

    private String[] parseArguments(String methodSignature) {
        if (methodSignature.indexOf("()") == -1) {
            int openParenIndex = methodSignature.indexOf("(");
            String afterFirstParen = methodSignature.substring(openParenIndex + 1);
            int closeParenIndex = afterFirstParen.indexOf(")");
            String argumentsOnly = afterFirstParen.substring(0, closeParenIndex);
            return argumentsOnly.split(",");
        } else {
            return new String[0];
        }
    }

    Element getElementByClass(Document document, PanopticodeClass panopticodeClass)
        throws CouldntFindElementException {
        Element fileElement = getElementByFile(document, panopticodeClass.getParentFile());
        String className = formatClassName(panopticodeClass);
        Element classElement = getElementByName(fileElement, "class", className);

        if (classElement == null) {
            throw new CouldntFindElementException("Couldn't find class '"
                    + className + "' in file '" + panopticodeClass.getParentFile().getPath() + "'");
        }

        return classElement;
    }

    Element getElementByFile(Document document, PanopticodeFile panopticodeFile)
        throws CouldntFindElementException {
        Element packageElement = getElementByPackage(document, panopticodeFile.getParentPackage());
        String fileName = panopticodeFile.getName();

        Element fileElement = getElementByName(packageElement, "srcfile", fileName);

        if (fileElement == null) {
            throw new CouldntFindElementException("Couldn't find file '"
                    + fileName + "' in package '" + panopticodeFile.getParentPackage().getName() + "'");
        }

        return fileElement;
    }

    Element getElementByPackage(Document document, PanopticodePackage panopticodePackage)
        throws CouldntFindElementException {
        String name = panopticodePackage.getName();

        if("".equals(name)) {
            name = "default package";
        }

        Element parent = document.getRootElement().element("data").element("all");

        Element packageElement = getElementByName(parent, "package", name);

        if (packageElement == null) {
            throw new CouldntFindElementException("Couldn't find package '" + name + "'");
        }

        return packageElement;
    }

    private Element getElementByName(Element parent, String tag, String name) {
        for (Object objElement : parent.elements(tag)) {
            Element candidate = (Element) objElement;
            if (name.equals(candidate.attributeValue("name"))) {
                return candidate;
            }
        }

        return null;
    }

    double getTotal(String attributeValue) {
        int slashIndex = attributeValue.indexOf("/") + 1;
        int closeParenIndex = attributeValue.indexOf(")");
        String total = attributeValue.substring(slashIndex, closeParenIndex);
        return Double.valueOf(total);
    }

    void loadClassData(PanopticodeProject project, Document document) {
        for (PanopticodeClass panopticodeClass : project.getClasses()) {
            if (coverageAppliesTo(panopticodeClass)) {
                try {
                    Element classElement = getElementByClass(document, panopticodeClass);

                    addCoverageToClass(classElement, panopticodeClass);
                } catch (CouldntFindElementException e) {
                    declaration.addError("ERROR - EmmaSupplement - " + e.getMessage());
                }
            }
        }
    }

    public void loadData(PanopticodeProject project, String[] arguments) {
        project.addSupplementDeclaration(getDeclaration());

        SAXReader saxReader;
        Document document;

        saxReader = new SAXReader();
        try {
            document = saxReader.read(arguments[0]);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

        loadMethodData(project, document);
        loadClassData(project, document);
    }

    void loadMethodData(PanopticodeProject project, Document document) {
        for (PanopticodeMethod panopticodeMethod : project.getMethods()) {
            if (coverageAppliesTo(panopticodeMethod)) {

                try {
                    Element methodElement = getElementByMethod(document, panopticodeMethod);

                    addCoverageToMethod(methodElement, panopticodeMethod);
                } catch (CouldntFindElementException e) {
                    declaration.addError("ERROR - EmmaSupplement - " + e.getMessage());
                } catch (CouldntNarrowMethodMatchesException e) {
                    declaration.addError("ERROR - EmmaSupplement - " + e.getMessage());
                }
            }
        }
    }

    boolean coverageAppliesTo(PanopticodeClass panopticodeClass) {
        return !panopticodeClass.isInterface();
    }

    boolean coverageAppliesTo(PanopticodeMethod panopticodeMethod) {
        return !(panopticodeMethod.isAbstract() || panopticodeMethod.getParentClass().isInterface());
    }
}
