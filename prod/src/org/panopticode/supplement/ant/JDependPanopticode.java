package org.panopticode.supplement.ant;

import java.io.File;

import org.panopticode.PanopticodeProject;
import org.panopticode.Supplement;
import org.panopticode.supplement.jdepend.JDependSupplement;

public class JDependPanopticode extends SupplementTask {
	
	private File jdependFile;

	public JDependPanopticode() {
		super(new JDependSupplement());
    }

	public JDependPanopticode(Supplement supplement) {
	    super(supplement);
    }
	
	public void setJDependFile(File file) { this.jdependFile = file; }

	protected void requireAttributes() {
		requireAttribute(jdependFile, "jdependFile");
		requireFileExists(jdependFile, "jdependFile");
    }

	@Override protected void loadSupplement(Supplement supplement, PanopticodeProject project) {
	    supplement.loadData(project, new String[] {jdependFile.getPath()});
    }

}
