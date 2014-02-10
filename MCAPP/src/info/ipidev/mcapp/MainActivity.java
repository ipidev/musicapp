package info.ipidev.mcapp;

import java.util.Timer;
import java.util.TimerTask;

import mcapp.Global;
import mcapp.Player;
import mcapp.ShowImage;
import mcapp.Song;
import mcapp.SoundPlayer;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
//import mcapp.Timer;

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
	 * Responsible for keeping track of how long each update step takes. I
	 * should replace this with my own Timer class eventually.
	 */
	private Timer _timer = null;
	
	/**
	 * Holds the song data.
	 */
	private Song _song = null;
	
	/**
	 * The song editor view. Give this a better name in the future.
	 */
	private ShowImage _showImage = null;

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
		_player.setBpm(120);
		
		_showImage = (ShowImage)findViewById(R.id.editor);
		ShowImage.setSong(_song);
		
		//Set up timer. Replace me.
		_timer = new Timer();
		_timer.scheduleAtFixedRate(new Updater(this), 0, 100);
		
		//Set up seek bar.
		SeekBar bpmBar = (SeekBar)findViewById(R.id.bpmBar);
		bpmBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{
			int _progress;
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				_progress = progress;
				if (_progress != 0)
					_player.setBpm(_progress);
			}
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				if (_progress != 0)
					_player.setBpm(_progress);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/**
	 * This is the method called by java.util.Timer.
	 */
	public void run()
	{
		_player.update(0.1f);
	}
	
	/**
	 * Event called when the play/pause button is clicked.
	 * @param view The view that was clicked.
	 */
	public void onPlayButton(View view)
	{
		if (!_player.isPlaying())
		{
			//Start playing.
			_player.play();
			
			Button button = (Button)view;
			button.setText(R.string.button_pause);
		}
		else
		{
			//Pause.
			_player.pause();
			
			Button button = (Button)view;
			button.setText(R.string.button_play);
		}
	}
	
	/**
	 * Event called when the stop button is clicked.
	 * @param view The view that was clicked.
	 */
	public void onStopButton(View view)
	{
		//Stop.
		_player.stop();
		
		Button button = (Button)findViewById(R.id.playButton);
		button.setText(R.string.button_play);
	}
	
	/**
	 * Event called when the BPM seek bar's value is changed.
	 * @param seekBar The seek bar.
	 * @param progress The new value of the seek bar.
	 * @param fromUser Whether or not the user made this change.
	 */
	public void onBpmChanged(SeekBar seekBar, int progress, boolean fromUser)
	{
		if (progress != 0)
			_player.setBpm(progress);
	}
}

/**
 * Stupid class that contains the callback function for the Timer... all the
 * more reason to properly implement my own.
 * @author Sean
 *
 */
class Updater extends TimerTask
{
	MainActivity _mainActivity;
	
	public Updater(MainActivity mainActivity)
	{
		_mainActivity = mainActivity;
	}
	
	public void run()
	{
		_mainActivity.run();
	}
}