package cz.datalite.exception;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit test pro {@link cz.datalite.exception.ProblemUtil}
 */
public class ProblemUtilTest {

	@Test
	public void testGetProblemKey() throws Exception {
		assertEquals("cz.datalite.exception.TestProblem.FOO", ProblemUtil.getProblemKey(TestProblem.FOO));
		Assert.assertNull(ProblemUtil.getProblemKey(null));
	}


	// public, aby prekladac nezahodil "nepouzivany" getter
	public static class TestWrapper {
		private String firstProp;

		public TestWrapper(String firstProp) {
			super();
			this.firstProp = firstProp;
		}

		public String getFirstProp() {
			return firstProp;
		}

		public void setFirstProp(String firstProp) {
			this.firstProp = firstProp;
		}

	}

	@Test
	public void testFormatMessage() {

		Object[] paramNoValues = new Object[] {};
		assertNull(ProblemUtil.formatMessage("", paramNoValues));

		assertEquals("No param", ProblemUtil.formatMessage("No param", paramNoValues));

		Object[] paramValues = new Object[] { Long.valueOf(1), Double.valueOf(7.8), new TestWrapper("superTruper") };
		// "third param toString {2}" vraci neco jako
		// "third param toString cz.datalite.jee.logging.aspect.SystemLogAspectTest$TestWrapper@35ec28b7"
		assertEquals("first param: 1, second param: 7.8, second param again: 7.8, third param value: superTruper",
				ProblemUtil.formatMessage(
						"first param: {0}, second param: {1}, second param again: {1}, third param value: {2.firstProp}", paramValues));

		assertEquals("superTruper", ProblemUtil.formatMessage("{0}", new Object[] { "superTruper" }));
	}

	@Test
	public void testFormatMessageNull() {

		Object[] paramValues = new Object[] { "", null, new TestWrapper(null) };

		assertEquals("first param: , second param: null, second param nested: null, third param: null", ProblemUtil.formatMessage(
				"first param: {0}, second param: {1}, second param nested: {1.prop.val}, third param: {2.firstProp}", paramValues));
	}

	@Test
	public void testFormatMessageWrong() {

		Object[] paramValues = new Object[] { "", null, new TestWrapper(null) };

		assertEquals("outOfBounds param: Wrong value specification",
				ProblemUtil.formatMessage("outOfBounds param: {9}", paramValues));

		assertEquals("Wrong log pattern",
				ProblemUtil.formatMessage("missing curly bracet: {0", paramValues));

		assertEquals("missing curly bracet: Wrong value specification",
				ProblemUtil.formatMessage("missing curly bracet: {0, second param: {1}", paramValues));

		assertEquals("missing property: Wrong value specification",
				ProblemUtil.formatMessage("missing property: {2.missing}", paramValues));
	}
}
