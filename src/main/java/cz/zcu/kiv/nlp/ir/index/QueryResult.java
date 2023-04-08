package cz.zcu.kiv.nlp.ir.index;

public class QueryResult implements Comparable<QueryResult> {
  private final double score;
  private final Document document;

  public QueryResult(final double score, final Document document) {
    this.score = score;
    this.document = document;
  }

  public double getScore() {
    return score;
  }

  public Document getDocument() {
    return document;
  }

  @Override
  public int compareTo(final QueryResult other) {
    // comparing algorithm is intentionally interchanged since java's PriorityQueue
    // implementation stores the "smallest" elements as first
    return score < other.score ? 1 : score > other.score ? -1 : 0;
  }

}
