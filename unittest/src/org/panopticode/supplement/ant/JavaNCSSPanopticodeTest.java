package org.panopticode.supplement.ant;

import java.io.File;

import org.jmock.Mock;
import org.panopticode.PanopticodeProject;
import org.panopticode.Supplement;

public class JavaNCSSPanopticodeTest extends PanopticodeAntTaskTestCase {
	private JavaNCSSPanopticode javancss;
	
	@Override protected void setUp() throws Exception {
		javancss = new JavaNCSSPanopticode();
	}
	
	public void testShouldEnforceJavaNCSSFileSet() throws Exception {
	    javancss.setPanopticodeFile(existingFile());
	    assertAttributeFailedWhenNotSet(javancss, "javancssFile");
    }
	
	public void testShouldEnforceJavaNCSSFileExists() throws Exception {
	    Mock mock = mockFile();
		mock.expects(once()).method("exists").withNoArguments().will(returnValue(false));
		mock.expects(once()).method("getName").withNoArguments().will(returnValue(FILE_NAME));
		File missingFile = (File)mock.proxy();
		
		javancss.setPanopticodeFile(existingFile());
		javancss.setJavaNCSSFile(missingFile);
		assertFileAttributeFailedWhenNotExists(javancss, "javancssFile");
    }
	
	public void testShouldLoadJavaNCSSSupplement() throws Exception {
	    Mock mockJavaNCSSFile = mockFile();
        mockJavaNCSSFile.expects(once()).method("exists").withNoArguments().will(returnValue(true));
        mockJavaNCSSFile.expects(once()).method("getName").withNoArguments().will(returnValue(FILE_NAME));
        mockJavaNCSSFile.expects(once()).method("getPath").withNoArguments().will(returnValue(FILE_NAME));
	    File javaNCSSFile = (File)mockJavaNCSSFile.proxy();
	    
	    Mock mockSupplement = mock(Supplement.class);
	    mockSupplement.expects(once())
		    .method("loadData")
		    .with(isA(PanopticodeProject.class), isA(String[].class))
		    .isVoid();
	    Supplement supplement = (Supplement)mockSupplement.proxy();

	    javancss = new JavaNCSSPanopticode(supplement);
	    loadSupplementWithProject(javancss);
	    javancss.setJavaNCSSFile(javaNCSSFile);
	    javancss.execute();
    }
}
