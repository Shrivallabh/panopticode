package org.panopticode.supplement.cobertura;

import org.panopticode.*;
import org.dom4j.io.SAXReader;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.util.LinkedList;
import java.util.List;

public class CoberturaSupplement implements Supplement {
    private SupplementDeclaration declaration;
    private RatioMetricDeclaration branchCoverageDeclaration;
    private RatioMetricDeclaration lineCoverageDeclaration;

    void addCoverageToClass(Element classElement, PanopticodeClass panopticodeClass) {
    	panopticodeClass.addMetric(computeLineMetric(classElement));
    	panopticodeClass.addMetric(computeBranchMetric(classElement));
    }
    
    void addCoverageToMethod(Element methodElement, PanopticodeMethod panopticodeMethod) {
    	panopticodeMethod.addMetric(computeLineMetric(methodElement));
    	panopticodeMethod.addMetric(computeBranchMetric(methodElement));
    }


    RatioMetric computeLineMetric(Element parentElement) {
        Element linesElement = parentElement.element("lines");
		List<Element> lineElements = linesElement.elements("line");
		double lineNumerator=0,lineDenominator=0;
        for(Element lineElement : lineElements) {
        	lineDenominator++;
        	String hits = lineElement.attributeValue("hits");
        	if(!"0".equals(hits)) {
        		lineNumerator++;
        	}
        }
        return lineCoverageDeclaration.createMetric(lineNumerator, lineDenominator);
    }


    RatioMetric computeBranchMetric(Element parentElement) {
        List<Element> lineElements = parentElement.element("lines").elements("line");
		double branchNumerator=0,branchDenominator=0;
        for(Element lineElement : lineElements) {
        	String branchAttr = lineElement.attributeValue("branch");
        	if("true".equals(branchAttr)) {
        		String conditionCoverageAttr = lineElement.attributeValue("condition-coverage");
        		branchNumerator+=getCovered(conditionCoverageAttr);
        		branchDenominator+=getTotal(conditionCoverageAttr);
        	}
        }
    	return branchCoverageDeclaration.createMetric(branchNumerator, branchDenominator);
    }
    
    
    double getCovered(String attributeValue) {
        int openParenIndex = attributeValue.indexOf("(") + 1;
        int slashIndex = attributeValue.indexOf("/");
        String covered = attributeValue.substring(openParenIndex, slashIndex);
        return Double.valueOf(covered);
    }

    double getTotal(String attributeValue) {
        int slashIndex = attributeValue.indexOf("/") + 1;
        int closeParenIndex = attributeValue.indexOf(")");
        String total = attributeValue.substring(slashIndex, closeParenIndex);
        return Double.valueOf(total);
    }


  

    public SupplementDeclaration getDeclaration() {
        if (declaration == null) {
            branchCoverageDeclaration = new RatioMetricDeclaration(this, "Branch Coverage");
            branchCoverageDeclaration.addLevel(Level.FILE);
            branchCoverageDeclaration.addLevel(Level.CLASS);
            branchCoverageDeclaration.addLevel(Level.METHOD);

            lineCoverageDeclaration = new RatioMetricDeclaration(this, "Line Coverage");
            lineCoverageDeclaration.addLevel(Level.FILE);
            lineCoverageDeclaration.addLevel(Level.CLASS);
            lineCoverageDeclaration.addLevel(Level.METHOD);

            declaration = new SupplementDeclaration(this.getClass().getName());
            declaration.addMetricDeclaration(branchCoverageDeclaration);
            declaration.addMetricDeclaration(lineCoverageDeclaration);
        }

        return declaration;
    }

    String formatClassName(PanopticodeClass panopticodeClass) {
        String name = panopticodeClass.getName();

        while (name.indexOf(".") != -1) {
            name = name.replace('.', '$');
        }
        if(panopticodeClass.getPackage().isDefaultPackage()) {
        	return name;
        }
        else {
        	return panopticodeClass.getPackage().getName()+"."+name;
        }
    }

    String formatMethodName(PanopticodeMethod panopticodeMethod) {
    	
    	if(panopticodeMethod.isConstructor()) {
    		return "<init>";
    	}
        StringBuffer sb = new StringBuffer();
        String name = panopticodeMethod.getName();

        while (name.indexOf(".") != -1) {
            name = name.replace('.', '$');
        }

        sb.append(name);

        return sb.toString();
    }

    String formatType(PanopticodeArgument panopticodeArgument) {
        String typeName = panopticodeArgument.getSimpleType();

        if ("T".equals(typeName)) {
            typeName = "Object";
        } else if (typeName.startsWith("T[")) {
            typeName = typeName.replace("T[", "Object[");
        }

        typeName = replaceAll(typeName, '.', '$');

        // We need to insert a space before the first bracket in array types
        int bracketIndex = typeName.indexOf("[");
        if (bracketIndex == -1) {
            return typeName;
        }

        String prefix = typeName.substring(0, bracketIndex);
        String suffix = typeName.substring(bracketIndex);

        return prefix + " " + suffix;
    }

