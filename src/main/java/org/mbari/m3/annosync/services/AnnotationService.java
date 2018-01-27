package org.mbari.m3.annosync.services;

import org.mbari.m3.annosync.ConceptNameChangedMsg;

import java.util.Collection;

/**
 * AnnotationService
 */
public interface AnnotationService {

    void handleNameChange(ConceptNameChangedMsg msg);
    
}