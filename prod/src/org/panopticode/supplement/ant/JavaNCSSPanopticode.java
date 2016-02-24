package org.panopticode.supplement.ant;

import java.io.File;

import org.panopticode.PanopticodeProject;
import org.panopticode.Supplement;
import org.panopticode.supplement.javancss.JavaNCSSSupplement;

public class JavaNCSSPanopticode extends SupplementTask {
	
	private File javancssFile;

	public JavaNCSSPanopticode() {
		super(new JavaNCSSSupplement());
    }

	public JavaNCSSPanopticode(Supplement supplement) {
	    super(supplement);
    }

	public void setJavaNCSSFile(File file) { this.javancssFile = file; }

	@Override protected void requireAttributes() {
		String name = "javancssFile";
		requireAttribute(javancssFile, name);
		requireFileExists(javancssFile, name);
	}

	@Override protected void loadSupplement(Supplement supplement, PanopticodeProject project) {
		supplement.loadData(project, new String[] {javancssFile.getPath()});
	}
}
