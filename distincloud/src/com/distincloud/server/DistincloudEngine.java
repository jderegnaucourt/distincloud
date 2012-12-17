package com.distincloud.server;
import java.util.List;

import javax.jdo.PersistenceManager;

import com.distincloud.server.modules.mUsers;
import com.distincloud.server.data.User;
import com.distincloud.server.data.WordComparisonResult;


public class DistincloudEngine {
	
	protected static  DistincloudEngine _engine = null;
	protected PersistenceManager _persistenceManager = null;
	
	protected mUsers _mUsers = null;
	protected MockService service = new MockService();
	
	public DistincloudEngine() {
		service.start();
		_persistenceManager = PMF.get().getPersistenceManager();
		_mUsers = new mUsers(_persistenceManager);
		generateSomeStuff();
	}
	
	private void generateSomeStuff() {
		User dude = _mUsers.getCachedUserList().get(0);
		proceed(dude, 8, "software", "hardware");
		proceed(dude, 8, "software", "program");
	}

	public String createUser(String username) {
		return _mUsers.createNewUser(username);
	}

	public static DistincloudEngine getInstance() {
		if (_engine == null) _engine = new DistincloudEngine();
		return _engine;
	}

	public List<User> fetchUserList() {
		return _mUsers.getCachedUserList();
	}

	public String requestUserCreation(String username) {
		return _mUsers.createNewUser(username);
	}
	
	public String proceed(User user, int max, String word1, String word2) {
		WordComparisonResult wcr = service.calculateRelatedness(max, word1, word2);
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
