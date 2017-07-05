package com.entity;

public class Counter {

	public void count(Human human, double a) {
		for (int i = 0; i < 1000000; i++) {
			a = a + Math.tan(a);
		}
		System.out
		        .println("In count method " + Thread.currentThread().getName());
		modify(human);
	}

	private Human modify(Human human) {
		if (human.getApis().size() == 3) {
			human.getApis().add("3");
		} else if (human.getApis().size() == 2) {
			human.getApis().add("2");
		} else if (human.getApis().size() == 1) {
			human.getApis().add("1");
		} else {
			human.getApis().add("0");
		}
		return human;
	}
}