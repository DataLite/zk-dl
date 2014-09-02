package cz.datalite.localization;

public enum TestEnumPlain implements Localizable {

	//Zmeny pronaset do zurnalu!

	FOO("foo.bar");

	private String key;

	private TestEnumPlain(String key) {
		this.key = key;
	}

	@Override
	public String getKey() {
		return key;
	}

}
