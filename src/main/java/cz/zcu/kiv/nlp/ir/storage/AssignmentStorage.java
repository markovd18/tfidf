package cz.zcu.kiv.nlp.ir.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import cz.zcu.kiv.nlp.ir.index.Document;

public class AssignmentStorage {

  private final List<Document> assignmentOneDocuments = new ArrayList<>(3);
  private final List<Document> assignmentTwoDocuments = new ArrayList<>(5);

  public AssignmentStorage() {
    assignmentOneDocuments.add(new Document("Plzeň je krásné město a je to krásné místo"));
    assignmentOneDocuments.add(new Document("Ostrava je ošklivé místo"));
    assignmentOneDocuments.add(new Document("Praha je také krásné město Plzeň je hezčí"));

    assignmentTwoDocuments.add(new Document("tropical fish include fish found in tropical environments"));
    assignmentTwoDocuments.add(new Document("fish live in a sea"));
    assignmentTwoDocuments.add(new Document("tropical fish are popular aquarium"));
    assignmentTwoDocuments.add(new Document("fish also live in Czechia"));
    assignmentTwoDocuments.add(new Document("Czechia is a country"));
  }

  public List<Document> getAssignmentOneDocuments() {
    return Collections.unmodifiableList(assignmentOneDocuments);
  }

  public List<Document> getAssignmentTwoDocuments() {
    return Collections.unmodifiableList(assignmentTwoDocuments);
  }
}
