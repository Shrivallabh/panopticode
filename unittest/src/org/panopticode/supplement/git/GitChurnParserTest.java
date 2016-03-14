package org.panopticode.supplement.git;

import java.io.File;
import java.util.Map;

import junit.framework.TestCase;

import org.panopticode.supplement.git.GitChurnParser.FileInfo;


public class GitChurnParserTest extends TestCase {


	public void testValidFile() {
		Map<String, FileInfo> ret = new GitChurnParser().parse(new File("sample_data/git-log.log"));
		assertNotNull(ret);
		assertEquals(15,ret.size());;
		FileInfo info = ret.get("pom.xml");
		assertEquals(3, info.getNumOfChanges());
		assertEquals(7, info.getAdded());
		assertEquals(7, info.getRemoved());
	}


	public void testIllegalLine() {
		String lines = "--a14582d--2016-01-25--Phillip Verheyden\n"+
					   "Illegal line\n"+
					   "5	1	pom.xml";
		try {
			new GitChurnParser().parse(lines);
			fail();
		} catch (GitChurnParseException gcpe) {
			assertEquals("Parsing failed at line 2", gcpe.getMessage());
		}
	}

	public void testInvalidLineRemoved() {
		String lines = "--a14582d--2016-01-25--Phillip Verheyden\n"+
					   "5	a	pom.xml";
		try {
			new GitChurnParser().parse(lines);
			fail();
		} catch (GitChurnParseException gcpe) {
			assertEquals("Parsing failed at line 2", gcpe.getMessage());
		}
	}
}
