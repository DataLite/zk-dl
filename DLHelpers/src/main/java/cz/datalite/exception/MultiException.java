package cz.datalite.exception;

import java.util.List;
import java.util.Map;

/**
 * Vyjimka shromazdujici vice vyjimek.
 * 
 * Pouziti: Napr. pokud v pripade hromadne editace vice editovanych objektu zpusobi vyjimku a pritom nechceme ukoncit proces uz pri prvni,
 * shromazdime vsechny vyjimky a sloucime je to teto.
 * 
 * @author mstastny
 */
public class MultiException
		extends ProblemException {

	private static final long serialVersionUID = 20130918L;

	private Map<?, Exception> exceptions;

	public MultiException(Problem problem, List<Object> parameters, Map<?, Exception> exceptions) {
		super(problem, parameters);
		this.exceptions = exceptions;
	}

	public MultiException(Problem problem, String message, Map<?, Exception> exceptions) {
		super(problem, message);
		this.exceptions = exceptions;
	}

	public MultiException(Problem problem, Map<?, Exception> exceptions) {
		super(problem);
		this.exceptions = exceptions;
	}

	public Map<?, Exception> getExceptions() {
		return exceptions;
	}

	public boolean causedException(Object key) {
		// @see getElementException
		//return exceptions.containsKey(key);
		return getElementException(key) != null;
	}

	public Exception getElementException(Object key) {
		for (Map.Entry<?, Exception>  entry : exceptions.entrySet()) {
			if (entry.getKey() == key) {
				return entry.getValue();
			}
		}
		return null;
		/*
		NM-581 toto neni mozne, protoze existuje interceptor (EntityRollbackHandler),
		ktery pri vyjimce odstrani z DO primarni klic - nejde pak porovnavat podle neho.
		return exceptions.get(key);
		*/
	}
}
