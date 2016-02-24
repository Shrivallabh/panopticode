package org.panopticode.supplement.ant;

import java.io.File;

import org.jmock.Mock;
import org.panopticode.PanopticodeProject;
import org.panopticode.Supplement;

public class EmmaPanopticodeTest extends PanopticodeAntTaskTestCase {
	private EmmaPanopticode emma;

	@Override protected void setUp() throws Exception {
	    emma = new EmmaPanopticode();
	}
	
	public void testShouldEnforcePanopticodeStructureSet() throws Exception {
		assertAttributeFailedWhenNotSet(emma, "panopticodeFile");
    }
	
	public void testShouldEnforcePanopticodeStructureExists() throws Exception {
		Mock mock = mockFile();
		mock.expects(once()).method("exists").withNoArguments().will(returnValue(false));
		mock.expects(once()).method("getName").withNoArguments().will(returnValue(FILE_NAME));
		File missingFile = (File)mock.proxy();
		
		emma.setPanopticodeFile(missingFile);
		assertFileAttributeFailedWhenNotExists(emma, "panopticodeFile");
	}
	
	public void testShouldEnforceEmmaFileSet() throws Exception {
	    emma.setPanopticodeFile(existingFile());
		assertAttributeFailedWhenNotSet(emma, "emmaFile");
    }
	
	public void testShouldEnforcePanopticodeFileExists() throws Exception {
		Mock mock = mockFile();
        mock.expects(once()).method("exists").withNoArguments().will(returnValue(false));
        mock.expects(once()).method("getName").withNoArguments().will(returnValue(FILE_NAME));
		File missingFile = (File)mock.proxy();
		
		emma.setPanopticodeFile(existingFile());
		emma.setEmmaFile(missingFile);
		assertFileAttributeFailedWhenNotExists(emma, "emmaFile");
    }
	
	public void testShouldLoadEmmaSupplement() throws Exception {
	    Mock mockEmmaFile = mockFile();
        mockEmmaFile.expects(once()).method("exists").withNoArguments().will(returnValue(true));
        mockEmmaFile.expects(once()).method("getName").withNoArguments().will(returnValue(FILE_NAME));
        mockEmmaFile.expects(once()).method("getPath").withNoArguments().will(returnValue(FILE_NAME));
	    File emmaFile = (File)mockEmmaFile.proxy();

	    Mock mockSupplement = mock(Supplement.class);
	    mockSupplement.expects(once())
		    .method("loadData")
		    .with(isA(PanopticodeProject.class), isA(String[].class))
		    .isVoid();
	    Supplement supplement = (Supplement)mockSupplement.proxy();

	    emma = new EmmaPanopticode(supplement);
	    loadSupplementWithProject(emma);
		emma.setEmmaFile(emmaFile);
	    emma.execute();
    }
}
