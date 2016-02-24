package org.panopticode.util;

import static org.panopticode.util.StringHelper.*;
import static org.panopticode.util.ArgumentValidation.*;

import org.dom4j.Element;

public class SVGHelper {
    private final static String SVG_NAMESPACE = "http://www.w3.org/2000/svg";
    public static Element addGrouping(Element parent, String id, String name, String description) {
        Element gElement;

        gElement = parent.addElement("g", SVG_NAMESPACE);

        addAttributeIfNotEmpty(gElement, "id", id);
        addAttributeIfNotEmpty(gElement, "name", name);
        addAttributeIfNotEmpty(gElement, "desc", description);

        return gElement;
    }

    public static Element addRectangle(Element parent,
                                       double x, double y, double width, double height,
                                       String fill, String border) {
        Element rectElement;

        failIfNotANumber(x, "x");
        failIfNotANumber(y, "y");
        failIfNotANumber(width, "width");
        failIfNotANumber(height, "height");

        rectElement = parent.addElement("rect", SVG_NAMESPACE);

        rectElement.addAttribute("x", String.valueOf(x));
        rectElement.addAttribute("y", String.valueOf(y));
        rectElement.addAttribute("width", String.valueOf(width));
        rectElement.addAttribute("height", String.valueOf(height));
        rectElement.addAttribute("fill", fill);
        rectElement.addAttribute("stroke", border);
        rectElement.addAttribute("stroke-width", "1");

        return rectElement;
    }

    public static Element addText(Element parent, double x, double y, String text) {
        Element textElement;

        textElement = parent.addElement("text", SVG_NAMESPACE);

        textElement.addAttribute("x", String.valueOf(x));
        textElement.addAttribute("y", String.valueOf(y));
        textElement.addText(text);

        return textElement;
    }

    private static void addAttributeIfNotEmpty(Element element, String name, String value) {
        if(notEmpty(value)) {
            element.addAttribute(name, value);
        }
    }

    public static String cleanId(String originalId) {
        // Id tokens must begin with a letter ([A-Za-z]) and may be followed by any number
        // of letters, digits ([0-9]), hyphens ("-"), underscores ("_"), colons (":"),
        // and periods (".")
        boolean firstCharacter;
        char[] characters;
        StringBuilder sb;

        characters = originalId.toCharArray();
        sb = new StringBuilder();

        firstCharacter = true;

        for (char character : characters) {
            if(firstCharacter) {
                if (Character.isLetter(character)) {
                    sb.append(character);
                } else {
                    sb.append("x_");
                }
            } else {
                if (Character.isLetter(character) || Character.isDigit(character) || validIdSpecialCharacter(character)) {
                    sb.append(character);
                } else {
                    sb.append('_');
                }
            }

            firstCharacter = false;
        }
        
        return sb.toString();
    }

    private static boolean validIdSpecialCharacter(char character) {
        switch(character) {
            case '-':
            case '_':
            case ':':
            case '.':
                return true;
            default:
                return false;
        }

    }
}
