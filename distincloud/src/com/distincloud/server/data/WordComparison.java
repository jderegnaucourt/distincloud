package com.distincloud.server.data;

import java.io.Serializable;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable
public class WordComparison implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Persistent
	String _strOwner;

	@Persistent
	String _strWord1;
	
	@Persistent
	String _strWord2;
	

	@Persistent
	int _iMaxRelatedness;
	
	@Persistent
	int _iRelatedness;
	
	public WordComparison(String word1, String word2, int maxRelatedness, int relatedness) {
		this._strWord1 = word1;
		this._strWord2 = word2;
		this._iMaxRelatedness = maxRelatedness;
		this._iRelatedness= relatedness;
	}
	
	public String getOwner() {
		return _strOwner;
	}
	
	public void setOwner(String owner) {
		_strOwner = owner;
	}
	
	public String getWord1() {
		return _strWord1;
	}
	
	public int getMaxRelatedness() {
		return _iMaxRelatedness;
	}

	public int getRelatedness() {
		return _iRelatedness;
	}

	public String getWord2() {
		return _strWord2;
	}
}
