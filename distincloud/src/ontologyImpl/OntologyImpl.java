package ontologyImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import ontologyInterface.ExternalWord;
import ontologyInterface.MainWordTransientPointer;
import ontologyInterface.Ontology;
import ontologyInterface.OntologyException;
import ontologyInterface.SemRelation;
import ontologyInterface.Word;
import ontologyInterface.OntologyException.ExceptionKind;

public class OntologyImpl implements Ontology {
  public static final int maxRelatedness = 8;
  protected Map<String, MainWord> mainWordHashtable =
      new HashMap<String, MainWord>();
  protected Map<String, ExternWord> externWordHashtable =
      new HashMap<String, ExternWord>();
  protected Map<String, MainWordTransientPointer> mainWordPointerHashtable =
      new HashMap<String, MainWordTransientPointer>();
  protected String mainOntologyFileName,
      externOntologyFileName;
  protected Thread loadingThread = null;
  protected Exception loadThreadIOException = null;

  public OntologyImpl(String mainOntologyFileName,
      String externOntologyFileName) {
    this.mainOntologyFileName = mainOntologyFileName;
    this.externOntologyFileName = externOntologyFileName;
    Runnable loadingRunnable = new Runnable() {
      public void run() {
        try {
          readMainOntology();
          readExternOntology();
          rebuildActiveSenseLinks();
        } catch (IOException e) {
          loadThreadIOException = e;
        }
      }
    };
    loadingThread = new Thread(loadingRunnable);
    loadingThread.start();
  }

  protected boolean joinLoadingThread() {
    if (loadingThread != null)
      try {
        loadingThread.join();
        if (loadThreadIOException == null)
          return true;
        else
          return false;
      } catch (InterruptedException e) {
      }
    return false;
  }

  protected OntologyImpl() {
  }

  public String getMainOntologyFileName() {
    return mainOntologyFileName;
  }

  public void setMainOntologyFileNameAndWrite(
      String mainOntologyFileName) throws IOException {
    if (joinLoadingThread()) {
      this.mainOntologyFileName = mainOntologyFileName;
      writeMainOntology();
    }
  }

  public String getExternOntologyFileName() {
    return externOntologyFileName;
  }

  public void setExternOntologyFileNameAndWrite(
      String externOntologyFileName) throws IOException {
    if (joinLoadingThread()) {
      this.externOntologyFileName =
          externOntologyFileName;
      writeExternOntology();
    }
  }

  public void writeMainOntology() throws IOException {
    if (mainOntologyFileName == null)
      return;
    FileOutputStream fileOutputStream =
        new FileOutputStream(mainOntologyFileName);
    ObjectOutputStream objectOutputStream =
        new ObjectOutputStream(fileOutputStream);
    objectOutputStream.writeObject(mainWordHashtable);
    objectOutputStream.close();
    fileOutputStream.close();
  }

  protected void finishReadMainOntology() {
    for (MainWord mainword : mainWordHashtable.values())
      mainword.setOntology(this);
  }

