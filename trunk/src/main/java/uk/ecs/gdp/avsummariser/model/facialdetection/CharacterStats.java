package uk.ecs.gdp.avsummariser.model.facialdetection;

import java.util.ArrayList;
import java.util.Arrays;

import org.openimaj.image.MBFImage;
import org.openimaj.image.processing.face.alignment.AffineAligner;
import org.openimaj.image.processing.face.alignment.FaceAligner;
import org.openimaj.image.processing.face.alignment.MeshWarpAligner;
import org.openimaj.image.processing.face.feature.LocalLBPHistogram;
import org.openimaj.image.processing.face.feature.comparison.FaceFVComparator;
import org.openimaj.image.processing.face.keypoints.KEDetectedFace;

public class CharacterStats {


	ArrayList<Integer> amyMatches = new ArrayList<Integer>(Arrays.asList(1,2,3,4,5,6,8,20,22,23,24,25,27,28,30,32,34,35,36,45,46,47,48,49,50,52,53,54,56,57,58,65,66,68,69,70,71,76,78,80,82,84,87,92,94,96,99,101,103));
	ArrayList<Integer> roryMatches  = new ArrayList<Integer>(Arrays.asList(60,61,62,63,105,106,108));
	ArrayList<Integer> riverMatches  = new ArrayList<Integer>(Arrays.asList(122,123,124,125,126,127,128,129,130,131,132,135,136,137,138,139,141,143,144,146,147,151,153,157,159,162,163,172,173));
	ArrayList<Integer> notFaces  = new ArrayList<Integer>(Arrays.asList(7,21,26,31,33,37,43,44,55,59,67,72,73,74,75,77,79,81,83,85,86,88,89,90,91,93,95,97,99,100,102,104,107,109,110,111,113,115,116,118,119,120,133,134,140,142,145,160,165,166,167,168,169,170,171,175,180,191,192,193));
	
	private double matchMeshMin = 100;
	private double matchMeshMax = 0;
	private double matchAffineMin = 100;
	private double matchAffineMax = 0;
	private double matchTotalMin = 100;
	private double matchTotalMax = 0;
	private double nonFaceMeshMin = 100;
	private double nonFaceMeshMax = 0;
	private double nonFaceAffineMin = 100;
	private double nonFaceAffineMax = 0;
	private double nonFaceTotalMin = 100;
	private double nonFaceTotalMax = 0;
	double xCompareMin = 100;
	double xCompareMax = 0;
	double yCompareMin = 100;
	double yCompareMax = 0;
	
	double xNonCompareMin = 100;
	double xNonCompareMax = 0;
	double yNonCompareMin = 100;
	double yNonCompareMax = 0;
	
	public void getPixelStats(MBFImage face, MBFImage faceToMatch, int iName, int faceNo)
	{
		
		double[] compValues = new double[2];
		int noPixels = 9;
		
		int ffX = face.getWidth()/2;
		int ffY = face.getHeight()/2;
		int cfX = faceToMatch.getWidth()/2;
		int cfY = faceToMatch.getHeight()/2;
		double xFFMean;
		double yFFMean;
		double xCFMean;
		double yCFMean;
		double xDiff;
		double yDiff;
		
		
		xFFMean = (face.getPixel(ffX, ffY)[0] + face.getPixel(ffX, ffY - 1)[0] + 
				face.getPixel(ffX + 1, ffY - 1)[0] + face.getPixel(ffX + 1, ffY)[0] + 
				face.getPixel(ffX + 1, ffY + 1)[0] + face.getPixel(ffX, ffY + 1)[0] + 
				face.getPixel(ffX - 1, ffY + 1)[0] + face.getPixel(ffX - 1, ffY)[0] + 
				face.getPixel(ffX - 1, ffY - 1)[0])/noPixels;                                                                                                                                                                                 
		                                                    
		yFFMean = (face.getPixel(ffX, ffY)[1] + face.getPixel(ffX, ffY - 1)[1] + 
				face.getPixel(ffX + 1, ffY - 1)[1] + face.getPixel(ffX + 1, ffY)[1] + 
				face.getPixel(ffX + 1, ffY + 1)[1] + face.getPixel(ffX, ffY + 1)[1] + 
				face.getPixel(ffX - 1, ffY + 1)[1] + face.getPixel(ffX - 1, ffY)[1] + 
				face.getPixel(ffX - 1, ffY - 1)[1])/noPixels;      

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
		
		xDiff = Math.abs(xFFMean - xCFMean);
		yDiff = Math.abs(yFFMean - yCFMean);
		
		if((amyMatches.contains(iName) && amyMatches.contains(faceNo)) || (roryMatches.contains(iName) && roryMatches.contains(faceNo)) || (riverMatches.contains(iName) && riverMatches.contains(faceNo)))
		{
			if(xDiff <  xCompareMin){xCompareMin = xDiff;}
			if(xDiff >  xCompareMax){xCompareMax = xDiff;}
			if(yDiff <  yCompareMin){yCompareMin = yDiff;}
			if(yDiff >  yCompareMax){yCompareMax = yDiff;}
		}
		else
		{
			if(xDiff <  xNonCompareMin){xNonCompareMin = xDiff;}
			if(xDiff >  xNonCompareMax){xNonCompareMax = xDiff;}
			if(yDiff <  yNonCompareMin){yNonCompareMin = yDiff;}
			if(yDiff >  yNonCompareMax){yNonCompareMax = yDiff;}
		}
	}
	
