package com.worker.impl;

import java.rmi.RemoteException;

import com.adaptor.Adaptor;
import com.entity.Counter;
import com.entity.Human;
import com.worker.Worker;

public class WorkerImpl implements Worker {

	private Human human;

	private Adaptor adaptor;

	public WorkerImpl(final Human human, final Adaptor adaptor) {
		this.human = human;
		this.adaptor = adaptor;
	}

	@Override
	public void run() {
		System.out.println("In worker");
		for (int i = 0; i < 5; i++) {
			new Counter().count(human, 5);
		}
		try {
			Thread.sleep(4000);
			System.out.println(Thread.currentThread().getName());
			System.out.println("Finished in call method");
			adaptor.onJobExecuted(human);
			adaptor.setNotBusy(true);
			synchronized (this) {
				this.adaptor.getManager().notifyManager();
			}
		} catch (InterruptedException | RemoteException e) {
			System.out.println("EXCEPTION IN WORKER THREAD");
			e.printStackTrace();
		}
	}
}
