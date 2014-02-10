package mcapp;

/**
 * Class dedicated to storing and maintaining a collection of notes. Several
 * of these appear for each instance of Score.
 * @author Josh, Sean
 * 
 */
public class Beat 
{
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
	{
		return (_notes[channel] == null);
	}
	

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
			if (isEmpty(i))
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
	{
		//Reverse order because the later-added notes have their images above
		//previously added ones.
		for (int i = Global.MAX_POLYPHONY - 1; i >= 0; --i)
		{
			if (!isEmpty(i) && _notes[i].getPitch() == pitch)
			{
				_notes[i] = null;
				return true;
			}
		}
		
		//No note at this pitch.
		return false;
	}
	
	@Override
	public String toString()
	{
		String string = "";
		
		for (int i = 0; i < Global.MAX_POLYPHONY; ++i)
		{
			if (!isEmpty(i))
				string += _notes[i].getPitch() + ", ";
			else
				string += "-, ";
		}
		
		return string;
	}
}