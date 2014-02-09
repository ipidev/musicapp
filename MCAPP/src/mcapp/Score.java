package mcapp;

/**
<<<<<<< HEAD
 * Responsible for the creation and storing of the musical layout.
 * 
 * @author Josh, Sean
=======
 * Class is responsible for the creation and storing of the musical layout.
 * 
 * @author Josh
>>>>>>> 6ec92a782d209bd0e17cddcff45cc6d7f15581f1
 *
 */
public class Score 
{
<<<<<<< HEAD
	Beat[] _beats;

	/**
	 * Default Constructor
	 */
	public Score()
	{
		_beats = new Beat[Global.BEATS_PER_SCORE];
	}

	/**
	 * Constructor for loaded file
	 * @param score Collection of beats used for this score. 
	 * */
	public Score(Beat[] beats)
	{
		_beats = new Beat[Global.BEATS_PER_SCORE];
	}

	/**
	 * Accessor for a Beat.
	 * @param position The target position in the Score.
	 * @return The Beat object, or null if the position is empty.
	 */
	public Beat getBeat(int position)
=======
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
>>>>>>> 6ec92a782d209bd0e17cddcff45cc6d7f15581f1
	{
		return _beats[position];
	}
	
	/**
<<<<<<< HEAD
	 * Adds a Note to the Score.
	 * @param position Position in the Score.
	 * @param instrumentID Instrument ID of new Note.
	 * @param pitch Pitch of new Note.
	 * @return True if the operation was successful, false if the position
	 * was occupied by the same note.
	 */
	public boolean addNote(int position, int instrumentID, int pitch)
=======
	 * Returns Score instance
	 * @return Current Score instance
	 */
	public Beat[] GetScore()
>>>>>>> 6ec92a782d209bd0e17cddcff45cc6d7f15581f1
	{
		//Adds a new Beat object if the current position is empty.
		if(_beats[position] == null)
			_beats[position] = new Beat();
		
		return _beats[position].addNote(instrumentID, pitch);
	}
<<<<<<< HEAD

	/**
	 * Removes a Sample or Beat from the Score array.
	 * @param position Position in the Score.
	 * @param pitch Pitch used for determining which Note to delete.
	 * @param deleteAll Check if Beat or Sample is to be deleted
	 * @return True if the operation was successful, false if there was no
	 * note at the given position.
	 */
	public boolean removeNote(int position, int pitch)
	{
		if(_beats[position] != null)
			return _beats[position].removeNote(pitch);

		//The position was empty.
		return false;
	}
}
=======
	
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
>>>>>>> 6ec92a782d209bd0e17cddcff45cc6d7f15581f1
