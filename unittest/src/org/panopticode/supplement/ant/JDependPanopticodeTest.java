package org.panopticode.supplement.ant;

import java.io.File;

import org.jmock.Mock;
import org.panopticode.PanopticodeProject;
import org.panopticode.Supplement;

public class JDependPanopticodeTest extends PanopticodeAntTaskTestCase {
	private JDependPanopticode jdepend;

	@Override protected void setUp() throws Exception {
	    jdepend = new JDependPanopticode();
	}
	
	public void testShouldEnforcePanopticodeStructureSet() throws Exception {
		assertAttributeFailedWhenNotSet(jdepend, "panopticodeFile");
    }
	
	public void testShouldEnforceJDependFileSet() throws Exception {
	    jdepend.setPanopticodeFile(existingFile());
	    assertAttributeFailedWhenNotSet(jdepend, "jdependFile");
    }
	
	public void testShouldEnforceJDependFileExists() throws Exception {
	    Mock mock = mockFile();
		mock.expects(once()).method("exists").withNoArguments().will(returnValue(false));
		mock.expects(once()).method("getName").withNoArguments().will(returnValue(FILE_NAME));
		File missingFile = (File)mock.proxy();
		
		jdepend.setPanopticodeFile(existingFile());
		jdepend.setJDependFile(missingFile);
		assertFileAttributeFailedWhenNotExists(jdepend, "jdependFile");
    }

	public void testShouldLoadJDependSupplement() throws Exception {
	    Mock mockJDependFile = mockFile();
        mockJDependFile.expects(once()).method("exists").withNoArguments().will(returnValue(true));
        mockJDependFile.expects(once()).method("getName").withNoArguments().will(returnValue(FILE_NAME));
        mockJDependFile.expects(once()).method("getPath").withNoArguments().will(returnValue(FILE_NAME));
	    File jdependFile = (File)mockJDependFile.proxy();
	    
	    Mock mockSupplement = mock(Supplement.class);
	    mockSupplement.expects(once())
		    .method("loadData")
		    .with(isA(PanopticodeProject.class), isA(String[].class))
		    .isVoid();
	    Supplement supplement = (Supplement)mockSupplement.proxy();

	    jdepend = new JDependPanopticode(supplement);
	    loadSupplementWithProject(jdepend);
	    jdepend.setJDependFile(jdependFile);
	    jdepend.execute();
    }
}
