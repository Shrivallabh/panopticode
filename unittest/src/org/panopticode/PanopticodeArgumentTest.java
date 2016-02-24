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


import static org.panopticode.TestHelpers.*;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;
import org.dom4j.DocumentException;
import junit.framework.TestCase;

import java.util.LinkedList;

public class PanopticodeArgumentTest extends TestCase {
    PanopticodeMethod goodParentMethod;
    String goodName;
    String goodFullyQualifiedType;
    String goodSimpleType;

    protected void setUp() throws Exception {
        super.setUp();

        goodParentMethod = createDummyMethod();
        goodName = "foo";
        goodFullyQualifiedType = "java.lang.String";
        goodSimpleType ="String";
    }

    public void testSetParentMethod() {
        PanopticodeArgument panopticodeArgument;

        panopticodeArgument = new PanopticodeArgument(goodName, goodFullyQualifiedType, goodSimpleType);
        
        try {
            panopticodeArgument.setParentMethod(null);
            fail();
        } catch(IllegalArgumentException e) {
            assertEquals("Argument 'parentMethod' may not be null.", e.getMessage());
        }
    }

    public void testConstructorFailsWhenRequiredValuesNotSet() {
        assertNotNull(new PanopticodeArgument(goodName, goodFullyQualifiedType, goodSimpleType));

        assertContructorThrowsIllegalArgumentExceptionWhenARequiredArgumentIsBlank(null, goodFullyQualifiedType);
        assertContructorThrowsIllegalArgumentExceptionWhenARequiredArgumentIsBlank("", goodFullyQualifiedType);
        assertContructorThrowsIllegalArgumentExceptionWhenARequiredArgumentIsBlank(" ", goodFullyQualifiedType);
        assertContructorThrowsIllegalArgumentExceptionWhenARequiredArgumentIsBlank("\t", goodFullyQualifiedType);

        assertContructorThrowsIllegalArgumentExceptionWhenARequiredArgumentIsBlank(goodName, null);
        assertContructorThrowsIllegalArgumentExceptionWhenARequiredArgumentIsBlank(goodName, "");
        assertContructorThrowsIllegalArgumentExceptionWhenARequiredArgumentIsBlank(goodName, " ");
        assertContructorThrowsIllegalArgumentExceptionWhenARequiredArgumentIsBlank(goodName, "\t");
    }

    private void assertContructorThrowsIllegalArgumentExceptionWhenARequiredArgumentIsBlank(String name, String type) {
        try {
            new PanopticodeArgument(name, type, type);
            fail();
        } catch(IllegalArgumentException e) {
            assertTrue(e.getMessage().startsWith("Argument '"));
            assertTrue(e.getMessage().endsWith("' may not be null or empty."));
        }
    }

    public void testConstructorSetsPropertiesToMatchArgumentsPassed() {
        PanopticodeArgument argument;

        argument = new PanopticodeArgument(goodName, goodFullyQualifiedType, goodSimpleType);
        assertEquals(goodName, argument.getName());
        assertEquals(goodFullyQualifiedType, argument.getFullyQualifiedType());
        assertEquals(goodSimpleType, argument.getSimpleType());
    }

    public void testEqualsAndHashcodeFollowJavaContract() {
        PanopticodeArgument argument;
        PanopticodeArgument equalArgument;
        PanopticodeArgument notEqualArgumentBecauseOfParent;
        PanopticodeArgument notEqualArgumentBecauseOfType;
        PanopticodeMethod differentMethod;
        String differentType;

        differentMethod = new PanopticodeMethod("different", new LinkedList<PanopticodeArgument>(), 1, 1);
        differentType = "java.lang.String[]";
        equalArgument = new PanopticodeArgument("bar", "java.lang.String", goodSimpleType);
        notEqualArgumentBecauseOfType = new PanopticodeArgument("foo", differentType, "String[]");

        argument = new PanopticodeArgument("foo", "java.lang.String", "String");
        assertEqualsAndHashcodeFollowJavaContract(argument, equalArgument, notEqualArgumentBecauseOfType);


        argument.setParentMethod(goodParentMethod);
        equalArgument.setParentMethod(goodParentMethod);
        notEqualArgumentBecauseOfParent = new PanopticodeArgument("bar", "java.lang.String", "String");
        notEqualArgumentBecauseOfParent.setParentMethod(differentMethod);
        notEqualArgumentBecauseOfType.setParentMethod(goodParentMethod);

        assertEqualsAndHashcodeFollowJavaContract(argument, equalArgument, notEqualArgumentBecauseOfParent);
        assertEqualsAndHashcodeFollowJavaContract(argument, equalArgument, notEqualArgumentBecauseOfType);
    }

