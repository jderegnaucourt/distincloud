package ontologyImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ontologyInterface.SemRelation;



import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

public class OntologyXmlFileReader extends OntologyImpl {
	
  protected MainWord getOrAddMainWord(String text) {
    MainWord word = searchMainWordPreprocess(text);
    if (word == null) {
      word = createNewMainWordPreprocess(text);
      addMainWordPreprocess(
          text, word);
    }
    return word;
  }

  protected MainSense getOrAddMainSense(MainWord word,
      int index, boolean active, String definition) {
    MainSense sense = word.getSenseAsMainSense(index);
    if (sense == null) {
      sense = new MainSense(active, definition);
      word.setSense(
          index, sense);
    } else {
      sense.setActive(active);
      sense.setSenseDefinition(definition);
    }
    return sense;
  }

  protected MainSense getOrAddMainSense(MainWord word,
      int index) {
    MainSense sense = word.getSenseAsMainSense(index);
    if (sense == null) {
      sense = new MainSense();
      word.setSense(
          index, sense);
    }
    return sense;
  }

  protected void insertPointerItemsList(
      MainSense currentSense,
      List<Element> pointerItemsList,
      String pointerItemsListType,
      List<String> elementsTextList) {
    for (Element pointerItem : pointerItemsList) {
      if (pointerItem.getAttributeValue(
          "active").equals(
          "yes") &&
          (elementsTextList.contains(pointerItem
              .getTextTrim()))) {
        MainWord pointerItemWord =
            getOrAddMainWord(pointerItem.getTextTrim());
        int pointerItemIndex;
        try {
          pointerItemIndex =
              Integer.parseInt(pointerItem
                  .getAttributeValue("sense"));
        } catch (NumberFormatException e) {
          pointerItemIndex = -1;
        }
        if ((pointerItemWord != null) &&
            (pointerItemIndex >= 0)) {
          if (pointerItemsListType.equals("ANTPTR"))
            currentSense.addLocalLink(
                SemRelation.ANTONYM,
                getOrAddMainSense(
                    pointerItemWord,
                    pointerItemIndex), false);
          if (pointerItemsListType.equals("HYPERPTR"))
            currentSense.addLocalLink(
                SemRelation.HYPERNYM,
                getOrAddMainSense(
                    pointerItemWord,
                    pointerItemIndex), true);
          if (pointerItemsListType.equals("HOLONYM"))
            currentSense.addLocalLink(
                SemRelation.HOLONYM,
                getOrAddMainSense(
                    pointerItemWord,
                    pointerItemIndex), true);
          if (pointerItemsListType.equals("SYNPTR"))
            currentSense.addLocalLink(
                SemRelation.SYNONYM,
                getOrAddMainSense(
                    pointerItemWord,
                    pointerItemIndex), false);
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  public void buildOntology(String filePath,
      String collectionTag, String elementTag,
      String synonymTag, String ptrTag,
      String mainOntologyFileName,
      String externOntologyFileName) {
    try {
      SAXBuilder builder = new SAXBuilder();
      Document document = builder.build(filePath);
      //XPath elementPath =
      //   XPath.newInstance("/" + collectionTag + "/" +
      //       elementTag + "[@kept=\"yes\"]");
      XPathExpression<Element> elementPath = XPathFactory.instance().compile("/" + collectionTag + "/" +
              elementTag + "[@kept=\"yes\"]", Filters.element());
      // List<Element> elementsList =
      //     elementPath.selectNodes(document);
      List<Element> elementsList = elementPath.evaluate(document);
      List<String> elementsTextList =
          new ArrayList<String>();
      for (Element element : elementsList)
        elementsTextList.add(element
            .getChildTextTrim("text"));
      // System.out.println(elementsList.size());
      // elementsList = new ArrayList<Element>();
      // elementsList.add((Element) elementPath.selectSingleNode(document));
      for (Element element : elementsList) {
        MainWord currentWord =
            getOrAddMainWord(element
                .getChildTextTrim("text"));
        System.out.println("Process current word:" + currentWord.text);
        if (currentWord != null) {
          List<Element> sensesList =
              element.getChildren("sense");
          int indexSense = 0;
          for (Element sense : sensesList) {
            MainSense currentSense =
                getOrAddMainSense(
                    currentWord,
                    indexSense,
                    sense.getAttributeValue(
                        "active")
                        .equals(
                            "yes"),
                    sense.getChildTextTrim("def"));
            List<Element> synonymsList =
                sense.getChildren(synonymTag);
            insertPointerItemsList(
                currentSense,
                synonymsList, "SYNPTR", elementsTextList);
            List<Element> pointersList =
                sense.getChildren("ptr");
            for (Element pointer : pointersList) {
              String pointerItemsListType =
                  pointer.getAttributeValue("typ");
              List<Element> pointerItemsList =
                  pointer.getChildren(ptrTag);
              insertPointerItemsList(
                  currentSense,
                  pointerItemsList, pointerItemsListType,
                  elementsTextList);
            }
            indexSense++;
            // GV
            System.out.println("Add new sense:" + currentSense.definition);
            System.out.println("Synonyms:");
            for (MainSense  lnk: currentSense.getLocalLinks(SemRelation.SYNONYM)) {
            	System.out.println(lnk.word.text);
            }
            System.out.println("Antonyms:");
            for (MainSense  lnk: currentSense.getLocalLinks(SemRelation.ANTONYM)) {
            	System.out.println(lnk.word.text);
            }
            
            
            // GV
          }
          System.out.println("=================");
        }
      }
      this.mainOntologyFileName = mainOntologyFileName;
      this.externOntologyFileName =
          externOntologyFileName;
      // TODO (Uncomment) writeMainOntology();
      // TOOD (Uncomment) readExternOntology();
      rebuildActiveSenseLinks();
    } catch (JDOMException e) {
      loadThreadIOException = e;
    } catch (IOException e) {
      loadThreadIOException = e;
    }
  }

  public OntologyXmlFileReader(final String filePath,
      final String collectionTag,
      final String elementTag, final String synonymTag,
      final String ptrTag,
      final String mainOntologyFileName,
      final String externOntologyFileName) {
    Runnable loadingRunnable = new Runnable() {
      public void run() {
        buildOntology(
            filePath, collectionTag,
            elementTag, synonymTag, ptrTag,
            mainOntologyFileName, externOntologyFileName);
      }
    };
    loadingThread = new Thread(loadingRunnable);
    loadingThread.start();
    joinLoadingThread();
  }
}
