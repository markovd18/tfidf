package cz.zcu.kiv.nlp.ir;

import java.util.List;

import cz.zcu.kiv.nlp.ir.index.AdvancedTokenizer;
import cz.zcu.kiv.nlp.ir.index.QueryResult;
import cz.zcu.kiv.nlp.ir.index.TfIdfIndex;
import cz.zcu.kiv.nlp.ir.storage.AssignmentStorage;

public class Main {

  private static final String STORAGE_DEFAULT_PATH = "./storage";

  public static void main(final String[] args) {
    testAssignmentDocuments();
  }

  private static void testAssignmentDocuments() {
    final var storage = new AssignmentStorage();
    final var tokenizer = new AdvancedTokenizer();
    final var index = new TfIdfIndex(tokenizer);

    index.index(storage.getAssignmentOneDocuments());
    final var resultOne = index.queryNDocuments("krásné město", 1);
    printResult(resultOne);

    index.index(storage.getAssignmentTwoDocuments());
    final var resultTwo = index.queryNDocuments("tropical fish sea", 1);
    printResult(resultTwo);
    final var resultThree = index.queryNDocuments("tropical fish", 1);
    printResult(resultThree);
  }

  private static void printResult(final List<QueryResult> result) {
    for (final var queryResult : result) {
      System.out.format("Document ID: %d, score: %.5f\n", queryResult.getDocumentId(), queryResult.getScore());
    }
  }
}