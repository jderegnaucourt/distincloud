package com.distincloud.server.data;

import java.io.Serializable;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import ontologyImpl.OntologyImpl;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	/**
	 * User display name
	 */
	
	@Persistent
	protected String _strUsername;
	
	/**
	 * List of comparisons performed by this user
	 */
	
	@Persistent
	protected List<WordComparison> _comparisons;
	
	/**
	 * Ontologies used by this user
	 */
	
	@Persistent
	protected List<OntologyImpl> _ontologies;
	
	public User(String username) {
		this._strUsername = username;
	}
	
	public void addWordComparison(WordComparison newComparison) {
		newComparison.setOwner(this._strUsername);
		_comparisons.add(newComparison);
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public String getKey() {
		return key.toString();
	}
	
}
