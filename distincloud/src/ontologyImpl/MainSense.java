package ontologyImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ontologyInterface.Sense;
import ontologyInterface.SemRelation;

public class MainSense extends AbstractSense implements
    Sense {
  private static final long serialVersionUID =
      8459985237027780028L;
  protected Map<SemRelation, List<MainSense>> localLinks;
  protected transient Map<SemRelation, List<ExternSense>> externLinks;
  protected MainWord word;

  public MainSense(boolean active, String definition) {
    super(active, definition);
    initializeAllLinks();
  }

  public MainSense() {
    super();
    initializeAllLinks();
  }

  private void readObject(java.io.ObjectInputStream in)
      throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    initializeExternTransientLinks();
  }

  private void initializeAllLinks() {
    localLinks =
        new HashMap<SemRelation, List<MainSense>>();
    for (SemRelation semRelation : SemRelation.values())
      localLinks.put(
          semRelation,
          new ArrayList<MainSense>());
    initializeExternTransientLinks();
  }

  private void initializeExternTransientLinks() {
    externLinks =
        new HashMap<SemRelation, List<ExternSense>>();
    for (SemRelation semRelation : SemRelation.values())
      externLinks.put(
          semRelation,
          new ArrayList<ExternSense>());
  }

  @Override
  public AbstractWord getWord() {
    return word;
  }

  public MainWord getWordAsMainWord() {
    return word;
  }

  public void setWord(MainWord word) {
    this.word = word;
  }

  @Override
  public void activateDeactivate(boolean activate)
      throws IOException {
    super.activateDeactivate(activate);
    getWord().getOntology().writeMainOntology();
  }

  public boolean addLocalLink(SemRelation semRelation,
      MainSense link, boolean addReverseLink) {
    if (this == link)
      return false;
    if (localLinks.get(
        semRelation).contains(
        link))
      return false;
    localLinks.get(
        semRelation).add(
        link);
    if (addReverseLink &&
        !link.localLinks.get(
            semRelation.oppositeRel())
            .contains(
                this))
      link.localLinks.get(
          semRelation.oppositeRel()).add(
          this);
    return true;
  }

  public List<MainSense> getLocalLinks(
      SemRelation semRelation) {
    return localLinks.get(semRelation);
  }

  @Override
  protected List<AbstractSense> getAllLinks(
      SemRelation semRelation) {
    List<AbstractSense> result =
        new ArrayList<AbstractSense>();
    result.addAll(localLinks.get(semRelation));
    result.addAll(externLinks.get(semRelation));
    return result;
  }

  public void addExternLink(SemRelation semRelation,
      ExternSense link) {
    if (!externLinks.get(
        semRelation).contains(
        link))
      externLinks.get(
          semRelation).add(
          link);
  }

  public void removeExternLink(SemRelation semRelation,
      ExternSense link) {
    if (externLinks.get(
        semRelation).contains(
        link))
      externLinks.get(
          semRelation).remove(
          link);
  }

  public List<ExternSense> getExternLinks(
      SemRelation semRelation) {
    return externLinks.get(semRelation);
  }

  // debug
  @Override
  public String toString() {
    String lineSeparator =
        System.getProperty("line.separator");
    String result = definition + lineSeparator;
    for (SemRelation semRelation : SemRelation.values())
      if (externLinks.get(
          semRelation).size() > 0) {
        result =
            result + " extern " + semRelation +
                " links: " + lineSeparator;
        for (ExternSense link : externLinks
            .get(semRelation))
          result =
              result +
                  "  " +
                  link.getWord().getText() +
                  " " +
                  link.getWord().getSenses()
                      .indexOf(
                          link) + lineSeparator;
      }
    return result;
  }
}
