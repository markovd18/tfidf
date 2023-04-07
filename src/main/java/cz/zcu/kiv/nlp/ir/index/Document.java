package cz.zcu.kiv.nlp.ir.index;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Document {

  private static int nextId = 0;

  private final int id;
  private final String text;
  private final Map<String, Double> termWeights = new HashMap<>();

  public Document(final String text) {
    this.id = nextId++;
    this.text = text;
  }

  public String getText() {
    return text;
  }

  public int getId() {
    return id;
  }

  public String[] tokenize(final Tokenizer tokenizer) {
    final var tokens = tokenizer.tokenize(text);
    for (final var token : tokens) {
      final var occurrenceCount = termWeights.putIfAbsent(token, 1.0);
      if (occurrenceCount != null) {
        termWeights.put(token, occurrenceCount + 1);
      }
    }

    return tokens;
  }

  public void updateTermWeight(final String term, final double weight) {
    if (termWeights.containsKey(term)) {
      termWeights.put(term, weight);
    }
  }

  public double getTermWeight(final String term) {
    if (termWeights.containsKey(term)) {
      return termWeights.get(term);
    }

    return 0;
  }

  public Set<TermWeight> getTermWeights() {
    return termWeights.entrySet()
        .stream()
        .map(entry -> new TermWeight(entry.getKey(), entry.getValue()))
        .collect(Collectors.toSet());
  }

}
