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

import org.jmock.cglib.MockObjectTestCase;
import org.jmock.Mock;
import org.panopticode.PanopticodeProject;
import com.sun.javadoc.*;

import java.io.IOException;
import java.io.File;

import static org.panopticode.TestHelpers.*;

public class PanopticodeDocletGatherStructureTest extends MockObjectTestCase {
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testGatherStructureFromDoclet() throws IOException {
        Mock mockRootDoc;
        PanopticodeDoclet panopticodeDoclet;
        PanopticodeProject project;
        RootDoc rootDoc;
        String srcHome = "/Users/panopticode/src/";

        mockRootDoc = mock(RootDoc.class);

        ClassDoc[] classDocs = new ClassDoc[] {
                buildClassDoc("package1", srcHome + "package1/Class1.java", "Class1"),
                buildClassDoc("package1", srcHome + "package1/Class2.java", "Class2"),
                buildClassDoc("package2", srcHome + "package2/Class3.java", "Class3")};

        mockRootDoc.stubs().method("classes").will(returnValue(classDocs));

        String[] projectNameOption = new String[] {"-projectName", "Whatever"};
        String[] versionOption = new String[] {"-projectVersion", "1"};
        String[][] options = new String[][] {projectNameOption, versionOption};

        mockRootDoc.stubs().method("options").will(returnValue(options));

        project = createDummyProject();
        rootDoc = (RootDoc) mockRootDoc.proxy();

        panopticodeDoclet = new PanopticodeDoclet();
        panopticodeDoclet.gatherStructure(rootDoc, project);

        assertEquals("package1", project.getPackageByName("package1").getName());
        assertEquals("package2", project.getPackageByName("package2").getName());
        assertEquals(2, project.getPackages().size());

        // TODO: Finish testGatherStructureFromDoclet
    }

    private ClassDoc buildClassDoc(String packageName, String fileName, String className) {
        Mock mockClassDoc;
        Mock mockFile;
        Mock mockSourcePosition;

        mockFile = mock(MockableFile.class);
        mockFile.stubs().method("getCanonicalPath").will(returnValue(fileName));
        mockFile.stubs().method("getName").will(returnValue(className + ".java"));

        mockSourcePosition = mock(SourcePosition.class);
        mockSourcePosition.stubs().method("file").will(returnValue(mockFile.proxy()));
        mockSourcePosition.stubs().method("line").will(returnValue(1));
        mockSourcePosition.stubs().method("column").will(returnValue(3));

        mockClassDoc = mock(ClassDoc.class);
        mockClassDoc.stubs().method("name").will(returnValue(className));
        mockClassDoc.stubs().method("position").will(returnValue(mockSourcePosition.proxy()));
        mockClassDoc.stubs().method("constructors").with(ANYTHING).will(returnValue(new ConstructorDoc[0]));
        mockClassDoc.stubs().method("methods").with(ANYTHING).will(returnValue(new MethodDoc[0]));
        mockClassDoc.stubs().method("innerClasses").with(ANYTHING).will(returnValue(new ClassDoc[0]));
        mockClassDoc.stubs().method("isEnum").will(returnValue(false));
        mockClassDoc.stubs().method("isInterface").will(returnValue(false));
        mockClassDoc.stubs().method("isStatic").will(returnValue(false));
        mockClassDoc.stubs().method("isAbstract").will(returnValue(false));

        addPackageDoc(mockClassDoc, packageName);

        return (ClassDoc) mockClassDoc.proxy();
    }

    private void addPackageDoc(Mock mockClassDoc, String packageName) {
        Mock mockPackageDoc;
        PackageDoc packageDoc;

        mockPackageDoc = mock(PackageDoc.class);
        mockPackageDoc.stubs().method("name").will(returnValue(packageName));

        packageDoc = (PackageDoc) mockPackageDoc.proxy();

        mockClassDoc.stubs().method("containingPackage").will(returnValue(packageDoc));
    }

    static class MockableFile extends File {

        public MockableFile() {
            super("foo");
        }
    }
}
