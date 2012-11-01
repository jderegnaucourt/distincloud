package ontologyInterface;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;

public interface Ontology {
  String getMainOntologyFileName();

  void setMainOntologyFileNameAndWrite(
      String mainOntologyFileName)
      throws IOException;

  String getExternOntologyFileName();

  void setExternOntologyFileNameAndWrite(
      String externOntologyFileName)
      throws IOException;

  Word searchWordMainOntology(String wordText)
      throws OntologyException;

  ExternalWord searchWordExternOntology(String wordText)
      throws OntologyException;

  int getRelatedness(int maxRelatedness,
      String wordText1, Set<Integer> sensesIndex1,
      String wordText2, Set<Integer> sensesIndex2,
      EnumSet<SemRelation> semanticRelations)
      throws OntologyException;

  ExternalWord addExternWord(String wordText,
      String wordTextCopySensesFrom)
      throws OntologyException, IOException;

  boolean removeExternWord(String wordText)
      throws OntologyException, IOException;
}
