package cz.zcu.kiv.nlp.ir.index;

import static cz.zcu.kiv.nlp.ir.utils.ValidationUtils.checkNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.stream.Collectors;

import cz.zcu.kiv.nlp.ir.utils.ValidationUtils;

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
        document.updateTermWeight(term, tfidf);
      }
    }

    for (final var document : documents) {
      normalizeDocumentWeights(document);
    }

    this.documents = documents;

  }

  public List<QueryResult> queryNDocuments(final String queryString, final int n) {
    ValidationUtils.checkNotNull(queryString, "Query");
    System.out.format("Querrying '%s'...\n", queryString);
    if (documents == null || documents.isEmpty()) {
      return Collections.emptyList();
    }

    final var queryDocument = new Document(queryString);
    queryDocument.tokenize(tokenizer);
    for (final var entry : dictionary.getRecords()) {
      final String term = entry.getTerm();
      final int documentFrequency = entry.getDocumentFrequency();
      final double idf = invertedDocumentFrequency(documents.size(), documentFrequency);
      final double termFrequency = queryDocument.getTermWeight(term);
      final double tfidf = tfIdfWeight(termFrequency, idf);
      queryDocument.updateTermWeight(term, tfidf);
    }

    normalizeDocumentWeights(queryDocument);

    final Queue<QueryResult> queue = new PriorityQueue<>();
    for (final var document : documents) {
      final double cosineSimiliarity = normalizedCosineSimiliarity(document, queryDocument);
      queue.add(new QueryResult(cosineSimiliarity, document));
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
    for (final var termWeight : document.getTermWeights()) {
      final double normalizedWeight = termWeight.getWeight() / norm;
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
