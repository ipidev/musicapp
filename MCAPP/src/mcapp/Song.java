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
<<<<<<< HEAD

	/**
	 * Initialises default song setup;
	 */
	public Song()
=======
	
	/**
	 * Intialises default song setup;
	 */
	public Song ()
>>>>>>> 6ec92a782d209bd0e17cddcff45cc6d7f15581f1
	{
		_name = "New Song";
		_description = "Default";
		tScore = new Score();
	}
<<<<<<< HEAD

	/**
	 * Initialises saved song through creation or import.
	 * @param name User-defined name of instance
	 * @param description User-define description of song.
	 */
	public Song(String name, String description)
=======
	
	/**
	 * Intialises saved song through creation or import.
	 * @param name User-defined name of instance
	 * @param description User-define description of song.
	 */
	public Song (String name, String description)
>>>>>>> 6ec92a782d209bd0e17cddcff45cc6d7f15581f1
	{
		_name = name;
		_description = description;
		tScore = new Score();
	}
	
	/**
	 * Accessor for a score.
	 * @param index Score index number.
	 * @return The Score object.
	 */
	public Score getScore(int index)
	{
		return tScore;
	}
}
