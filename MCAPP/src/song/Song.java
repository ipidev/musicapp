package song;

import score.*;

//Class dedicated to the creation and general storage of a 'song'
public class Song 
{
	//Member Variables
	String _name;
	String _description;
	
	//Temp Score;
	Score tScore;
	
	//Constructor;
	public Song ()
	{
		_name = "New Song";
		_description = "Default";
		tScore = new Score();
	}
	
	public Song (String name, String description)
	{
		_name = name;
		_description = description;
		tScore = new Score();
	}
}
