package com.distincloud.server.modules;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.distincloud.server.data.User;

public class UsersModule {
	
	protected PersistenceManager _persistenceManager = null;
	protected List<User> _userList = new ArrayList<User>();
	
	public UsersModule(PersistenceManager persistenceManager) {
		_persistenceManager = persistenceManager;
		refreshUserCache();
		createNewUser("testUser");
	}

	public String createNewUser(String username) {
		if(UserExists(username)) return "null";
		else {
			String newUserKey = addUserToDatabase(username);
			refreshUserCache();
			return newUserKey;
		}
	}
	
	private void refreshUserCache() {
		_userList = fetchAllUser();
	}

	private String addUserToDatabase(String username) {
		_persistenceManager.currentTransaction().begin();
		User newUser = new User(username);
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

	@SuppressWarnings("unchecked")
	public boolean UserExists(String username) {
		Query query = _persistenceManager.newQuery(User.class, "_strUsername == _strSUsernameParam");
		query.declareParameters("String _strUsernameParam");	
		List<User> queryResult = (List<User>) query.execute(username);
		if(queryResult.isEmpty()) return false;
		else return true;
	}
}
