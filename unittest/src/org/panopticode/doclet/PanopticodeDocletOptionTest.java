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
import org.dom4j.*;
import org.dom4j.io.*;
import org.jmock.*;

import java.io.File;

public class PanopticodeDocletOptionTest extends MockObjectTestCase {
    private Mock mockRootDoc;
    private RootDoc rootDoc;
    private String outputFile;
    private String projectName;
    private String version;
    private String[] outputFileOption;
    private String[] projectNameOption;
    private String[] versionOption;

    protected void setUp() throws Exception {
        super.setUp();

        mockRootDoc = mock(RootDoc.class);
        mockRootDoc.stubs().method("classes").will(returnValue(new ClassDoc[0]));

        projectName = "Panopticode";
        projectNameOption = new String[] {"-projectName", projectName};
        version = "alpha";
        versionOption = new String[] {"-projectVersion", version};
        outputFile = "unit-test-output.xml";
        outputFileOption = new String[] {"-outputFile", outputFile};
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        new File(outputFile).delete();
    }

    public void testOutputToFile() throws Exception {
        Document doc;
        SAXReader reader;
        String[][] options;

        options = new String[][] {projectNameOption, versionOption, outputFileOption};

        mockRootDoc.stubs().method("options").will(returnValue(options));
        mockRootDoc.stubs().method("classNamed").with(ANYTHING).will(returnValue(null));

        rootDoc = (RootDoc) mockRootDoc.proxy();

        assertTrue(PanopticodeDoclet.start(rootDoc));


        reader = new SAXReader();
        doc = reader.read(new File(outputFile));

        assertEquals(projectName, doc.getRootElement().element("project").attributeValue("name"));
    }

    public void testMultipleOptionsSpecified() throws Exception {
        String[][] options;

        options = new String[][] {projectNameOption, versionOption};

        mockRootDoc.stubs().method("options").will(returnValue(options));
        mockRootDoc.stubs().method("classNamed").with(ANYTHING).will(returnValue(null));

        rootDoc = (RootDoc) mockRootDoc.proxy();
        assertTrue(PanopticodeDoclet.start(rootDoc));
        assertEquals(projectName, PanopticodeDoclet.getProject().getName());
        assertEquals(version, PanopticodeDoclet.getProject().getVersion());
    }

    public void testOneOptionSpecified() throws Exception {
        String[][] options;

        options = new String[][] {projectNameOption};

        mockRootDoc.stubs().method("options").will(returnValue(options));
        mockRootDoc.stubs().method("classNamed").with(ANYTHING).will(returnValue(null));

        rootDoc = (RootDoc) mockRootDoc.proxy();
        assertTrue(PanopticodeDoclet.start(rootDoc));
        assertEquals(projectName, PanopticodeDoclet.getProject().getName());
        assertNull(PanopticodeDoclet.getProject().getVersion());
    }
}
