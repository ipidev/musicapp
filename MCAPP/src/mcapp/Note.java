package mcapp;

/**
 * Responsible for storing the information for one note.
 * @author Josh, Sean
 *
 */
public class Note
{
	private int _instrumentID;
	private int _pitch;
	
	/**
	 * The length of the note (as a ratio of one bar).
	 */
	private float _length = 1.0f;

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
		_length = 1.0f;
	}
	
	public Note(int instrumentID, int pitch, float length)
	{
		_instrumentID = instrumentID;
		_pitch = pitch;
		_length = length;
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
	 * @return The vertical index of the note. This does not define its actual
	 * pitch!!
	 */
	public int getPitch()
	{
		return _pitch;
	}
	
	public float getLength()
	{
		return _length;
	}
	
	public void setLength(float length)
	{
		_length = length;
	}
}