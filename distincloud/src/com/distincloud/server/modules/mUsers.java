package com.distincloud.server.modules;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.distincloud.server.data.User;

public class mUsers {
	
	protected PersistenceManager _persistenceManager = null;
	protected List<User> _userList = new ArrayList<User>();
	private static final Logger log = Logger.getLogger(mUsers.class.getName());
	
	public mUsers(PersistenceManager persistenceManager) {
		log.info("[mUsers] Constructor called");
		_persistenceManager = persistenceManager;
		generateSampleUsers();
		refreshUserCache();	
	}

	private void generateSampleUsers() {
		log.info("[mUsers] generating sample users");
		createNewUser("testUser");
		createNewUser("anotherTestUser");
		createNewUser("lastTestUser");
	}

	public User createNewUser(String username) {
		log.info("[mUsers] trying to create user : "+username);
		if(UserExists(username)) {
			log.info("[mUsers] user already exists : "+username);
			return null;
		}
		else {
			addUserToDatabase(username);
			log.info("[mUsers] user has been created : "+username);
			refreshUserCache();
			return getCachedUser(username);
		}
	}
	
	private void refreshUserCache() {
		log.info("[mUsers] refreshing Users cache");
		_userList = fetchAllUser();
	}

	private String addUserToDatabase(String username) {
		_persistenceManager.currentTransaction().begin();
		User newUser = new User(username, "pass");
		_persistenceManager.makePersistent(newUser);
		_persistenceManager.currentTransaction().commit();
		return newUser.getKey();
	}
	
	public List<User> fetchAllUser() {
		Query query = _persistenceManager.newQuery(User.class);
		@SuppressWarnings("unchecked")
		List<User> queryResult = (List<User>) query.execute();
		return queryResult;
	}
	
	public void deleteUser(String username) {
		for(User u : fetchAllUser()) {
			if(u.getUsername().matches(username)) {
				if(!_persistenceManager.currentTransaction().isActive()) _persistenceManager.currentTransaction().begin();
				_persistenceManager.deletePersistent(u);
				_persistenceManager.currentTransaction().commit();
			}
		}
		refreshUserCache();
	}

	@SuppressWarnings("unchecked")
	public boolean UserExists(String username) {
		Query query = _persistenceManager.newQuery(User.class, "_strUsername == _strUsernameParam");
		query.declareParameters("String _strUsernameParam");	
		List<User> queryResult = (List<User>) query.execute(username);
		if(queryResult.isEmpty()) return false;
		else return true;
	}

	public List<User> getCachedUserList() {
		return _userList;
	}

	public User getCachedUser(String username) {
		for(User usr : _userList) {
			if ( usr.getUsername().matches(username) ) return usr;
		}
		return null ;
	}
	
	public User getCachedUserWithKey(String key) {
		for(User usr : _userList) {
			if ( usr.getKey().matches(key) ) return usr;
		}
		return null;
	}

}