  @SuppressWarnings("unchecked")
  protected void readMainOntology() throws IOException {
    if (mainOntologyFileName == null)
      return;
    FileInputStream fileInputStream =
        new FileInputStream(mainOntologyFileName);
    ObjectInputStream objectInputStream =
        new ObjectInputStream(fileInputStream);
    try {
      mainWordHashtable =
          (HashMap<String, MainWord>) objectInputStream
              .readObject();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    objectInputStream.close();
    fileInputStream.close();
    finishReadMainOntology();
  }

  public void writeExternOntology() throws IOException {
    if (externOntologyFileName == null)
      return;
    FileOutputStream fileOutputStream =
        new FileOutputStream(externOntologyFileName);
    ObjectOutputStream objectOutputStream =
        new ObjectOutputStream(fileOutputStream);
    objectOutputStream.writeObject(externWordHashtable);
    objectOutputStream
        .writeObject(mainWordPointerHashtable);
    objectOutputStream.close();
    fileOutputStream.close();
  }

  protected void finishReadExternOntology() {
    for (Entry<String, MainWordTransientPointer> entry : mainWordPointerHashtable
        .entrySet())
      entry.getValue().setMainWord(
          mainWordHashtable.get(entry.getKey()));
    for (ExternWord externWord : externWordHashtable
        .values()) {
      externWord.setOntology(this);
      externWord.rebuildMainWordExternSenseLink();
    }
  }

  @SuppressWarnings("unchecked")
  protected void readExternOntology() throws IOException {
    if (externOntologyFileName == null)
      return;
    File file = new File(externOntologyFileName);
    if (!file.exists())
      return;
    FileInputStream fileInputStream =
        new FileInputStream(externOntologyFileName);
    ObjectInputStream objectInputStream =
        new ObjectInputStream(fileInputStream);
    try {
      externWordHashtable =
          (HashMap<String, ExternWord>) objectInputStream
              .readObject();
      mainWordPointerHashtable =
          (HashMap<String, MainWordTransientPointer>) objectInputStream
              .readObject();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    objectInputStream.close();
    fileInputStream.close();
    finishReadExternOntology();
  }

  public void rebuildActiveSenseLinks() {
    for (MainWord mainWord : mainWordHashtable.values())
      mainWord.rebuildActiveSenseLinks();
    for (ExternWord externWord : externWordHashtable
        .values())
      externWord.rebuildActiveSenseLinks();
  }

  public MainWord searchMainWord(String wordText) {
    return mainWordHashtable.get(wordText);
  }

  public ExternWord searchExternWord(String wordText) {
    return externWordHashtable.get(wordText);
  }

  protected String preprocessString(String wordText) {
    return wordText.trim().toLowerCase();
  }

  public MainWord
      searchMainWordPreprocess(String wordText) {
    return searchMainWord(preprocessString(wordText));
  }

  public ExternWord searchExternWordPreprocess(
      String wordText) {
    return searchExternWord(preprocessString(wordText));
  }

  public Word searchWordMainOntology(String wordText)
      throws OntologyException {
    if (joinLoadingThread()) {
      return searchMainWordPreprocess(wordText);
    } else
      return null;
  }

  public ExternalWord searchWordExternOntology(
      String wordText) throws OntologyException {
    if (joinLoadingThread()) {
      return searchExternWordPreprocess(wordText);
    } else
      return null;
  }

  public MainWordTransientPointer searchMainWordPointer(
      MainWord mainWord) {
    return mainWordPointerHashtable.get(mainWord
        .getText());
  }

  protected MainWord createNewMainWord(String wordText) {
    return new MainWord(wordText, this);
  }

  protected MainWord createNewMainWordPreprocess(
      String wordText) {
    return createNewMainWord(preprocessString(wordText));
  }

  protected void addMainWord(String wordText,
      MainWord newMainWord) {
    mainWordHashtable.put(
        wordText, newMainWord);
  }

  protected void addMainWordPreprocess(String wordText,
      MainWord newMainWord) {
    addMainWord(
        preprocessString(wordText),
        newMainWord);
  }

  protected ExternWord
      createNewExternWord(String wordText) {
    return new ExternWord(wordText, this);
  }

  protected ExternWord createNewExternWordPreprocess(
      String wordText) {
    return createNewExternWord(preprocessString(wordText));
  }

  protected void addExternWord(String wordText,
      ExternWord newExternWord) {
    externWordHashtable.put(
        wordText, newExternWord);
  }

  protected void addExternWordPreprocess(String wordText,
      ExternWord newExternWord) {
    addExternWord(
        preprocessString(wordText),
        newExternWord);
  }

  public MainWordTransientPointer
      insertNewMainWordPointer(
          MainWord mainWord) {
    MainWordTransientPointer newMainWordTransientPointer =
        new MainWordTransientPointer(mainWord);
    mainWordPointerHashtable.put(
        mainWord.getText(),
        newMainWordTransientPointer);
    return newMainWordTransientPointer;
  }

  public void removeMainWordPointer(MainWord mainWord) {
    mainWordPointerHashtable.remove(mainWord.getText());
  }

  public boolean isMainWordPointerUsed(
      MainWordTransientPointer mainWordPointerUsed) {
    for (ExternWord externWord : externWordHashtable
        .values())
      if (externWord
          .isMainWordPointerUsed(mainWordPointerUsed))
        return true;
    return false;
  }

  public ExternalWord addExternWord(String wordText,
      String wordTextCopySensesFrom)
      throws OntologyException, IOException {
    if (!joinLoadingThread())
      return null;
    if (searchExternWordPreprocess(wordText) != null)
      throw new OntologyException(
          ExceptionKind.WORDALREADYPRESENT);
    ExternWord newExternWord =
        createNewExternWordPreprocess(wordText);
    if (wordTextCopySensesFrom != null) {
      MainWord mainWordCopySensesFrom =
          searchMainWordPreprocess(wordTextCopySensesFrom);
      if (mainWordCopySensesFrom != null)
        newExternWord
            .copySensesFromMainWord(mainWordCopySensesFrom);
      else {
        ExternWord externWordCopySensesFrom =
            searchExternWordPreprocess(wordTextCopySensesFrom);
        if (externWordCopySensesFrom == null)
          throw new OntologyException(
              ExceptionKind.WORDNOTFOUND);
        newExternWord
            .copySensesFromExternWord(externWordCopySensesFrom);
      }
    }
    addExternWordPreprocess(
        wordText, newExternWord);
    writeExternOntology();
    return newExternWord;
  }

  public boolean removeExternWord(String wordText)
      throws OntologyException, IOException {
    if (!joinLoadingThread())
      return false;
    ExternWord externWord =
        searchExternWordPreprocess(wordText);
    if (externWord != null) {
      externWord.removeAllSenses();
      externWordHashtable.remove(externWord.getText());
      writeExternOntology();
      return true;
    } else
      return false;
  }

  public int getRelatedness(int maxRelatedness,
      String wordText1, Set<Integer> sensesIndex1,
      String wordText2, Set<Integer> sensesIndex2,
      EnumSet<SemRelation> semanticRelations)
      throws OntologyException {
    if (!joinLoadingThread())
      throw new OntologyException(
          ExceptionKind.DATANOTLOADED);
    if (maxRelatedness > OntologyImpl.maxRelatedness)
      throw new OntologyException(
          ExceptionKind.MAXRELATEDNESSTOOHIGH);
    if (wordText1.equals(wordText2))
      return maxRelatedness;
    if (wordText1.matches("^[0-9/±.,+\\-]+$") &&
        wordText2.matches("^[0-9/±.,+\\-]+$"))
      return maxRelatedness;
    AbstractWord word1 =
        searchMainWordPreprocess(wordText1);
    if (word1 == null) {
      word1 = searchExternWordPreprocess(wordText1);
      if (word1 == null)
        throw new OntologyException(
            ExceptionKind.WORD1NOTFOUND);
    }
    AbstractWord word2 =
        searchMainWordPreprocess(wordText2);
    if (word2 == null) {
      word2 = searchExternWordPreprocess(wordText2);
      if (word2 == null)
        throw new OntologyException(
            ExceptionKind.WORD2NOTFOUND);
    }
    return word1.getRelatedness(
        maxRelatedness,
        sensesIndex1, word2, sensesIndex2,
        semanticRelations);
  }

  // debug
  public String toString() {
    String lineSeparator =
        System.getProperty("line.separator");
    String result;
    result = "Extern Ontology" + lineSeparator;
    for (ExternWord externWord : externWordHashtable
        .values())
      result = result + externWord;
    result = result + "Main linked words" + lineSeparator;
    for (MainWordTransientPointer mainWordTransientPointer : mainWordPointerHashtable
        .values())
      result =
          result +
              mainWordTransientPointer.getMainWord()
                  .getText() + lineSeparator;
    return result;
  }
}
