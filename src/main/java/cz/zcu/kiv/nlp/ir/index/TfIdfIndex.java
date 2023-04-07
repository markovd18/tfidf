package cz.zcu.kiv.nlp.ir.index;

import static cz.zcu.kiv.nlp.ir.utils.ValidationUtils.checkNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Collectors;

public class TfIdfIndex {

  private final Tokenizer tokenizer;

  private Dictionary dictionary = new Dictionary();
  private List<Document> documents;

  public TfIdfIndex(final Tokenizer tokenizer) {
    checkNotNull(tokenizer, "tokenizer");
    this.tokenizer = tokenizer;
  }

  public void index(final List<Document> documents) {
    if (documents == null || documents.isEmpty()) {
      return;
    }

    dictionary.clear();
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
        System.out.format("Term: %s, Document ID: %d, TF-IDF: %.5f\n", term, document.getId(), tfidf);
        document.updateTermWeight(term, tfidf);
      }
      System.out.format("Term: %s, document frequency: %d, IDF: %.5f\n", term, documentFrequency, idf);
    }

    for (final var document : documents) {
      normalizeDocumentWeights(document);
    }

    this.documents = documents;

  }

  public List<QueryResult> queryNDocuments(final String queryString, final int n) {
    if (documents == null || documents.isEmpty()) {
      return Collections.emptyList();
    }

    // TODO udělat nějak hezky, aby tady nebyla tak silná vazba na implementaci
    // dokumentu
    final var queryDocument = new Document(queryString);
    queryDocument.tokenize(tokenizer);
    for (final var entry : dictionary.getRecords()) {
      final String term = entry.getTerm();
      final int documentFrequency = entry.getDocumentFrequency();
      final double idf = invertedDocumentFrequency(documents.size(), documentFrequency);
      final double termFrequency = queryDocument.getTermWeight(term);
      final double tfidf = tfIdfWeight(termFrequency, idf);
      System.out.format("Term: %s, TF-IDF: %.5f\n", term, tfidf);
      queryDocument.updateTermWeight(term, tfidf);
    }

    normalizeDocumentWeights(queryDocument);

    final Queue<QueryResult> queue = new PriorityQueue<>();
    for (final var document : documents) {
      final double cosineSimiliarity = normalizedCosineSimiliarity(document, queryDocument);
      System.out.format("Document ID: %d, similiarity: %.5f\n", document.getId(), cosineSimiliarity);
      queue.add(new QueryResult(cosineSimiliarity, document.getId()));
    }

    final List<QueryResult> result = new ArrayList<>();
    var queryResult = queue.poll();
    while (result.size() != n && queryResult != null) {
      result.add(queryResult);
      queryResult = queue.poll();
    }

    return result;
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
    System.out.format("Document ID: %d, norm: %.5f\n", document.getId(), norm);
    for (final var termWeight : document.getTermWeights()) {
      final double normalizedWeight = termWeight.getWeight() / norm;
      System.out.format("Term: %s, normalized weight: %.5f\n", termWeight.getTerm(), normalizedWeight);
      document.updateTermWeight(termWeight.getTerm(), normalizedWeight);
    }
  }

  private double normalizedCosineSimiliarity(final Document first, final Document second) {
    double similiarity = 0;
    for (final var entry : dictionary.getRecords()) {
      final String term = entry.getTerm();
      similiarity += first.getTermWeight(term) * second.getTermWeight(term);
    }

    return similiarity;
  }

}