    public void testToXML() {
        Element argumentElement;
        Element parentElement;
        PanopticodeArgument panopticodeArgument;

        parentElement = createDummyElement();
        panopticodeArgument = new PanopticodeArgument(goodName, goodFullyQualifiedType, goodSimpleType);
        panopticodeArgument.toXML(parentElement);

        argumentElement = parentElement.element("argument");
        assertEquals(goodName, argumentElement.attributeValue("name"));
        assertEquals(goodFullyQualifiedType, argumentElement.attributeValue("type"));
        assertNull(argumentElement.attributeValue("parameterizedType"));
        assertNull(argumentElement.attributeValue("vararg"));

        parentElement = createDummyElement();
        panopticodeArgument = new PanopticodeArgument(goodName, goodFullyQualifiedType, goodSimpleType);
        panopticodeArgument.setVarArg(true);
        panopticodeArgument.setParameterizedType(true);
        panopticodeArgument.toXML(parentElement);

        argumentElement = parentElement.element("argument");
        assertEquals(goodName, argumentElement.attributeValue("name"));
        assertEquals(goodFullyQualifiedType, argumentElement.attributeValue("type"));
        assertEquals("true", argumentElement.attributeValue("parameterizedType"));
        assertEquals("true", argumentElement.attributeValue("vararg"));
    }

    public void testFromXML() throws DocumentException {
        Element element;
        PanopticodeArgument panopticodeArgument;
        StringBuffer sb;

        sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<argument name='foo' type='java.lang.String' simpleType='String' parameterizedType='true' vararg='true' />");

        element = DocumentHelper.parseText(sb.toString()).getRootElement();

        panopticodeArgument = PanopticodeArgument.fromXML(element);
        assertEquals("foo", panopticodeArgument.getName());
        assertEquals("java.lang.String", panopticodeArgument.getFullyQualifiedType());
        assertEquals("String", panopticodeArgument.getSimpleType());
        assertTrue(panopticodeArgument.isParameterizedType());
        assertTrue(panopticodeArgument.isVarArg());

        sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<argument name='bar' type='int' simpleType='int' />");

        element = DocumentHelper.parseText(sb.toString()).getRootElement();

        panopticodeArgument = PanopticodeArgument.fromXML(element);
        assertEquals("bar", panopticodeArgument.getName());
        assertEquals("int", panopticodeArgument.getFullyQualifiedType());
        assertEquals("int", panopticodeArgument.getSimpleType());
        assertFalse(panopticodeArgument.isParameterizedType());
        assertFalse(panopticodeArgument.isVarArg());
    }

    public void testGetSetVararg() {
        PanopticodeArgument panopticodeArgument;

        panopticodeArgument = new PanopticodeArgument(goodName, goodFullyQualifiedType, goodSimpleType);
        assertFalse(panopticodeArgument.isVarArg());

        panopticodeArgument.setVarArg(true);
        assertTrue(panopticodeArgument.isVarArg());

        panopticodeArgument.setVarArg(false);
        assertFalse(panopticodeArgument.isVarArg());
    }

    public void testGetSetParemterizedType() {
        PanopticodeArgument panopticodeArgument;

        panopticodeArgument = new PanopticodeArgument(goodName, goodFullyQualifiedType, goodSimpleType);
        assertFalse(panopticodeArgument.isParameterizedType());

        panopticodeArgument.setParameterizedType(true);
        assertTrue(panopticodeArgument.isParameterizedType());

        panopticodeArgument.setParameterizedType(false);
        assertFalse(panopticodeArgument.isParameterizedType());
    }
}
