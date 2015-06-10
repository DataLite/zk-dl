package cz.datalite.collection;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

public class ArrayUtilTest {

	@Test
	public void testAddNoExists() {
		List<Long> src = new LinkedList<>();
		src.add(1L);
		src.add(3L);
		src.add(4L);

		List<Long> dst = new LinkedList<>();
		src.add(3L);

		ArrayUtil.addNoExists(dst, src);

		assertEquals(3, dst.size());
		assertEquals((Object) 1L, dst.get(0));
		assertEquals((Object) 3L, dst.get(1));
		assertEquals((Object) 4L, dst.get(2));
	}

	@Test
	public void testExpand() {
		Object[] result = ArrayUtil.expand(1L);
		assertEquals(1, result.length);
		assertEquals(1L, result[0]);

		result = ArrayUtil.expand(1L, 2L);
		assertEquals(2, result.length);
		assertEquals(1L, result[0]);
		assertEquals(2L, result[1]);

		result = ArrayUtil.expand(1L, new Object[] { 3L, 4L }, 2L);
		assertEquals(4, result.length);
		assertEquals(1L, result[0]);
		assertEquals(3L, result[1]);
		assertEquals(4L, result[2]);
		assertEquals(2L, result[3]);

		result = ArrayUtil.expand(1L);
		assertEquals(1, result.length);
		assertEquals(1L, result[0]);

		result = ArrayUtil.expand();
		assertEquals(0, result.length);
	}

	@Test
	public void testContainsNotNull() throws Exception {
		assertFalse(ArrayUtil.containsNotNull(new Object[]{}));
		assertFalse(ArrayUtil.containsNotNull(new Object[] {null, null}));
		assertFalse(ArrayUtil.containsNotNull(new Object[] {null}));

		assertTrue(ArrayUtil.containsNotNull(new String[] {"foo", null}));
		assertTrue(ArrayUtil.containsNotNull(new String[] {"foo", "bar"}));
	}
}
