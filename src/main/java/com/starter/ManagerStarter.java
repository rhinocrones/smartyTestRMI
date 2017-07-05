package com.starter;

import java.rmi.RemoteException;

import com.manager.impl.ManagerImpl;

public class ManagerStarter implements Runnable {

	public void main() {
		ManagerImpl managerImpl = new ManagerImpl();
		managerImpl.exportManager();
		System.out.println("Manager is running");
		while (true) {
			try {
				managerImpl.readParseAndPush();
			} catch (RemoteException | InterruptedException e) {
				System.out.println("EXCEPTION IN MAIN");
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		main();
	}
}
