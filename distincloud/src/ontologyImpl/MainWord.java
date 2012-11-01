package ontologyImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ontologyInterface.Sense;
import ontologyInterface.Word;

public class MainWord extends AbstractWord implements
    Word {
  private static final long serialVersionUID =
      -3721145436206294228L;
  protected List<MainSense> senses =
      new ArrayList<MainSense>();

  public MainWord(String text, OntologyImpl ontology) {
    super(text, ontology);
  }

  public MainWord() {
    super();
  }

  @Override
  public void activateDeactivateSenses(
      boolean[] activateTab) throws IOException {
    super.activateDeactivateSenses(activateTab);
    getOntology().writeMainOntology();
  }

  @Override
  public List<AbstractSense> getSenses() {
    return new ArrayList<AbstractSense>(senses);
  }

  public List<MainSense> getSensesAsMainSense() {
    return senses;
  }

  public MainSense getSenseAsMainSense(int index) {
    if (index < senses.size())
      return senses.get(index);
    else
      return null;
  }

  public Sense getSense(int senseIndex) {
    if (senseIndex < getSenses().size())
      return senses.get(senseIndex);
    else
      return null;
  }

  public void setSense(int index, MainSense aSense) {
    int previousSize = senses.size();
    aSense.setWord(this);
    if (index >= previousSize)
      for (int i = 0; i <= index - previousSize; i++)
        senses.add(null);
    senses.set(
        index, aSense);
  }

  // debug
  @Override
  public String toString() {
    String lineSeparator =
        System.getProperty("line.separator");
    String result = "Word: " + text + lineSeparator;
    for (MainSense mainSense : senses)
      result = result + mainSense;
    return result;
  }
}
