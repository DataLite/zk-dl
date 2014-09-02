package cz.datalite.exception;

/**
 * Problemy persistenci
 */
public enum PersistenceProblem implements Problem {

	/**
	 * napr. pri StaleObjectStateException
	 */
	OPTIMISTIC_LOCKING,
	/**
	 * ConstraintViolationException: nesplněna podmínka jedinečnosti
	 */
	CONSTRAINT,

	/**
	 *  javax.persistence.EntityNotFoundException
	 */
	ENTITY_NOT_FOUND;

	@Override
	public boolean isStackTraceMuted() {
		return false;
	}
}
