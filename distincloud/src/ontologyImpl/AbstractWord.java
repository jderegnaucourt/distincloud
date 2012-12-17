package ontologyImpl;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import ontologyInterface.Ontology;
import ontologyInterface.SemRelation;

public abstract class AbstractWord implements
    Serializable {
  private static final long serialVersionUID =
      7102622853808015183L;
  protected String text;
  protected transient Ontology ontology;

  public AbstractWord(String text, Ontology ontology) {
    super();
    this.text = text;
    this.ontology = ontology;
  }

  public AbstractWord() {
    super();
  }

  public String getText() {
    return text;
  }

  public void setOntology(Ontology ontology) {
    this.ontology = ontology;
  }

  public Ontology getOntology() {
    return ontology;
  }

  public abstract List<AbstractSense> getSenses();

  public int getSensesCount() {
    return getSenses().size();
  }

  public AbstractSense getSenseFromIndex(int senseIndex) {
    if (senseIndex < getSenses().size())
      return getSenses().get(
          senseIndex);
    else
      return null;
  }

  public void activateDeactivateSenses(
      boolean[] activateTab) throws IOException {
    int count =
        Math.min(
            activateTab.length, getSensesCount());
    List<AbstractSense> senses = getSenses();
    for (int senseIndex = 0; senseIndex < count; senseIndex++)
      senses.get(
          senseIndex).setActive(
          activateTab[senseIndex]);
    getOntology().rebuildActiveSenseLinks();
  }

  public List<AbstractSense>
      getActiveSensesListFromIndex(
          Set<Integer> sensesSet) {
    List<AbstractSense> result =
        new ArrayList<AbstractSense>();
    if (sensesSet.size() == 0) {
      for (AbstractSense sense : this.getSenses())
        if (sense.isActive())
          result.add(sense);
    } else {
      for (Integer senseIndex : sensesSet) {
        AbstractSense sense =
            this.getSenseFromIndex(senseIndex);
        if (sense.isActive())
          result.add(sense);
      }
    }
    return result;
  }

  public void rebuildActiveSenseLinks() {
    for (AbstractSense sense : this.getSenses())
      if (sense.isActive())
        sense.rebuildActiveLinksLists();
  }

  public int getRelatedness(int maxRelatedness,
      Set<Integer> sensesIndex1, AbstractWord word2,
      Set<Integer> sensesIndex2,
      EnumSet<SemRelation> semanticRelations) {
    List<AbstractSense> word1Senses =
        this.getActiveSensesListFromIndex(sensesIndex1);
    List<AbstractSense> word2Senses =
        word2.getActiveSensesListFromIndex(sensesIndex2);
    if ((word1Senses.size() > 0) &&
        (word2Senses.size() > 0)) {
      if (isASynonym(
          word1Senses, word2Senses,
          semanticRelations) ||
          isASynonym(
              word2Senses, word1Senses,
              semanticRelations))
        return maxRelatedness;
      int bestRelatedness =
          calcBestRelatedness(
              maxRelatedness, -1,
              word1Senses, word2Senses, semanticRelations);
      bestRelatedness =
          calcBestRelatedness(
              maxRelatedness,
              bestRelatedness, word2Senses, word1Senses,
              semanticRelations);
      return bestRelatedness;
    } else
      return -1;
  }

  protected boolean isASynonym(
      List<AbstractSense> wordSenses,
      List<AbstractSense> destWordSenses,
      EnumSet<SemRelation> semanticRelations) {
    if (!semanticRelations.contains(SemRelation.SYNONYM))
      return false;
    for (AbstractSense wordSense : wordSenses)
      for (AbstractSense synonym : wordSense
          .getActiveLinks(SemRelation.synonymRelation))
        if (destWordSenses.contains(synonym))
          return true;
    return false;
  }

  protected int calcBestRelatedness(int maxRelatedness,
      int bestRelatedness,
      List<AbstractSense> wordSenses,
      List<AbstractSense> destWordSenses,
      EnumSet<SemRelation> semanticRelations) {
    AbstractSense.RelatednessInfo relatednessInfo =
        new AbstractSense.RelatednessInfo();
    relatednessInfo.maxRelatedness = maxRelatedness;
    relatednessInfo.previousVisits =
        new ArrayList<AbstractSense>();
    relatednessInfo.bestRelatedness = bestRelatedness;
    relatednessInfo.semanticUpwardRelations =
        new HashSet<SemRelation>(
            SemRelation.upwardRelations);
    relatednessInfo.semanticUpwardRelations
        .retainAll(semanticRelations);
    relatednessInfo.semanticDownwardRelations =
        new HashSet<SemRelation>(
            SemRelation.downwardRelations);
    relatednessInfo.semanticDownwardRelations
        .retainAll(semanticRelations);
    relatednessInfo.semanticHorizontalRelations =
        new HashSet<SemRelation>(
            SemRelation.horizontalRelations);
    relatednessInfo.semanticHorizontalRelations
        .retainAll(semanticRelations);
    relatednessInfo.destWordSenses = destWordSenses;
    for (AbstractSense wordSense : wordSenses) {
      relatednessInfo.previousVisits.clear();
      int newRelatedness =
          wordSense.getRelatedness(
              0, 0,
              relatednessInfo);
      if (newRelatedness > relatednessInfo.bestRelatedness)
        relatednessInfo.bestRelatedness = newRelatedness;
    }
    return relatednessInfo.bestRelatedness;
  }
}
