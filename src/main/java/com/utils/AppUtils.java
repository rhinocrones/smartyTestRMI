package com.utils;

import java.io.IOException;
import java.util.Random;

import org.codehaus.jackson.map.ObjectMapper;

import com.entity.Human;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.starter.AdaptorStarter;
import com.starter.ManagerStarter;

import redis.clients.jedis.Jedis;

public class AppUtils {

	private static AppUtils instance;

	private Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private Jedis jedis = new Jedis("localhost");

	private ObjectMapper mapper = new ObjectMapper();

	private AppUtils() {
	}

	public static AppUtils getInstance() {
		if (instance == null) {
			synchronized (AppUtils.class) {
				if (instance == null) {
					instance = new AppUtils();
				}
			}
		}
		return instance;
	}

	public Human lpopHuman() {
		String JsonHuman = jedis.lpop("Human");
		if (JsonHuman != null) {
			try {
				return mapper.readValue(JsonHuman, Human.class);
			} catch (IOException e) {
				return new Human();
			}
		} else {
			return new Human();
		}
	}

	public void rpushHuman(final Human human) {
		String JsonHuman = gson.toJson(human);
		jedis.rpush("Human", JsonHuman);
	}

	public void fillUpRedis(final int i) {
		for (int j = 0; j < i; j++) {
			Random random = new Random();
			int randomInt = random.nextInt(4);
			Human human = new Human();
			human.setName("Vasja NO " + j);
			human.setAge(18 + j);
			if (randomInt == 3) {
				human.getApis().add("uk");
				human.getApis().add("en");
				human.getApis().add("ru");
			} else if (randomInt == 2) {
				human.getApis().add("en");
				human.getApis().add("uk");
			} else {
				human.getApis().add("uk");
			}
			String objectForStore = gson.toJson(human);
			jedis.rpush("Human", objectForStore);
		}
	}

	public void clearRedis(final int i) {
		for (int j = 0; j < i; j++) {
			System.out.println(jedis.lpop("Human"));
		}
	}

	public void start() {
		fillUpRedis(100);
		new Thread(new AdaptorStarter()).start();
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		new Thread(new ManagerStarter()).start();
	}
}
