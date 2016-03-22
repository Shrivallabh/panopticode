package org.panopticode.supplement.ant;

import java.io.File;

import org.panopticode.PanopticodeProject;
import org.panopticode.Supplement;
import org.panopticode.supplement.c3.C3Supplement;
import org.panopticode.supplement.jdepend.JDependSupplement;

public class C3Panopticode extends SupplementTask {
	

	public C3Panopticode() {
		super(new C3Supplement());
    }

	public C3Panopticode(Supplement supplement) {
	    super(supplement);
    }
	

	@Override protected void loadSupplement(Supplement supplement, PanopticodeProject project) {
	    supplement.loadData(project, new String[] {});
    }

	@Override
	protected void requireAttributes() {
		
	}
	
	

}
