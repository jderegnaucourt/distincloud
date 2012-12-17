package ontologyInterface;

import java.io.IOException;
import java.io.Serializable;

public interface ExternalSense extends Sense {
	
  void setDefinition(String definition)
      throws IOException;

  boolean addMainLink(SemRelation semRelation,
      String mainWordText, int senseIndex)
      throws IOException;

  boolean removeMainLink(SemRelation semRelation,
      String mainWordText, int senseIndex)
      throws IOException;

  boolean addExternalLink(SemRelation semRelation,
      String externWordText, int senseIndex)
      throws IOException;

  boolean removeExternalLink(SemRelation semRelation,
      String externWordText, int senseIndex)
      throws IOException;

  boolean addMainSynonymLink(String mainWordText,
      int senseIndex) throws IOException;

  boolean addMainAntonymLink(String mainWordText,
      int senseIndex) throws IOException;

  boolean addMainHypernymLink(String mainWordText,
      int senseIndex) throws IOException;

  boolean addMainHolonymLink(String mainWordText,
      int senseIndex) throws IOException;

  boolean addMainHyponymLink(String mainWordText,
      int senseIndex) throws IOException;

  boolean addMainMeronymLink(String mainWordText,
      int senseIndex) throws IOException;

  boolean removeMainSynonymLink(String mainWordText,
      int senseIndex) throws IOException;

  boolean removeMainAntonymLink(String mainWordText,
      int senseIndex) throws IOException;

  boolean removeMainHypernymLink(String mainWordText,
      int senseIndex) throws IOException;

  boolean removeMainHolonymLink(String mainWordText,
      int senseIndex) throws IOException;

  boolean removeMainHyponymLink(String mainWordText,
      int senseIndex) throws IOException;

  boolean removeMainMeronymLink(String mainWordText,
      int senseIndex) throws IOException;

  boolean addExternalSynonymLink(String lexicalWordText,
      int senseIndex) throws IOException;

  boolean addExternalAntonymLink(String lexicalWordText,
      int senseIndex) throws IOException;

  boolean addExternalHypernymLink(String lexicalWordText,
      int senseIndex) throws IOException;

  boolean addExternalHolonymLink(String lexicalWordText,
      int senseIndex) throws IOException;

  boolean addExternalHyponymLink(String lexicalWordText,
      int senseIndex) throws IOException;

  boolean addExternalMeronymLink(String lexicalWordText,
      int senseIndex) throws IOException;

  boolean removeExternalSynonymLink(
      String lexicalWordText, int senseIndex)
      throws IOException;

  boolean removeExternalAntonymLink(
      String lexicalWordText, int senseIndex)
      throws IOException;

  boolean removeExternalHypernymLink(
      String lexicalWordText, int senseIndex)
      throws IOException;

  boolean removeExternalHolonymLink(
      String lexicalWordText, int senseIndex)
      throws IOException;

  boolean removeExternalHyponymLink(
      String lexicalWordText, int senseIndex)
      throws IOException;

  boolean removeExternalMeronymLink(
      String lexicalWordText, int senseIndex)
      throws IOException;

  void setUserData(Serializable data);

  Serializable getUserData();
}
