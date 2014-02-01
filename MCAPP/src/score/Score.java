package score;

//Import Objects etc. Notes, Beats, Bars
import score.Objects.*;

//Class is responsible for the creation and storing of the musical layout.
public class Score 
{
	Note[][] _score;
	
	//Default Constructor
	public Score ()
	{
		SetScore();
	}
	
	//Constructor for Loaded File
	public Score (Note[][] score)
	{
		SetScore(score);
	}
	
	//Setter for Score instance. New Score
	public void SetScore()
	{
		_score = new Note[12][3];
	}
	
	//Setter for Score instance. Loaded Score
	public void SetScore(Note[][] score)
	{
		_score = score;
	}
	
	public Note[][] GetScore()
	{
		return _score;
	}
	
	//Adds to score based on Grid layout.
	public void AddToScore(int x, int y, Note note)
	{
		if(_score[x][y] == null)
		{
			_score[x][y] = note;
		}
		else
		{
			//Note Occupying current space. Error Logic
		}
	}
	
	//Remove Note from current score
	public void RemoveFromScore(int x, int y)
	{
		if(_score[x][y] != null)
		{
			_score[x][y] = null;
		}
		else
		{
			//Note not occupying current space. Error Logic
		}
	}
}
