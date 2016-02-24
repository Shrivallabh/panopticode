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
package org.panopticode;

import org.dom4j.Element;

import java.util.List;
import java.util.LinkedList;
import java.util.Collections;

public class SupplementDeclaration {
    private List<MetricDeclaration> metricDeclarations = new LinkedList<MetricDeclaration>();
    private List<String> errors = new LinkedList<String>();
    private String supplementClass;

    public SupplementDeclaration(String supplementClass) {
        this.supplementClass = supplementClass;
    }

    public String getSupplementClass() {
        return supplementClass;
    }

    public void addError(String message) {
        if (!errors.contains(message)) {
            errors.add(message);
        }
    }

    public void toXML(Element parentElement) {
        Element metricElement = null;

        metricElement = parentElement.addElement("supplement");

        metricElement.addAttribute("class", supplementClass);

        for (MetricDeclaration metricDeclaration : metricDeclarations) {
            metricDeclaration.toXML(metricElement);
        }

        if (errors.size() > 0) {
            Element errorsElement = metricElement.addElement("errors");
            errorsElement.addAttribute("numErrors", String.valueOf(errors.size()));

            for (String errorMessage : errors) {
                errorsElement.addElement("error").addAttribute("message", errorMessage);
            }
        }
    }

    public static SupplementDeclaration fromXML(Element suppementElement) {
        SupplementDeclaration supplementDeclaration;
        String theClass;

        theClass = suppementElement.attributeValue("class");

        Supplement supplement = loadSupplement(theClass);

        // This loads the declarations for the currently visible version of the supplement
        supplementDeclaration = supplement.getDeclaration();

        Element errorsElement = suppementElement.element("errors");

        if (errorsElement != null) {
            for (Object errorElement : suppementElement.element("errors").elements("error")) {
                String errorMessage = ((Element) errorElement).attributeValue("message");
                supplementDeclaration.addError(errorMessage);
            }
        }

        return supplementDeclaration;
    }

    public void addMetricDeclaration(MetricDeclaration metricDeclaration) {
        metricDeclarations.add(metricDeclaration);
    }

    public List<MetricDeclaration> getMetricsDeclared() {
        return Collections.unmodifiableList(metricDeclarations);
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    private static Supplement loadSupplement(String supplementClassName) {
        Supplement supplement;

        try {
            supplement = (Supplement) Class.forName(supplementClassName).newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not load supplement '" + supplementClassName + "'", e);
        }

        return supplement;
    }

}
