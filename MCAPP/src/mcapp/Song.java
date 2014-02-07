package mcapp;

/**
 * Class dedicated to the creation and general storage of a 'Song'
 * Contains a temporary Score object to use whilst loading and saving.
 * 
 * @author Josh
 *
 */
public class Song 
{
	//Member Variables
	String _name;
	String _description;
	
	//Temp Score;
	Score tScore;
	
	/**
	 * Intialises default song setup;
	 */
	public Song ()
	{
		_name = "New Song";
		_description = "Default";
		tScore = new Score();
	}
	
	/**
	 * Intialises saved song through creation or import.
	 * @param name User-defined name of instance
	 * @param description User-define description of song.
	 */
	public Song (String name, String description)
	{
		_name = name;
		_description = description;
		tScore = new Score();
	}
}
