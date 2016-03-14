package org.panopticode.supplement.git;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Map;

import org.panopticode.IntegerMetricDeclaration;
import org.panopticode.Level;
import org.panopticode.PanopticodeFile;
import org.panopticode.PanopticodeProject;
import org.panopticode.Supplement;
import org.panopticode.SupplementDeclaration;
import org.panopticode.supplement.git.GitChurnParser.FileInfo;


public class GitChurnSupplement implements Supplement {

	private SupplementDeclaration declaration;
	private IntegerMetricDeclaration linesAddedDeclaration;
	private IntegerMetricDeclaration linesRemovedDeclaration;
	private IntegerMetricDeclaration timesModifiedDeclaration;
	private IntegerMetricDeclaration churnPeriodInDaysDeclaration;

	@Override
	public void loadData(PanopticodeProject project, String[] arguments) {
		Date from, to;
		GitChurnParser parser;
		Map<String, FileInfo> parseResult;
		Duration duration;

		project.addSupplementDeclaration(getDeclaration());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yyyy");
		try {
			from = simpleDateFormat.parse(arguments[0]);
			to = simpleDateFormat.parse(arguments[1]);
			duration = Duration.between(from.toInstant(), to.toInstant());
			project.addMetric(churnPeriodInDaysDeclaration.createMetric((int)duration.toDays()));
			parser = new GitChurnParser();
			parseResult = parser.parse(new File(arguments[2]));
			for(PanopticodeFile file : project.getFiles()) {
				String path = file.getPath().replaceAll("\\\\", "/");
				FileInfo info = parseResult.get(path);
				if(info!=null) {
					file.addMetric(linesAddedDeclaration.createMetric(info.getAdded()));
					file.addMetric(linesRemovedDeclaration.createMetric(info.getRemoved()));
					file.addMetric(timesModifiedDeclaration.createMetric(info.getNumOfChanges()));
				} else {
					file.addMetric(linesAddedDeclaration.createMetric(0));
					file.addMetric(linesRemovedDeclaration.createMetric(0));
					file.addMetric(timesModifiedDeclaration.createMetric(0));
				}
			}
		} catch (ParseException e) {
			throw new RuntimeException("Unable to parse date", e);
		}
        
	}


	@Override
	public SupplementDeclaration getDeclaration() {
        if (declaration == null) {
            linesAddedDeclaration = new IntegerMetricDeclaration(this, "Lines Added");
            linesAddedDeclaration.addLevel(Level.FILE);

            linesRemovedDeclaration = new IntegerMetricDeclaration(this, "Lines Removed");
            linesRemovedDeclaration.addLevel(Level.FILE);

            timesModifiedDeclaration = new IntegerMetricDeclaration(this, "Times Changed");
            timesModifiedDeclaration.addLevel(Level.FILE);

            churnPeriodInDaysDeclaration= new IntegerMetricDeclaration(this, "Churn Duration");
            churnPeriodInDaysDeclaration.addLevel(Level.PROJECT);

            declaration = new SupplementDeclaration(this.getClass().getName());
            declaration.addMetricDeclaration(linesAddedDeclaration);
            declaration.addMetricDeclaration(linesRemovedDeclaration);
            declaration.addMetricDeclaration(timesModifiedDeclaration);
            declaration.addMetricDeclaration(churnPeriodInDaysDeclaration);
        }
        return declaration;
	}

}
