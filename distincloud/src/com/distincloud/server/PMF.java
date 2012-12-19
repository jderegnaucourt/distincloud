package com.distincloud.server;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

public final class PMF {

	// GAE xml
	private static final PersistenceManagerFactory pmfInstance = JDOHelper.getPersistenceManagerFactory("transactions-optional");


	private PMF() {
		pmfInstance.setNontransactionalRead(true);
		pmfInstance.setNontransactionalWrite(true);
	}

	public static PersistenceManagerFactory get() {	
		return pmfInstance;
	}

}