	public void getMatchStats(KEDetectedFace face, KEDetectedFace faceToMatch, int iName, int faceNo)
	{
		FaceAligner<KEDetectedFace> myFaceAligner1 = new MeshWarpAligner();
		FaceAligner<KEDetectedFace> myFaceAligner2 = new AffineAligner();

		boolean match = false;
		double meshCompare;
		double affineCompare;
		double totalCompare;
		
		LocalLBPHistogram.Factory<KEDetectedFace> myFactoryFace1 = new LocalLBPHistogram.Factory<KEDetectedFace>(myFaceAligner1, 25, 25, 8, 1);
		LocalLBPHistogram.Factory<KEDetectedFace> myFactoryFace2 = new LocalLBPHistogram.Factory<KEDetectedFace>(myFaceAligner2, 25, 25, 8, 1);
		
		LocalLBPHistogram myFaceFeature1 = myFactoryFace1.createFeature(face, false);
		LocalLBPHistogram newFaceFeature1 = myFactoryFace1.createFeature(faceToMatch, true);
		LocalLBPHistogram myFaceFeature2 = myFactoryFace2.createFeature(face, false);
		LocalLBPHistogram newFaceFeature2 = myFactoryFace2.createFeature(faceToMatch, true);
		FaceFVComparator<LocalLBPHistogram> faceComparator = new FaceFVComparator<LocalLBPHistogram>();
		
		meshCompare = faceComparator.compare(newFaceFeature1, myFaceFeature1);
		affineCompare = faceComparator.compare(newFaceFeature2, myFaceFeature2);
		totalCompare = meshCompare + affineCompare;
		
		if((amyMatches.contains(iName) && amyMatches.contains(faceNo)) || (roryMatches.contains(iName) && roryMatches.contains(faceNo)) || (riverMatches.contains(iName) && riverMatches.contains(faceNo)))
		{
			if(meshCompare < matchMeshMin){matchMeshMin = meshCompare;}
			if(meshCompare > matchMeshMax){matchMeshMax = meshCompare;}
			if(affineCompare < matchAffineMin){matchAffineMin = affineCompare;}
			if(affineCompare > matchAffineMax){matchAffineMax = affineCompare;}
			if(totalCompare < matchTotalMin){matchTotalMin = totalCompare;}
			if(totalCompare > matchTotalMax){matchTotalMax = totalCompare;}
		}
		else  //if(notFaces.contains(iName) || notFaces.contains(faceNo))
		{
			if(meshCompare < nonFaceMeshMin){nonFaceMeshMin = meshCompare;}
			if(meshCompare > nonFaceMeshMax){nonFaceMeshMax = meshCompare;}
			if(affineCompare < nonFaceAffineMin){nonFaceAffineMin = affineCompare;}
			if(affineCompare > nonFaceAffineMax){nonFaceAffineMax = affineCompare;}
			if(totalCompare < nonFaceTotalMin){nonFaceTotalMin = totalCompare;}
			if(totalCompare > nonFaceTotalMax){nonFaceTotalMax = totalCompare;}
		}
	}

}
