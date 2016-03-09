package org.panopticode.supplement.cobertura;

import org.panopticode.PanopticodeMethod;
import org.dom4j.Element;

import java.util.List;

public class CouldntNarrowMethodMatchesException extends Exception {
    private PanopticodeMethod panopticodeMethod;
    private List<Element> possibleElements;

    public CouldntNarrowMethodMatchesException(PanopticodeMethod panopticodeMethod, List<Element> possibleElements) {
        super("");

        this.panopticodeMethod = panopticodeMethod;
        this.possibleElements = possibleElements;
    }

    public String getMessage() {
        StringBuffer sb = new StringBuffer();

        sb.append("Couldn't narrow match for method '");
        sb.append(panopticodeMethod.getSignature());
        sb.append("' from [");
        boolean first = true;
        for (Element possibleMatch : possibleElements) {
            if (!first) {
                sb.append(", ");
            }

            sb.append("'");
            sb.append(possibleMatch.attributeValue("name"));
            sb.append("'");

            first = false;
        }

        sb.append("] in class '");
        sb.append(panopticodeMethod.getParentClass().getFullyQualifiedName());
        sb.append("'");

        return sb.toString();
    }
}
