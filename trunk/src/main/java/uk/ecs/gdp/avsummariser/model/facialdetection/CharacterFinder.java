package uk.ecs.gdp.avsummariser.model.facialdetection;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Vector;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.colour.Transforms;
import org.openimaj.image.processing.face.alignment.FaceAligner;
import org.openimaj.image.processing.face.alignment.MeshWarpAligner;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.image.processing.face.feature.LocalLBPHistogram;
import org.openimaj.image.processing.face.feature.comparison.FaceFVComparator;
import org.openimaj.image.processing.face.keypoints.FKEFaceDetector;
import org.openimaj.image.processing.face.keypoints.KEDetectedFace;
import org.openimaj.image.processing.face.recognition.FaceMatchResult;
import org.openimaj.image.processing.face.recognition.NaiveBayesRecogniser;
import org.openimaj.video.Video;

import uk.ecs.gdp.avsummariser.model.VideoFile;


import com.moviejukebox.thetvdb.model.Actor;

/**
 * Class which runs through and finds all the characters and matches/identifies them.
 * 
 * @author Michael Harris
 * @author Samantha Kanza
 * @version 1
 */

public class CharacterFinder extends Observable implements Runnable {
	private Video<MBFImage> video;
	private VideoFile videoFile;
	private ArrayList<VideoCharacter> characters;
	private int sampleEveryXFrames;
	private List<Actor> seriesActors;
	private FaceAligner<KEDetectedFace> myFaceAligner;
	private LocalLBPHistogram.Factory<KEDetectedFace> myFactoryFace;
	private LocalLBPHistogram myFaceFeature;
	private FaceFVComparator<LocalLBPHistogram> fc;
	private String genreSelected;
	int nonCharIndex = 0;
	private FKEFaceDetector fd;
	
	public CharacterFinder(Video<MBFImage> video, VideoFile videoFile, int sampleEveryXFrames, List<Actor> seriesActors, String genreSelected) 
	{
		this.video = video;
		this.videoFile = videoFile;
		this.seriesActors = seriesActors;
		characters = new ArrayList<VideoCharacter>();
		this.sampleEveryXFrames = sampleEveryXFrames;
		myFaceAligner = new MeshWarpAligner();
		myFactoryFace = new LocalLBPHistogram.Factory<KEDetectedFace>(myFaceAligner, 25, 25, 8, 1);
		fc = new FaceFVComparator<LocalLBPHistogram>();
		fd = new FKEFaceDetector();
		this.genreSelected = genreSelected;
		//generates image for first series characters for histogram comparison
		if(! seriesActors.isEmpty())
		{
			try 
			{
				KEDetectedFace seriesFace = null;
				for(Actor actor : seriesActors)
				{
					List<KEDetectedFace> list = fd.detectFaces(Transforms.calculateIntensity(ImageUtilities.readMBF(new URL(actor.getImage()))));
					if(! list.isEmpty())
					{
						seriesFace = list.get(0);
						break;
					}
				}
				myFaceFeature = myFactoryFace.createFeature(seriesFace, false);
			} 
			catch (MalformedURLException e) {e.printStackTrace();} 
			catch (IOException e) {e.printStackTrace();}
		}
	} 

	public void run() 
	{
		int faceDetects = 0;
		int frameCount = 1;
		while (video.hasNextFrame()) 
		{
			while (frameCount != sampleEveryXFrames && video.hasNextFrame()) 
			{
				video.getNextFrame();// throw away some frames
				frameCount++;
			}
			frameCount = 1;

			MBFImage myFrame = video.getNextFrame();
			if (myFrame != null) 
			{
				List<KEDetectedFace> faces = fd.detectFaces(Transforms.calculateIntensity(myFrame));
				for (KEDetectedFace face : faces) 
				{
					faceDetects++;
					setChanged();
					MBFImage myCharImage = myFrame.extractROI(face.getBounds());
					boolean newFace = false;

					if(getLBPHistogramComparisonValues(face))
					{
						if((! genreSelected.equals("Action and Adventure") && ! genreSelected.equals("Science-Fiction")) || checkFace(myCharImage, 9, 12) == true)
						{
							if(characters.isEmpty() != true)
							{									
								VideoCharacter charMatch = null;
								int highestMatch = 0;
								for(VideoCharacter vidChar: characters)
								{

									int siftMatch = vidChar.getSIFTComparisonValues(myCharImage, faceDetects);
									if(siftMatch > 3 && siftMatch > highestMatch)
									{
										highestMatch = siftMatch;
										charMatch = vidChar;
									}
									
								}
								if(charMatch != null)
								{
									System.out.println("match size = " + highestMatch);
									charMatch.addCharImage(ImageUtilities.createBufferedImage(myCharImage));
									charMatch.addCharFace(face);
									charMatch.addTimestamp(video.getTimeStamp());
									
									if(charMatch.getCharImages().size() == 1)
									{
										System.out.println("drawing");
										notifyObservers(charMatch);
									} 
								}
								else
								{
									VideoCharacter newChar = new VideoCharacter(face,
											myCharImage, Integer.toString(faceDetects));
									newChar.addTimestamp(video.getTimeStamp());
									characters.add(newChar);
										
									//notifyObservers(newChar);
								}
							}
							else
							{
								VideoCharacter newChar = new VideoCharacter(face,
										myCharImage, Integer.toString(faceDetects));
								newChar.addTimestamp(video.getTimeStamp());
								characters.add(newChar);
								//notifyObservers(newChar);
							}
						}
					}

				}	
			}
			else
			{
				matchActors();
				
				videoFile.setCharactersAppearances(characters);
				videoFile.setCharacterFinderFinished();
				break;
			}
		}
	}
	
