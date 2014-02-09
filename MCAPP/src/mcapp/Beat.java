package mcapp;

<<<<<<< HEAD
/**
 * Class dedicated to storing and maintaining a collection of notes. Several
 * of these appear for each instance of Score.
 * @author Josh, Sean
=======
import java.util.LinkedList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * Responsible for storing sample information.
 * @param sampleID The Unique identifier given to a sample.
 * @param instrument The Instrument ID of the loaded Sample.
 * @param pitch The number of semitones above or below middle-C. This value
 * must be between -12 (C4) and 12 (C5).
 * 
 * @author Josh
 * 
 */
class Sample{
	int sampleID;
	int instrumentID;
	int pitch;
	
	/**
	 * 
	 * @param sampleID
	 * @param instrumentID
	 * @param pitch
	 */
	public Sample(int sampleID, int instrumentID, int pitch)
	{
			this.sampleID = sampleID;
			this.instrumentID = instrumentID;
			this.pitch = pitch;
	}
}

/**
 * Class dedicated to storing three instances of Sample.
 * The class is implemented in the Score class. 
>>>>>>> 6ec92a782d209bd0e17cddcff45cc6d7f15581f1
 * 
 */
public class Beat 
{
<<<<<<< HEAD
	/**
	 * The note data.
	 */
	Note _notes[];

	/**
	 * Initialises LinkedList
	 */
	public Beat()
	{
		_notes = new Note[Global.MAX_POLYPHONY];
	}

	/**
	 * Accessor for the notes.
	 * @return A linked list of notes.
	 */
	public Note[] getNotes()
	{
		return _notes;
	}
	
	/**
	 * Accessor for one note.
	 * @param channel The target channel.
	 * @return The note in this channel, or null if there is none.
	 */
	public Note getNote(int channel)
	{
		return _notes[channel];
	}

	/**
	 * Returns whether or not the entire Beat is empty.
	 * @return True if the Beat has no Notes.
	 */
	public boolean isEmpty()
	{
		for (int i = 0; i < Global.MAX_POLYPHONY; ++i)
		{
			if (_notes[i] != null)
				return false;
		}
		
		return true;
	}
	
	/**
	 * Returns whether or not a specific channel in the beat.
	 * @return True if the channel has no Notes.
	 */
	public boolean isEmpty(int channel)
=======
	LinkedList<Sample> _beat;
	boolean[] isEmpty;
	int count;
	 
	/**
	 * Intialises LinkedList
	 */
	public Beat()
	{
		count = 0;
		isEmpty = new boolean[3];
		_beat = new LinkedList<Sample>();
	}
	
	/**
	 * Getter for Beat instance
	 * @return Current LinkedList<Sample>. Possible use for loading.
	 */
	public LinkedList<Sample> GetBeat()
>>>>>>> 6ec92a782d209bd0e17cddcff45cc6d7f15581f1
	{
		return (_notes[channel] == null);
	}
	
<<<<<<< HEAD

	/**
	 * Adds a Note to the next available channel in this Beat.
	 * @param instrumentID The new note's instrument.
	 * @param pitch The new note's pitch.
	 * @return True if the operation was successful, false if the Beat is full.
	 */
	public boolean addNote(int instrumentID, int pitch)
	{
		for (int i = 0; i < Global.MAX_POLYPHONY; ++i)
		{
			if (!isEmpty(i))
			{
				//If the channel is empty, check the other notes to see that
				//none of them have the same pitch and instrument as the new one
				for (int j = 0; j < Global.MAX_POLYPHONY; ++j)
				{
					if (i == j)
						continue;
					
					if (!isEmpty(j) &&
						_notes[j].getInstrument() == instrumentID &&
						_notes[j].getPitch() == pitch)
					{
						//Non-unique note.
						return false;	
					}
				}
				
				//This is a unique note - add it.
				_notes[i] = new Note(instrumentID, pitch);
				return true;
			}
		}
		
		//Beat is full.
		return false;
	}


	/**
	 * Removes Note from Beat.
	 * @param pitch Pitch used for determining which Note to delete.
	 * @return True if the note was deleted, false if there was no note at this
	 * pitch.
	 */
	public boolean removeNote(int pitch)
=======
	/**
	 * Getter for the size of the List
	 * @return Size count of List.
	 */
	public int GetSize()
	{
		return count;
	}
	
	/**
	 * Adds Sample object to LinkedList<Sample>
	 * @param sampleID ID of stored Sample
	 * @param instrumentID ID of associated instrument
	 * @param pitch Attribute of Sample
	 */
	public void AddSample(int sampleID, int instrumentID, int pitch)
	{
		Sample tSamp = new Sample(sampleID, instrumentID, pitch);
		
		int index = 0;
		boolean check = _beat.isEmpty();
		
		List<boolean[]> list = Arrays.asList(isEmpty);
		
		//Checks that the beat does not exceed the size of 3
		if(check != true && count == 3)
		{
			//Beat is full. Error Logic
		}
		else
		{
			//Finds free space in beat
			for(boolean b: isEmpty)
			{
				if(b != true)
				{
					index++;
				}
				else
				{
					index = list.indexOf(b);
				}
			}
			
			//Adds Sample normally or via index location
			if(_beat.getLast() == null)
			{
				_beat.add(tSamp);
				isEmpty[count] = false;
			}
			else
			{
				_beat.add(index, tSamp);
				isEmpty[index] = false;
			}

			count++;
		}		
	}
	
	
	/**
	 * Removes Sample from Beat.
	 * @param position Index of chosen Sample to be removed.
	 */
	public void RemoveSample(int position)
>>>>>>> 6ec92a782d209bd0e17cddcff45cc6d7f15581f1
	{
		//Reverse order because the later-added notes have their images above
		//previously added ones.
		for (int i = Global.MAX_POLYPHONY; i > 0; --i)
		{
<<<<<<< HEAD
			if (!isEmpty(i) && _notes[i].getPitch() == pitch)
			{
				_notes[i] = null;
				return true;
			}
		}
		
		//No note at this pitch.
		return false;
	}
}
=======
			_beat.remove(position);
			isEmpty[position] = true;
			count--;
		}
		else
		{
			//Error Logic
		}
	}
}
>>>>>>> 6ec92a782d209bd0e17cddcff45cc6d7f15581f1
