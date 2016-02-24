package org.panopticode.report.treemap;

import org.dom4j.Element;
import org.panopticode.MetricTarget;
import edu.umd.cs.treemap.Rect;

import java.util.List;
import java.util.LinkedList;

public class Categorizer {
    private List<Category> categories;
    private DefaultCategory defaultCategory;

    public Categorizer(List<Category> categories, DefaultCategory defaultCategory) {
        this.categories = new LinkedList<Category>(categories);

        this.defaultCategory = defaultCategory;
    }

    public Category getCategory(MetricTarget target) {
        for(Category cat : categories) {
            if(cat.inCategory(target)) {
                return cat;
            }
        }
        
        return defaultCategory;
    }

    List<Category> getCategories() {
        return categories;
    }

    DefaultCategory getDefaultCategory() {
        return defaultCategory;
    }

    public void renderHorizontalLegend(Rect bounds, Element parentElement) {
        double boxX = bounds.x + 3.0;
        double boxY = bounds.y;

        double xOffset = bounds.w / (categories.size() + 1);

        renderRectangle(parentElement, bounds.x, bounds.y, bounds.w, bounds.h, "black", "none");
        
        for (Category cat : categories) {
            renderHorizontalLegendItem(parentElement, cat, boxX, boxY);

            boxX += xOffset;
        }

        renderHorizontalLegendItem(parentElement, defaultCategory, boxX, boxY);
    }

    public void renderHorizontalLegendItem(Element parentElement, Category cat, double x, double y) {
        renderRectangle(parentElement, x + 3.0, y + 5.0, 15.0, 15.0, cat.getBorder(), cat.getFill());

        Element keyText = parentElement.addElement("text", "http://www.w3.org/2000/svg");
        keyText.addAttribute("x", String.valueOf(x + 25.0));
        keyText.addAttribute("y", String.valueOf(y + 17.0));
        keyText.addText(cat.getLegendLabel());
    }

    private Element renderRectangle(Element parentElement, double x, double y, double width, double height, String border, String fill) {
        Element rect = parentElement.addElement("rect", "http://www.w3.org/2000/svg");

        rect.addAttribute("x", String.valueOf(x));
        rect.addAttribute("y", String.valueOf(y));
        rect.addAttribute("width", String.valueOf(width));
        rect.addAttribute("height", String.valueOf(height));
        rect.addAttribute("fill", fill);
        rect.addAttribute("stroke", border);
        rect.addAttribute("stroke-width", "1");

        return rect;
    }
}
