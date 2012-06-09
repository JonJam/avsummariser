package uk.ecs.gdp.avsummariser.model.facialdetection;

import org.openimaj.image.MBFImage;
import org.openimaj.image.processing.face.alignment.AffineAligner;
import org.openimaj.image.processing.face.alignment.FaceAligner;
import org.openimaj.image.processing.face.alignment.MeshWarpAligner;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.feature.DoGSIFTFeature;
import org.openimaj.image.processing.face.feature.LocalLBPHistogram;
import org.openimaj.image.processing.face.feature.comparison.DoGSIFTFeatureComparator;
import org.openimaj.image.processing.face.feature.comparison.FaceFVComparator;
import org.openimaj.image.processing.face.keypoints.KEDetectedFace;

public class CharacterExtra {

	// Extra pixel comparison method that was tried but not used in the final implementation
	// test for totalComp values being < 0.2 except for mcv which is < 0.3 
	public double[] getMatchByPixelComparison(VideoCharacter vidChar, MBFImage myCharImage)
	{
		int height  = myCharImage.getHeight()/2;
		int width = myCharImage.getWidth()/2;
		double[] mcv = vidChar.getPixelComparisonValues(myCharImage, 1, 9, 5);
		double[] tcv = vidChar.getPixelComparisonValues(myCharImage, 5, height-3, 1);
		double[] bcv = vidChar.getPixelComparisonValues(myCharImage, 5, height-3, 3);
		double[] rcv = vidChar.getPixelComparisonValues(myCharImage, 5, width-3, 2);
		double[] lcv = vidChar.getPixelComparisonValues(myCharImage, 5, width-3, 4);
		
		double[] totalComp = new double[10];
		totalComp[0] = mcv[0];
		totalComp[1] = mcv[1];
		totalComp[2] = tcv[0];
		totalComp[3] = tcv[1];
		totalComp[4] = bcv[0];
		totalComp[5] = bcv[1];
		totalComp[6] = rcv[0];
		totalComp[7] = rcv[1];
		totalComp[8] = lcv[0];
		totalComp[9] = lcv[1];
		
		return totalComp;
	}

	//Extra LBPHistogram comparison method that was tried but not used in the final implementation
	//Test for meshVal between 10.2 and 10.8 to be a face and then test within the range for matches
	public boolean getMatchByLBPHistogram(VideoCharacter vidChar, KEDetectedFace face)
	{
		boolean isMatch = false;
		FaceAligner<KEDetectedFace> myFaceAligner = new MeshWarpAligner();
		LocalLBPHistogram.Factory<KEDetectedFace> myFactoryFace = new LocalLBPHistogram.Factory<KEDetectedFace>(myFaceAligner, 25, 25, 8, 1);
		LocalLBPHistogram myFaceFeature = myFactoryFace.createFeature(face, false);
		LocalLBPHistogram newFaceFeature = myFactoryFace.createFeature(vidChar.getFace(), true);		
		FaceFVComparator<LocalLBPHistogram> faceComparator = new FaceFVComparator<LocalLBPHistogram>(); 
		double meshVal = faceComparator.compare(newFaceFeature, myFaceFeature);
		
		double meshDiff = Math.abs(meshVal - 10.5);
		if(meshVal > 10.2 && meshVal < 10.8 && meshDiff < 0.1)
		{
			isMatch = true;
		}
		return isMatch;
	}
	
	public double[] getComparisonValues(KEDetectedFace faceToMatch)
	{
		double[] compValues = new double[2];
		

		
		return compValues;
	}
	
	public void getSiftFeatureComparisons(DetectedFace query, DetectedFace target)
	{
		DoGSIFTFeature.Factory myFeatureFactory = new DoGSIFTFeature.Factory();
		
		DoGSIFTFeature featureQuery = myFeatureFactory.createFeature(query, true);
		DoGSIFTFeature featureTarget = myFeatureFactory.createFeature(target, false);
		DoGSIFTFeatureComparator myFC = new DoGSIFTFeatureComparator();
		myFC.compare(featureQuery, featureTarget);
	}
	
}
