package com.wy.test.cache;

interface Cache {

	void put(int key, String value);

	String get(int key);

}
