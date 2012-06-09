package uk.ecs.gdp.avsummariser.model.facialdetection;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;

import javax.imageio.ImageIO;

import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.feature.local.matcher.FastBasicKeypointMatcher;
import org.openimaj.feature.local.matcher.LocalFeatureMatcher;
import org.openimaj.feature.local.matcher.consistent.ConsistentLocalFeatureMatcher2d;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import org.openimaj.image.feature.local.engine.DoGSIFTEngine;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.feature.DoGSIFTFeature;
import org.openimaj.image.processing.face.feature.comparison.DoGSIFTFeatureComparator;
import org.openimaj.image.processing.face.keypoints.KEDetectedFace;
import org.openimaj.math.geometry.point.Point2d;
import org.openimaj.math.geometry.transforms.AffineTransformModel;
import org.openimaj.math.model.fit.RANSAC;

/**
 * Class which stores each character, their name, matching images, timestamps, detected face and performs various comparison operations.
 * 
 * @author Samantha Kanza
 * @version 1
 */

public class VideoCharacter implements Serializable{
	
	private int noMatches = 0;
	private int detectScore = 1;
	private KEDetectedFace face;
	private MBFImage faceFrame;
	private String name;
	private ArrayList<Long> timestamps;
	private ArrayList<KEDetectedFace> charFaces;
	private double actorScore = -1;
	private String actorName = "";
	private String fileString;
	private FileOutputStream fileOutputStream;
	private ArrayList<String> fileStrings = new ArrayList<String>();
	private int counter = 0;
	
	
	public VideoCharacter(KEDetectedFace face, MBFImage faceFrame, String name)
	{
		charFaces = new ArrayList<KEDetectedFace>();
		this.face = face;
		this.faceFrame = faceFrame;
		this.name = name;
		timestamps = new ArrayList<Long>();
		noMatches = 1;
		fileString = "VideoCharacterTempFile" + name;		
	}
	
	public void getSiftFeatureComparisons(DetectedFace query, DetectedFace target)
	{
		DoGSIFTFeature.Factory myFeatureFactory = new DoGSIFTFeature.Factory();
		
		DoGSIFTFeature featureQuery = myFeatureFactory.createFeature(query, true);
		DoGSIFTFeature featureTarget = myFeatureFactory.createFeature(target, false);
		DoGSIFTFeatureComparator myFC = new DoGSIFTFeatureComparator();
		myFC.compare(featureQuery, featureTarget);
	}
	
	public int getSIFTComparisonValues(MBFImage target, int faceDetects)
	{	
		DoGSIFTEngine engine = new DoGSIFTEngine();
		LocalFeatureList<org.openimaj.image.feature.local.keypoints.Keypoint> queryKeypoints = engine.findFeatures(faceFrame.flatten());
		LocalFeatureList<org.openimaj.image.feature.local.keypoints.Keypoint> targetKeypoints = engine.findFeatures(target.flatten());
		
		AffineTransformModel fittingModel = new AffineTransformModel(5);
		RANSAC<Point2d,Point2d> ransac = new RANSAC<Point2d,Point2d>(fittingModel, 1500, new RANSAC.PercentageInliersStoppingCondition(0.5), true);
		
		LocalFeatureMatcher<org.openimaj.image.feature.local.keypoints.Keypoint> matcher = new ConsistentLocalFeatureMatcher2d<org.openimaj.image.feature.local.keypoints.Keypoint>(new FastBasicKeypointMatcher<org.openimaj.image.feature.local.keypoints.Keypoint>(8), ransac);
		matcher.setModelFeatures(queryKeypoints);
		matcher.findMatches(targetKeypoints);
		
		return matcher.getMatches().size();
	}
	
