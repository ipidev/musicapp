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
import mcapp.KeySignature;
import mcapp.Note;
import mcapp.Player;
import mcapp.Score;
import mcapp.Song;
import mcapp.SoundPlayer;
import mcapp.SoundRecorder;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
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
	 * Responsible for recording and saving new sounds.
	 */
	private SoundRecorder _soundRecorder = null;
	
	/**
	 * Left drawer stuff.
	 */
	private DrawerLayout _drawerLayout;
    private ListView _drawerList;
    private ActionBarDrawerToggle _drawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		//Android creation stuff.
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Create fileDirector if loaded on first attempt
		File folder = new File(Environment.getExternalStorageDirectory() +File.separator + "swifTone");

		if (!folder.exists()) { //Make Directory if it does not exist

			Global.firstLoadTime = true;

			folder.mkdir();
		}

		if (Global.firstLoadTime = true) //Initial Load Load
		{

			Global.fileDirectory = Environment.getExternalStorageDirectory() +File.separator + "swifTone";

			//Export Stream
			byte[] fileDir = Global.fileDirectory.getBytes();

			File settings = new File(Global.fileDirectory);

			try
			{
				FileOutputStream fileOut = new FileOutputStream(settings);
				ObjectOutputStream out = new ObjectOutputStream(fileOut);

				//Use this to test the system output to see if data is passing out.
				//System.out.println(Global.fileDirectory);
				//System.out.println(settings.getPath());

				//Outputs Byte Information
				out.write('#');
				out.write(fileDir);
				out.write('#');
				out.close();

				//Delete Instances
				fileDir = null;

				fileOut.close();
			}
			catch(IOException i)
			{
				i.printStackTrace();
			}
		}
		else
		{
			//Creates File Path for Song
			String _filePath = Environment.getExternalStorageDirectory() +File.separator + "swifTone";
			_filePath += "/settings.txt";

			File file = new File(_filePath);
			FileInputStream fin = null;

			try
			{
				// create FileInputStream object
		        fin = new FileInputStream(file);
		        byte fileContent[] = new byte[(int)file.length()];

		        // Reads up to certain bytes of data from this input stream into an array of bytes.
		        fin.read(fileContent);

		        //create string from byte array
		        String s = new String(fileContent);
		        System.out.println("File content: " + s);
		    }
		    catch (FileNotFoundException e)
		    {
		    	System.out.println("File not found" + e);
		    }
		    catch (IOException ioe)
		    {
		        System.out.println("Exception while reading file " + ioe);
		    }
		    finally
		    {
		        // close the streams using close method
		        try
		        {
		            if (fin != null)
		                fin.close();
		        }
		        catch (IOException ioe)
		        {
		            System.out.println("Error while closing stream: " + ioe);
		        }
		    }

		}
		
		//Set up sound player and load the sample.
		_soundPlayer = new SoundPlayer(this);
		Global.pianoID = _soundPlayer.load(R.raw.piano);
		
		//Create other stuff.
		_song = new Song();
		_player = new Player(_song, _soundPlayer);
		_player.setBpm(120);
		
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
		
		//Set up drawer.
		_drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		_drawerList = (ListView) findViewById(R.id.left_drawer);
		
		//Temporary array.
		String[] tempStrings = {"hello", "poop", "bumhole", "does this work"};
		
		// set a custom shadow that overlays the main content when the drawer opens
        _drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        _drawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, tempStrings));
        _drawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        //Set up drawer events I think??
        _drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                _drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            @Override 
			public void onDrawerClosed(View view)
            {
            	//Reset action bar title. We're not using this.
                //getActionBar().setTitle(mTitle);
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            @Override
			public void onDrawerOpened(View drawerView)
            {
            	//Reset action bar title. We're not using this.
                //getActionBar().setTitle(mDrawerTitle);
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        _drawerLayout.setDrawerListener(_drawerToggle);

        if (savedInstanceState == null)
        {
            selectItem(0);
        }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	/* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //This is where you can hide things on the action bar if needbe.
        boolean drawerOpen = _drawerLayout.isDrawerOpen(_drawerList);
        return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        _drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        _drawerToggle.onConfigurationChanged(newConfig);
    }
	
	/**
	 *  The click listener for ListView in the navigation drawer. Basically,
	 *  a callback class.
	 */
	private class DrawerItemClickListener implements ListView.OnItemClickListener
	{
	    @Override
	    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        selectItem(position);
	    }
	}
	
	/**
	 * Callback function for when an item is chosen from the drawer.
	 * @param position
	 */
	private void selectItem(int position)
	{
		new AlertDialog.Builder(this)
			.setTitle("Woah!!")
		    .setMessage("You pressed " + position)
		    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
		    {
		        @Override
				public void onClick(DialogInterface dialog, int which)
		        { 
		            //Something
		        }
		    })
		    .show();
	}
	
	/**
	 * This is the method called by java.util.Timer.
	 */
	public void run()
	{
		_player.update(0.1f);
		_soundRecorder.update(0.1f);
		
		//Calculations for the indicator position.
		if(_beat + 4 * _multiplier != (int)_player.getCurrentBeat())
		{			
			_beat = (int)_player.getCurrentBeat() - 4 * _multiplier;
			if(_beat % 5 == 0 && _beat != 0)
			{				
				_beat -= 4;
				_multiplier++;
				if (Display.getIndicatorScreen() == Display.getScreen())
					Display.scoreNext();
				
				Display.setIndicatorScreen(Display.getIndicatorScreen() + 1);
			}
		}
		Display.passPlayProgress(_beat, _player.getBeatProgress());
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
			
			Display.passPlayerStatus(true);
			
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
		Display.passPlayerStatus(false);
		Display.resetIndicatorPosition();
		Display.setIndicatorScreen(0);
		
		Button button = (Button)findViewById(R.id.playButton);
		button.setText(R.string.button_play);		
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
			
			Button button = (Button)view;
			button.setText(R.string.button_startRecording);
			
			//Load the new sample.
			Global.recordedID = _soundPlayer.load(_soundRecorder.getFilePath());
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
		Display.scoreBack();
	}
	
	/**
	 * Event called when the Next button is pressed
	 * @param view The view that was clicked.
	 */
	public void onNextButton(View view)
	{
		Display.scoreNext();
	}
	
	/**
	 * Event called when the Clear button is pressed
	 * @param view The view that was clicked.
	 */
	public void onClearButton(View view)
	{
		Display.clear();
	}
	
	/**
	 * Event called when the Change length button is pressed
	 * @param view The view that was clicked.
	 */
	public void onChangeNoteLengthButton(View view)
	{
		Display.noteLengthMenu();
	}
	
	public void onChangeSignature(View view)
	{
		new AlertDialog.Builder(this)
	    .setTitle("Choose signature")
	    .setItems(Display.getSignatures(), new DialogInterface.OnClickListener()
	    {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				if(!(which == 1 || which == 9))
				{
					Display.setSignature(which);
					
					//Big ugly switch statement to set the key signature.
					int root;
					int key = KeySignature.KEY_MAJOR;
					
					switch (which)
					{
					case 0:
						root = KeySignature.ROOT_C;
						break;
					case 2:
						root = KeySignature.ROOT_G;
						break;
					case 3:
						root = KeySignature.ROOT_D;
						break;
					case 4:
						root = KeySignature.ROOT_A;
						break;
					case 5:
						root = KeySignature.ROOT_E;
						break;
					case 6:
						root = KeySignature.ROOT_B;
						break;
					case 10:
						root = KeySignature.ROOT_F;
						break;
					default:
						root = KeySignature.ROOT_C;
						break;
					}
					
					_song.getScore(0).getKeySignature().set(root, key);
				}
			}
	    })
	     .show();
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
	
	@Override
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
	
	@Override
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
	
	@Override
	public void callback()
	{
		_mainActivity.endOfSong();
	}
}