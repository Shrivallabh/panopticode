package org.panopticode.util;

public class IndicatorUtil {
	
	/**
	 * Map the rate of changes to a file per day to range [0-1]
	 * @param numOfChanges
	 * @param days
	 * @return
	 */
	public static Double computeChangeFrequencyIndicator(int numOfChanges, int days) {
		// Indicator will be at 0.9 for churn of 1 change per day
		return 1-Math.exp((-1*2.3025*numOfChanges)/days);
	}


	/**
	 * Maps the rate of lines changed per day to range [0-1]
	 * @param linesChanged
	 * @param days
	 * @return
	 */
	public static Double computeLinesChangedIndicator(int linesChanged, int days) {
		// Indicator will be at 0.9 for churn of 40 lines per day
		return 1-Math.exp((-1*0.05756*linesChanged)/days);
	}


	/**
	 * Maps CCN value to range [0-1]
	 * @param maxCCN
	 * @return
	 */
	public static Double computeMaxCCNIndicator(int maxCCN) {
		// Indicator will be at 0.9 for max CCN of 25
		return 1-Math.exp(-1*0.092103*maxCCN);
	}
	
	public static double computeC3Indicator(Double linesChangedIndicator, Double changeFrequencyIndicator, double coverage, double maxCCNIndicator) {
		return (((linesChangedIndicator+changeFrequencyIndicator)/2)+maxCCNIndicator+1-coverage)/3;
	}

}
