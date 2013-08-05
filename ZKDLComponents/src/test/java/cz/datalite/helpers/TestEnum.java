package cz.datalite.helpers;

enum TestEnum {
	
	ONE("first"),
	TWO("second"),
	THREE("third");

	@SuppressWarnings("unused")
	private String row;

	private TestEnum(String row) {
		this.row = row;
	}
	
}
