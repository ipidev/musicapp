package mcapp;

/**
 * Class is responsible for the creation and storing of the musical layout.
 * 
 * @author Josh
 *
 */
public class Score 
{
	Beat[] _score;
	Beat _beat;
	
	/**
	 * Default Constructor
	 */
	public Score ()
	{
		_beat = new Beat();
		SetScore();
	}
	
	/**
	 * Constructor for Loaded File
	 * @param score Score to be Loaded. 
	 * */
	public Score (Beat[] score)
	{
		_beat = new Beat();
		SetScore(score);
	}
	
	/**
	 * Set for Score instance. New Score
	 */
	public void SetScore()
	{
		_score = new Beat[12];
	}
	
	/**
	 * Sets Score instance. Used while Loading.
	 * @param score Score instance to be loaded.
	 */
	public void SetScore(Beat[] score)
	{
		_score = score;
	}
	
	/**
	 * Returns Score instance
	 * @return Current Score instance
	 */
	public Beat[] GetScore()
	{
		return _score;
	}
	
	/**
	 * Adds Beat instance to Score. Adds Sample to Beat object.
	 * @param x Position in Score Array
	 * @param sampleID ID of Sample used
	 * @param instrumentID Instrument ID of Sample
	 * @param pitch Attribute of Sample
	 */
	public void AddToScore(int x, int sampleID, int instrumentID, int pitch)
	{
		//Checks if position in Score is empty
		if(_score[x] == null)
		{
			_beat = new Beat();
			_score[x]= _beat;
		}
		
		//Loads stored Beat
		_score[x] = _beat;
		
		if(_beat.GetSize() < 3)
		{
			_beat.AddSample(sampleID, instrumentID, pitch);
			_score[x] = _beat;
		}
		else
		{
			//Error. Beat is full at location.
		}
	}
	
	/**
	 * Removes a Sample or Beat from the Score array.
	 * @param x Position in Score Array
	 * @param y Position in Sample List
	 * @param deleteAll Check if Beat or Sample is to be deleted
	 */
	public void RemoveFromScore(int x, int y, boolean deleteAll)
	{
		//Check current values
		if(_score[x] != null)
		{
			//Delete Beat or Sample
			if(deleteAll != true)
			{
				_score[x].RemoveSample(y);
			}
			else
			{
				_score[x] = null;
			}
		}
		else
		{
			//Note not occupying current space. Error Logic
		}
	}
}
