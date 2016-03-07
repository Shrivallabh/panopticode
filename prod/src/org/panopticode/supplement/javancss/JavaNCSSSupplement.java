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
package org.panopticode.supplement.javancss;

import org.panopticode.*;
import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.util.List;

public class JavaNCSSSupplement implements Supplement {
    private SupplementDeclaration declaration;
    private IntegerMetricDeclaration ccnDeclaration;
    private IntegerMetricDeclaration ncssDeclaration;

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

    void loadClassData(PanopticodeProject project, Document document) {
        for (PanopticodeClass panopticodeClass : project.getClasses()) {
            if (panopticodeClass.isInnerClass()) {
                int ncss = 0;
                for (PanopticodeMethod panopticodeMethod : panopticodeClass.getMethods()) {
                    IntegerMetric integerMetric = ((IntegerMetric) panopticodeMethod.getMetricByName("NCSS"));
                    if(integerMetric != null) {
                        ncss += integerMetric.getValue();
                    }
                }
                panopticodeClass.addMetric(ncssDeclaration.createMetric(ncss));
            } else {
                Element objectElement = getElementByClass(document, panopticodeClass);

                if (objectElement != null) {
                    int ncss = Integer.parseInt(objectElement.elementText("ncss"));

                    panopticodeClass.addMetric(ncssDeclaration.createMetric(ncss));
                } else {
                    declaration.addError("ERROR - JavaNCSSSupplement - Could not find match for class '"
                            + formatClassName(panopticodeClass) + "'");
                }
            }
        }
    }

    boolean isSyntheticEnumMethod(PanopticodeMethod panopticodeMethod) {
        if (!panopticodeMethod.getParentClass().isEnum()) {
            return false;
        }

        return isValueOfEnumMethod(panopticodeMethod) || isValuesEnumMethod(panopticodeMethod);
    }

    private boolean isValuesEnumMethod(PanopticodeMethod panopticodeMethod) {
        return "values".equals(panopticodeMethod.getName())
                && panopticodeMethod.getArguments().size() == 0;

    }

    private boolean isValueOfEnumMethod(PanopticodeMethod panopticodeMethod) {
        return "valueOf".equals(panopticodeMethod.getName())
                && panopticodeMethod.getArguments().size() == 1
                && "java.lang.String".equals(panopticodeMethod.getArguments().get(0).getFullyQualifiedType());

    }

    void loadMethodData(PanopticodeProject project, Document document) {
        for (PanopticodeMethod panopticodeMethod : project.getMethods()) {
            if (!isSyntheticEnumMethod(panopticodeMethod)) {
                Element functionElement = getElementByMethod(document, panopticodeMethod);

                if (functionElement != null) {
                    int ncss = Integer.parseInt(functionElement.elementText("ncss"));
                    int ccn = Integer.parseInt(functionElement.elementText("ccn"));

                    panopticodeMethod.addMetric(ncssDeclaration.createMetric(ncss));
                    panopticodeMethod.addMetric(ccnDeclaration.createMetric(ccn));
                } else {
                    declaration.addError("WARNING - JavaNCSSSupplement - Could not find match for method '"
                            + formatMethodName(panopticodeMethod) + "'");
                }
            }
        }
    }

    public SupplementDeclaration getDeclaration() {
        if (declaration == null) {
            ncssDeclaration = new IntegerMetricDeclaration(this, "NCSS");
            ncssDeclaration.addLevel(Level.CLASS);
            ncssDeclaration.addLevel(Level.METHOD);

            ccnDeclaration = new IntegerMetricDeclaration(this, "CCN");
            ccnDeclaration.addLevel(Level.METHOD);

            declaration = new SupplementDeclaration(this.getClass().getName());
            declaration.addMetricDeclaration(ncssDeclaration);
            declaration.addMetricDeclaration(ccnDeclaration);
        }

        return declaration;
    }

    String formatClassName(PanopticodeClass panopticodeClass) {
        return panopticodeClass.getFullyQualifiedName();
    }

    String getAdjustedInnerClassConstructorName(PanopticodeMethod panopticodeMethod) {
        StringBuffer sb = new StringBuffer();

        sb.append(panopticodeMethod.getParentClass().getFullyQualifiedName());
        String methodName = panopticodeMethod.getName();

        int dotIndex = methodName.indexOf(".");
        sb.append(dotIndex==-1?methodName:methodName.substring(dotIndex));

        return sb.toString();
    }

    String formatMethodName(PanopticodeMethod panopticodeMethod) {
        StringBuffer sb = new StringBuffer();

        if (panopticodeMethod.isConstructor() && panopticodeMethod.getParentClass().isInnerClass()) {
            sb.append(getAdjustedInnerClassConstructorName(panopticodeMethod));
        } else {
            sb.append(panopticodeMethod.getFullyQualifiedName());
        }

        sb.append("(");
        sb.append(formatArguments(panopticodeMethod.getArguments()));
        sb.append(")");

        return sb.toString();
    }

    private String formatArguments(List<PanopticodeArgument> arguments) {
        StringBuffer sb = new StringBuffer();

        boolean first = true;
        for (PanopticodeArgument panopticodeArgument : arguments) {
            if (!first) {
                sb.append(",");
            }

            sb.append(panopticodeArgument.getSimpleType());

            // correct for varargs
            if (panopticodeArgument.isVarArg()) {
                // pulling off the '[]'
                sb.delete(sb.length() - 2, sb.length());
            }

            first = false;
        }

        return sb.toString();
    }


    Element getElementByClass(Document doc, PanopticodeClass panopticodeClass) {
        String className;

        className = formatClassName(panopticodeClass);

        for (Object objObjectElement : doc.getRootElement().element("objects").elements("object")) {
            Element objectElement;
            String javaNCSSObjectName;

            objectElement = (Element) objObjectElement;
            javaNCSSObjectName = objectElement.elementText("name");

            if (className.equals(javaNCSSObjectName)) {
                return objectElement;
            }
        }

        return null;
    }

    Element getElementByMethod(Document doc, PanopticodeMethod panopticodeMethod) {
        String methodName;

        methodName = formatMethodName(panopticodeMethod);

        for (Object objFunctiontElement : doc.getRootElement().element("functions").elements("function")) {
            Element functionElement;
            String javaNCSSFunctionName;

            functionElement = (Element) objFunctiontElement;
            javaNCSSFunctionName = functionElement.elementText("name");

            if (methodName.equals(javaNCSSFunctionName)) {
                return functionElement;
            }
        }

        return null;
    }
}