	public double[] getPixelComparisonValues(MBFImage faceToMatch, int radius, int noPixels, int direction)
	{
		double[] compValues = new double[2];
		int ffX = faceFrame.getWidth()/2;
		int ffY = faceFrame.getHeight()/2;
		int cfX = faceToMatch.getWidth()/2;
		int cfY = faceToMatch.getHeight()/2;
		double xFFMean = 0;
		double yFFMean = 0;
		double xCFMean = 0;
		double yCFMean = 0;
		
		//1 = north, 2 = east, 3 = south, 4 = west, 5 = center
		if(direction < 5)
		{
			if(direction == 1)
			{
				ffY = ffY - radius;
			}
			else if(direction == 2)
			{
				ffX = ffX + radius;
			}
			else if(direction == 3)
			{
				ffY = ffY + radius;
			}
			else
			{
				ffX = ffX - radius;
			}
			
			xFFMean = (faceFrame.getPixel(ffX - 1, ffY)[0] + faceFrame.getPixel(ffX, ffY)[0] + faceFrame.getPixel(ffX + 1, ffY)[0])/noPixels;
			yFFMean = (faceFrame.getPixel(ffX - 1, ffY)[1] + faceFrame.getPixel(ffX, ffY)[1] + faceFrame.getPixel(ffX + 1, ffY)[1])/noPixels;
			
			xCFMean = (faceToMatch.getPixel(cfX - 1, cfY)[0] + faceToMatch.getPixel(cfX, cfY)[0] + faceToMatch.getPixel(cfX + 1, cfY)[0])/noPixels;
			yCFMean = (faceToMatch.getPixel(cfX - 1, cfY)[1] + faceToMatch.getPixel(cfX, cfY)[1] + faceToMatch.getPixel(cfX + 1, cfY)[1])/noPixels;
		}
		else
		{
			xFFMean = (faceFrame.getPixel(ffX, ffY)[0] + faceFrame.getPixel(ffX, ffY - 1)[0] + 
					faceFrame.getPixel(ffX + 1, ffY - 1)[0] + faceFrame.getPixel(ffX + 1, ffY)[0] + 
					faceFrame.getPixel(ffX + 1, ffY + 1)[0] + faceFrame.getPixel(ffX, ffY + 1)[0] + 
					faceFrame.getPixel(ffX - 1, ffY + 1)[0] + faceFrame.getPixel(ffX - 1, ffY)[0] + 
					faceFrame.getPixel(ffX - 1, ffY - 1)[0])/noPixels;                                                                                                                                                                                 
			                                                    
			yFFMean = (faceFrame.getPixel(ffX, ffY)[1] + faceFrame.getPixel(ffX, ffY - 1)[1] + 
					faceFrame.getPixel(ffX + 1, ffY - 1)[1] + faceFrame.getPixel(ffX + 1, ffY)[1] + 
					faceFrame.getPixel(ffX + 1, ffY + 1)[1] + faceFrame.getPixel(ffX, ffY + 1)[1] + 
					faceFrame.getPixel(ffX - 1, ffY + 1)[1] + faceFrame.getPixel(ffX - 1, ffY)[1] + 
					faceFrame.getPixel(ffX - 1, ffY - 1)[1])/noPixels;      

			xCFMean = (faceToMatch.getPixel(cfX, cfY)[0] + faceToMatch.getPixel(cfX, cfY - 1)[0] + 
					faceToMatch.getPixel(cfX + 1, cfY - 1)[0] + faceToMatch.getPixel(cfX + 1, cfY)[0] + 
					faceToMatch.getPixel(cfX + 1, cfY + 1)[0] + faceToMatch.getPixel(cfX, cfY + 1)[0] + 
					faceToMatch.getPixel(cfX - 1, cfY + 1)[0] + faceToMatch.getPixel(cfX - 1, cfY)[0] + 
					faceToMatch.getPixel(cfX - 1, cfY - 1)[0])/noPixels;  
			

			yCFMean = (faceToMatch.getPixel(cfX, cfY)[1] + faceToMatch.getPixel(cfX, cfY - 1)[1] + 
					faceToMatch.getPixel(cfX + 1, cfY - 1)[1] + faceToMatch.getPixel(cfX + 1, cfY)[1] + 
					faceToMatch.getPixel(cfX + 1, cfY + 1)[1] + faceToMatch.getPixel(cfX, cfY + 1)[1] + 
					faceToMatch.getPixel(cfX - 1, cfY + 1)[1] + faceToMatch.getPixel(cfX - 1, cfY)[1] + 
					faceToMatch.getPixel(cfX - 1, cfY - 1)[1])/noPixels;  
		}
		
		compValues[0] = Math.abs(xFFMean - xCFMean); 
		compValues[1] = Math.abs(yFFMean - yCFMean);
		
		return compValues;
	}
	
	public MBFImage getCharImage()
	{
		return faceFrame;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String newName)
	{
		name = newName;
	}
	
	public KEDetectedFace getFace()
	{
		return face;
	}
	
	public ArrayList<KEDetectedFace> getCharFaces()
	{
		return charFaces;
	}
	
	public void addCharFace(KEDetectedFace newFace)
	{
		charFaces.add(newFace);
	}
	
	public void addTimestamp(Long timestamp)
	{
		timestamps.add(timestamp);
	}
	
	public ArrayList<Long> getTimestamps()
	{
		return timestamps;
	}
	
	public int getMatches()
	{
		return noMatches;
	}
	
	public void addMatch()
	{
		noMatches++;
	}
	
	public void addCharImage(BufferedImage image)
	{
		try 
		{
			fileOutputStream = new FileOutputStream(fileString + "img" + counter);
			fileStrings.add(fileString + "img" + counter);
			ObjectOutputStream objOutStream = new ObjectOutputStream(fileOutputStream);
			ImageIO.write(image, "jpg", ImageIO.createImageOutputStream(objOutStream));
			
			noMatches++;
			counter++;
		} 
		catch (FileNotFoundException e) {e.printStackTrace();} 
		catch (IOException e) {e.printStackTrace();} 
	     
	} 

	public ArrayList<BufferedImage> getCharImages()
	{
		ArrayList<BufferedImage> characters = new ArrayList<BufferedImage>();
		
		Object image = null;
		for(String fileString : fileStrings)
		{
			FileInputStream fileInputStream;
			ObjectInputStream objectInputStream;
			try {
				fileInputStream = new FileInputStream(fileString);
				objectInputStream = new ObjectInputStream(fileInputStream);
				image = ImageIO.read(ImageIO.createImageInputStream(objectInputStream));
				if(image != null)
				{
					characters.add((BufferedImage) image);
				}
			} 
			catch (FileNotFoundException e) {e.printStackTrace();} 
			catch (IOException e) {e.printStackTrace();}
		}

		System.out.println("size = " + characters.size());
        return characters;
	}
	
	public double getActorScore()
	{
		return actorScore;
	}
	
	public void setActorScore(double newScore)
	{
		actorScore = newScore;
	}
	
	public String getActorName()
	{
		return actorName;
	}
	
	public void setActorName(String newName)
	{
		actorName = newName;
	}
}
