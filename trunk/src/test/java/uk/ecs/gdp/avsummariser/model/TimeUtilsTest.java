package uk.ecs.gdp.avsummariser.model;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import uk.ecs.gdp.avsummariser.model.time.TimeUtils;

public class TimeUtilsTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@Test
	public void testGetTimeInString() {
		long time = 100000; //time in milliseconds
		String result = TimeUtils.getTimeInString(time);
		
		assertNotNull(result);
		assertEquals(result, "00:01:40");
	}

	@Test
	public void testGetTimeInMilliSeconds() {
		String time = "00:01:40.0"; //time in milliseconds
		long result = TimeUtils.getTimeInMilliSeconds(time);
	
		assertNotNull(result);
		assertEquals(result,100000L);
	}

	@Test
	public void testConvertMMSStoTimeInMilliseconds() {
		String time = "01:40"; //time in milliseconds
		long result = TimeUtils.convertMMSStoTimeInMilliseconds(time);
	
		assertNotNull(result);
		assertEquals(result,100000L);
	}

	@Test
	public void testFormatMillisIntoSeconds() {
		long time = 100000; //time in milliseconds
		double result = TimeUtils.formatMillisIntoSeconds(time);
		assertNotNull(result);
		assertEquals(100, result, 0);
	}

	@Test
	public void testFormatMillisIntoMin() {
		long time = 900000; //time in milliseconds
		double result = TimeUtils.formatMillisIntoMin(time);
		assertNotNull(result);
		assertEquals(15, result, 0);
	}

}
