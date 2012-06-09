package uk.ecs.gdp.avsummariser.model.music;

import java.io.File;

import com.echonest.api.v4.EchoNestAPI;
import com.echonest.api.v4.Track;

import uk.ecs.gdp.avsummariser.model.VideoFile;
import uk.ecs.gdp.avsummariser.model.section.MusicSection;
import uk.ecs.gdp.avsummariser.model.videotools.VideoFileHelper;
	
/**
 * MusicIdentifier class contains a function to identify a music segment using the EchoNest music identification service.
 * 
 * EchoNest API Information:
 *  Our API Key: WE1MQYOOHJ9MZXVSI
 *  Our Consumer Key: f0b60cd334402ebecba50e571f12d89f 
 *	Our Shared Secret: OVoKCNhaS1C03lzkV90IfQ
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class MusicIdentifier {

	//Echonest API Key
	private static final String API_KEY = "WE1MQYOOHJ9MZXVSI";
		
	
	//Method to identify an audio segment from the video passed in. Will return a Track object if identified successfully or null otherwise.
	public static MusicSection identifyMusic(VideoFile video, long startTime, long endTime){
		
		//Create video of segment specified in start and end time values.
		File musicSegment = VideoFileHelper.splitVideo(new File(video.getAbsolutePath()), startTime, endTime);
		
		EchoNestAPI en = new EchoNestAPI(API_KEY);
		Track track = null;
		
		try {
			 track = en.uploadTrack(musicSegment, true);
			 track.waitForAnalysis(300000); //5 minutes
			 
			 if(track.getStatus() == Track.AnalysisStatus.COMPLETE){
				 if(track.getArtistName() == null && track.getTitle() == null && track.getReleaseName() == null){
					 //Not matched a track so return MusicSection without a track object.
					 return new MusicSection(video,startTime,endTime,null);
				 } else{
					 //Matched a track
					 return new MusicSection(video,startTime,endTime,track);
				 }
			 } else{
				 //Analysis failed so return MusicSection without a track object.
				 return new MusicSection(video,startTime,endTime,null);
			 }
		} catch (Exception e){
			//Analysis failed so return MusicSection without a track object.
			e.printStackTrace();
			return new MusicSection(video,startTime,endTime,null);
		}
	}
}
