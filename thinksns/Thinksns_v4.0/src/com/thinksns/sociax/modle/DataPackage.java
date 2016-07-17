package com.thinksns.sociax.modle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataPackage {

	public static List<Contact> getContactList() {

		List<Contact> contactList = new ArrayList<Contact>();

		// Contact contact1 = new Contact(1, "aska", "产品经理", "产品",
		// "18632321212",
		// "aska@zhishi.com");

		for (int i = 0; i < 15; i++) {

			// contactList.add(new Contact(1, getRandomString(5), "产品经理", "产品",
			// "18632321212", getRandomString(5) + "@zhishi.com"));
		}

		return contactList;
	}

	// length表示生成字符串的长度
	public static String getRandomString(int length) {
		String base = "abcdefghijklmnopqrstuvwxyz张王李赵钱孙李0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}
}
