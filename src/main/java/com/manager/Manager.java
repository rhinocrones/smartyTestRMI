package com.manager;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.adaptor.Adaptor;
import com.entity.Human;

public interface Manager extends Remote {

	void readParseAndPush() throws RemoteException, InterruptedException;

	void jobExecuted(final Human human, final Adaptor adaptor)
	        throws RemoteException, InterruptedException;

	void notifyManager() throws RemoteException;
}
