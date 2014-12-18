package cz.datalite.zk.components.list.controller.impl;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit test for {@link DLListboxComponentControllerImpl}
 */
public class DLListboxComponentControllerImplTest {

	@Test
	public void testGetFieldType() throws Exception {

		assertEquals(String.class, DLListboxComponentControllerImpl.getFieldType(TestClass.class, "stringProp"));
		assertEquals(String.class, DLListboxComponentControllerImpl.getFieldType(TestClass.class, "stringPropNoGetters"));
		assertEquals(Integer.TYPE, DLListboxComponentControllerImpl.getFieldType(TestClass.class, "intProp"));
		assertEquals(Integer.class, DLListboxComponentControllerImpl.getFieldType(TestClass.class, "integerProp"));
		assertEquals(TestClass.TestClassPK.class, DLListboxComponentControllerImpl.getFieldType(TestClass.class, "pk"));
		assertEquals(TestClass.class, DLListboxComponentControllerImpl.getFieldType(TestClass.class, "loop"));
		assertEquals(Integer.TYPE, DLListboxComponentControllerImpl.getFieldType(TestClass.class, "pk.pk1"));
		assertEquals(String.class, DLListboxComponentControllerImpl.getFieldType(TestClass.class, "pk.pk2"));
		assertEquals(BigDecimal.class, DLListboxComponentControllerImpl.getFieldType(TestClass.class, "transientProp"));

		assertNull(DLListboxComponentControllerImpl.getFieldType(TestClass.class, "non"));

	}

	public class TestClass {
		private String stringProp;
		private String stringPropNoGetters;
		private int intProp;
		private Integer integerProp;
		private TestClassPK pk;
		private TestClass loop;

		public class TestClassPK {
			private int pk1;
			private String pk2;

			public int getPk1() {
				return pk1;
			}

			public void setPk1(int pk1) {
				this.pk1 = pk1;
			}

			public String getPk2() {
				return pk2;
			}

			public void setPk2(String pk2) {
				this.pk2 = pk2;
			}
		}

		public BigDecimal getTransientProp() {
			return null;
		}

		public String getStringProp() {
			return stringProp;
		}

		public void setStringProp(String stringProp) {
			this.stringProp = stringProp;
		}

		public int getIntProp() {
			return intProp;
		}

		public void setIntProp(int intProp) {
			this.intProp = intProp;
		}

		public Integer getIntegerProp() {
			return integerProp;
		}

		public void setIntegerProp(Integer integerProp) {
			this.integerProp = integerProp;
		}

		public TestClassPK getPk() {
			return pk;
		}

		public void setPk(TestClassPK pk) {
			this.pk = pk;
		}

		public TestClass getLoop() {
			return loop;
		}

		public void setLoop(TestClass loop) {
			this.loop = loop;
		}
	}
}