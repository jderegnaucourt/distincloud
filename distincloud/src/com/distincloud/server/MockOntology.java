package com.distincloud.server;

import ontologyImpl.MainSense;
import ontologyImpl.MainWord;
import ontologyImpl.OntologyImpl;

class MockOntology extends OntologyImpl {
	MockOntology() {
    super();
  }

  MainWord addMainWord(String text) {
    MainWord word = createNewMainWordPreprocess(text);
    addMainWordPreprocess(
        text, word);
    return word;
  }

  MainSense addMainSense(MainWord word,
      int index, boolean active, String definition) {
    MainSense sense = new MainSense(active, definition);
    word.setSense(
        index, sense);
    return sense;
  }

  @Override
  protected boolean joinLoadingThread() {
    return true;
  }
}

