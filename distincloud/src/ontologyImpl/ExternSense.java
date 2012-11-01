package ontologyImpl;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ontologyInterface.ExternalSense;
import ontologyInterface.SemRelation;

public class ExternSense extends AbstractSense implements
    ExternalSense {
  public static class MainWordTransientPointer implements
      Serializable {
    private static final long serialVersionUID =
        -3333713029262279060L;
    protected transient MainWord mainWord;

    public MainWordTransientPointer(MainWord mainWord) {
      super();
      this.mainWord = mainWord;
    }

    // for serialization needs
    public MainWordTransientPointer() {
      super();
    }

    public MainWord getMainWord() {
      return mainWord;
    }

    public void setMainWord(MainWord mainWord) {
      this.mainWord = mainWord;
    }
  }

  public static class LinkValue implements Serializable {
    private static final long serialVersionUID =
        -5278483357004096855L;
    protected MainWordTransientPointer wordPointer;
    protected int senseIndex;

    public LinkValue(
        MainWordTransientPointer wordPointer,
        int senseIndex) {
      super();
      this.wordPointer = wordPointer;
      this.senseIndex = senseIndex;
    }

    // for serialization needs
    public LinkValue() {
      super();
    }

    public MainWordTransientPointer getWordPointer() {
      return wordPointer;
    }

    public int getSenseIndex() {
      return senseIndex;
    }
  }

  private static final long serialVersionUID =
      4537635307316030059L;
  protected Map<SemRelation, List<ExternSense>> localLinks;
  protected transient Map<SemRelation, List<MainSense>> mainLinks;
  protected Map<SemRelation, List<LinkValue>> mainStoredLinks;
  protected ExternWord word;
  protected Serializable userData;

  public ExternSense(boolean active, String definition) {
    super(active, definition);
    initializeAllLinks();
  }

  public ExternSense() {
    super();
    initializeAllLinks();
  }

  public void setDefinition(String definition)
      throws IOException {
    setSenseDefinition(definition);
    getWord().getOntology().writeExternOntology();
  }

  private void readObject(java.io.ObjectInputStream in)
      throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    initializeMainTransientLinks();
  }

  private void initializeAllLinks() {
    localLinks =
        new HashMap<SemRelation, List<ExternSense>>();
    for (SemRelation semRelation : SemRelation.values())
      localLinks.put(
          semRelation,
          new ArrayList<ExternSense>());
    mainStoredLinks =
        new HashMap<SemRelation, List<LinkValue>>();
    for (SemRelation semRelation : SemRelation.values())
      mainStoredLinks.put(
          semRelation,
          new ArrayList<LinkValue>());
    initializeMainTransientLinks();
  }

  private void initializeMainTransientLinks() {
    mainLinks =
        new HashMap<SemRelation, List<MainSense>>();
    for (SemRelation semRelation : SemRelation.values())
      mainLinks.put(
          semRelation,
          new ArrayList<MainSense>());
  }

  @Override
  public AbstractWord getWord() {
    return word;
  }

  public void setWord(ExternWord word) {
    this.word = word;
  }

  @Override
  public void activateDeactivate(boolean activate)
      throws IOException {
    super.activateDeactivate(activate);
    getWord().getOntology().writeExternOntology();
  }

  public List<ExternSense> getLocalLinks(
      SemRelation semRelation) {
    return localLinks.get(semRelation);
  }

  @Override
  protected List<AbstractSense> getAllLinks(
      SemRelation semRelation) {
    List<AbstractSense> result =
        new ArrayList<AbstractSense>();
    result.addAll(localLinks.get(semRelation));
    result.addAll(mainLinks.get(semRelation));
    return result;
  }

  protected LinkValue getNewLinkValueFromMainSense(
      MainSense sense) {
    MainWord word = sense.getWordAsMainWord();
    MainWordTransientPointer wordPointer =
        getWord().getOntology().searchMainWordPointer(
            word);
    if (wordPointer == null)
      wordPointer =
          getWord().getOntology()
              .insertNewMainWordPointer(
                  word);
    return new LinkValue(wordPointer, word.getSenses()
        .indexOf(
            sense));
  }

  public boolean addMainLink(SemRelation semRelation,
      MainSense link) {
    if (!mainLinks.get(
        semRelation).contains(
        link)) {
      mainLinks.get(
          semRelation).add(
          link);
      mainStoredLinks.get(
          semRelation).add(
          getNewLinkValueFromMainSense(link));
      link.addExternLink(
          semRelation.oppositeRel(), this);
      return true;
    } else
      return false;
  }

  public void rebuildMainWordExternSenseLink() {
    for (SemRelation semRelation : SemRelation.values()) {
      for (LinkValue linkValue : mainStoredLinks
          .get(semRelation)) {
        MainSense mainSense =
            linkValue.getWordPointer().getMainWord()
                .getSenseAsMainSense(
                    linkValue.getSenseIndex());
        mainLinks.get(
            semRelation).add(
            mainSense);
        mainSense.addExternLink(
            semRelation.oppositeRel(), this);
      }
    }
  }

  public boolean isMainWordPointerUsed(
      MainWordTransientPointer mainWordPointerUsed) {
    for (SemRelation semRelation : SemRelation.values())
      for (LinkValue linkValue : mainStoredLinks
          .get(semRelation))
        if (linkValue.getWordPointer() == mainWordPointerUsed)
          return true;
    return false;
  }

  public boolean removeMainLink(SemRelation semRelation,
      MainSense link) {
    if (mainLinks.get(
        semRelation).contains(
        link)) {
      mainLinks.get(
          semRelation).remove(
          link);
      MainWord word = link.getWordAsMainWord();
      int linkSenseIndex = word.getSenses().indexOf(
          link);
      Iterator<LinkValue> itStoredLinks =
          mainStoredLinks.get(
              semRelation).iterator();
      boolean foundLinkValue = false;
      while (!foundLinkValue && itStoredLinks.hasNext()) {
        LinkValue currentLinkValue = itStoredLinks.next();
        if ((currentLinkValue.getSenseIndex() == linkSenseIndex) &&
            (currentLinkValue.getWordPointer()
                .getMainWord() == word)) {
          foundLinkValue = true;
          mainStoredLinks.get(
              semRelation).remove(
              currentLinkValue);
          if (!getWord().getOntology()
              .isMainWordPointerUsed(
                  currentLinkValue.getWordPointer()))
            getWord().getOntology()
                .removeMainWordPointer(
                    currentLinkValue.getWordPointer()
                        .getMainWord());
        }
      }
      link.removeExternLink(
          semRelation.oppositeRel(),
          this);
      return true;
    } else
      return false;
  }

  public List<MainSense> getMainLinks(
      SemRelation semRelation) {
    return mainLinks.get(semRelation);
  }

  public boolean addLocalLink(SemRelation semRelation,
      ExternSense link, boolean addReverseLink) {
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

  public boolean removeLocalLink(SemRelation semRelation,
      ExternSense link, boolean removeReverseLink) {
    if (!localLinks.get(
        semRelation).contains(
        link))
      return false;
    localLinks.get(
        semRelation).remove(
        link);
    if (removeReverseLink &&
        link.localLinks.get(
            semRelation.oppositeRel())
            .contains(
                this))
      link.localLinks.get(
          semRelation.oppositeRel())
          .remove(
              this);
    return true;
  }

  public void removeAllLinks() {
    for (SemRelation semRelation : SemRelation.values()) {
      while (localLinks.get(
          semRelation).size() > 0)
        removeLocalLink(
            semRelation, localLinks.get(
                semRelation).get(
                0), true);
      while (mainLinks.get(
          semRelation).size() > 0)
        removeMainLink(
            semRelation, mainLinks.get(
                semRelation).get(
                0));
    }
  }

  public Serializable getUserData() {
    return userData;
  }

  public void setUserData(Serializable userData) {
    this.userData = userData;
  }

  public boolean addMainLink(SemRelation semRelation,
      String mainWordText, int senseIndex)
      throws IOException {
    MainWord mainWord =
        getWord().getOntology().searchMainWordPreprocess(
            mainWordText);
    if (mainWord == null)
      return false;
    MainSense mainSense =
        mainWord.getSenseAsMainSense(senseIndex);
    if (mainSense == null)
      return false;
    boolean result = addMainLink(
        semRelation, mainSense);
    getWord().getOntology().rebuildActiveSenseLinks();
    getWord().getOntology().writeExternOntology();
    return result;
  }

  public boolean removeMainLink(SemRelation semRelation,
      String mainWordText, int senseIndex)
      throws IOException {
    MainWord mainWord =
        getWord().getOntology().searchMainWordPreprocess(
            mainWordText);
    if (mainWord == null)
      return false;
    MainSense mainSense =
        mainWord.getSenseAsMainSense(senseIndex);
    if (mainSense == null)
      return false;
    boolean result =
        removeMainLink(
            semRelation, mainSense);
    getWord().getOntology().rebuildActiveSenseLinks();
    getWord().getOntology().writeExternOntology();
    return result;
  }

  public boolean addExternalLink(SemRelation semRelation,
      String externWordText, int senseIndex)
      throws IOException {
    ExternWord externWord =
        getWord().getOntology()
            .searchExternWordPreprocess(
                externWordText);
    if (externWord == null)
      return false;
    ExternSense externSense =
        externWord.getSenseAsExternSense(senseIndex);
    if (externSense == null)
      return false;
    boolean result =
        addLocalLink(
            semRelation, externSense, true);
    getWord().getOntology().rebuildActiveSenseLinks();
    getWord().getOntology().writeExternOntology();
    return result;
  }

  public boolean removeExternalLink(
      SemRelation semRelation, String externWordText,
      int senseIndex) throws IOException {
    ExternWord externWord =
        getWord().getOntology()
            .searchExternWordPreprocess(
                externWordText);
    if (externWord == null)
      return false;
    ExternSense externSense =
        externWord.getSenseAsExternSense(senseIndex);
    if (externSense == null)
      return false;
    boolean result =
        removeLocalLink(
            semRelation, externSense, true);
    getWord().getOntology().rebuildActiveSenseLinks();
    getWord().getOntology().writeExternOntology();
    return result;
  }

  public boolean addMainSynonymLink(String mainWordText,
      int senseIndex) throws IOException {
    return addMainLink(
        SemRelation.SYNONYM, mainWordText,
        senseIndex);
  }

  public boolean addMainAntonymLink(String mainWordText,
      int senseIndex) throws IOException {
    return addMainLink(
        SemRelation.ANTONYM, mainWordText,
        senseIndex);
  }

  public boolean addMainHypernymLink(String mainWordText,
      int senseIndex) throws IOException {
    return addMainLink(
        SemRelation.HYPERNYM,
        mainWordText, senseIndex);
  }

  public boolean addMainHolonymLink(String mainWordText,
      int senseIndex) throws IOException {
    return addMainLink(
        SemRelation.HOLONYM, mainWordText,
        senseIndex);
  }

  public boolean addMainHyponymLink(String mainWordText,
      int senseIndex) throws IOException {
    return addMainLink(
        SemRelation.HYPONYM, mainWordText,
        senseIndex);
  }

  public boolean addMainMeronymLink(String mainWordText,
      int senseIndex) throws IOException {
    return addMainLink(
        SemRelation.MERONYM, mainWordText,
        senseIndex);
  }

  public boolean removeMainSynonymLink(
      String mainWordText, int senseIndex)
      throws IOException {
    return removeMainLink(
        SemRelation.SYNONYM,
        mainWordText, senseIndex);
  }

  public boolean removeMainAntonymLink(
      String mainWordText, int senseIndex)
      throws IOException {
    return removeMainLink(
        SemRelation.ANTONYM,
        mainWordText, senseIndex);
  }

  public boolean removeMainHypernymLink(
      String mainWordText, int senseIndex)
      throws IOException {
    return removeMainLink(
        SemRelation.HYPERNYM,
        mainWordText, senseIndex);
  }

  public boolean removeMainHolonymLink(
      String mainWordText, int senseIndex)
      throws IOException {
    return removeMainLink(
        SemRelation.HOLONYM,
        mainWordText, senseIndex);
  }

  public boolean removeMainHyponymLink(
      String mainWordText, int senseIndex)
      throws IOException {
    return removeMainLink(
        SemRelation.HYPONYM,
        mainWordText, senseIndex);
  }

  public boolean removeMainMeronymLink(
      String mainWordText, int senseIndex)
      throws IOException {
    return removeMainLink(
        SemRelation.MERONYM,
        mainWordText, senseIndex);
  }

  public boolean addExternalSynonymLink(
      String lexicalWordText, int senseIndex)
      throws IOException {
    return addExternalLink(
        SemRelation.SYNONYM,
        lexicalWordText, senseIndex);
  }

  public boolean addExternalAntonymLink(
      String lexicalWordText, int senseIndex)
      throws IOException {
    return addExternalLink(
        SemRelation.ANTONYM,
        lexicalWordText, senseIndex);
  }

  public boolean addExternalHypernymLink(
      String lexicalWordText, int senseIndex)
      throws IOException {
    return addExternalLink(
        SemRelation.HYPERNYM,
        lexicalWordText, senseIndex);
  }

  public boolean addExternalHolonymLink(
      String lexicalWordText, int senseIndex)
      throws IOException {
    return addExternalLink(
        SemRelation.HOLONYM,
        lexicalWordText, senseIndex);
  }

  public boolean addExternalHyponymLink(
      String lexicalWordText, int senseIndex)
      throws IOException {
    return addExternalLink(
        SemRelation.HYPONYM,
        lexicalWordText, senseIndex);
  }

  public boolean addExternalMeronymLink(
      String lexicalWordText, int senseIndex)
      throws IOException {
    return addExternalLink(
        SemRelation.MERONYM,
        lexicalWordText, senseIndex);
  }

  public boolean removeExternalSynonymLink(
      String lexicalWordText, int senseIndex)
      throws IOException {
    return removeExternalLink(
        SemRelation.SYNONYM,
        lexicalWordText, senseIndex);
  }

  public boolean removeExternalAntonymLink(
      String lexicalWordText, int senseIndex)
      throws IOException {
    return removeExternalLink(
        SemRelation.ANTONYM,
        lexicalWordText, senseIndex);
  }

  public boolean removeExternalHypernymLink(
      String lexicalWordText, int senseIndex)
      throws IOException {
    return removeExternalLink(
        SemRelation.HYPERNYM,
        lexicalWordText, senseIndex);
  }

  public boolean removeExternalHolonymLink(
      String lexicalWordText, int senseIndex)
      throws IOException {
    return removeExternalLink(
        SemRelation.HOLONYM,
        lexicalWordText, senseIndex);
  }

  public boolean removeExternalHyponymLink(
      String lexicalWordText, int senseIndex)
      throws IOException {
    return removeExternalLink(
        SemRelation.HYPONYM,
        lexicalWordText, senseIndex);
  }

  public boolean removeExternalMeronymLink(
      String lexicalWordText, int senseIndex)
      throws IOException {
    return removeExternalLink(
        SemRelation.MERONYM,
        lexicalWordText, senseIndex);
  }

  // debug
  @Override
  public String toString() {
    String lineSeparator =
        System.getProperty("line.separator");
    String result = definition + lineSeparator;
    for (SemRelation semRelation : SemRelation.values())
      if (localLinks.get(
          semRelation).size() > 0) {
        result =
            result + " local " + semRelation +
                " links: " + lineSeparator;
        for (ExternSense link : localLinks
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
    for (SemRelation semRelation : SemRelation.values())
      if (mainLinks.get(
          semRelation).size() > 0) {
        result =
            result + " main " + semRelation + " links: " +
                lineSeparator;
        for (MainSense link : mainLinks.get(semRelation))
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
