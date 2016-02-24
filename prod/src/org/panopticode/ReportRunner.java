package org.panopticode;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.io.File;

public class ReportRunner {
    private Report report;
    private String panopticodeFileName;
    private String[] reportArguments;

    public static void main(String[] arguments) throws DocumentException {
        String reportClassName;
        String panopticodeFileName;
        String[] reportArguments;

        reportClassName = arguments[0];
        panopticodeFileName = arguments[1];

        reportArguments = new String[arguments.length - 2];
        System.arraycopy(arguments, 2, reportArguments, 0, arguments.length - 2);

        ReportRunner runner = new ReportRunner(reportClassName, panopticodeFileName, reportArguments);

        runner.executeReport();
    }

    public ReportRunner(String reportClassName,
                        String panopticodeFileName,
                        String[] reportArguments) {
        report = loadReport(reportClassName);

        this.panopticodeFileName = panopticodeFileName;

        this.reportArguments = reportArguments;
    }

    public void executeReport() throws DocumentException {
        Document           document;
        PanopticodeProject project;
        SAXReader          reader;

        reader = new SAXReader();
        document = reader.read(new File(panopticodeFileName));
        project = PanopticodeProject.fromXML(document.getRootElement().element("project"));

        report.runReport(project, reportArguments);
    }

    public static Report loadReport(String reportClassName) {
        Report report;

        try {
            report = (Report) Class.forName(reportClassName).newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not load report '" + reportClassName + "'", e);
        }

        return report;
    }
}
