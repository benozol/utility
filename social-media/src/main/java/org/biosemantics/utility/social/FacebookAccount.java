package org.biosemantics.utility.social;

public enum FacebookAccount {

	ACCESS_TOKEN("CAACEdEose0cBAFKHTX6GcttUziRkUSgC91tlbwkrorK2rs8dqZBt06T87mMSFnw1MSKnFKVWlUggLh8ZB26jqoDulsLuWnt1FVLnaR7suUbIzM4B4senO4bXzGnmXZB0tlc0ZBVQYlSapWYwgA86vmVx5HUafn26HhPIVQTajUSRZCWzPFPbTQbWlnBBhOJh8LqNW2kSTUwZDZD"),
	TOKEN(""),
	TOKEN_SECRET("");

	private String value;

	private FacebookAccount(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
