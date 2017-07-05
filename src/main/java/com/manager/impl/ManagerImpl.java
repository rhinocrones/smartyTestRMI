package com.manager.impl;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import com.adaptor.Adaptor;
import com.entity.Human;
import com.manager.Manager;
import com.utils.AppUtils;

public class ManagerImpl implements Manager {

	private final AppUtils UTILS = AppUtils.getInstance();

	private final List<String> properties = new ArrayList<>();

	@Override
	public void readParseAndPush()
	        throws InterruptedException, RemoteException {
		Thread.sleep(1000);
		Human human = UTILS.lpopHuman();
		showPopedAndDeserializedHuman(human);
		for (String string : properties) {
			Adaptor adaptor;
			try {
				adaptor = (Adaptor) Naming.lookup(string);
			} catch (MalformedURLException | RemoteException
			        | NotBoundException e) {
				System.out.println("No adaptors online!!!");
				adaptor = null;
			}
			System.out.println(adaptor);
			if (adaptor != null) {
				synchronized (adaptor) {
					adaptor.notify();
					adaptor.setManager(this);
				}
				if (adaptor.isAvailable(human)) {
					adaptor.executeJob(human);
					return;
				}
			}
		}
		rpushIfNoneCanHandeThisHuman(human);
		synchronized (this) {
			System.err.println("Manager: I AM WAITING :(");
			this.wait();
		}
	}

	@Override
	public void jobExecuted(final Human human, final Adaptor adaptor)
	        throws InterruptedException {
		synchronized (this) {
			UTILS.rpushHuman(human);
			Thread.sleep(2000);
			System.out.println("Object human was right pushed to the redis!");
			System.err.println("Work is done for Human: " + human);
		}
	}

	@Override
	public void notifyManager() {
		synchronized (this) {
			System.err.println("Manager: is awaked :)");
			this.notify();
		}
	}

	public void exportManager() {
		getProperties();
		Manager stub;
		Registry registry;
		try {
			stub = (Manager) UnicastRemoteObject.exportObject(this, 2020);
			registry = LocateRegistry.createRegistry(2020);
			registry.bind("manager", stub);
		} catch (RemoteException | AlreadyBoundException e) {
			System.out.println("EXCEPTION IN exportManager");
			e.printStackTrace();
		}

	}

	private void getProperties() {
		properties.add("rmi://localhost:2021/adaptor");
		properties.add("rmi://localhost:2022/adaptor");
		properties.add("rmi://localhost:2023/adaptor");
	}

	private void showPopedAndDeserializedHuman(final Human human)
	        throws InterruptedException {
		Thread.sleep(1000);
		System.err.println("Poped object");
		System.out.println("------------");
		System.out.println(human.toString());
	}

	private void rpushIfNoneCanHandeThisHuman(final Human human)
	        throws InterruptedException {
		System.out.println("Returned to redis queue");
		System.out.println("-----------------------");
		UTILS.rpushHuman(human);
		Thread.sleep(2000);
	}
}
