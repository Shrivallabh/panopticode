package org.panopticode;

import junit.framework.TestCase;

import org.dom4j.DocumentException;

public class ReportRunnerTest extends TestCase {
	public void testReport() throws DocumentException {
		new ReportRunner("org.panopticode.report.treemap.ComplexityTreemap", "sample_data/panopticode.xml", new String[] {
				"target/complexity-treemap.svg", "-interactive" }).executeReport();
	}
}
