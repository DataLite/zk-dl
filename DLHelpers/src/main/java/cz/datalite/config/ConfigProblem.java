package cz.datalite.config;

import cz.datalite.exception.Problem;

/**
 * @author mstastny
 */
public enum ConfigProblem
		implements Problem {

	FILE_LOAD,
	NOT_FOUND,
	REMOTE_UPDATE;

	@Override
	public boolean isStackTraceMuted() {
		return true;
	}

}
