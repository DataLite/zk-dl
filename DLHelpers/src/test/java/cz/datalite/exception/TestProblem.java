package cz.datalite.exception;

public enum TestProblem
		implements Problem {

	FOO;

	@Override
	public boolean isStackTraceMuted() {
		return true;
	}

}
