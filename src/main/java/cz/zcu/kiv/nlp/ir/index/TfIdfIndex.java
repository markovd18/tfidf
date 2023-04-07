package cz.zcu.kiv.nlp.ir.index;

import static cz.zcu.kiv.nlp.ir.utils.ValidationUtils.checkNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TfIdfIndex {

  private final Tokenizer tokenizer;

  private List<Document> documents;

  public TfIdfIndex(final Tokenizer tokenizer) {
    checkNotNull(tokenizer, "tokenizer");
    this.tokenizer = tokenizer;
  }

  public void index(final List<Document> documents) {
    if (documents == null || documents.isEmpty()) {
      return;
    }

    final Dictionary dictionary = new Dictionary();
    for (final var document : documents) {
      final String[] tokens = document.tokenize(tokenizer);
      dictionary.addRecords(Arrays.stream(tokens).collect(Collectors.toSet()));
    }

    final int documentCount = documents.size();
    for (final var entry : dictionary.getRecords()) {
      final String term = entry.getTerm();
      final int documentFrequency = entry.getDocumentFrequency();
      final double idf = invertedDocumentFrequency(documentCount, documentFrequency);
      for (final var document : documents) {
        final double termFrequency = document.getTermWeight(term);
        final double tfidf = tfIdfWeight(termFrequency, idf);
        System.out.format("Term: %s, TF-IDF: %.5f\n", term, tfidf);
        document.updateTermWeight(term, tfidf);
      }
      System.out.format("Term: %s, document frequency: %d, IDF: %.5f\n", term, documentFrequency, idf);
    }

    for (final var document : documents) {
      normalizeDocumentWeights(document);
    }

    this.documents = documents;

  }

  public List<Document> queryNDocuments(final String queryString, final int n) {
    if (documents == null || documents.isEmpty()) {
      return Collections.emptyList();
    }

    // TODO
    return documents;
  }

  private double invertedDocumentFrequency(final int documentCount, final int termDocumentFrequency) {
    if (termDocumentFrequency <= 0 || documentCount <= 0) {
      return 0;
    }

    return Math.log10(documentCount / ((double) termDocumentFrequency));
  }

  private double tfIdfWeight(final double termFrequency, final double invertedDocumentFrequency) {
    if (termFrequency <= 0) {
      return 0;
    }

    return (1 + Math.log10(termFrequency)) * invertedDocumentFrequency;
  }

  private static double calculateDocumentNorm(final Document document) {
    double sum = 0;
    for (final var termWeight : document.getTermWeights()) {
      sum += termWeight.getWeight() * termWeight.getWeight();
    }

    return Math.sqrt(sum);
  }

  private static void normalizeDocumentWeights(final Document document) {
    final double norm = calculateDocumentNorm(document);
    for (final var termWeight : document.getTermWeights()) {
      final double normalizedWeight = termWeight.getWeight() / norm;
      System.out.format("Term: %s, normalized weight: %.5f\n", termWeight.getTerm(), normalizedWeight);
      document.updateTermWeight(termWeight.getTerm(), normalizedWeight);
    }
  }

}
