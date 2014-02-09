package mcapp;

/**
 * Responsible for storing the information for one note.
 * @author Josh, Sean
 *
 */
public class Note
{
	//The note does not need to store a sample ID - the relationship between
	//the instrument and its sample is done later on (by the Player).
	//private int _sampleID;
	private int _instrumentID;
	private int _pitch;

	/**
	 * Creates a new note.
	 * @param instrument The instrument ID of the loaded Sample.
	 * @param pitch The number of semitones above or below middle-C. This value
	 * must be between -12 (C4) and 12 (C6).
	 */
	public Note(int instrumentID, int pitch)
	{
		_instrumentID = instrumentID;
		_pitch = pitch;
	}
	
	/**
	 * Accessor for the note's instrument.
	 * @return The instrument ID.
	 */
	public int getInstrument()
	{
		return _instrumentID;
	}
	
	/**
	 * Accessor for the note's pitch.
	 * @return The number of semitones above/below the sample's base note.
	 */
	public int getPitch()
	{
		return _pitch;
	}
}