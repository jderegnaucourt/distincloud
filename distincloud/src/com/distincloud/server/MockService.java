package com.distincloud.server;

import java.util.HashSet;

import ontologyImpl.MainSense;
import ontologyImpl.MainWord;
import ontologyInterface.OntologyException;
import ontologyInterface.SemRelation;

public class MockService {

	MockOntology myOntology = null ;
	static MockService _service = null;
	
	public MockService() {
		this.start();
	}
	
	public void start() {
		myOntology = new MockOntology(); 	
		generateWords();    
	    myOntology.rebuildActiveSenseLinks();  
	}
	
	String getInfos() {
		if(myOntology != null) return myOntology.toString();
		else return "null";
	}
	
	public int calculateRelatedness(int max, String word1, String word2) {
		try {
			return myOntology.getRelatedness(max, word1, new HashSet<Integer>(), word2, new HashSet<Integer>(), SemRelation.allSemanticRelations);
		} catch (OntologyException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private void generateWords() {
		MainWord newWord1 = myOntology.addMainWord("software");
	    MainSense newSense11 = myOntology.addMainSense(
	        newWord1, 0, true, "software sense 1");
	    
	    MainWord newWord2 = myOntology.addMainWord("program");
	    MainSense newSense21 = myOntology.addMainSense(
	        newWord2, 0, true, "program sense 1");
	    
	    MainWord newWord3 = myOntology.addMainWord("hardware");
	    MainSense newSense31 = myOntology.addMainSense(
	    		newWord3, 0, true, "hardware sense 1");
	    
	    MainWord newWord4 = myOntology.addMainWord("CPU");
	    MainSense newSense41 = myOntology.addMainSense(
	    		newWord4, 0, true, "CPU sense 1");
	    
	    newSense11.addLocalLink(SemRelation.SYNONYM, newSense21, false);
	    newSense11.addLocalLink(SemRelation.ANTONYM, newSense31, true);
	    newSense41.addLocalLink(SemRelation.MERONYM, newSense31, true);
	}
	
	public static MockService getInstance() {
		if(_service == null) _service = new MockService();
		return _service;
	}

}
