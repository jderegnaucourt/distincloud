package com.distincloud.server.data;

import java.io.Serializable;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.xml.bind.annotation.XmlRootElement;

import ontologyImpl.OntologyImpl;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(detachable="true")
@XmlRootElement
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
	
	@Persistent
	protected String _strPassword;
	

	/**
	 * List of comparisons performed by this user
	 */
	
	/*
	@Persistent
	protected List<WordComparisonResult> _comparisons;
	*/
	
	/**
	 * Ontologies used by this user
	 */
	
	/*
	@Persistent
	protected List<OntologyImpl> _ontologies;
	*/
	
	public User() {}
	
	public User(String username, String password) {
		this._strUsername = username;
		this._strPassword = password;
	}
	
	public void addWordComparison(WordComparisonResult newComparison) {
		newComparison.setOwner(this._strUsername);
		// _comparisons.add(newComparison);
	}

	public String getKey() {
		return key.toString();
	}
	
	public String getUsername() {
		return _strUsername;
	}
	
}
