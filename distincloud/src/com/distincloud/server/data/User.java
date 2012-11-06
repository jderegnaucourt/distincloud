package com.distincloud.server.data;

import java.io.Serializable;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

import ontologyInterface.Ontology;

@PersistenceCapable
public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * User display name
	 */
	
	@PrimaryKey
	@Persistent
	protected String _username;
	
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	/**
	 * List of comparisons performed by this user
	 */
	
	@Persistent
	protected List<WordComparison> _comparisons;
	
	/**
	 * Ontologies used by this user
	 */
	
	@Persistent
	protected List<Ontology> _ontologies;
	
	public User(String username) {
		this._username = username;
	}
	
	public void addWordComparison(WordComparison newComparison) {
		newComparison.setOwner(this._username);
		_comparisons.add(newComparison);
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public String getKey() {
		return key.toString();
	}
	
}
