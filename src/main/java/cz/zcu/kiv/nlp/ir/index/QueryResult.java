package cz.zcu.kiv.nlp.ir.index;

public class QueryResult implements Comparable<QueryResult> {
  private final double score;
  private final int documentId;

  public QueryResult(final double score, final int documentId) {
    this.score = score;
    this.documentId = documentId;
  }

  public double getScore() {
    return score;
  }

  public int getDocumentId() {
    return documentId;
  }

  @Override
  public int compareTo(final QueryResult other) {
    return score < other.score ? 1 : score > other.score ? -1 : 0;
  }

}
