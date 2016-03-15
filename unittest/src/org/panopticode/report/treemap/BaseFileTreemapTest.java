package org.panopticode.report.treemap;

import static org.panopticode.TestHelpers.createDummyElement;
import static org.panopticode.TestHelpers.createDummyMetricDeclaration;
import static org.panopticode.TestHelpers.createDummyProject;
import static org.panopticode.TestHelpers.createDummySupplementDeclaration;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.dom4j.Document;
import org.dom4j.Element;
import org.panopticode.IntegerMetricDeclaration;
import org.panopticode.Level;
import org.panopticode.MetricDeclaration;
import org.panopticode.PanopticodeArgument;
import org.panopticode.PanopticodeClass;
import org.panopticode.PanopticodeFile;
import org.panopticode.PanopticodeMethod;
import org.panopticode.PanopticodePackage;
import org.panopticode.PanopticodeProject;
import org.panopticode.SupplementDeclaration;

import edu.umd.cs.treemap.Rect;

public class BaseFileTreemapTest extends TestCase {
	public void testGenerateXMLDoccument() throws IOException {
		Element svgElement;
		NonRenderingFileTreemap treemap;
		PanopticodeProject project;

		treemap = new NonRenderingFileTreemap();
		project = createDummyProject();

		Document doc = treemap.generateXMLDocument(project, false);

		svgElement = doc.getRootElement();
		assertNotNull(svgElement);
		assertEquals("svg", svgElement.getQName().getName());

		assertSame(project, treemap.renderedProject);
		assertSame(svgElement, treemap.svgElement);
	}

	public void testGetData() {
		DummyFileTreemap treemap;
		ContainerMapItem projectData;
		ContainerMapItem packageData;
		FileLeafMapItem fileData;
		IntegerMetricDeclaration metricDeclaration;
		PanopticodeProject panopticodeProject;
		PanopticodePackage panopticodePackage;
		PanopticodeFile panopticodeFile;

		metricDeclaration = new IntegerMetricDeclaration(null, "NCSS");
		metricDeclaration.addLevel(Level.METHOD);

		treemap = new DummyFileTreemap();
		treemap.categorizer = new Categorizer(new LinkedList<Category>(), new DefaultCategory("", "", ""));

		panopticodeProject = createDummyProject();
		panopticodePackage = panopticodeProject.createAndAddPackage("org.foo");
		panopticodeFile = panopticodePackage.createAndAddFile("Foo.java", "Foo.java");
		panopticodeFile.addMetric(metricDeclaration.createMetric(3));

		projectData = treemap.getData(panopticodeProject, false);
		packageData = (ContainerMapItem) projectData.getChildren()[0];
		fileData = (FileLeafMapItem) packageData.getChildren()[0];

		assertEquals("Project - " + panopticodeProject.getName(), projectData.getName());
		assertEquals("Package - " + panopticodePackage.getName(), packageData.getName());
		assertEquals(panopticodeFile.getName(), fileData.getName());
	}

	public void testGetLayout() {
		BaseTreemap treemap;
		Map<String, Rect> layout;
		Rect outerBounds;

		treemap = new DummyFileTreemap();
		outerBounds = new Rect(0, 0, 1000, 1000);

		layout = treemap.getLayout(outerBounds, true);
		assertEquals(4, layout.keySet().size());

		layout = treemap.getLayout(outerBounds, false);
		assertEquals(3, layout.keySet().size());
	}

	public void testInteractiveLayoutDoesntExceedBoundaries() {
		BaseTreemap treemap;
		Rect outerBounds;

		treemap = new DummyFileTreemap();
		outerBounds = new Rect(0, 0, 1000, 1000);
		Map<String, Rect> layout = treemap.getInteractiveLayout(outerBounds);

		assertEquals(4, layout.keySet().size());

		assertWithin(outerBounds, layout.get("title"));
		assertWithin(outerBounds, layout.get("legend"));
		assertWithin(outerBounds, layout.get("contents"));
		assertWithin(outerBounds, layout.get("details"));
	}

