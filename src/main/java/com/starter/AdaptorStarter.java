package com.starter;

import com.adaptor.impl.AdaptorImpl;

public class AdaptorStarter implements Runnable {

	@Override
	public void run() {
		new Thread(() -> {
			AdaptorImpl adaptor = AdaptorImpl.newBuilder().setPort(2021)
			        .setEnCall(1).setRuCall(7).setUkCall(6)
			        .setgetConnectionLimits(14).build();
			adaptor.exportAdaptor();
			try {
				adaptor.findManager();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
		new Thread(() -> {
			AdaptorImpl adaptor = AdaptorImpl.newBuilder().setPort(2022)
			        .setEnCall(3).setRuCall(3).setUkCall(3)
			        .setgetConnectionLimits(9).build();
			adaptor.exportAdaptor();
			try {
				adaptor.findManager();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
		new Thread(() -> {
			AdaptorImpl adaptor = AdaptorImpl.newBuilder().setPort(2023)
			        .setEnCall(6).setRuCall(2).setUkCall(1)
			        .setgetConnectionLimits(4).build();
			adaptor.exportAdaptor();
			try {
				adaptor.findManager();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
	}
}
