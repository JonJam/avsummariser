package uk.ecs.gdp.avsummariser.model.videotools;

import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.event.AudioSamplesEvent;
import com.xuggle.mediatool.event.IAddStreamEvent;
import com.xuggle.mediatool.event.IAudioSamplesEvent;
import com.xuggle.mediatool.event.ICloseCoderEvent;
import com.xuggle.mediatool.event.ICloseEvent;
import com.xuggle.mediatool.event.IOpenCoderEvent;
import com.xuggle.mediatool.event.IOpenEvent;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.mediatool.event.VideoPictureEvent;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IVideoPicture;

/**
 * Class to concatenate videos.
 * 
 * @author Michael Harris
 * @version 1
 */
public class MediaConcatenator extends MediaToolAdapter {
	
	//Current offset
	private long mOffset = 0;
	//Next video timestamp
	private long mNextVideo = 0;
	//Next audio timestamp
	private long mNextAudio = 0;
	//Index of the audio stream
	private final int mAudoStreamIndex;
	//Index of the video stream
	private final int mVideoStreamIndex;


	protected MediaConcatenator(int audioStreamIndex, int videoStreamIndex) {
		mAudoStreamIndex = audioStreamIndex;
		mVideoStreamIndex = videoStreamIndex;
	}


	public void onAudioSamples(IAudioSamplesEvent event) {
		
		IAudioSamples samples = event.getAudioSamples();

		//Set the new time stamp to the original plus the offset established for this media file
		long newTimeStamp = samples.getTimeStamp() + mOffset;

		/* Keep track of predicted time of the next audio samples, if the end
		* of the media file is encountered, then the offset will be adjusted
		* to this time.
		*/
		mNextAudio = samples.getNextPts();

		//Set the new timestamp on audio samples
		samples.setTimeStamp(newTimeStamp);

		//Create a new audio samples event with the one true audio stream index
		super.onAudioSamples(new AudioSamplesEvent(this, samples,mAudoStreamIndex));
	}

	public void onVideoPicture(IVideoPictureEvent event) {
		IVideoPicture picture = event.getMediaData();
		long originalTimeStamp = picture.getTimeStamp();

		//Set the new time stamp to the original plus the offset established for this media file
		long newTimeStamp = originalTimeStamp + mOffset;

		/* Keep track of predicted time of the next video picture, if the end
		 * of the media file is encountered, then the offset will be adjusted
		 * to this this time.
		 *
		 * You'll note in the audio samples listener above we used
		 * a method called getNextPts(). Video pictures don't have
		 * a similar method because frame-rates can be variable, so
		 * we don't now. The minimum thing we do know though (since
		 * all media containers require media to have monotonically
		 * increasing time stamps), is that the next video timestamp
		 * should be at least one tick ahead. So, we fake it.
		 */
		mNextVideo = originalTimeStamp + 1;

		//Set the new timestamp on video samples
		picture.setTimeStamp(newTimeStamp);

		//Create a new video picture event with the one true video stream index
		super.onVideoPicture(new VideoPictureEvent(this, picture,mVideoStreamIndex));
	}

	public void onClose(ICloseEvent event) {
		//Update the offset by the larger of the next expected audio or video frame time
		mOffset = Math.max(mNextVideo, mNextAudio);

		if (mNextAudio < mNextVideo) {
			/* In this case we know that there is more video in the
			 * last file that we read than audio. Technically you
			 * should pad the audio in the output file with enough
			 * samples to fill that gap, as many media players (e.g.
			 * Quicktime, Microsoft Media Player, MPlayer) actually
			 * ignore audio time stamps and just play audio sequentially.
			 * If you don't pad, in those players it may look like
			 * audio and video is getting out of sync.
             *
			 * However kiddies, this is demo code, so that code
			 * is left as an exercise for the readers. As a hint,
			 * see the IAudioSamples.defaultPtsToSamples(...) methods.
			 */ 
		}
	}

	public void onAddStream(IAddStreamEvent event) {
		// Overridden to ensure that add stream events are not passed down the tool chain to the writer, which could cause problems
	}

	public void onOpen(IOpenEvent event) {
		// Overridden to ensure that open events are not passed down the tool chain to the writer, which could cause problems
	}

	public void onOpenCoder(IOpenCoderEvent event) {
		// Overridden to ensure that open coder events are not passed down the tool chain to the writer, which could cause problems
	}

	public void onCloseCoder(ICloseCoderEvent event) {
		// Overridden to ensure that close coder events are not passed down the tool chain to the writer, which could cause problems
	}
}