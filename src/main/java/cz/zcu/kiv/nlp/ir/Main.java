package cz.zcu.kiv.nlp.ir;

import java.util.List;

import cz.zcu.kiv.nlp.ir.index.AdvancedTokenizer;
import cz.zcu.kiv.nlp.ir.index.Document;
import cz.zcu.kiv.nlp.ir.index.QueryResult;
import cz.zcu.kiv.nlp.ir.index.TfIdfIndex;
import cz.zcu.kiv.nlp.ir.index.Tokenizer;
import cz.zcu.kiv.nlp.ir.storage.AssignmentStorage;
import cz.zcu.kiv.nlp.ir.storage.FSStorage;

public class Main {

  private static final String STORAGE_DEFAULT_PATH = "./storage";

  public static void main(final String[] args) {
    final var tokenizer = new AdvancedTokenizer();
    final var index = new TfIdfIndex(tokenizer);
    testAssignmentDocuments(index, tokenizer);
    testCrawledDocuments(index, tokenizer);
  }

  private static void testAssignmentDocuments(final TfIdfIndex index, final Tokenizer tokenizer) {
    final var storage = new AssignmentStorage();

    index.index(storage.getAssignmentOneDocuments());
    final var resultOne = index.queryNDocuments("krásné město", 1);
    printResult(resultOne);

    index.index(storage.getAssignmentTwoDocuments());
    final var resultTwo = index.queryNDocuments("tropical fish sea", 1);
    printResult(resultTwo);
    final var resultThree = index.queryNDocuments("tropical fish", 1);
    printResult(resultThree);
  }

  private static void testCrawledDocuments(final TfIdfIndex index, final Tokenizer tokenizer) {
    final var storage = new FSStorage<Document>(STORAGE_DEFAULT_PATH,
        (lines) -> new Document(String.join("\n", lines)));
    index.index(storage.getEntries().stream().toList());
    final int resultSize = 2;

    // article-04.txt
    var result = index.queryNDocuments("SESTŘIHY 50. kola:", resultSize);
    printResult(result);

    // article-02.txt
    result = index.queryNDocuments("nikdo jiný nám nepomůže", resultSize);
    printResult(result);

    // article-13.txt
    result = index.queryNDocuments("Miroslav Holec se okamžitě po svém příchodu", resultSize);
    printResult(result);
  }

  private static void printResult(final List<QueryResult> result) {
    for (final var queryResult : result) {
      final var document = queryResult.getDocument();
      System.out.format("Document ID: %d, score: %.5f, snippet: %s...\n", document.getId(), queryResult.getScore(),
          document.getText().subSequence(0, Math.min(50, document.getText().length())));
    }
  }
}