package mcapp;

/**
 * Defines a key signature and determines which notes should be raised/lowered.
 * @author Sean
 *
 */
public class KeySignature
{
	/**
	 * The root note of the key signature.
	 */
	private int _root;
	public static final int ROOT_A = 0;
	public static final int ROOT_B = 1;
	public static final int ROOT_C = 2;
	public static final int ROOT_D = 3;
	public static final int ROOT_E = 4;
	public static final int ROOT_F = 5;
	public static final int ROOT_G = 6;
	
	/**
	 * The key.
	 */
	private int _key;
	public static final int KEY_MAJOR = 0;
	public static final int KEY_MINOR = 1;
	
	private int[] _accidentals;
	
	public KeySignature()
	{
		this(ROOT_C, KEY_MAJOR);
	}
	
	public KeySignature(int root, int key)
	{
		_root = root;
		_key = key;
		_accidentals = new int[Global.GRID_TO_PITCH.length];
		updateAccidentals();
	}
	
	private void updateAccidentals()
	{
		//Fill it with C major for now.
		for (int i = 0; i < _accidentals.length; i++)
			_accidentals[i] = 0;
	}
	
	/**
	 * Gets the accidental (semitone above/below) for the given note index.
	 * @param index The vertical index of the note.
	 * @return -1, 0 or 1: the number of semitones the note should be adjusted
	 * by.
	 */
	public int getAccidental(int index)
	{
		return _accidentals[index];
	}
}
