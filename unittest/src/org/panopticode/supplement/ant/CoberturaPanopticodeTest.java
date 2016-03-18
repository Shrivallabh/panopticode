package org.panopticode.supplement.ant;

import java.io.File;

import org.jmock.Mock;
import org.panopticode.PanopticodeProject;
import org.panopticode.Supplement;

public class CoberturaPanopticodeTest extends PanopticodeAntTaskTestCase {
	private CoberturaPanopticode cobertura;

	@Override protected void setUp() throws Exception {
	    cobertura = new CoberturaPanopticode();
	}
	
	public void testShouldEnforcePanopticodeStructureSet() throws Exception {
		assertAttributeFailedWhenNotSet(cobertura, "panopticodeFile");
    }
	
	public void testShouldEnforcePanopticodeStructureExists() throws Exception {
		Mock mock = mockFile();
		mock.expects(once()).method("exists").withNoArguments().will(returnValue(false));
		mock.expects(once()).method("getName").withNoArguments().will(returnValue(FILE_NAME));
		File missingFile = (File)mock.proxy();
		
		cobertura.setPanopticodeFile(missingFile);
		assertFileAttributeFailedWhenNotExists(cobertura, "panopticodeFile");
	}
	
	public void testShouldEnforceCoberturaFileSet() throws Exception {
	    cobertura.setPanopticodeFile(existingFile());
		assertAttributeFailedWhenNotSet(cobertura, "coberturaFile");
    }
	
	public void testShouldEnforcePanopticodeFileExists() throws Exception {
		Mock mock = mockFile();
        mock.expects(once()).method("exists").withNoArguments().will(returnValue(false));
        mock.expects(once()).method("getName").withNoArguments().will(returnValue(FILE_NAME));
		File missingFile = (File)mock.proxy();
		
		cobertura.setPanopticodeFile(existingFile());
		cobertura.setCoberturaFile(missingFile);
		assertFileAttributeFailedWhenNotExists(cobertura, "coberturaFile");
    }
	
	public void testShouldLoadCoberturaSupplement() throws Exception {
	    Mock mockCoberturaFile = mockFile();
        mockCoberturaFile.expects(once()).method("exists").withNoArguments().will(returnValue(true));
        mockCoberturaFile.expects(once()).method("getName").withNoArguments().will(returnValue(FILE_NAME));
        mockCoberturaFile.expects(once()).method("getPath").withNoArguments().will(returnValue(FILE_NAME));
	    File coberturaFile = (File)mockCoberturaFile.proxy();

	    Mock mockSupplement = mock(Supplement.class);
	    mockSupplement.expects(once())
		    .method("loadData")
		    .with(isA(PanopticodeProject.class), isA(String[].class))
		    .isVoid();
	    Supplement supplement = (Supplement)mockSupplement.proxy();

	    cobertura = new CoberturaPanopticode(supplement);
	    loadSupplementWithProject(cobertura);
		cobertura.setCoberturaFile(coberturaFile);
	    cobertura.execute();
    }
}
