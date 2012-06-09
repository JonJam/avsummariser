package uk.ecs.gdp.avsummariser.model.subtitles;

/**
 * Class to contain name scores used in obtaining main names from parsed subtitles.
 * 
 * @author Jonathan Harrison
 * @version 1
 */
public class NameScore {
	
	private String name;
	private int count;
	
	public NameScore(String name, int count){
		this.name = name;
		this.count = count;
	}
	
	/**
	 * Get name value
	 * 
	 * @return String (Value of name)
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Get count value.
	 * 
	 * @return int (Value of count)
	 */
	public int getCount(){
		return count;
	}
}