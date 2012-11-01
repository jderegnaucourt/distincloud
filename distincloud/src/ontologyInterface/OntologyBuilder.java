package ontologyInterface;

import ontologyImpl.OntologyImpl;

public abstract class OntologyBuilder {
  public static Ontology createNewOntology(
      String mainOntologyFileName,
      String externOntologyFileName) {
    return new OntologyImpl(mainOntologyFileName,
        externOntologyFileName);
  }
}
