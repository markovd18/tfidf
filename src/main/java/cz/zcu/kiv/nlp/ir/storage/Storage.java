package cz.zcu.kiv.nlp.ir.storage;

import java.util.Set;

/**
 * Storage provides an interface to load indexable documents.
 */
public interface Storage<TDocument> {

  Set<TDocument> getEntries();
}