    private String replaceAll(String replaceIn, char replace, char with) {
        String replaced = replaceIn;

        while (replaced.indexOf(replace) != -1) {
            replaced = replaced.replace(replace, with);
        }

        return replaced;
    }

    Element getElementByMethod(Document document,
                               PanopticodeMethod panopticodeMethod)
        throws CouldntFindElementException, CouldntNarrowMethodMatchesException {
        Element classElement = getElementByClass(document, panopticodeMethod.getParentClass());
        Element methodsElement = classElement.element("methods");
        LinkedList<Element> possibleElements = getPossibleMethodMatches(panopticodeMethod, methodsElement);

        if (possibleElements.size() == 1) {
            return possibleElements.get(0);
        } else {
            throw new CouldntNarrowMethodMatchesException(panopticodeMethod, possibleElements);
        }
    }

    private LinkedList<Element> getPossibleMethodMatches(PanopticodeMethod panopticodeMethod,
                                                         Element methodsElement)
        throws CouldntFindElementException {
        String methodName = formatMethodName(panopticodeMethod);
        LinkedList<Element> possibleElements = new LinkedList<Element>();

        for (Object objMethodElement : methodsElement.elements("method")) {
            Element methodElement = (Element) objMethodElement;
            String elementMethodName = methodElement.attributeValue("name");
            String signature = methodElement.attributeValue("signature");
            if (elementMethodName.equals(methodName)) {
                if (argumentsCouldMatch(panopticodeMethod, signature)) {
                    possibleElements.add(methodElement);
                }
            }
        }

        if (possibleElements.size() == 0) {
            throw new CouldntFindElementException("Couldn't find method '"
                    + methodName + "' in class '" + panopticodeMethod.getParentClass().getFullyQualifiedName() + "'");
        }

        return possibleElements;
    }




    boolean argumentsCouldMatch(PanopticodeMethod panopticodeMethod, String signature) {
        List<PanopticodeArgument> methodArguments;
        List<String> arguments;

        arguments = parseArguments(signature);
        methodArguments = panopticodeMethod.getArguments();

        PanopticodeClass parentClass = panopticodeMethod.getParentClass();
        if(panopticodeMethod.isConstructor() && parentClass.isInnerClass() && !parentClass.isStatic()) {
            // Pull the one synthetic argument off the beginning of the list
        	arguments.remove(0);
        }

        if(parentClass.isEnum() && panopticodeMethod.isConstructor()) {
            // Pull the two synthetic args offthe beginning of the list
        	arguments.remove(0);
        	arguments.remove(0);
        }
        if (arguments.size()!= methodArguments.size()) {
            // filter out any with the wrong number of arguments
            return false;
        }

        for (int i = 0; i < arguments.size(); i++) {
            // filter out any with impossible argument types
            String typeName = methodArguments.get(i).getFullyQualifiedType();
            if (!arguments.get(i).trim().equals(typeName)) {
                return false;
            }
        }

        return true;
    }

    private List<String> parseArguments(String methodSignature) {
        if (methodSignature.indexOf("()") == -1) {
            int openParenIndex = methodSignature.indexOf("(");
            String afterFirstParen = methodSignature.substring(openParenIndex + 1);
            int closeParenIndex = afterFirstParen.indexOf(")");
            String argumentsOnly = afterFirstParen.substring(0, closeParenIndex);
            
            List<String> arguments = new LinkedList<String>();
        	boolean isArray = false;
            for(int index=0;index<argumentsOnly.length();index++) {
            	if(argumentsOnly.charAt(index)=='B') {
            		arguments.add(isArray?"byte[]":"byte");
            		isArray = false;
            	} else if(argumentsOnly.charAt(index)=='S') {
            		arguments.add(isArray?"short[]":"short");
            		isArray = false;
            	} else if(argumentsOnly.charAt(index)=='C') {
            		arguments.add(isArray?"char[]":"char");
            		isArray = false;
            	} else if(argumentsOnly.charAt(index)=='I') {
            		arguments.add(isArray?"int[]":"int");
            		isArray = false;
            	} else if(argumentsOnly.charAt(index)=='J') {
            		arguments.add(isArray?"long[]":"long");
            		isArray = false;
            	} else if(argumentsOnly.charAt(index)=='Z'){
            		arguments.add(isArray?"boolean[]":"boolean");
            		isArray = false;
            	} else if(argumentsOnly.charAt(index)=='F') {
            		arguments.add(isArray?"float[]":"float");
            		isArray = false;
            	} else if(argumentsOnly.charAt(index)=='D'){
            		arguments.add(isArray?"double[]":"double");
            		isArray = false;
            	} else if(argumentsOnly.charAt(index)=='['){
            		isArray = true;
            	} else if(argumentsOnly.charAt(index)=='L') {
            		int colonIndex = argumentsOnly.indexOf(';', index);
            		String argument = argumentsOnly.substring(index+1, colonIndex).replaceAll("/", ".");
					arguments.add(isArray?argument+"[]":argument);
            		index=colonIndex;
            		isArray = false;
            	} else {
            		// Specialize the exception.
            		throw new RuntimeException("Found invalid character - " + argumentsOnly.charAt(index));
            	}
            }
            
            return arguments;
        } else {
            return new LinkedList<String>();
        }
    }

