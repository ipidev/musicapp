package mcapp;

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
 * 
 */
public class Beat 
{
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
	{
		return _beat;
	}
	
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
	{
		if(position < 3 && position > -1)
		{
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
