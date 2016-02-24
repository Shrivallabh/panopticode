package org.panopticode.supplement.ant;

import java.io.File;

import org.panopticode.PanopticodeProject;
import org.panopticode.Supplement;
import org.panopticode.supplement.emma.EmmaSupplement;

public class EmmaPanopticode extends SupplementTask {

	private File emmaFile;

	public EmmaPanopticode() {
		super(new EmmaSupplement());
    }

	public EmmaPanopticode(Supplement supplement) {
		super(supplement);
    }

	public void setEmmaFile(File file) { this.emmaFile = file; }
	
	@Override protected void requireAttributes() {
	    requireAttribute(emmaFile, "emmaFile");
        requireFileExists(emmaFile, "emmaFile");
    }

	@Override protected void loadSupplement(Supplement supplement, PanopticodeProject project) {
	    supplement.loadData(project, new String[] {emmaFile.getPath()});
    }
}
