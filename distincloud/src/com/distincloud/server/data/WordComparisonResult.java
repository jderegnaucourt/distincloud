package com.distincloud.server.data;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable
public class WordComparisonResult implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
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
	
	public WordComparisonResult(String word1, String word2, int maxRelatedness, int relatedness) {
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

	public String getKey() {
		return KeyFactory.keyToString(key);
	}
}
