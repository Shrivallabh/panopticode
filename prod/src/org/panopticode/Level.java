package org.panopticode;

public enum Level {
    PROJECT ("project"),
    PACKAGE ("package"),
    FILE ("file"),
    CLASS ("class"),
    METHOD ("method");

    private String name;

    Level(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
