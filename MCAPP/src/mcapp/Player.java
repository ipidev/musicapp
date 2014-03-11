package mcapp;

import android.util.Log;


/**
 * Class responsible for handling any sound output as well as progressing
 * through the song and playing its notes. 
 * @author Sean, Shavarsh
 *
 */
public class Player 
{
	/**
	 * Holds the song data.
	 */
	private Song _song = null;
	
	/**
	 * Plays sounds for the app.
	 */
	private SoundPlayer _soundPlayer = null;
	
	/**
	 * Holds the index of the current score. This is always 0 in simple mode.
	 * This might prove problematic if we have more than one track (i.e. more
	 * than one score playing at one time).
	 */
	private int _currentScore = 0;
	
	/**
	 * Holds the index of the current beat (the most recent beat played).
	 */
	private int _currentBeat = -1;
	
	/**
	 * The number seconds it takes for one beat to pass.
	 */
	private float _secondsPerBeat = 0.4f;
	
	/**
	 * Increases by delta time until it reaches _secondsPerBeat, and 
	 */
	private float _nextBeatCount = 0.0f;

	/**
	 * Whether or not the player is playing.
	 */
	private boolean _isPlaying = false;
	
	
	/**
	 * Constructs an editor object.
	 * @param soundPlayer The object that is responsible for outputting sound.
	 */
	public Player(Song song, SoundPlayer soundPlayer)
	{
		_song = song;
		_soundPlayer = soundPlayer;
	}
	
	
	/**
	 * Starts playing or resumes the song.
	 */
	public void play()
	{
		_isPlaying = true;
	}
	
	/**
	 * Pauses the song; it can be resumed with play().
	 */
	public void pause()
	{
		_isPlaying = false;
	}
	
	/**
	 * Stops the song and sets the marker to the beginning of the score.
	 */
	public void stop()
	{
		_isPlaying = false;
		_currentBeat = -1;
		_nextBeatCount = 0.0f;
	}
	
	/**
	 * Playing state accessor.
	 * @return Whether or not the player is playing.
	 */
	public boolean isPlaying()
	{
		return _isPlaying;
	}
	
	/**
	 * Gets the player's current progress the next beat. This can be used for
	 * a playing animation.
	 * @return The progress to the next beat, as a ratio between 0.0 and 1.0.
	 */
	public float getBeatProgress()
	{
		return _nextBeatCount / _secondsPerBeat;
	}
	
	/**
	 * Gets the player's current beat. Used for playing animation.
	 * @return The current beat the player is at.
	 */
	public float getCurrentBeat()
	{
		return _currentBeat;
	}
	
	/**
	 * Sets the BPM (beats per minute) of the song.
	 * @param bpm The new BPM.
	 */
	public void setBpm(int bpm)
	{
		//Convert from beats/minute to seconds/beat.
		_secondsPerBeat = 1.0f / (bpm / 60.0f);
		
		//Also set the song BPM (so it can be saved)
	}
	
	/**
	 * Updates the player. This method is to be called as fast as possible.
	 * @param deltaTime The amount of time that has passed since the last update
	 * call.
	 */
	public void update(float deltaTime)
	{
		if (_isPlaying)
		{
			_nextBeatCount += deltaTime;
			
			//Play the next beat if it's time.
			if (_nextBeatCount >= _secondsPerBeat)
			{
				_currentBeat++;
				
				//If the player has reached the end of the score, stop.
				if (_currentBeat >= Global.BEATS_PER_SCORE)
					stop();
				else
				{
					_nextBeatCount -= _secondsPerBeat;
					
					//Play notes from song.
					Beat beat = _song.getScore(_currentScore).getBeat(_currentBeat);
					
					if (beat != null)
					{
						for (int i = 0; i < Global.MAX_POLYPHONY; ++i)
						{
							if (!beat.isEmpty(i))
							{
								int pitch = beat.getNote(i).getPitch();
								int accidental = _song.getScore(_currentScore).getKeySignature().getAccidental(pitch);
								
								_soundPlayer.play(Global.useRecordedSound ? Global.recordedID : Global.pianoID,
											      Global.GRID_TO_PITCH[pitch] + accidental);
							}
						}
					}
				}
			}
		}
	}
}
