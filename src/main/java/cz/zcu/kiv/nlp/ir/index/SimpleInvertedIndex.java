package cz.zcu.kiv.nlp.ir.index;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SimpleInvertedIndex implements InvertedIndex {

  private final Map<String, Set<Integer>> termToPostingsMap = new HashMap<>();

  public void addPosting(final String term, final int docId) {
    final var postings = termToPostingsMap.get(term);
    if (postings == null) {
      final var newPostings = new HashSet<Integer>();
      newPostings.add(docId);
      termToPostingsMap.put(term, newPostings);
    } else {
      postings.add(docId);
    }
  }

}
