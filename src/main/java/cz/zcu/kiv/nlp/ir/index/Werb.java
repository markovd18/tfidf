package cz.zcu.kiv.nlp.ir.index;

import static cz.zcu.kiv.nlp.ir.utils.ValidationUtils.checkNotNull;

public class Werb {

  private final String term;
  private final int documentFrequency;

  public Werb(final String term, final int termFrequency) {
    checkNotNull(term, "Term");

    this.term = term;
    this.documentFrequency = termFrequency;
  }

  public String getTerm() {
    return term;
  }

  public int getDocumentFrequency() {
    return documentFrequency;
  }

}
