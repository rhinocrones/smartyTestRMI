package com.adaptor;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.entity.Human;
import com.manager.Manager;

public interface Adaptor extends Remote {

	void executeJob(final Human human)
	        throws RemoteException, InterruptedException;

	boolean isAvailable(final Human human) throws RemoteException;

	void setManager(final Manager manager) throws RemoteException;

	void findManager() throws RemoteException, InterruptedException;

	void onJobExecuted(final Human human)
	        throws RemoteException, InterruptedException;

	void setNotBusy(boolean notBusy) throws RemoteException;

	Manager getManager() throws RemoteException;
}
