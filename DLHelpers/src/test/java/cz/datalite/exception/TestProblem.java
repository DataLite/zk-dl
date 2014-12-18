package cz.datalite.exception;

import cz.datalite.exception.Problem;

public enum TestProblem
		implements Problem {

	FOO;

	@Override
	public boolean isStackTraceMuted() {
		return true;
	}

}
