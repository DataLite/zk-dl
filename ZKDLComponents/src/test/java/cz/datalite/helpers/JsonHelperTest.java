package cz.datalite.helpers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

public class JsonHelperTest {

	@Test(expected = IllegalArgumentException.class)
	public void testWrongBean() {
		WrongClz value = new WrongClz();
		JsonHelper.toJsonObject(value);		
	}
	
	@Test
	public void testGoodBean() {
		TestBean value = new TestBean(12345);		
		Assert.assertEquals(value, JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value), value.getClass().getName()));
		
		value = null;		
		Assert.assertEquals(value, JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value), TestBean.class.getName()));
	}
	
	@Test
	public void testString() {
		String value = "text.text.123.ěšč/\\";
		Assert.assertEquals(value, JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value), value.getClass().getName()));
		
		value = "";
		Assert.assertEquals(value,	JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value), value.getClass().getName()));
		
		value = null;
		Assert.assertNull(JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value), String.class.getName()));
	}
	
	@Test
	public void testDate() {
		Date value = new Date();
		Assert.assertEquals(value, JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value), value.getClass().getName()));
		
		java.sql.Date value2 = new java.sql.Date(value.getTime());
		Assert.assertEquals(value2,	JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value2), value2.getClass().getName()));
		
		value = null;
		Assert.assertNull(JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value), Date.class.getName()));
	}
	
	@Test
	public void testBoolean() {
		Boolean value = new Boolean(true);
		Assert.assertEquals(value, JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value), value.getClass().getName()));
		
		value = null;
		Assert.assertNull(JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value), Boolean.class.getName()));
	}
	
	@Test
	public void testInteger() {
		Integer value = Integer.MAX_VALUE;
		Assert.assertEquals(value, JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value), value.getClass().getName()));
		
		value = Integer.MIN_VALUE;
		Assert.assertEquals(value, JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value), value.getClass().getName()));
		
		value = null;
		Assert.assertNull(JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value), Integer.class.getName()));
	}
	
	@Test
	public void testLong() {
		Long value = Long.MAX_VALUE;
		Assert.assertEquals(value, JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value), value.getClass().getName()));
		
		value = Long.MIN_VALUE;
		Assert.assertEquals(value, JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value), value.getClass().getName()));
		
		value = null;
		Assert.assertNull(JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value), Long.class.getName()));
	}
	
	@Test
	public void testFloat() {
		Float value = Float.MAX_VALUE;
		Assert.assertEquals(value, JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value), value.getClass().getName()));
		
		value = Float.MIN_VALUE;
		Assert.assertEquals(value, JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value), value.getClass().getName()));
		
		value = null;
		Assert.assertNull(JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value), Float.class.getName()));
	}
	
	@Test
	public void testDouble() {
		Double value = Double.MAX_VALUE;
		Assert.assertEquals(value, JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value), value.getClass().getName()));
		
		value = Double.MIN_VALUE;
		Assert.assertEquals(value, JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value), value.getClass().getName()));
		
		value = null;
		Assert.assertNull(JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value), Double.class.getName()));
	}
	
	@Test
	public void testBigDecimal() {
		BigDecimal value = BigDecimal.ONE;
		Assert.assertEquals(value, JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value), value.getClass().getName()));
		
		value = new BigDecimal("-1000");
		Assert.assertEquals(value, JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value), value.getClass().getName()));
		
		value = null;
		Assert.assertNull(JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value), BigDecimal.class.getName()));
	}
	
	@Test
	public void testEnum() {
		TestEnum value = TestEnum.ONE;
		Assert.assertEquals(value, JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value), value.getClass().getName()));
		
		value = TestEnum.THREE;
		Assert.assertEquals(value, JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value), value.getClass().getName()));
		
		value = null;
		Assert.assertNull(JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value), TestEnum.class.getName()));
	}
	
	@Test
	public void testEnumCollection() {
		List<TestEnum> value = new ArrayList<>();
		value.add(TestEnum.ONE);
		value.add(TestEnum.TWO);
		
		Assert.assertEquals(value, JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value),
			value.getClass().getName() + "#" + TestEnum.class.getName()));
		
		Set<TestEnum> value2 = new HashSet<>();
		value2.add(TestEnum.ONE);
		value2.add(TestEnum.TWO);
		
		Assert.assertEquals(value2, JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value2),
			value2.getClass().getName() + "#" + TestEnum.class.getName()));
		
		value2 = null;
		Assert.assertNull(JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value2),
			List.class.getName() + "#" + TestEnum.class.getName()));
	}
	
	@Test
	public void testStringCollection() {
		List<String> value = new ArrayList<>();
		value.add("STR1");
		value.add("STR2");
		
		Assert.assertEquals(value, JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value),
			value.getClass().getName() + "#" + String.class.getName()));
		
		Set<String> value2 = new HashSet<>();
		value2.add("STR1");
		value2.add("STR2");
		
		Assert.assertEquals(value2, JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value2),
			value2.getClass().getName() + "#" + String.class.getName()));		
		
		value2 = null;
		Assert.assertNull(JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value2),
			List.class.getName() + "#" + String.class.getName()));
	}
	
	@Test
	public void testDateCollection() {
		List<Date> value = new ArrayList<>();
		value.add(new Date());
		value.add(new Date());
		
		Assert.assertEquals(value, JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value),
			value.getClass().getName() + "#" + Date.class.getName()));
		
		Set<Date> value2 = new HashSet<>();
		value.add(new Date());
		value.add(new Date());
		
		Assert.assertEquals(value2, JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value2),
			value2.getClass().getName() + "#" + Date.class.getName()));		
		
		value2 = null;
		Assert.assertNull(JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value2),
			List.class.getName() + "#" + Date.class.getName()));
	}
	
	@Test
	public void testLongCollection() {
		List<Long> value = new ArrayList<>();
		value.add(1L);
		value.add(2L);
		
		Assert.assertEquals(value, JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value),
			value.getClass().getName() + "#" + Long.class.getName()));
		
		Set<Long> value2 = new HashSet<>();
		value2.add(1L);
		value2.add(2L);
		
		Assert.assertEquals(value2, JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value2),
			value2.getClass().getName() + "#" + Long.class.getName()));		
		
		value2 = null;
		Assert.assertNull(JsonHelper.fromJsonObject(JsonHelper.toJsonObject(value2),
			List.class.getName() + "#" + Long.class.getName()));
	}
	
	private class WrongClz {
		
	}
}
