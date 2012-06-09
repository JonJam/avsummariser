package uk.ecs.gdp.avsummariser.model.videotools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;

/**
 * Class which contains methods to split a video and to concatenate a list of videos together.
 * 
 * @author Michael Harris
 * @version 1
 */
public class VideoFileHelper {

	/**
	 * Method which given a video file and start and end timestamps (time in milliseconds),
	 * this method will create a new video file which is a subset of the
	 * original. 
	 * 
	 * Only some formats are supported for now use .mp4.
	 * To convert to
	 * 		mp4 try: ffmpeg -i input.ts -vcodec copy -acodec copy output.mp4
	 * To chop a video:
	 * 		ffmpeg -i input.avi -vcodec copy -acodec copy -ss 00:00:00 -t 00:30:00 output1.avi
	 * 
	 * @param inputFile (File object)
	 * @param startTime (long value)
	 * @param endTime (long value)
	 * @return File object
	 */
	public static File splitVideo(File inputFile, long startTime, long endTime) {
		
		String fileName = inputFile.getName();
		String ext = fileName.substring(fileName.lastIndexOf(".") + 1,fileName.length());
		
		IMediaReader reader = ToolFactory.makeReader(inputFile.toString());
		GetSubsetAdapter subsetAdaptor = new GetSubsetAdapter(reader,startTime, endTime, ext);

		reader.addListener(subsetAdaptor);

		return subsetAdaptor.start();
	}

	/**
	 * Method to concatenate a list of files together
	 * 
	 * @param filesList
	 * @return
	 */
	public static File concatFiles(List filesList) {
		
		ArrayList<File> files = new ArrayList<File>();
		files.addAll(filesList);
		
		int noOfFiles = files.size();
		
		if (noOfFiles == 0) {
			return null;
		}
		
		if (noOfFiles == 1) {
			return (File) files.get(0);
		} else if (noOfFiles == 2) {
			return concat(files.get(0), files.get(1));
		} else {

			return concat(concat(files.get(0),files.get(1)), concatFiles(files.subList(2, noOfFiles)));
		}
	}
	
	/**
	 * Method to concatenate two files together.
	 * 
	 * @param file1 (File object)
	 * @param file2 (File object)
	 * @return File object
	 */
	private static File concat(File file1, File file2) {
		
		if(file2 == null){
			return file1;
		}
		
		File outFile = null;
		try {
			outFile = File.createTempFile("outfile", ".mp4");

			//Video stream index.
			final int videoStreamIndex = 0;
			//Audio stream index.
			final int audioStreamIndex = 1;

			//Create the media concatenator
			IMediaWriter writer = null;
			MediaConcatenator concatenator = new MediaConcatenator(audioStreamIndex, videoStreamIndex);

			//Create the media readers
			IMediaReader reader1 = ToolFactory.makeReader(file1.getAbsolutePath());
			IMediaReader reader2 = ToolFactory.makeReader(file2.getAbsolutePath());
			reader1.addListener(concatenator);
			reader2.addListener(concatenator);

			//Create the media writer which listens to the concatenator
			writer = ToolFactory.makeWriter(outFile.toString(), reader1);
			concatenator.addListener(writer);

			while (reader1.readPacket() == null);
			while (reader2.readPacket() == null);

			writer.flush();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return outFile;
	}
}
