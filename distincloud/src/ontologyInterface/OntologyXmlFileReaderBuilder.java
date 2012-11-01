package ontologyInterface;

import java.io.IOException;

import ontologyImpl.OntologyXmlFileReader;

public abstract class OntologyXmlFileReaderBuilder {
  public static Ontology createNewOntologyXmlFileReader(
      String filePath, String collectionTag,
      String elementTag, String synonymTag,
      String ptrTag, String mainOntologyFileName,
      String externOntologyFileName) throws IOException {
    return new OntologyXmlFileReader(filePath,
        collectionTag, elementTag, synonymTag, ptrTag,
        mainOntologyFileName, externOntologyFileName);
  }
}
