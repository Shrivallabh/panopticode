package org.panopticode.supplement.ant;

import java.io.File;

import org.panopticode.PanopticodeProject;
import org.panopticode.Supplement;
import org.panopticode.supplement.git.GitChurnSupplement;

public class GitChurnPanopticode  extends SupplementTask {

	private String from;
	private String to;
	private File gitLog;
	
	public GitChurnPanopticode() {
		this(new GitChurnSupplement());
	}
	
	public GitChurnPanopticode(Supplement supplement) {
		super(supplement);
		// TODO Auto-generated constructor stub
	}

	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public File getGitLog() {
		return gitLog;
	}



	public void setGitLog(File gitLog) {
		this.gitLog = gitLog;
	}



	@Override
	protected void loadSupplement(Supplement supplement,
			PanopticodeProject project) {
		supplement.loadData(project, new String[] {from, to, gitLog.getAbsolutePath()});
	}

	@Override
	protected void requireAttributes() {
		requireAttribute(from, "from");
		requireAttribute(to, "to");
		requireAttribute(gitLog, "gitLog");
		requireFileExists(gitLog, "gitLog");
	}

}