    Element getElementByClass(Document document, PanopticodeClass panopticodeClass)
        throws CouldntFindElementException {
        Element packageElement = getElementByPackage(document, panopticodeClass.getPackage());
        Element classesElement = packageElement.element("classes");
        String className = formatClassName(panopticodeClass);
        Element classElement = getElementByName(classesElement, "class", className);

        if (classElement == null) {
            throw new CouldntFindElementException("Couldn't find class '"
                    + className + "' in file '" + panopticodeClass.getParentFile().getPath() + "'");
        }

        return classElement;
    }


    Element getElementByPackage(Document document, PanopticodePackage panopticodePackage)
        throws CouldntFindElementException {
        String name = panopticodePackage.getName();

        Element parent = document.getRootElement().element("packages");

        Element packageElement = getElementByName(parent, "package", name);

        if (packageElement == null) {
            throw new CouldntFindElementException("Couldn't find package '" + name + "'");
        }

        return packageElement;
    }

    /**
     * Get element using the name attribute.
     * @param parent The parent XML element
     * @param tag The name of the tag
     * @param name The value of name attribute.
     * @return
     */
    private Element getElementByName(Element parent, String tag, String name) {
        for (Object objElement : parent.elements(tag)) {
            Element candidate = (Element) objElement;
            if (name.equals(candidate.attributeValue("name"))) {
                return candidate;
            }
        }

        return null;
    }


    void loadClassData(PanopticodeProject project, Document document) {
        for (PanopticodeClass panopticodeClass : project.getClasses()) {
            if (coverageAppliesTo(panopticodeClass)) {
                try {
                    Element classElement = getElementByClass(document, panopticodeClass);

                    addCoverageToClass(classElement, panopticodeClass);
                } catch (CouldntFindElementException e) {
                    declaration.addError("ERROR - CoberturaSupplement - " + e.getMessage());
                }
            }
        }
    }

    public void loadData(PanopticodeProject project, String[] arguments) {
        project.addSupplementDeclaration(getDeclaration());

        SAXReader saxReader;
        Document document;

        saxReader = new SAXReader();
        try {
        	saxReader.setValidation(false);
            document = saxReader.read(arguments[0]);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }

        loadMethodData(project, document);
        loadClassData(project, document);
        computeFileDataFromClassData(project);
    }

    private void computeFileDataFromClassData(PanopticodeProject project) {
    	for(PanopticodeFile file : project.getFiles()) {
    		file.addMetric(aggregateMetricPerFile(file, "Line Coverage", lineCoverageDeclaration));
    		file.addMetric(aggregateMetricPerFile(file, "Branch Coverage", branchCoverageDeclaration));
    	}
	}
    
	RatioMetric aggregateMetricPerFile(PanopticodeFile file, String name, RatioMetricDeclaration decl) {
		double numerator = 0, denominator = 0;
		for(PanopticodeClass clazz : file.getClasses()) {
			RatioMetric metric = (RatioMetric) clazz.getMetricByName(name);
			if(metric!=null) {
				numerator+=metric.getNumeratorValue();
				denominator+=metric.getDenominatorValue();
			}
		}
		return decl.createMetric(numerator, denominator);
	}


	void loadMethodData(PanopticodeProject project, Document document) {
        for (PanopticodeMethod panopticodeMethod : project.getMethods()) {
            if (coverageAppliesTo(panopticodeMethod)) {

                try {
                    Element methodElement = getElementByMethod(document, panopticodeMethod);

                    addCoverageToMethod(methodElement, panopticodeMethod);
                } catch (CouldntFindElementException e) {
                    declaration.addError("ERROR - CoberturaSupplement - " + e.getMessage());
                } catch (CouldntNarrowMethodMatchesException e) {
                    declaration.addError("ERROR - CoberturaSupplement - " + e.getMessage());
                }
            }
        }
    }
    
    void loadFileDataFromClassData(PanopticodeProject project, Document document) {
    	for(PanopticodeFile file : project.getFiles()) {
    		file.addMetric(computeFileMetric(file, "Line Coverage", lineCoverageDeclaration));
    		file.addMetric(computeFileMetric(file, "Branch Coverage", branchCoverageDeclaration));
    	}
    }

	private RatioMetric computeFileMetric(PanopticodeFile file, String metricName, RatioMetricDeclaration decl) {
		double numerator=0, denominator=0;
		for(PanopticodeClass clazz : file.getClasses()) {
			
			Metric metric = clazz.getMetricByName(metricName);
			if(metric!=null) {
				numerator+=((RatioMetric)metric).getNumeratorValue();
				denominator+=((RatioMetric)metric).getDenominatorValue();
			}
		}
		return decl.createMetric(numerator, denominator);
	}

    boolean coverageAppliesTo(PanopticodeClass panopticodeClass) {
        return !panopticodeClass.isInterface();
    }

    boolean coverageAppliesTo(PanopticodeMethod panopticodeMethod) {
        return !(panopticodeMethod.isAbstract() || panopticodeMethod.getParentClass().isInterface());
    }
}
