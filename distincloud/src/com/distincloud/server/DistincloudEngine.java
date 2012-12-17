package com.distincloud.server;
import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;

import com.distincloud.server.modules.mOntologies;
import com.distincloud.server.modules.mUsers;
import com.distincloud.server.data.User;
import com.distincloud.server.data.WordComparisonResult;


public class DistincloudEngine {
	
	protected static  DistincloudEngine _engine = null;
	protected PersistenceManager _persistenceManager = null;
	
	protected mUsers _mUsers = null;
	protected MockService service = new MockService();
	protected mOntologies _mOntologies;
	
	public DistincloudEngine() {
		service.start();
		_persistenceManager = PMF.get().getPersistenceManager();
		_mUsers = new mUsers(_persistenceManager);
		generateSomeStuff();
	}
	
	private void generateSomeStuff() {
		try {
			_mOntologies = new mOntologies("./reducedNoun.xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
		User ontoTest = _mUsers.getCachedUser("ontoTest");
		proceed(ontoTest, 8, "people", "person");
	}

	public String createUser(String username) {
		User usr = _mUsers.createNewUser(username);
		_mOntologies.createNewOntologyFor(usr);
		return usr.getUsername(); 
	}
	
	public User createUserAndFetch(String username) {
		User usr = _mUsers.createNewUser(username);
		_mOntologies.createNewOntologyFor(usr);
		return usr; 
	}

	public static DistincloudEngine getInstance() {
		if (_engine == null) _engine = new DistincloudEngine();
		return _engine;
	}

	public List<User> fetchUserList() {
		return _mUsers.getCachedUserList();
	}

	public String requestUserCreation(String username) {
		return _mUsers.createNewUser(username).getUsername();
	}
	
	public String proceed(User user, int max, String word1, String word2) {
		if(_mOntologies.getOntologyFor(user) == null) _mOntologies.createNewOntologyFor(user);
		int relat = _mOntologies.getWordRelatednessFor(user, word1, word2, max);
		WordComparisonResult wcr = new WordComparisonResult(word1, word2, max, relat);
		user.addWordComparison(wcr);
		return wcr.getKey();
	}
	
	public boolean userExists(String username) {
		if( _mUsers.getCachedUser(username) == null) return false;
		else return true;
	}

	public User checkExistanceOf(String username) {
		return _mUsers.getCachedUser(username);
	}

	public String usernameForKey(String usrKey) {
		return _mUsers.getCachedUserWithKey(usrKey).getUsername();
	}
	
}
