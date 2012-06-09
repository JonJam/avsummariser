package uk.ecs.gdp.avsummariser.model.videotools;

import java.io.File;
import java.io.IOException;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;

/**
 * MediaToolAdapter class which is used as part of splitting up video files.
 * 
 * @author Michael Harris
 * @version 1
 */
public class GetSubsetAdapter extends MediaToolAdapter {
	
	private IMediaReader reader;
	private boolean end;
	private long startTime;
	private long endTime;
	private double timebase;
	private String extension;
	private long timestamp;

	protected GetSubsetAdapter(IMediaReader reader, long startTime,long endTime, String extension) {
		
		end = false;
		this.reader = reader;
		this.startTime = startTime;
		this.endTime = endTime;
		this.extension = "." + extension;
	}

	public void onVideoPicture(IVideoPictureEvent event) {

		timestamp = (long) ((event.getPicture().getTimeStamp() * event.getPicture().getTimeBase().getDouble()) * 1000);
		long endTimestamp = endTime;
		
		if (timestamp >= endTimestamp){
			//Found end
			end = true;
		} else {
			//Not found end
			super.onVideoPicture(event);
		}
	}

	/**
	 * Method to start splitting of a video file.
	 * 
	 * @return File object
	 */
	public File start() {
		File file = null;
		
		try {
			file = File.createTempFile("split-"+ startTime + "--" + endTime +"__", extension);
			IMediaWriter writer = ToolFactory.makeWriter(file.toString(),reader);
			this.addListener(writer);

			reader.open();

			//Get number of streams
			int numStreams = reader.getContainer().getNumStreams();
			int videoStreamId = -1;

			//Loop to find the video stream
			for (int j = 0; j < numStreams; j++) {
				
				IStream stream = reader.getContainer().getStream(j);
				IStreamCoder coder = stream.getStreamCoder();

				if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
					//Found video stream.
					timebase = reader.getContainer().getStream(j).getTimeBase().getDouble();
					videoStreamId = j;
					break;
				}
			}

			double timebase = reader.getContainer().getStream(0).getTimeBase().getDouble();
			long position = startTime;

			seekPrecise(position, videoStreamId);

			while (reader.readPacket() == null && end == false)
				do {
				} while (false);

			if (writer.isOpen()){
				writer.flush();
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * Method to seek to time in video.
	 * 
	 * @param timestamp (double value)
	 * @param streamIndex (int value)
	 */
	public void seek(double timestamp, int streamIndex) {
		timestamp /= 1000;
		
		//Make timestamp into seconds
		double timebase = this.reader.getContainer().getStream(streamIndex).getTimeBase().getDouble();

		long position = (long) (timestamp / timebase);
		reader.getContainer().seekKeyFrame(streamIndex, position - 100,position, position, 0);
	}
	
	/**
	 * From XuggleVideo OpenIMAJ class.
	 * 
	 * @param timestamp (double value)
	 * @param streamIndex (int value)
	 */
	public void seekPrecise(double timestamp, int streamIndex) {
		// Use the Xuggle seek method first to get near the frame
		this.seek(timestamp, streamIndex);

		// Work out the number of milliseconds per frame
		double timePerFrame = 1000d / 25;

		// If we're not in the right place, keep reading until we are.
		// Note the right place is the frame before the timestamp we're given:
		// |---frame 1---|---frame2---|---frame3---|
		// ^- given timestamp
		// ... so we should show frame2 not frame3.
		while (this.timestamp <= timestamp - timePerFrame && reader.readPacket() != null);
	}
}
