package mcapp;

/**
 * Responsible for the creation and storing of the musical layout.
 * 
 * @author Josh, Sean
 *
 */
public class Score 
{
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
	{
		return _beats[position];
	}
	
	/**
	 * Adds a Note to the Score.
	 * @param position Position in the Score.
	 * @param pitch Pitch of new Note.
	 * @param instrumentID Instrument ID of new Note.
	 * @return True if the operation was successful, false if the position
	 * was occupied by the same note.
	 */
	public boolean addNote(int position, int pitch, int instrumentID)
	{
		//Adds a new Beat object if the current position is empty.
		if(_beats[position] == null)
			_beats[position] = new Beat();
		
		return _beats[position].addNote(instrumentID, pitch);
	}

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
	
	public void clearScore()
	{
		for(int i = 0; i < _beats.length; i++)
		{
			_beats[i] = null;
		}
	}
}