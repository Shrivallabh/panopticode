package org.panopticode.supplement.ant;

import java.io.File;

import org.panopticode.PanopticodeProject;
import org.panopticode.Supplement;
import org.panopticode.supplement.cobertura.CoberturaSupplement;

public class CoberturaPanopticode extends SupplementTask {

	private File coberturaFile;

	public CoberturaPanopticode() {
		super(new CoberturaSupplement());
    }

	public CoberturaPanopticode(Supplement supplement) {
		super(supplement);
    }

	public void setCoberturaFile(File file) { this.coberturaFile = file; }
	
	@Override protected void requireAttributes() {
	    requireAttribute(coberturaFile, "coberturaFile");
        requireFileExists(coberturaFile, "coberturaFile");
    }

	@Override protected void loadSupplement(Supplement supplement, PanopticodeProject project) {
	    supplement.loadData(project, new String[] {coberturaFile.getPath()});
    }
}
