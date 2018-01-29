package org.mbari.m3.annosync.services.annosaurus.v1;

/**
 * @author Brian Schlining
 * @since 2018-01-26T11:38:00
 */
public class RenameResponse {
    private final String oldConcept;
    private final String newConcept;
    private final Integer numberUpdated;

    public RenameResponse(String oldConcept, String newConcept, Integer numberUpdated) {
        this.oldConcept = oldConcept;
        this.newConcept = newConcept;
        this.numberUpdated = numberUpdated;
    }

    public String getOldConcept() {
        return oldConcept;
    }

    public String getNewConcept() {
        return newConcept;
    }

    public Integer getNumberUpdated() {
        return numberUpdated;
    }
}
