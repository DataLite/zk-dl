package cz.datalite.zk.components.list.view;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DLListheaderTest {

	private DLListheader header;

	@Before
	public void before() {
		header = new DLListheader();
	}


	@Test
	public void testSetSortNone() {
		header.setSort("none");
		assertFalse(header.sortable);
		assertFalse(header.sortZk);
	}

	@Test
	public void testSetSortAuto() {
		header.setSort("auto");
		assertTrue(header.sortable);
		assertTrue(header.sortZk);
		assertEquals("", header.sortColumn);
	}

	@Test
	public void testSetSortAutoColumn() {
		header.setSort("auto(foo)");
		assertEquals("foo", header.sortColumn);
	}

	@Test
	public void testSetSortDb() {
		header.setSort("db()");
		assertTrue(header.sortable);
		assertFalse(header.sortZk);
		assertEquals("", header.sortColumn);
	}

	@Test
	public void testSetSortDbColumn() {
		header.setSort("db(foo)");
		assertEquals("foo", header.sortColumn);
	}


	@Test
	public void testSetSortUnknown() {
		try {
			header.setSort("neznamy");
			fail("Neznámý druh řazení");
		} catch (UnsupportedOperationException expected) {
			// ocekavany
		}
	}


}