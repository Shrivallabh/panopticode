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

import static org.panopticode.util.ArgumentValidation.*;
import org.dom4j.Element;

public class PanopticodeArgument {
    private PanopticodeMethod parentMethod;
    private String name;
    private String fullyQualifiedType;
    private String simpleType;
    private boolean isVarArg = false;
    private boolean isParameterizedType = false;

    public PanopticodeArgument(String name, String fullyQualifiedType, String simpleType) {
        failIfEmpty(name, "name");
        failIfEmpty(fullyQualifiedType, "fullyQualifiedType");
        failIfEmpty(simpleType, "simpleType");

        this.name = name;
        this.fullyQualifiedType = fullyQualifiedType;
        this.simpleType = simpleType;
    }

    public String getSimpleType() {
        return simpleType;
    }

    public boolean isParameterizedType() {
        return isParameterizedType;
    }

    public void setParameterizedType(boolean isParameterizedType) {
        this.isParameterizedType = isParameterizedType;
    }

    /**
     * PanopticodeArguments are considered equal if they have the same fully qualified
     * type and parentMethod. This is because argument names do not affect method
     * signatures.
     *
     * @param o The object to check equality against.
     *
     * @return Returns true if equal, otherwise false.
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        PanopticodeArgument that = (PanopticodeArgument) o;

        if (parentMethod != null ? !parentMethod.equals(that.parentMethod) : that.parentMethod != null) {
            return false;
        }

        return fullyQualifiedType.equals(that.fullyQualifiedType);
    }

    public String getName() {
        return name;
    }

    public String getFullyQualifiedType() {
        return fullyQualifiedType;
    }

    public int hashCode() {
        int result;
        result = (parentMethod != null ? parentMethod.hashCode() : 0);
        result = 31 * result + fullyQualifiedType.hashCode();
        return result;
    }

    public boolean isVarArg() {
        return isVarArg;
    }

    public void setParentMethod(PanopticodeMethod parentMethod) {
        failIfNull(parentMethod, "parentMethod");

        this.parentMethod = parentMethod;
    }

    public void toXML(Element parentElement) {
        Element argumentElement = null;

        argumentElement = parentElement.addElement("argument");

        argumentElement.addAttribute("name", name);
        argumentElement.addAttribute("type", fullyQualifiedType);
        argumentElement.addAttribute("simpleType", simpleType);

        if (isParameterizedType) {
            argumentElement.addAttribute("parameterizedType", "true");
        }

        if (isVarArg) {
            argumentElement.addAttribute("vararg", "true");
        }
    }

    public void setVarArg(boolean isVarArg) {
        this.isVarArg = isVarArg;
    }

    public static PanopticodeArgument fromXML(Element argumentElement) {
        PanopticodeArgument panopticodeArgument;
        String name;
        String fullyQualifiedType;
        String simpleType;

        name = argumentElement.attributeValue("name");
        fullyQualifiedType = argumentElement.attributeValue("type");
        simpleType = argumentElement.attributeValue("simpleType");

        panopticodeArgument = new PanopticodeArgument(name, fullyQualifiedType, simpleType);

        if ("true".equals(argumentElement.attributeValue("parameterizedType"))) {
            panopticodeArgument.setParameterizedType(true);
        }

        if ("true".equals(argumentElement.attributeValue("vararg"))) {
            panopticodeArgument.setVarArg(true);
        }
        
        return panopticodeArgument;
    }
}
