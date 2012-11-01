package ontologyImpl;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ontologyImpl.ExternSense.MainWordTransientPointer;
import ontologyInterface.ExternalSense;
import ontologyInterface.ExternalWord;
import ontologyInterface.SemRelation;

public class ExternWord extends AbstractWord implements
    ExternalWord {
  private static final long serialVersionUID =
      983587581301932786L;
  protected List<ExternSense> senses =
      new ArrayList<ExternSense>();
  protected Serializable userData;

  public ExternWord(String text, OntologyImpl ontology) {
    super(text, ontology);
  }

  public ExternWord() {
  }

  @Override
  public void activateDeactivateSenses(
      boolean[] activateTab) throws IOException {
    super.activateDeactivateSenses(activateTab);
    getOntology().writeExternOntology();
  }

  @Override
  public List<AbstractSense> getSenses() {
    return new ArrayList<AbstractSense>(senses);
  }

  public ExternSense getSenseAsExternSense(int index) {
    if (index < senses.size())
      return senses.get(index);
    else
      return null;
  }

  public List<ExternSense> getSensesAsExternSense() {
    return senses;
  }

  public void copySensesFromMainWord(
      MainWord mainWordCopySensesFrom) {
    for (MainSense mainSenseToCopy : mainWordCopySensesFrom
        .getSensesAsMainSense()) {
      ExternSense newExternSense =
          new ExternSense(mainSenseToCopy.isActive(),
              mainSenseToCopy.getDefinition());
      senses.add(newExternSense);
      newExternSense.setWord(this);
      for (SemRelation semRelation : SemRelation.values())
        for (MainSense link : mainSenseToCopy
            .getLocalLinks(semRelation))
          newExternSense.addMainLink(
              semRelation, link);
      for (SemRelation semRelation : SemRelation.values())
        for (ExternSense link : mainSenseToCopy
            .getExternLinks(semRelation))
          newExternSense.addLocalLink(
              semRelation, link,
              true);
      for (SemRelation semRelation : SemRelation.values())
        for (AbstractSense link : mainSenseToCopy
            .getActiveLinks(semRelation))
          newExternSense.addActiveLink(
              semRelation, link);
    }
  }

  public void copySensesFromExternWord(
      ExternWord externWordCopySensesFrom) {
    for (ExternSense externSenseToCopy : externWordCopySensesFrom
        .getSensesAsExternSense()) {
      ExternSense newExternSense =
          new ExternSense(externSenseToCopy.isActive(),
              externSenseToCopy.getDefinition());
      senses.add(newExternSense);
      newExternSense.setWord(this);
      for (SemRelation semRelation : SemRelation.values())
        for (ExternSense link : externSenseToCopy
            .getLocalLinks(semRelation))
          newExternSense.addLocalLink(
              semRelation, link,
              true);
      for (SemRelation semRelation : SemRelation.values())
        for (MainSense link : externSenseToCopy
            .getMainLinks(semRelation))
          newExternSense.addMainLink(
              semRelation, link);
      for (SemRelation semRelation : SemRelation.values())
        for (AbstractSense link : externSenseToCopy
            .getActiveLinks(semRelation))
          newExternSense.addActiveLink(
              semRelation, link);
    }
  }

  public void rebuildMainWordExternSenseLink() {
    for (ExternSense sense : senses)
      sense.rebuildMainWordExternSenseLink();
  }

  public void removeAllSenses() {
    for (ExternSense sense : senses)
      sense.removeAllLinks();
    senses.clear();
    ontology.rebuildActiveSenseLinks();
  }

  public Serializable getUserData() {
    return userData;
  }

  public void setUserData(Serializable userData)
      throws IOException {
    this.userData = userData;
    ontology.writeExternOntology();
  }

  public int addSense(boolean active, String definition)
      throws IOException {
    ExternSense newExternSense =
        new ExternSense(active, definition);
    senses.add(newExternSense);
    newExternSense.setWord(this);
    ontology.writeExternOntology();
    return senses.indexOf(newExternSense);
  }

  public ExternalSense getSense(int senseIndex) {
    if (senseIndex < getSenses().size())
      return senses.get(senseIndex);
    else
      return null;
  }

  public boolean removeSense(int senseIndex)
      throws IOException {
    if (senseIndex < getSenses().size()) {
      ExternSense sense = senses.get(senseIndex);
      sense.removeAllLinks();
      senses.remove(sense);
      ontology.rebuildActiveSenseLinks();
      ontology.writeExternOntology();
      return true;
    } else
      return false;
  }

  public boolean isMainWordPointerUsed(
      MainWordTransientPointer mainWordPointerUsed) {
    for (ExternSense externSense : senses)
      if (externSense
          .isMainWordPointerUsed(mainWordPointerUsed))
        return true;
    return false;
  }

  // debug
  @Override
  public String toString() {
    String lineSeparator =
        System.getProperty("line.separator");
    String result = "Word: " + text + lineSeparator;
    for (ExternSense externSense : senses)
      result = result + externSense;
    return result;
  }
}
