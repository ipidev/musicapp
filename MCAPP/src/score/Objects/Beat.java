package score.Objects;

//Class dedicated to storing three instances of Note. 
public class Beat 
{
	Note _note;
	Note[] _beat;
	
	//Constructor
	public Beat()
	{
		_beat = new Note [3];
	}
	
	
	//Getter for Beat instance
	public Note[] GetBeat()
	{
		return _beat;
	}
	
	//Adds Note object to Beat Array
	public void AddNote(Note note)
	{
		//Loop to add Note object
		for(int i =0 ; i < 3; i++)
		{
			if (_beat[i] == null)
			{
				_beat[i] = note;
			}
			else if (_beat[i] != null && i == 2)
			{
				//Beat is full. Error Logic
			}
		}
	}
	
	//Removes Note from Beat. Sets position as NULL
	public void RemoveNote(int position)
	{
		if(position < 3 && position > -1)
		{
			_beat[position] = null;
		}
		else
		{
			//Error Logic
		}
	}
}
