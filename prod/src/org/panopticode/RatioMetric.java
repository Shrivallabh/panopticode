/*
 * Copyright (c) 2006-2007 Julias R. Shaw
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to
 * deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
package org.panopticode;

import org.dom4j.Element;

public class RatioMetric extends Metric {
    private Double denominator;
    private Double numerator;

    public RatioMetric(RatioMetricDeclaration metricDeclaration, Double numerator, Double denominator) {
        super(metricDeclaration);
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public void addValue(Element metricElement) {
        metricElement.addAttribute("numerator", String.valueOf(numerator));
        metricElement.addAttribute("denominator", String.valueOf(denominator));
    }

    public double getDenominatorValue() {
        return denominator;
    }

    public double getNumeratorValue() {
        return numerator;
    }

    public double getPercentValue() {
        int temp = (int)((numerator / denominator) * 1000.0);
        
        return temp / 10.0;
    }

    public String getStringValue() {
        StringBuffer sb = new StringBuffer();

        sb.append(getPercentValue());
        sb.append("% (");
        sb.append(numerator);
        sb.append("/");
        sb.append(denominator);
        sb.append(")");

        return sb.toString();
    }
}
