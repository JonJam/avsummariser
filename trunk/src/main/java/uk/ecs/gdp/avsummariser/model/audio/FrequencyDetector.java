package uk.ecs.gdp.avsummariser.model.audio;

import java.io.File;
import java.util.ArrayList;
import org.openimaj.audio.FourierTransform;
import org.openimaj.audio.SampleChunk;
import org.openimaj.video.xuggle.XuggleAudio;

import uk.ecs.gdp.avsummariser.model.VideoFile;
import uk.ecs.gdp.avsummariser.model.section.FrequencySection;

/**
 * Class to calculate frequency of audio samples in the audio stream of a file.
 * 
 * This class contains commented out methods which are part of attempt to automatically detect music segments in audio. Due to 
 * project time, this is incomplete.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class FrequencyDetector extends AudioDetector{

	/**
	 * Method to calculate create Frequency Objects for all audio samples (Containing frequency and intensity)
	 * 
	 * @param video (VideoFile object)
	 */
	public static void findAllFrequenciesInVideo(VideoFile video){
		ArrayList<FrequencySection> allFrequencies = calculateFrequenciesInVideo(video);
		video.setAllFrequencies(allFrequencies);
	}
	
	/**
	 * Method to create FrequencyObjects for audio samples.
	 * 
	 * @param video(VideoFile video)
	 * @return ArrayList<FrequencySection>
	 */
	private static ArrayList<FrequencySection> calculateFrequenciesInVideo(VideoFile video){
		ArrayList<FrequencySection> frequencies = new ArrayList<FrequencySection>();
		
		XuggleAudio xa = new XuggleAudio(new File(video.getAbsolutePath()));
			
		FrequencySection section = new FrequencySection(video);
		SampleChunk chunk = xa.nextSampleChunk();
		
		//While loop to work out create FrequencySection objects for the video.
		while(true){
			try{
				long timeCode = chunk.getStartTimecode().getTimecodeInMilliseconds();
				section.setStartTime(timeCode);
				
				FourierTransform fftp = new FourierTransform();
				fftp.process(chunk);
				
				float[] f = fftp.getLastFFT();
				ArrayList<FrequencyObject> sampleFrequencies = new ArrayList<FrequencyObject>();
								
				for(int i = 0; i < f.length / 4; i++){
					float re = f[i * 2];
					float im = f[i * 2 + 1];
					
					float freq = (float) Math.log(Math.sqrt(re * re + im * im) + 1) / 5f;
					float intensity = (float) Math.log(Math.sqrt(re * re + im * im) + 1) / 6f;
					
					if(intensity > 1 ){
						intensity = 1;
					}
					
					sampleFrequencies.add(new FrequencyObject(freq,intensity));
				}
				
				section.setFrequencyObjects(sampleFrequencies);
				
				chunk = xa.nextSampleChunk();
				
				if(chunk == null){
					//Next sample chunk is null so break
					section.setEndTime(timeCode + 1000);	
					frequencies.add(section);
					break;
				}
				
				section.setEndTime(chunk.getStartTimecode().getTimecodeInMilliseconds());
				frequencies.add(section);
				section = new FrequencySection(video);
			
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		
		return frequencies;
	}
	
	/**
	 * NOT WORKING - PART OF ATTEMPT TO DETECT POSSIBLE MUSIC SEGEMENTS IN VIDEO
	 * 
	 * Method to work out which audio section are likely to be Music.
	 * 
	 * @param video (VideoFile object)
	 */
	/*public static void findMusicSectionsInVideo(VideoFile video){
		//ArrayList<FrequencySection> allFrequencies = calculateFrequenciesInVideo(video);
		//ArrayList<FrequencySection> intenseSections = getSectionsWithIntenseSamplesOnly(allFrequencies);
		//ArrayList<FrequencySection> possibleMusicSections = findPossibleMusicSectionsUsingFrequencies(intenseSections);
		
		//Or 
		
		detectBeats(video);
		
	}*/
	
	/**
	 * NOT WORKING - PART OF ATTEMPT TO DETECT POSSIBLE MUSIC SEGEMENTS IN VIDEO
	 * 
	 * Attempt at doing beat detection using OpenIMAJ BeatDetection class
	 * 
	 * @param video (VideoFile object)
	 */
	/*private static void detectBeats(VideoFile video){
		XuggleAudio xa = new XuggleAudio(new File(video.getAbsolutePath()));
		
		FrequencySection section = new FrequencySection(video);
		SampleChunk chunk = xa.nextSampleChunk();
		
		while(true){
			try{
				BeatDetector bD = new BeatDetector(xa.getFormat());
				
				long timeCode = chunk.getStartTimecode().getTimecodeInMilliseconds();
				section.setStartTime(timeCode);
				
				bD.process(chunk);
						
				chunk = xa.nextSampleChunk();
				
				if(chunk == null){
					//Next sample chunk is null so break
					section.setEndTime(timeCode + 1000);
					break;
				}
				
				section.setEndTime(chunk.getStartTimecode().getTimecodeInMilliseconds());
				
				System.out.println("s: " + section.getStartTime() + " e: " + section.getEndTime() + " b: " + bD.beatDetected());
				
				section = new FrequencySection(video);
			} catch (Exception e){
				e.printStackTrace();
			}
		}
	}*/

	/**
	 * NOT WORKING - PART OF ATTEMPT TO DETECT POSSIBLE MUSIC SEGEMENTS IN VIDEO
	 * 
	 * Attempt at doing beat detection using calculations from http://www.flipcode.com/misc/BeatDetectionAlgorithms.pdf before OpenIMAJ added BeatDetection
	 * 
	 * This method requires changes to FrequencySection object to store an ArrayList of float values
	 * 
	 * @param video (VideoFile object)
	 */
	/*private static final int FFT_DIVIDE_BY = 4;
	private static final int C = 250;
	private static void detectBeats(VideoFile video){
		ArrayList<FrequencySection> frequencies = new ArrayList<FrequencySection>();
		FrequencySection section = new FrequencySection(video);
		
		XuggleAudio xa = new XuggleAudio(new File(video.getAbsolutePath()));
		SampleChunk chunk = xa.nextSampleChunk();
		
		int sampleLength = 0;
		int numberOfSubbands = 0;
		long numOfSamplesPerSecond = 0;
		
		//While loop to work out effective sound pressure for samples
		while(true){
			try{
				long timeCode = chunk.getStartTimecode().getTimecodeInMilliseconds();
				section.setStartTime(timeCode);
				
				FourierTransform fftp = new FourierTransform();
				fftp.process(chunk);
				
				//New Buffer to store frequency amplitudes will be of length f.length / FFT_DIVIDE_BY
				ArrayList<Float> fftValues = new ArrayList<Float>(); 
				
				//Complex numbers from FFT for samples
				float[] f = fftp.getLastFFT();
				
				if(sampleLength == 0){
					//Set sample length
					sampleLength = f.length / FFT_DIVIDE_BY;
					numberOfSubbands = (int) Math.sqrt(sampleLength);
				}
				
				for(int i = 0; i < sampleLength; i++){
					
					//Get module of value and square TODO MAY NEED TO CHANGE TO (float) Math.log(Math.sqrt(re * re + im * im) + 1) / 5f;
					float toAdd = Math.abs(f[i]) * Math.abs(f[i]);
					//float re = f[i * 2];
					//float im = f[i * 2 + 1];
					
					//float toAdd = (float) Math.log(Math.sqrt(re * re + im * im) + 1) / 5f;
					
					//Add to new buffer.
					fftValues.add(toAdd);
				}
				
				section.setFFTValues(fftValues);
				
				chunk = xa.nextSampleChunk();
				
				if(chunk == null){
					//Next sample chunk is null so break
					section.setEndTime(timeCode + 1000);	
					frequencies.add(section);
					break;
				}
				
				section.setEndTime(chunk.getStartTimecode().getTimecodeInMilliseconds());
				
				if(numOfSamplesPerSecond == 0){
					long difference = section.getEndTime() - section.getStartTime();
					numOfSamplesPerSecond = (int) ((sampleLength / difference) * 1000);
				}
				
				frequencies.add(section);
				section = new FrequencySection(video);
			
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		
		ArrayList<LinkedList<Float>> subbandHistory = new ArrayList<LinkedList<Float>>();
		
		for(int i = 0; i < numberOfSubbands; i++){
			subbandHistory.add(new LinkedList<Float>());
		}
		
		//For each sampleLength samples
		for(FrequencySection  fS: frequencies){
			
			//This stores energy of each subband. 
			ArrayList<Float> subBandEnergies = new ArrayList<Float>();
			
			ArrayList<Float> freqAmps = fS.getFFTValues();
			float total = 0;
			int count = 0;
			
			//Calculate total frequency for each subband
			for(int i = 0; i < freqAmps.size(); i++){
				//Sum frequency amplitudes from i * Math.sqrt(sampleLength) to  (i + 1) * Math.sqrt(sampleLength) and add to subBandEnergies ArrayList
				total += freqAmps.get(i);
				count++;
				
				if(count == numberOfSubbands){
					//Reached end of subband so add total
					
					float subBandEnergy = (float) ((Math.sqrt(sampleLength) / sampleLength) * total); 
					subBandEnergies.add(subBandEnergy);
					
					total = 0;
					count = 0;
				} else if(i == freqAmps.size() - 1){
					//Last sum so needs to be added before exits loop
					
					float subBandEnergy = (float) ((Math.sqrt(sampleLength) / sampleLength) * total); 
					subBandEnergies.add(subBandEnergy);
				} 
			}	
		
			//Updating history list with new frequencies for each subband
			for(int i = 0; i < subBandEnergies.size(); i++){
				
				LinkedList<Float> iSubbandHistory = subbandHistory.get(i);
				
				if(iSubbandHistory.size() >= ((int)(numOfSamplesPerSecond / sampleLength))){
					//If history list has length equal or greater than numOfSamplesPerSecond / sampleLength, remove last one.
					iSubbandHistory.removeLast();
				}
				
				//Append new energy value to history
				iSubbandHistory.addFirst(subBandEnergies.get(i));
				
				//Set it in ArrayList
				subbandHistory.set(i, iSubbandHistory);
			}
			
						
			//boolean int to determine if all subbands meet condition to determine beat
			int containsBeat = 0;
			
			
			//Loop to check whether a beat
			for(int i = 0; i < subbandHistory.size(); i++){
				
				LinkedList<Float> iSubbandHistory = subbandHistory.get(i);
				float historyTotal = 0;
				//Work out average energy of subband
				for(int j = 0; j < iSubbandHistory.size(); j++){
					historyTotal += iSubbandHistory.get(j);
				}
				
				float eI = (1 / ((int)(numOfSamplesPerSecond / sampleLength))) * historyTotal;
				
				if(iSubbandHistory.get(0) > (C * eI)){
					//True so add one (i.e. true)
					containsBeat++;
				} else{
					//Not true so minus one.
					containsBeat--;
				}
			}
			
			if(containsBeat == subbandHistory.size()){
				//Means all met condition and therefore a beat.
				System.out.println("BEAT: s: " + fS.getStartTime() + " e: " + fS.getEndTime());
			} else{
				System.out.println("NOT BEAT: s: " + fS.getStartTime() + " e: " + fS.getEndTime());
			}
		}
	}*/
	

	/**
	 * NOT WORKING - PART OF ATTEMPT TO DETECT POSSIBLE MUSIC SEGEMENTS IN VIDEO
	 * 
	 * Method to get an ArrayList of Intense Sections.
	 * 
	 * @param video (VideoFile object)
	 */
	/*private static final float INTENSITY_THRESHOLD = (float) 0.005;
	private static final int INTENSITY_NUM = 300;
	private static ArrayList<FrequencySection> getSectionsWithIntenseSamplesOnly(ArrayList<FrequencySection> allFrequencies){
		ArrayList<FrequencySection> sectionsWithIntenseSamplesOnly = new ArrayList<FrequencySection>();
		
		for(FrequencySection fs : allFrequencies){
			
			int numOfIntenseSamples = 0;
			ArrayList<FrequencyObject> freqObjects = new ArrayList<FrequencyObject>();
				
			for(FrequencyObject fO : fs.getFrequencyObjects()){
				float intensity = fO.getIntensity();
				
				if(intensity >= INTENSITY_THRESHOLD){
					freqObjects.add(fO);
					numOfIntenseSamples++;
				}
			}
			
			if(numOfIntenseSamples >= INTENSITY_NUM){
				fs.setFrequencyObjects(freqObjects);
				sectionsWithIntenseSamplesOnly.add(fs);
			}
			
		}
		
		return sectionsWithIntenseSamplesOnly;
	}*/
	
	/**
	 * NOT WORKING - PART OF ATTEMPT TO DETECT POSSIBLE MUSIC SEGEMENTS IN VIDEO
	 * 
	 * Method to find possible music sections in video using frequency patterns like in Spectra graph i.e. drawing lines between frequencies.
	 * 
	 * @param sectionsWithIntenseSamplesOnly (ArrayList of FrequencySection objects produced from getSectionsWithIntenseSamplesOnly)
	 * 
	 */
	/*private final static long MERGE_DIFFERENCE = 1000;
	private static final float FREQ_LINE_ALLOWANCE = (float) 0.0001;
	private static final int NUM_OF_FREQ_LINES = 10;
	private static ArrayList<FrequencySection> findPossibleMusicSectionsUsingFrequencies(ArrayList<FrequencySection> sectionsWithIntenseSamplesOnly){
		
		ArrayList<FrequencySection> likelyMusicSections = new ArrayList<FrequencySection>();
		FrequencySection current = null;
		boolean adding = false;
		
		for(FrequencySection fs : sectionsWithIntenseSamplesOnly){			
			if(current == null){
				//First entry into loop
				current = fs;
			} else{
				
				
				if(current.getEndTime() + MERGE_DIFFERENCE >= fs.getStartTime()){
					//Should be checked for lines of frequency as could be merged
					
					int numOfLines = 0;
					
					for(FrequencyObject fO : fs.getFrequencyObjects()){
						float frequency = fO.getFrequency();
						
						for(FrequencyObject cFO: current.getFrequencyObjects()){
							float cFrequency = cFO.getFrequency();
								
							//Check whether straight line between frequency points in Spectra.
							float absGradient = Math.abs(frequency - cFrequency);
														
							//if(absGradient <= FREQ_LINE_ALLOWANCE){
							if(absGradient <= FREQ_LINE_ALLOWANCE && (Math.abs(fO.getIntensity() - cFO.getIntensity()) <= 0.00001)){
								numOfLines++;
							}
						}
					}
						
					if(numOfLines >= NUM_OF_FREQ_LINES && adding == false){
						//Above threshold and not adding so set adding to true and merge current and fs sections.
						
						adding = true;
						current.setEndTime(fs.getEndTime());
					} else if(numOfLines >= NUM_OF_FREQ_LINES && adding == true){
						//Above threshold and adding so merge current and fs sections.
						
						current.setEndTime(fs.getEndTime());
					} else if(numOfLines < NUM_OF_FREQ_LINES && adding == true){
						//Below threshold and was adding so add current to ArrayList, set adding to false and set current to fs.
					
						likelyMusicSections.add(current);
						adding = false;
						current = fs;
					} else if(numOfLines < NUM_OF_FREQ_LINES && adding == false){
						//Below threshold and wasn't adding so set current to fs.
						
						current = fs;
					}
					
				} else if(current.getEndTime() + MERGE_DIFFERENCE < fs.getStartTime() && adding == false){
					//Sections too far apart but wasn't adding so just set current.
					
					current = fs;
				} else if(current.getEndTime() + MERGE_DIFFERENCE < fs.getStartTime() && adding == true){
					//Sections too far apart but was adding so add previous current to ArrayList and set current.
					
					likelyMusicSections.add(current);
					adding = false;
					current = fs;
					
				}
			}
		
			if(sectionsWithIntenseSamplesOnly.indexOf(fs) == sectionsWithIntenseSamplesOnly.size() - 1 && adding == true){
				likelyMusicSections.add(current);
			}
		}
		
		return likelyMusicSections;
	}*/
}


