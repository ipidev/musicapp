package info.ipidev.mcapp;

import java.util.Timer;
import java.util.TimerTask;

import mcapp.Global;
import mcapp.Player;
import mcapp.Display;
import mcapp.Song;
import mcapp.SoundPlayer;
import mcapp.SoundRecorder;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
	 * The song editor view. Give this a better name in the future (like Display)
	 */
	private Display _display = null;
	
	/**
	 * Responsible for recording and saving new sounds.
	 */
	private SoundRecorder _soundRecorder = null;

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
		
		_display = (Display)findViewById(R.id.display);
		Display.setSong(_song);
		
		_soundRecorder = new SoundRecorder();
		
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
		float beat = -1;
		_player.update(0.1f);
		beat = _player.getCurrentBeat();
		/*if((int)_player.getCurrentBeat() % 10 == 9)
		{
			_display.scoreNext();
			beat = _player.getCurrentBeat() - 4;
		}*/	
		_display.moveIndicator(_player.getBeatProgress(), beat);
	}
	
	/**
	 * Event called when the play/pause button is clicked.
	 * @param view The view that was clicked.
	 */
	public void onPlayButton(View view)
	{
		if (!_player.isPlaying() && !_soundRecorder.isRecording())
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
		_display.resetIndicatorPosition(60.0f);
	}
	
	/**
	 * Event called when the record button is clicked.
	 * @param view The view that was clicked.
	 */
	public void onRecordButton(View view)
	{
		if (!_soundRecorder.isRecording())
		{
			//Start recording.
			_soundRecorder.start("temp");
			_player.stop();
			
			Button button = (Button)view;
			button.setText(R.string.button_stopRecording);
			
			//Unload the current recorded sample from memory (if any).
			if (Global.recordedID != 0)
			{
				_soundPlayer.unload(Global.recordedID);
				Global.recordedID = 0;
			}	
		}
		else
		{
			//Stop recording.
			_soundRecorder.stop();
			
			Button button = (Button)view;
			button.setText(R.string.button_startRecording);
			
			//Load the new sample.
			Global.recordedID = _soundPlayer.load(_soundRecorder.getFilePath());
		}
	}
	
	/**
	 * Event called when the use recorded sound checkbox is clicked.
	 * @param view The view that was clicked.
	 */
	public void onRecordCheckBox(View view)
	{
		CheckBox checkbox = (CheckBox)view;
		
		if (!_soundRecorder.isRecording())
			Global.useRecordedSound = checkbox.isChecked();
	}
	
	/**
	 * Event called when the Back button is pressed
	 * @param view The view that was clicked.
	 */
	public void onBackButton(View view)
	{
		_display.scoreBack();
	}
	
	/**
	 * Event called when the Next button is pressed
	 * @param view The view that was clicked.
	 */
	public void onNextButton(View view)
	{
		_display.scoreNext();
	}
	
	public void onClearButton(View view)
	{
		_display.clear();
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