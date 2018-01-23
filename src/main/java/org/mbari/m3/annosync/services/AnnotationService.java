package org.mbari.m3.annosync.services;

import java.util.Collection;

/**
 * AnnotationService
 */
public interface AnnotationService {

    public void updateConceptInAnnotations(String newName, Collection<String> oldNames);
    
}