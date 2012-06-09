package uk.ecs.gdp.avsummariser.model.audio;

/**
 * FrequencyObject is a class that represents a detected frequency in a SampleChunk. It contains the frequency and intensity float values from FFT. 
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class FrequencyObject {
	
	//Value of Frequency from FFT.
	private float frequency;
	//Intensity of frequency from FFT.
	private float intensity;
	
	public FrequencyObject(float frequency, float intensity){
		this.frequency = frequency;
		this.intensity = intensity;
	}
	
	/**
	 * Method to get value of frequency
	 * 
	 * @return frequency
	 */
	public float getFrequency(){
		return frequency;
	}
	
	/**
	 * Method to get value of intensity
	 * 
	 * @return frequency
	 */
	public float getIntensity(){
		return intensity;
	}
	
	/**
	 * Method to set value of frequency
	 * 
	 * @param frequency (float)
	 */
	public void setFrequency(float frequency){
		this.frequency = frequency;
	}
	
	/**
	 * Method to set value of intensity
	 * 
	 * @param intensity (float)
	 */
	public void setIntensity(float intensity){
		this.intensity = intensity;
	}
}
