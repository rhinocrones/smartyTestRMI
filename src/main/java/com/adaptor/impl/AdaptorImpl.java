package com.adaptor.impl;

import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.adaptor.Adaptor;
import com.entity.Human;
import com.manager.Manager;
import com.worker.impl.WorkerImpl;

public class AdaptorImpl implements Adaptor {

	private Manager manager;

	private int port;

	private int connectionLimits;

	private int ukCall;

	private int enCall;

	private int ruCall;

	private boolean notBusy = true;

	private ExecutorService service = Executors
	        .newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

	private AdaptorImpl() {

	}

	public void exportAdaptor() {
		Adaptor stub;
		Registry registry;
		try {
			stub = (Adaptor) UnicastRemoteObject.exportObject(this, port);
			registry = LocateRegistry.createRegistry(port);
			registry.bind("adaptor", stub);
		} catch (RemoteException | AlreadyBoundException e) {
			System.out.println("EXCEPTION IN exportAdaptor");
			e.printStackTrace();
		}
	}

	@Override
	public void executeJob(final Human human) throws InterruptedException {
		setNotBusy(false);
		System.out.println("NotBussy in executeJob: " + notBusy);
		System.out.println("In adapter method");
		Thread.sleep(new Random().nextInt(2000));
		sendJobToWorker(human);
	}

	@Override
	public void findManager() throws InterruptedException {
		try {
			manager = (Manager) Naming.lookup("rmi://localhost:2020/manager");
		} catch (Exception e) {
			synchronized (this) {
				System.err.println("Adaptor: I AM WAITING :(");
				this.wait();
			}
		}

	}

	@Override
	public boolean isAvailable(final Human human) {
		if (availableConnection() && adapterLimitPerAllApiCallNotReached()
		        && canHeWorkWhitThisHuman(human) && ifIamNotBussy()) {
			return true;
		}
		return false;
	}

	@Override
	public void setManager(final Manager manager) {
		this.manager = manager;
	}

	@Override
	public void onJobExecuted(final Human human) throws InterruptedException {
		try {
			manager.jobExecuted(human, this);
		} catch (RemoteException e) {
			System.out.println("EXCEPTION IN onJobExecuted");
			e.printStackTrace();
		}
	}

	@Override
	public void setNotBusy(final boolean notBusy) {
		this.notBusy = notBusy;
	}

	@Override
	public Manager getManager() {
		return manager;
	}

	private boolean availableConnection() {
		if (connectionLimits > 0) {
			return true;
		}
		return false;
	}

	private boolean adapterLimitPerAllApiCallNotReached() {
		boolean a = false;
		if (ukCall > 0 || enCall > 0 || ruCall > 0) {
			a = true;
			System.out.println(a);
			return a;
		}
		System.out.println(a);
		return a;
	}

	private boolean canHeWorkWhitThisHuman(final Human human) {
		List<String> humanApis = human.getApis();
		List<String> adapterAvailableApis = fillUpAdaptorWhitAvailableApis();
		System.out.println(humanApis);
		System.out.println(adapterAvailableApis);
		boolean a = false;
		if (adapterAvailableApis.containsAll(humanApis)) {
			a = true;
			System.out.println(a);
			return a;
		}
		System.out.println(a);
		return false;
	}

	private List<String> fillUpAdaptorWhitAvailableApis() {
		List<String> list = new ArrayList<>();
		if (ukCall > 0) {
			list.add("uk");
		}
		if (enCall > 0) {
			list.add("en");
		}
		if (ruCall > 0) {
			list.add("ru");
		}
		return list;
	}

	private void sendJobToWorker(final Human human)
	        throws InterruptedException {
		System.out.println("In sendJobWorkers");
		reduceApisAndConLim(human);
		System.out.println(connectionLimits);
		System.out.println("UK " + ukCall);
		System.out.println("EN " + enCall);
		System.out.println("RU " + ruCall);
		System.out.println("Count of calls " + connectionLimits);
		Thread.sleep(1000);
		System.out.println("Not bussy in sendJobWorker: " + notBusy);
		CompletableFuture.runAsync(new WorkerImpl(human, this), service);
	}

	private void reduceApisAndConLim(final Human human) {
		connectionLimits -= 1;
		for (String apiCall : human.getApis()) {
			if (apiCall.equals("en")) {
				enCall -= 1;
			} else if (apiCall.equals("uk")) {
				ukCall -= 1;
			} else {
				ruCall -= 1;
			}
		}
	}

	private boolean ifIamNotBussy() {
		System.out.println("Not Bussy check: " + notBusy);
		if (notBusy) {
			return true;
		}
		return false;
	}

	public int getPort() {
		return port;
	}

	public int getConnectionLimits() {
		return connectionLimits;
	}

	public int getUkCall() {
		return ukCall;
	}

	public int getEnCall() {
		return enCall;
	}

	public int getRuCall() {
		return ruCall;
	}

	public static Builder newBuilder() {
		return new AdaptorImpl().new Builder();
	}

	public class Builder {

		private Builder() {

		}

		public Builder setPort(int port) {
			AdaptorImpl.this.port = port;
			return this;
		}

		public Builder setgetConnectionLimits(int connectionLimits) {
			AdaptorImpl.this.connectionLimits = connectionLimits;
			return this;
		}

		public Builder setUkCall(int ukCall) {
			AdaptorImpl.this.ukCall = ukCall;
			return this;
		}

		public Builder setEnCall(int enCall) {
			AdaptorImpl.this.enCall = enCall;
			return this;
		}

		public Builder setRuCall(int ruCall) {
			AdaptorImpl.this.ruCall = ruCall;
			return this;
		}

		public AdaptorImpl build() {
			return AdaptorImpl.this;
		}
	}
}
