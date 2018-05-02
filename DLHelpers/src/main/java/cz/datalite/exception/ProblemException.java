package cz.datalite.exception;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Obecna vyjimka pouzitelna i s {@link Problem}em.
 * 
 * @see Problem
 */
public class ProblemException
		extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Parametry vyjimky - obvykle se pouzivaji k doplneni lokalizacni zpravy definovaneho problemu.
	 */
	private Map<Problem, List<Object>> problems = null;

	/**
	 * Zprava z root cause (muze byt {@code null})
	 */
	private String rootCauseMessage = null;

	/**
	 * 
	 * @param problems
	 * @param message
	 */
	public ProblemException(Map<Problem, List<Object>> problems, String message) {
		super(message);
		this.problems = problems;
	}

	/**
	 * 
	 * @param cause
	 * @param problem
	 */
	public ProblemException(Throwable cause, Problem problem) {
		super(cause);
		setProblem(problem);

		Throwable rootCause = ExceptionUtils.getRootCause(cause);
		if (rootCause != null)
			this.rootCauseMessage = rootCause.getMessage();
	}

	/**
	 * 
	 * @param problem
	 * @param message
	 */
	public ProblemException(Problem problem, String message) {
		super(message);
		setProblem(problem);
	}

	public ProblemException(Problem problem, String message, Object... parameters) {
		super(message);
		add(problem, Arrays.asList(parameters));
	}

	/**
	 * 
	 * @param cause
	 * @param problem
	 * @param message
	 */
	public ProblemException(Throwable cause, Problem problem, String message) {
		super(message, cause);
		setProblem(problem);
	}

	/**
	 * 
	 * @param cause
	 * @param problem
	 * @param message zpráva do logu
	 * @param parameters parametry, do lokalizované zprávy se předávávají pomocí placeholderů {@code {0}, {1}}
	 */
	public ProblemException(Throwable cause, Problem problem, String message, Object... parameters) {
		super(message, cause);
		add(problem, Arrays.asList(parameters));
	}

	/**
	 * 
	 * @param problem
	 */
	public ProblemException(Problem problem) {
		setProblem(problem);
	}

	/**
	 * 
	 * @param problem
	 * @param parameters
	 */
	public ProblemException(Problem problem, Object... parameters) {
		add(problem, Arrays.asList(parameters));
	}

	public void add(Problem problem, List<Object> parameters) {

		if (problems == null) {
			problems = new HashMap<>();
		}
		problems.put(problem, parameters);
	}

	public Map<Problem, List<Object>> getAllProblems() {
		return problems;
	}

	/**
	 * @return the problem
	 */
	public Problem getProblem() {
		return problems == null ? null : problems.keySet().iterator().next();
	}

	/**
	 * @param problem
	 *        the problem to set
	 */
	private void setProblem(Problem problem) {
		this.problems = new HashMap<>();
		this.problems.put(problem, new ArrayList<>());
	}

	/**
	 * @param problem
	 * @return {@code true} pokud je na vyjimce specifikovany problem, jinak {@code false}
	 */
	public boolean hasProblem(Problem problem) {
		return problems != null && problems.containsKey(problem);
	}

	/**
	 *
	 * @return
	 */
	public String getProblemMessageKey() {
		return getProblem() != null ? ProblemUtil.getProblemKey(getProblem()) : null;
	}

	/**
	 * @return the parameters
	 */
	public List<Object> getParameters(Problem problem) {
		return problems.get(problem);
	}

	/**
	 * 
	 * @return
	 */
	public Object[] getParametersArray(Problem problem) {
		List<Object> parameterList = getParameters(problem);
		return parameterList != null ? parameterList.toArray() : null;
	}

	/**
	 * 
	 * @return
	 */
	public String getDescription() {
		StringBuilder builder = new StringBuilder();

		if (problems != null) {
			for (Problem problem : problems.keySet()) {
				builder.append(problem.getClass().getSimpleName());
				builder.append(".");
				builder.append(problem.name());
				builder.append(" ");
			}
		}
		if (getMessage() != null) {
			builder.append(" message: '");
			builder.append(getMessage());
			builder.append("'");
		}
		if (getRootCauseMessage() != null) {
			builder.append(", '");
			builder.append(getRootCauseMessage());
			builder.append("'");
		}
		return builder.toString();
	}

	/**
	 * @return the rootCauseMessage
	 */
	public String getRootCauseMessage() {
		return rootCauseMessage;
	}

	/**
	 * Vrati {@code true} pokud ma byt stacktrace utlumeny (nezalogovany), jinak {@code false}. Pokud je definovany problem, bere
	 * se tato informace z neho. Pokud neni, koukame na standardni
	 * 
	 * @return {@code true} pokud ma byt StackTrace utlumeny (nezalogovany), jinak {@code false}
	 */
	public boolean isStackTraceMuted() {
		return getProblem() != null ? getProblem().isStackTraceMuted() : isStackTraceMutedByDefault();
	}

	/**
	 * V pripade nutnosti muze potomek prekryt a tim nastavit standardni hodnotu pro vsechny vyjimky urciteho typu.
	 * 
	 * @return
	 */
	protected boolean isStackTraceMutedByDefault() {
		return false;
	}

	@Override
	public String toString() {
		return getDescription();
	}
}
