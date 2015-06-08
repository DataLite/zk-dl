package cz.datalite.zk.converter;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test pro {@link CollectionPropertyConverter}
 */
public class CollectionPropertyConverterTest {

	@Test
	public void testToUI() throws Exception {
		List<TestBean> beans = new LinkedList<>();
		{
			TestBean t = new TestBean();
			t.setIntProp(2);
			beans.add(t);
		}
		{
			TestBean t = new TestBean();
			t.setIntProp(3);
			beans.add(t);
		}

		assertEquals("2, 3", new CollectionPropertyConverter().toUI(beans, ", ", "intProp"));

		assertNotNull(new CollectionPropertyConverter().toUI(beans, ", ", "fail"));
	}
}