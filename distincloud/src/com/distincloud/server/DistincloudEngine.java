package com.distincloud.server;

import javax.jdo.PersistenceManager;

import com.distincloud.server.modules.mOntologies;
import com.distincloud.server.modules.mUsers;


public class DistincloudEngine {
	
	protected static  DistincloudEngine _engine = null;
	protected PersistenceManager _persistenceManager = null;
	
	protected mUsers _mUsers = null;
	protected mOntologies _mOntologies = null;
	
	public DistincloudEngine() {
		_persistenceManager = PMF.get().getPersistenceManager();
		_mUsers = new mUsers(_persistenceManager);
	}
	
	public String createUser(String username) {
		return _mUsers.createNewUser(username);
	}

	public static DistincloudEngine getInstance() {
		if (_engine == null) _engine = new DistincloudEngine();
		return _engine;
	}
	
}
