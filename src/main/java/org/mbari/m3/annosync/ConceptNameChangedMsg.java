package org.mbari.m3.annosync;

import java.util.List;

/**
 * @author Brian Schlining
 * @since 2018-01-26T09:30:00
 */
public class ConceptNameChangedMsg {

    private final String message = "rename concept";
    private final String newName;
    private final List<String> oldNames;

    public ConceptNameChangedMsg(String newName, List<String> oldNames) {
        this.newName = newName;
        this.oldNames = oldNames;
    }

    public String getMessage() {
        return message;
    }

    public String getNewName() {
        return newName;
    }

    public List<String> getOldNames() {
        return oldNames;
    }

}
