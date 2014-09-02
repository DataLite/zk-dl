package cz.datalite.xml;

import cz.datalite.exception.Problem;

/**
 * @author mstastny
 */
public enum XmlProblem
		implements Problem {

	PARSER_CONFIGURATION,
	PARSING,
	XPATH,
	PARAMETER,
	SERIALIZE_DOM,
	INVALID_ENCODING,
	SERIALIZE,
	DESERIALIZE;

	@Override
	public boolean isStackTraceMuted() {
		return true;
	}

}
