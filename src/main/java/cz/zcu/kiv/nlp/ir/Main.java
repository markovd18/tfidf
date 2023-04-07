package cz.zcu.kiv.nlp.ir;

import cz.zcu.kiv.nlp.ir.index.AdvancedTokenizer;
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
    final var resultOne = index.queryNDocuments("krásné město", 2);

  }
}