	public void testRenderInteractiveDetails() {
		BaseTreemap treemap;
		Element parentElement;
		Element detailGroupingElement;
		MetricDeclaration metricDeclaration;
		PanopticodeProject project;
		Rect bounds;
		SupplementDeclaration supplementDeclaration;

		supplementDeclaration = createDummySupplementDeclaration();
		metricDeclaration = createDummyMetricDeclaration();
		supplementDeclaration.addMetricDeclaration(metricDeclaration);

		bounds = new Rect(0, 0, 1000, 1000);
		parentElement = createDummyElement();
		project = createDummyProject();
		project.addSupplementDeclaration(supplementDeclaration);

		treemap = new DummyFileTreemap();
		treemap.renderDetails(bounds, parentElement, project, true);

		assertEquals(1, parentElement.elements("script").size());

		detailGroupingElement = parentElement.element("g");

		assertNotNull(detailGroupingElement);
		assertEquals("details", detailGroupingElement.attributeValue("id"));

		List<Element> textElements = detailGroupingElement.elements("text");
		assertEquals(6, textElements.size());
		assertEquals("Details", textElements.get(0).getText());
		assertEquals("( Click on a rectangle to view details )", textElements.get(1).getText());
		assertEquals("Project: ", textElements.get(2).getText());
		assertEquals(metricDeclaration.getName() + ": ", textElements.get(3).getText());
		assertEquals("Package: ", textElements.get(4).getText());
		assertEquals("File: ", textElements.get(5).getText());
	}

	public void testRenderStaticDetails() {
		BaseTreemap treemap;
		Element parentElement;

		parentElement = createDummyElement();

		treemap = new DummyFileTreemap();
		treemap.renderDetails(null, parentElement, null, false);

		assertEquals(0, parentElement.elements().size());
	}

	public void testRenderTitle() {
		DummyFileTreemap treemap;
		Element parentElement;
		Element titleElement;
		PanopticodeProject project;
		Rect bounds;
		String title;

		title = "skdfsdk";

		treemap = new DummyFileTreemap();
		treemap.title = title;

		parentElement = createDummyElement();
		bounds = new Rect(0, 0, 1000, 25);
		project = createDummyProject();

		treemap.renderTitle(parentElement, bounds, project);

		titleElement = parentElement.element("text");
		assertEquals(title, titleElement.getText());
	}

	public void testRunReport() {
		// TODO: Make a real test for this
		NonRenderingFileTreemap treemap;
		PanopticodeProject project;

		treemap = new NonRenderingFileTreemap();
		project = createDummyProject();

		treemap.runReport(project, new String[] { "sample_data/doesntexist.xml" });
		treemap.runReport(project, new String[] { "sample_data/doesntexist.xml", "-interactive" });
		treemap.runReport(project, new String[] { null });
	}

	public void testStaticLayoutDoesntExceedBoundaries() {
		BaseTreemap treemap;
		Rect outerBounds;

		treemap = new DummyFileTreemap();
		outerBounds = new Rect(0, 0, 1000, 1000);
		Map<String, Rect> layout = treemap.getStaticLayout(outerBounds);

		assertEquals(3, layout.keySet().size());

		assertWithin(outerBounds, layout.get("title"));
		assertWithin(outerBounds, layout.get("legend"));
		assertWithin(outerBounds, layout.get("contents"));
	}

	private void assertWithin(Rect outerBounds, Rect innerBounds) {
		assertTrue(outerBounds.x <= innerBounds.x);

		assertTrue(outerBounds.y <= innerBounds.y);

		double outerX2 = outerBounds.x + outerBounds.w;
		double innerX2 = innerBounds.x + innerBounds.w;
		assertTrue(outerX2 >= innerX2);

		double outerY2 = outerBounds.y + outerBounds.h;
		double innerY2 = innerBounds.y + innerBounds.h;
		assertTrue(outerY2 >= innerY2);
	}
}

class DummyFileTreemap extends BaseFileTreemap {
	public Categorizer categorizer;
	public String title;

	@Override
	public Categorizer getCategorizer() {
		return categorizer;
	}

	@Override
	public String getTitle(PanopticodeProject project) {
		return title;
	}
}

class NonRenderingFileTreemap extends DummyFileTreemap {
	PanopticodeProject renderedProject;
	Element svgElement;

	@Override
	void renderSVG(PanopticodeProject project, Element svgElement, boolean interactive, Rect bounds) {
		renderedProject = project;
		this.svgElement = svgElement;
	}
}
