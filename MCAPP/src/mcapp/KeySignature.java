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
	public static final int ROOT_C = 0;
	public static final int ROOT_C_SHARP = 1;
	public static final int ROOT_D = 2;
	public static final int ROOT_D_SHARP = 3;
	public static final int ROOT_E = 4;
	public static final int ROOT_F = 5;
	public static final int ROOT_F_SHARP = 6;
	public static final int ROOT_G = 7;
	public static final int ROOT_G_SHARP = 8;
	public static final int ROOT_A = 9;
	public static final int ROOT_A_SHARP = 10;
	public static final int ROOT_B = 11;
	
	/**
	 * The key.
	 */
	private int _key;
	public static final int KEY_MAJOR = 0;
	public static final int KEY_MINOR = 1;
	
	/**
	 * The major scale - the values represent the increase in pitch in semitones
	 * above the root.
	 */
	private static final int[] MAJOR_SCALE =
	{
		0, 2, 4, 5, 7, 9, 11
	};
	
	/**
	 * The minor scale.
	 */
	private static final int[] MINOR_SCALE =
	{
		0, 2, 3, 5, 7, 8, 10
	};
	
	/**
	 * Lookup table for the position on the scale for each note, assuming C
	 * is 0, D is 1, etc... Use NOTE_ACCIDENTALS to find out if the note is sharp
	 * or not.
	 */
	private static final int[] NOTE_TO_SCALE_POSITION =
	{
		0, 0, 1, 1, 2, 3, 3, 4, 4, 5, 5, 6
	};
	
	/**
	 * Lookup table for each note's accidental. The value is 0 if it is natural
	 * (i.e. a white key) and 1 if it is sharp (i.e. a black key).
	 */
	private static final int[] NOTE_ACCIDENTALS =
	{
		0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 1, 0
	};
	
	private int[] _accidentals;
	
	private boolean _hasSharps = false;
	
	public KeySignature()
	{
		this(ROOT_C, KEY_MAJOR, false);
	}
	
	public KeySignature(int root, int key, boolean hasSharps)
	{
		_accidentals = new int[Global.GRID_TO_PITCH.length];
		set(root, key, hasSharps);
	}
	
	/**
	 * Mutator for the key signature.
	 * @param root The new root note.
	 * @param key The new key.
	 */
	/*public void set(int root, int key)
	{
		_root = root;
		_key = key;
		updateAccidentals();
	}*/
	
	/**
	 * Mutator for the key signature.
	 * @param root The new root note.
	 * @param key Whether it has sharps or not uuuGHGHGHHGHGG
	 */
	public void set(int root, int key, boolean hasSharps)
	{
		_root = root;
		_key = key;
		_hasSharps = hasSharps;
		updateAccidentals();
	}
	
	private void updateAccidentals()
	{
		//Assumes note 0 = B, note 1 = C, note 2 = D...
		
		//C/ Am
		if (_root == ROOT_C && _key == KEY_MAJOR)
		{
			for (int i = _accidentals.length - 1; i >= 0; i--)
			{
				_accidentals[i] = 0;
			}
		}
		//G / Em
		else if (_root == ROOT_G && _key == KEY_MAJOR && _hasSharps == true)
		{
			for (int i = _accidentals.length - 1; i >= 0; i--)
			{
				switch (Global.GRID_TO_PITCH[i] % 12)
				{
					case ROOT_F:
						_accidentals[i] = 1;
						break;
					default:
						_accidentals[i] = 0;
				}
			}
		}
		//D / Bm
		else if (_root == ROOT_D && _key == KEY_MAJOR && _hasSharps == true)
		{
			for (int i = _accidentals.length - 1; i >= 0; i--)
			{
				switch (Global.GRID_TO_PITCH[i] % 12)
				{
					case ROOT_F:
					case ROOT_C:
						_accidentals[i] = 1;
						break;
					default:
						_accidentals[i] = 0;
				}
			}
		}
		//A / F#m
		else if (_root == ROOT_A && _key == KEY_MAJOR && _hasSharps == true)
		{
			for (int i = _accidentals.length - 1; i >= 0; i--)
			{
				switch (Global.GRID_TO_PITCH[i] % 12)
				{
					case ROOT_F:
					case ROOT_C:
					case ROOT_G:
						_accidentals[i] = 1;
						break;
					default:
						_accidentals[i] = 0;
				}
			}
		}
		//E / C#m
		else if (_root == ROOT_E && _key == KEY_MAJOR && _hasSharps == true)
		{
			for (int i = _accidentals.length - 1; i >= 0; i--)
			{
				switch (Global.GRID_TO_PITCH[i] % 12)
				{
					case ROOT_F:
					case ROOT_C:
					case ROOT_G:
					case ROOT_D:
						_accidentals[i] = 1;
						break;
					default:
						_accidentals[i] = 0;
				}
			}
		}
		//B / G#m
		else if (_root == ROOT_B && _key == KEY_MAJOR && _hasSharps == true)
		{
			for (int i = _accidentals.length - 1; i >= 0; i--)
			{
				switch (Global.GRID_TO_PITCH[i] % 12)
				{
					case ROOT_F:
					case ROOT_C:
					case ROOT_G:
					case ROOT_D:
					case ROOT_A:
						_accidentals[i] = 1;
						break;
					default:
						_accidentals[i] = 0;
				}
			}
		}
		//F# / D#m
		else if (_root == ROOT_F_SHARP && _key == KEY_MAJOR && _hasSharps == true)
		{
			for (int i = _accidentals.length - 1; i >= 0; i--)
			{
				switch (Global.GRID_TO_PITCH[i] % 12)
				{
					case ROOT_F:
					case ROOT_C:
					case ROOT_G:
					case ROOT_D:
					case ROOT_A:
					case ROOT_E:
						_accidentals[i] = 1;
						break;
					default:
						_accidentals[i] = 0;
				}
			}
		}
		//C# / A#m
		else if (_root == ROOT_C_SHARP && _key == KEY_MAJOR && _hasSharps == true)
		{
			for (int i = _accidentals.length - 1; i >= 0; i--)
			{
				_accidentals[i] = 1;
			}
		}
		//F / Dm
		else if (_root == ROOT_F && _key == KEY_MAJOR && _hasSharps == false)
		{
			for (int i = _accidentals.length - 1; i >= 0; i--)
			{
				switch (Global.GRID_TO_PITCH[i] % 12)
				{
					case ROOT_B:
						_accidentals[i] = -1;
						break;
					default:
						_accidentals[i] = 0;
				}
			}
		}
		//Bb / Gm
		else if (_root == ROOT_A_SHARP && _key == KEY_MAJOR && _hasSharps == false)
		{
			for (int i = _accidentals.length - 1; i >= 0; i--)
			{
				switch (Global.GRID_TO_PITCH[i] % 12)
				{
					case ROOT_B:
					case ROOT_E:
						_accidentals[i] = -1;
						break;
					default:
						_accidentals[i] = 0;
				}
			}
		}
		//Eb / Gm
		else if (_root == ROOT_D_SHARP && _key == KEY_MAJOR && _hasSharps == false)
		{
			for (int i = _accidentals.length - 1; i >= 0; i--)
			{
				switch (Global.GRID_TO_PITCH[i] % 12)
				{
					case ROOT_B:
					case ROOT_E:
					case ROOT_A:
						_accidentals[i] = -1;
						break;
					default:
						_accidentals[i] = 0;
				}
			}
		}
		//Ab / Fm
		else if (_root == ROOT_G_SHARP && _key == KEY_MAJOR && _hasSharps == false)
		{
			for (int i = _accidentals.length - 1; i >= 0; i--)
			{
				switch (Global.GRID_TO_PITCH[i] % 12)
				{
					case ROOT_B:
					case ROOT_E:
					case ROOT_A:
					case ROOT_D:
						_accidentals[i] = -1;
						break;
					default:
						_accidentals[i] = 0;
				}
			}
		}
		//Db / Bbm
		else if (_root == ROOT_C_SHARP && _key == KEY_MAJOR && _hasSharps == false)
		{
			for (int i = _accidentals.length - 1; i >= 0; i--)
			{
				switch (Global.GRID_TO_PITCH[i] % 12)
				{
					case ROOT_B:
					case ROOT_E:
					case ROOT_A:
					case ROOT_D:
					case ROOT_G:
						_accidentals[i] = -1;
						break;
					default:
						_accidentals[i] = 0;
				}
			}
		}
		//Gb / Ebm
		else if (_root == ROOT_F_SHARP && _key == KEY_MAJOR && _hasSharps == false)
		{
			for (int i = _accidentals.length - 1; i >= 0; i--)
			{
				switch (Global.GRID_TO_PITCH[i] % 12)
				{
					case ROOT_B:
					case ROOT_E:
					case ROOT_A:
					case ROOT_D:
					case ROOT_G:
					case ROOT_C:
						_accidentals[i] = -1;
						break;
					default:
						_accidentals[i] = 0;
				}
			}
		}
		else
		{
			throw new IllegalArgumentException("root " + _root + ", key " + _key + ", sharps " + _hasSharps);
		}
	}
	
	//not quite as old
	/*private void updateAccidentals()
	{
		//The scale to use.
		int[] targetScale = _key == KEY_MAJOR ? MAJOR_SCALE : MINOR_SCALE;
		
		//The note value of the root note.
		int rootNote = _root % 12;
		
		String debug = "key sig: ";
		
		//Iterate from the bottom up.
		for (int i = _accidentals.length - 1; i >= 0; i--)
		{
			//Get the current note value.
			int note = Global.GRID_TO_PITCH[i] % 12;
			
			//Get the note's position in the target key's scale.
			int indexInScale = Global.dumbModuloPoopCheck(note - _root, 12);
			Log.d("KEY", "Index of " + note + " in scale is " + indexInScale);
			
			//Get the value of the root's equivalent MIDI note that is lower
			//than the current note.
			int nextLowestRoot = (Global.GRID_TO_PITCH[i] / 12) * 12 + _root;
			if (nextLowestRoot > Global.GRID_TO_PITCH[i])
				nextLowestRoot -= 12;
			
			Log.d("KEY", "Next lowest root from " + Global.GRID_TO_PITCH[i] + " is " + nextLowestRoot);
			
			//Finally work out the target note.
			Log.d("KEY", "Note " + Global.GRID_TO_PITCH[i] + " should be raised by " + targetScale[NOTE_TO_SCALE_POSITION[indexInScale]]);
			int targetNote = nextLowestRoot + targetScale[NOTE_TO_SCALE_POSITION[indexInScale]] +
					NOTE_ACCIDENTALS[rootNote];
			
			//Input the difference.
			_accidentals[i] = Global.GRID_TO_PITCH[i] - targetNote;
			debug += _accidentals[i] + ", ";
		}
		
		Log.d("KEY", debug);
	}*/
	
	//old
	/*private void updateAccidentals()
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
		 *
		int scaleIndex = ((Global.BOTTOM_NOTE - _root) + 7) % 7;
		Log.d("KEY", "scale index = " + scaleIndex);
		if (scaleIndex < 0)
			return;
		
		String debug = "key sig: ";
		
		//TODO: F major fails because the B needs to be flat... thus everything is offset.
		
		/*
		 * Accumulating sum of note values, used to determine the difference
		 * between the desired note and the note in the unedited scale.
		 *
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
	}*/
	
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
