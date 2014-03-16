package mcapp;

import android.util.Log;

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
	public static final int ROOT_A = 5;
	public static final int ROOT_B = 6;
	public static final int ROOT_C = 0;
	public static final int ROOT_D = 1;
	public static final int ROOT_E = 2;
	public static final int ROOT_F = 3;
	public static final int ROOT_G = 4;
	
	/**
	 * The key.
	 */
	private int _key;
	public static final int KEY_MAJOR = 0;
	public static final int KEY_MINOR = 1;
	
	/**
	 * The major scale - the values represent the differences in pitch from the
	 * previous note (i.e. index 0 represents the change in pitch from I to II).
	 */
	private static final int[] MAJOR_SCALE =
	{
		2, 2, 1, 2, 2, 2, 1
	};
	
	/**
	 * The minor scale.
	 */
	private static final int[] MINOR_SCALE =
	{
		2, 1, 2, 2, 1, 2, 2
	};
	
	private int[] _accidentals;
	
	public KeySignature()
	{
		this(ROOT_C, KEY_MAJOR);
	}
	
	public KeySignature(int root, int key)
	{
		_accidentals = new int[Global.GRID_TO_PITCH.length];
		set(root, key);
	}
	
	/**
	 * Mutator for the key signature.
	 * @param root The new root note.
	 * @param key The new key.
	 */
	public void set(int root, int key)
	{
		_root = root;
		_key = key;
		updateAccidentals();
	}
	
	private void updateAccidentals()
	{
		//The scale to use.
		int[] usedScale = _key == KEY_MAJOR ? MAJOR_SCALE : MINOR_SCALE;
		
		Log.d("KEY", "scale index");
		Log.d("KEY", "scale index = ((" + Global.BOTTOM_NOTE  + " - " + _root + ") + 7) % 7");
		
		/*
		 * Get the index in the scale array. This is done to find the
		 * corresponding... number (i.e. I, II, III...) of the lowest note
		 * possible on the scale... spent ages trying to figure this out,
		 * don't ask. It works though (except for any root note with accidentals)
		 */
		int scaleIndex = ((Global.BOTTOM_NOTE - _root) + 7) % 7;
		Log.d("KEY", "scale index = " + scaleIndex);
		if (scaleIndex < 0)
			return;
		
		String debug = "key sig: ";
		
		//TODO: F major fails because the B needs to be flat... thus everything is offset.
		
		/*
		 * Accumulating sum of note values, used to determine the difference
		 * between the desired note and the note in the unedited scale.
		 */
		int accumulatedNotes = Global.GRID_TO_PITCH[_accidentals.length - 1]; // + accidental?
		
		for (int i = _accidentals.length - 1; i >= 0; i--)
		{
			Log.d("KEY", "i: " + i + " -  pitch " + Global.GRID_TO_PITCH[i] + ", acc " + accumulatedNotes + "... += " + usedScale[scaleIndex]);
			
			_accidentals[i] = accumulatedNotes - Global.GRID_TO_PITCH[i];
			debug += _accidentals[i] + ", ";
			
			//Increment.
			accumulatedNotes += usedScale[scaleIndex];
			scaleIndex = (scaleIndex + 1) % 7;
		}
		_root = 0;
		Log.d("KEY", debug);
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
