package uk.ecs.gdp.avsummariser.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class VideoFileTest {

	VideoFile videoFile;

	@Before
	public void before() {
		videoFile = new VideoFile("niceName", "fileName", "/path");

	}

	@Test
	public void testVideoFile() {
		assertNotNull(videoFile);
	}

	@Test
	public void testGetFriendlyName() {
		assertEquals(videoFile.getFriendlyName(), "niceName");
	}

	@Test
	public void testGetFileName() {
		assertEquals(videoFile.getFileName(), "fileName");
	}

	@Test
	public void testGetAbsolutePath() {
		assertNotNull(videoFile.getAbsolutePath());
	}

	@Test
	public void testGetSetSubtitleFilePath() {
		videoFile.setSubtitleFilePath("subtitlePath");
		assertNotNull(videoFile.getSubtitleFilePath());
	}

}
