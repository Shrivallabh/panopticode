package org.panopticode.supplement.ant;

import java.io.File;

import org.jmock.Mock;
import org.panopticode.PanopticodeProject;
import org.panopticode.Supplement;

public class GitChurnPanopticodeTest extends PanopticodeAntTaskTestCase {
	private GitChurnPanopticode gitChurn;

	@Override protected void setUp() throws Exception {
	    gitChurn = new GitChurnPanopticode();
	}

	
	public void testShouldEnforcePanopticodeStructureSet() throws Exception {
		gitChurn.setFrom("18/03/2016");
		gitChurn.setTo("18/04/2016");
		assertAttributeFailedWhenNotSet(gitChurn, "panopticodeFile");
    }
	
	public void testShouldEnforcePanopticodeStructureExists() throws Exception {
		gitChurn.setFrom("18/03/2016");
		gitChurn.setTo("18/04/2016");
		Mock mock = mockFile();
		mock.expects(once()).method("exists").withNoArguments().will(returnValue(false));
		mock.expects(once()).method("getName").withNoArguments().will(returnValue(FILE_NAME));
		File missingFile = (File)mock.proxy();
		
		gitChurn.setPanopticodeFile(missingFile);
		assertFileAttributeFailedWhenNotExists(gitChurn, "panopticodeFile");
	}
	
	public void testShouldEnforceGitChurnFileSet() throws Exception {
		gitChurn.setFrom("18/03/2016");
		gitChurn.setTo("18/04/2016");
	    gitChurn.setPanopticodeFile(existingFile());
		assertAttributeFailedWhenNotSet(gitChurn, "gitLog");
    }

	public void testShouldEnforceFromSet() {
	    gitChurn.setPanopticodeFile(existingFile());
		assertAttributeFailedWhenNotSet(gitChurn, "from");
	}
	
	public void testShouldEnforceToSet() {
	    gitChurn.setPanopticodeFile(existingFile());
		gitChurn.setFrom("18/03/2016");
		assertAttributeFailedWhenNotSet(gitChurn, "to");
	}
	

	public void testShouldEnforcePanopticodeFileExists() throws Exception {
		gitChurn.setFrom("18/03/2016");
		gitChurn.setTo("18/04/2016");
		Mock mock = mockFile();
        mock.expects(once()).method("exists").withNoArguments().will(returnValue(false));
        mock.expects(once()).method("getName").withNoArguments().will(returnValue(FILE_NAME));
		File missingFile = (File)mock.proxy();
		
		gitChurn.setPanopticodeFile(existingFile());
		gitChurn.setGitLog(missingFile);
		assertFileAttributeFailedWhenNotExists(gitChurn, "gitLog");
    }
	
	public void testShouldLoadGitChurnSupplement() throws Exception {
		// TODO: Mock object causes abnormal VM termination due to unknown cause.
//	    Mock mockGitChurnFile = mockFile();
//        mockGitChurnFile.expects(once()).method("exists").withNoArguments().will(returnValue(true));
//        mockGitChurnFile.expects(once()).method("getName").withNoArguments().will(returnValue(FILE_NAME));
//        mockGitChurnFile.expects(once()).method("getPath").withNoArguments().will(returnValue(FILE_NAME));
//	    File gitLog = (File)mockGitChurnFile.proxy();
//
//	    Mock mockSupplement = mock(Supplement.class);
//	    mockSupplement.expects(once())
//		    .method("loadData")
//		    .with(isA(PanopticodeProject.class), isA(String[].class))
//		    .isVoid();
//	    Supplement supplement = (Supplement)mockSupplement.proxy();
//
//	    gitChurn = new GitChurnPanopticode(supplement);
//		gitChurn.setFrom("18/03/2016");
//		gitChurn.setTo("18/04/2016");
//	    loadSupplementWithProject(gitChurn);
//		gitChurn.setGitLog(gitLog);
//	    gitChurn.execute();
    }
}
