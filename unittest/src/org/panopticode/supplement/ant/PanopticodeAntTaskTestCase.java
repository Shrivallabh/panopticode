package org.panopticode.supplement.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.jmock.Mock;
import org.jmock.cglib.MockObjectTestCase;
import org.panopticode.PanopticodeProject;

public abstract class PanopticodeAntTaskTestCase extends MockObjectTestCase {

	protected static final String FILE_NAME = "file.xml";

	public PanopticodeAntTaskTestCase() {
		super();
	}

	protected Mock mockProject() {
        return mock(PanopticodeProject.class, "mockProject",
    			new Class[] {String.class, String.class},
    			new Object[] {"name", "basePath"});
    }

	protected File existingFile() {
        return mockCalledFile(true);
    }

	private File mockCalledFile(boolean exists) {
        Mock mockFile = mockFile();
        mockFile.expects(once()).method("exists").withNoArguments().will(returnValue(exists));
        mockFile.expects(once()).method("getName").withNoArguments().will(returnValue(FILE_NAME));
        return (File)mockFile.proxy();
    }

	protected Mock mockFile() {
        return mock(File.class, "mockFile",
        		new Class[]  {String.class},
        		new Object[] {FILE_NAME});
    }

	protected void assertAttributeFailedWhenNotSet(SupplementTask supplement, String attribute) {
    	    try {
    	        supplement.execute();
    	        fail("Should be an exception when " + attribute + " not set");
            } catch (BuildException expected) {
    			assertEquals("Required attribute: " + attribute, expected.getMessage());
            }
        }
    /*
    	
    	public void testShouldEnforceEmmaFileSet() throws Exception {
    	    emma.setPanopticodeFile(existingFile());
    	    try {
    	        emma.execute();
    	        fail("Should be an exception when initial panopticode structure not set");
            } catch (BuildException expected) {
    			assertEquals("Required attribute: emmaFile", expected.getMessage());
            }
        }
    	
    	public void testShouldEnforcePanopticodeFileExists() throws Exception {
    		Mock mock = mockFile();
            mock.expects(once()).method("exists").withNoArguments().will(returnValue(false));
            mock.expects(once()).method("getName").withNoArguments().will(returnValue(NAME));
    		File missingFile = (File)mock.proxy();
    		
    		emma.setPanopticodeFile(existingFile());
    		emma.setEmmaFile(missingFile);
    	    try {
    	        emma.execute();
    	        fail("Should be an exception when initial panopticode structure not exists");
            } catch (BuildException expected) {
    			assertEquals("Emma file " + NAME  + " does not exist", expected.getMessage());
            }
        }
     */

	protected void assertFileAttributeFailedWhenNotExists(SupplementTask supplement, String attribute) {
        try {
            supplement.execute();
    		fail("Should be an exception when " + attribute + " does not exist");
        } catch (BuildException expected) {
    		assertEquals(attribute + " " + FILE_NAME  + " does not exist", expected.getMessage());
        }
    }

	protected void loadSupplementWithProject(SupplementTask task) {
        Mock mockProject = mockProject();
    	mockProject.expects(once())
    		.method("toFile")
    		.with(isA(File.class))
    		.isVoid();
    	PanopticodeProject project = (PanopticodeProject) mockProject.proxy();
    	
        Mock mockPanopticodeFile = mockFile();
        mockPanopticodeFile.expects(once()).method("exists").withNoArguments().will(returnValue(true));
        mockPanopticodeFile.expects(atLeastOnce()).method("getName").withNoArguments().will(returnValue(FILE_NAME));
        File panopticodeFile = (File) mockPanopticodeFile.proxy();
    
        task.setPanopticodeFile(panopticodeFile);
    	task.setProject(project);
    }

}