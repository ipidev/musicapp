package info.ipidev.mcapp;

import mcapp.Global;
import mcapp.Player;
import mcapp.Song;
import mcapp.SoundPlayer;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends Activity
{
	/**
	 * Loads, plays and unloads sounds.
	 */
	private SoundPlayer _soundPlayer = null;
	
	/**
	 * Responsible for traversing through the song and playing it.
	 */
	private Player _player = null;
	
	/**
	 * Holds the song data.
	 */
	private Song _song = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		//Android creation stuff.
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Set up sound player and load the sample.
		_soundPlayer = new SoundPlayer(this);
		Global.pianoID = _soundPlayer.load(R.raw.piano);
		
		//Create other stuff.
		_song = new Song();
		_player = new Player(_song, _soundPlayer);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
}
