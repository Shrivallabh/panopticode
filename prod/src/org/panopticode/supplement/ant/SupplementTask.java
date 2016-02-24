package org.panopticode.supplement.ant;

import java.io.File;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.dom4j.DocumentException;
import org.panopticode.PanopticodeProject;
import org.panopticode.Supplement;

public abstract class SupplementTask extends Task {

	protected File panopticodeStructure;
	private PanopticodeProject projectLazyLoad;
	private final Supplement supplement;
	
	public SupplementTask(Supplement supplement) {
		this.supplement = supplement;
    }

	public void setPanopticodeFile(File file) { this.panopticodeStructure = file; }
	
	@Override public void execute() throws BuildException {
        requireAttribute(panopticodeStructure, "panopticodeFile");
		requireFileExists(panopticodeStructure, "panopticodeFile");
		requireAttributes();

		try {
        	PanopticodeProject project = project();
        	log("Loading supplement: " + supplement.getClass().getName());
			loadSupplement(supplement, project);
        	
        	log("Writing back to panopticode structure: " + panopticodeStructure.getName());
	        project.toFile(panopticodeStructure);
        } catch (Exception e) {
        	throw new BuildException(e.getMessage());
        }
	}

	protected void requireAttribute(Object parameter, String name) {
    	if (parameter == null) { throw new BuildException("Required attribute: " + name); }
    }

	protected void requireFileExists(File file, String name) {
    	String message = name + " " + file.getName()  + " does not exist";
        if (!file.exists()) { throw new BuildException(message); }
    }

	protected void setProject(PanopticodeProject project) {
    	if (this.projectLazyLoad != null) { throw new RuntimeException("Should not set the project a second time."); }
    	this.projectLazyLoad = project;
    }

	protected PanopticodeProject project() throws DocumentException {
    	if (projectLazyLoad == null) { 
    		log("Loading panopticode structure: " + panopticodeStructure.getName());
        	projectLazyLoad = PanopticodeProject.fromFile(panopticodeStructure);
    	}
    	return projectLazyLoad;
    }

	protected abstract void loadSupplement(Supplement supplement, PanopticodeProject project);

	protected abstract void requireAttributes();
}