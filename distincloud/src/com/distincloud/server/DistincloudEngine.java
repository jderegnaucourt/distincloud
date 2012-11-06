package com.distincloud.server;

import javax.jdo.PersistenceManager;

import com.distincloud.server.modules.UsersModule;


public class DistincloudEngine {
	
	protected static  DistincloudEngine _engine = null;
	protected PersistenceManager _persistenceManager = null;
	
	protected UsersModule _mUsers = null;
	
	public DistincloudEngine() {
		_persistenceManager = PMF.get().getPersistenceManager();
		_mUsers = new UsersModule(_persistenceManager);
	}
	
	public String createUser(String username) {
		return _mUsers.createNewUser(username);
	}

	public static DistincloudEngine getInstance() {
		if (_engine == null) _engine = new DistincloudEngine();
		return _engine;
	}
	
}