	public void matchActors()
	{
		Vector<KEDetectedFace> seriesCharacters = new Vector<KEDetectedFace>();
		Vector<MBFImage> seriesImages = new Vector<MBFImage>();
		NaiveBayesRecogniser<LocalLBPHistogram, KEDetectedFace> faceRecogniser = new NaiveBayesRecogniser<LocalLBPHistogram, KEDetectedFace>(myFactoryFace);
		
		try 
		{
			for(Actor myActor : seriesActors)
			{
				seriesImages.add(ImageUtilities.readMBF(new URL(myActor.getImage())));
				seriesCharacters.add(fd.detectFaces(Transforms.calculateIntensity(ImageUtilities.readMBF(new URL(myActor.getImage())))).get(0));
			}
			
		} 
		catch (MalformedURLException e) {e.printStackTrace();} 
		catch (IOException e) {e.printStackTrace();}
		
		//match chars with series characters 
		for(VideoCharacter  myChar: characters)
		{
			if(myChar.getCharImages().size() > 1)
			{
				faceRecogniser.addInstance(myChar.getName(), myChar.getFace());
				for(KEDetectedFace myFace : myChar.getCharFaces())
				{
					faceRecogniser.addInstance(myChar.getName(), myFace);
				}
			}
		}
		
		faceRecogniser.train();
		
		for(int i = 0; i < seriesCharacters.size(); i++)
		{
			System.out.println("loop = " + i);
			FaceMatchResult fmr = faceRecogniser.queryBestMatch(seriesCharacters.get(i));
			double score = -1.0;
				VideoCharacter currentChar = findCharByName(fmr.getIdentifier());
				if(score == -1.0 || score < currentChar.getActorScore())
				{
					score = currentChar.getActorScore();
					System.out.println("Setting " + currentChar.getName() + " as " + seriesActors.get(i).getName());
					currentChar.setActorScore(fmr.getScore());
					currentChar.setActorName(seriesActors.get(i).getName());
				}	
		}
		
		for(VideoCharacter vidChar : characters)
		{
			System.out.println("vid char top score = " + vidChar.getActorScore() + ", top actor = " + vidChar.getActorName());
			if(vidChar.getActorName().equals(""))
			{
				vidChar.setName("Unknown Character");
			}
			else
			{
				vidChar.setName(vidChar.getActorName());
			}
		}
		
	}
	
	public VideoCharacter findCharByName(String name)
	{
		VideoCharacter matchChar = null;
		for(VideoCharacter vidChar : characters)
		{
			if(vidChar.getName().equals(name))
			{
				matchChar = vidChar;
				break;
			}
		}
		return matchChar;
	}
	
	public boolean checkFace(MBFImage faceToMatch, int noPixels, int radius)
	{
		boolean isFace = true;
		int cfX = faceToMatch.getWidth()/2;
		int cfY = faceToMatch.getHeight()/2;
		double xCFMean = (faceToMatch.getPixel(cfX, cfY)[0] + faceToMatch.getPixel(cfX, cfY - radius)[0] + 
				faceToMatch.getPixel(cfX + radius, cfY - radius)[0] + faceToMatch.getPixel(cfX + radius, cfY)[0] + 
				faceToMatch.getPixel(cfX + radius, cfY + radius)[0] + faceToMatch.getPixel(cfX, cfY + radius)[0] + 
				faceToMatch.getPixel(cfX - radius, cfY + radius)[0] + faceToMatch.getPixel(cfX - radius, cfY)[0] + 
				faceToMatch.getPixel(cfX - radius, cfY - radius)[0])/noPixels;  
		

		double yCFMean = (faceToMatch.getPixel(cfX, cfY)[1] + faceToMatch.getPixel(cfX, cfY - radius)[1] + 
				faceToMatch.getPixel(cfX + radius, cfY - radius)[1] + faceToMatch.getPixel(cfX + radius, cfY)[1] + 
				faceToMatch.getPixel(cfX + radius, cfY + radius)[1] + faceToMatch.getPixel(cfX, cfY + radius)[1] + 
				faceToMatch.getPixel(cfX - radius, cfY + radius)[1] + faceToMatch.getPixel(cfX - radius, cfY)[1] + 
				faceToMatch.getPixel(cfX - radius, cfY - radius)[1])/noPixels;  
		
		
		if(xCFMean < 0.3 || yCFMean < 0.3 || xCFMean > 0.6 || yCFMean > 0.6)
		{
			isFace = false;
		}
		
		return isFace;
	}
	
	public boolean getLBPHistogramComparisonValues(KEDetectedFace faceToMatch)
	{
		boolean match = false;
		
		LocalLBPHistogram newFaceFeature1 = myFactoryFace.createFeature(faceToMatch, true);

		if(fc.compare(newFaceFeature1, myFaceFeature) > 10.2 && fc.compare(newFaceFeature1, myFaceFeature) < 11.0)
		{
			match = true;
		}

		return match;
	}
	
}
