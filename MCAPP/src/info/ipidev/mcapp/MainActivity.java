package info.ipidev.mcapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import mcapp.Beat;
import mcapp.Display;
import mcapp.Global;
import mcapp.Note;
import mcapp.Player;
import mcapp.Score;
import mcapp.Song;
import mcapp.SoundPlayer;
import mcapp.SoundRecorder;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
//import mcapp.Timer;

public class MainActivity extends Activity
{
	//Beat tracker
	int _beat = -1;
	int _multiplier = 0;
	
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
		
		//Hide the action bar.
		//Check https://developer.android.com/tools/support-library/setup.html#libs-with-res
		
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
		_player.update(0.1f);
		
		_soundRecorder.update(0.1f);
		
		//Calculations for the indicator position.
		if(_beat + 5 * _multiplier != (int)_player.getCurrentBeat())
		{			
			_beat = (int)_player.getCurrentBeat() - 5 * _multiplier;
			if(_beat % 6 == 0 && _beat != 0)
			{				
				_display.scoreNext();
				_beat -= 5;
				_multiplier++;				
			}
		}
		_display.moveIndicator(_beat, _player.getBeatProgress());
	}
	
	/**
	 * Event called when the play/pause button is clicked.
	 * @param view The view that was clicked.
	 */
	public void onPlayButton(View view)
	{
		if (!_player.isPlaying() && !_soundRecorder.isRecording())
		{
			//Make callback function object.
			EndOfSongCallback endOfSong = new EndOfSongCallback(this);
			
			//Start playing.
			_player.play(endOfSong);
			
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
		sharedStopStuff();
		
		Button button = (Button)findViewById(R.id.playButton);
		button.setText(R.string.button_play);		
	}
	
	/**
	 * Callback function for when the song ends.
	 */
	public void endOfSong()
	{
		sharedStopStuff();
		
		//Need to jump back to the UI thread in order to edit the button.
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				Button button = (Button)findViewById(R.id.playButton);
				button.setText(R.string.button_play);
			}
		});
	}
	
	/**
	 * Stuff shared by both the stop button function and the end of song callback.
	 */
	public void sharedStopStuff()
	{
		//Stop.
		_player.stop();
		_beat = -1;
		_multiplier = 0;
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
			//Make callback function object.
			StopRecording stopRecording = new StopRecording(this);
			
			//Start recording.
			_soundRecorder.start("temp", stopRecording);
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
			stopRecording();
		}
	}
	
	/**
	 * Changes the record button and stuff.
	 */
	public void stopRecording()
	{
		//Need to jump back to the UI thread in order to edit the button.
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				Button button = (Button)findViewById(R.id.recordButton);
				button.setText(R.string.button_startRecording);
			}
		});
		
		//Load the new sample.
		Global.recordedID = _soundPlayer.load(_soundRecorder.getFilePath());
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
	
	public void onChangeNoteLengthButton(View view)
	{
		_display.noteLengthMenu();
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

/**
*	Callback class for finished recording.
*/
class StopRecording implements SoundRecorder.SoundRecorderCallback
{
	MainActivity _mainActivity;
	
	public StopRecording(MainActivity mainActivity)
	{
		_mainActivity = mainActivity;
	}
	
	public void callback()
	{
		_mainActivity.stopRecording();
	}
}

/**
* Callback class for end of song.
*/
class EndOfSongCallback implements Player.EndOfSongCallback
{
	MainActivity _mainActivity;
	
	public EndOfSongCallback(MainActivity mainActivity)
	{
		_mainActivity = mainActivity;
	}
	
	public void callback()
	{
		_mainActivity.endOfSong();
	}
}

/**
 * Import Data from a specified File path. Creates a Song instance loaded with the imported data
 * @param fileName Name of Desired File
 * @return Constructed Song Instance
 */
public Song songImport(String fileName)
{
	Song tSong = new Song();
	
	//Creates File Path for Song
	String _filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
	_filePath += "/" + fileName + ".txt";
	
	File file = new File(_filePath);
	FileInputStream fin = null;
	
	try {// create FileInputStream object
        fin = new FileInputStream(file);
        byte fileContent[] = new byte[(int)file.length()];
        
        // Reads up to certain bytes of data from this input stream into an array of bytes.
        fin.read(fileContent);
        
        //create string from byte array
        String s = new String(fileContent);
        System.out.println("File content: " + s);
    }
    catch (FileNotFoundException e) {
    System.out.println("File not found" + e);
    }
    catch (IOException ioe) {
        System.out.println("Exception while reading file " + ioe);
    }
    finally {
        // close the streams using close method
        try {
            if (fin != null) {
                fin.close();
            }
        }
        catch (IOException ioe) {
            System.out.println("Error while closing stream: " + ioe);
        }
    }
	
	Score tScore = new Score();
	
	return tSong;
}

/**
 * Exports the Song Object to a file for storage.
 * @param fileName Name of File
 * @param song Current Song instance
 */
public void songExport(String fileName, Song song)
{
	//Creates File Path for Song
	String _filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
	_filePath += "/" + fileName + ".txt";

	//Current Instance of Song
	Score tScore = song.getScore(0);
	
	//Export Stream
	byte[] stream = new byte[(12*3*4)*Integer.SIZE];
	byte[] name = song.getName().getBytes();
	byte[] description = song.getDescription().getBytes();
	int count = 0;
	
	for (int i=0; i<12; i++) 
	{
	    Beat beat = tScore.getBeat(i);
	    Note[] notes = beat.getNotes();
	    
	    int j = 0;
	    
	    //Converts Note Information into Bytes
	    for(Note n : notes)
	    {
	    	byte instrumentID = (byte) (n.getInstrument() & 0xff);
            byte pitch = (byte) ((n.getPitch() & 0xff00 ) >> 8);
            byte beatPos = (byte) ((byte) (i & 0xff0000 ) >> 8);
            byte notePos = (byte) ((byte) (j & 0xff0001 ) >> 8);
            
			stream[count] = instrumentID;    
            stream[count + 1] = pitch;
            stream[count + 2] = beatPos;
            stream[count + 3] = notePos;
            
            count = count + 4;
            j++;
	    }
	    j = 0;
	 }
	
	try
      {
         FileOutputStream fileOut = new FileOutputStream(_filePath);
         ObjectOutputStream out = new ObjectOutputStream(fileOut);
         
         //Use this to test the system output to see if data is passing out.
         //System.out.println(name);
         //System.out.println(description);
         //System.out.println(stream);
         
         //Outputs Byte Information
         out.write('#');
         out.write(name);
         out.write('#');
         out.write(description);
         out.write('#');
         out.write(stream);
         out.write('#');
         out.close();
         
         //Delete Instances
         name = null;
         description = null;
         stream = null;
         
         fileOut.close();
      }
      catch(IOException i)
      {
          i.printStackTrace();
      }
	}
}