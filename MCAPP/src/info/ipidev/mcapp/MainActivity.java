package info.ipidev.mcapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Timer;
import java.util.TimerTask;

import mcapp.Display;
import mcapp.Global;
import mcapp.InstrumentManager;
import mcapp.KeySignature;
import mcapp.Player;
import mcapp.Song;
import mcapp.SongExporter;
import mcapp.SongImporter;
import mcapp.SoundPlayer;
import mcapp.SoundRecorder;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
//import mcapp.Timer;

public class MainActivity extends Activity
{
	//Beat tracker
	int _beat = -1;
	int _multiplier = 0;

	/**
	 * Reference to itself.
	 */
	public Activity that = this;
	
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
	
	private InstrumentManager _instrumentManager = null;
	
	/**
	 * Left drawer stuff.
	 */
	private DrawerLayout _drawerLayout;
    private ListView _drawerList;
    private ActionBarDrawerToggle _drawerToggle;
    
    /**
     * The index of the "record new" item in the instruments list.
     */
    private int _recordNewIndex;
    
    //Recording stuffs.
    private boolean _isReadyingRecorder = false;
    private float _readyTimer = 0.0f;
    private static final float RECORDING_READY_DURATION = 2.0f;
    private AlertDialog _recordingDialog = null;
    
    /**
     * Length of one frame, in milliseconds.
     */
    private static final int FRAME_LENGTH = 50;
    private static final float FRAME_LENGTH_FLOAT = FRAME_LENGTH / 1000.0f;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		//Android creation stuff.
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Set up sound player and load the sample.
		_soundPlayer = new SoundPlayer(this);
		
		//Set up instruments.
		_instrumentManager = new InstrumentManager(_soundPlayer);
		_instrumentManager.add(R.raw.grand_piano, getString(R.string.inst_grand_piano));
		_instrumentManager.add(R.raw.electric_piano, getString(R.string.inst_electric_piano));
		_instrumentManager.add(R.raw.vibraphone, getString(R.string.inst_vibraphone));
		_instrumentManager.add(R.raw.acoustic_guitar, getString(R.string.inst_acoustic_guitar));
		_instrumentManager.add(R.raw.electric_bass, getString(R.string.inst_electric_bass));
		_instrumentManager.add(R.raw.violin, getString(R.string.inst_violin));
		_instrumentManager.add(R.raw.pizzicato, getString(R.string.inst_pizzicato));
		_instrumentManager.add(R.raw.vocal_doo, getString(R.string.inst_vocal_doos));
		_instrumentManager.add(R.raw.trumpet, getString(R.string.inst_trumpet));
		_instrumentManager.add(R.raw.flute, getString(R.string.inst_flute));
		_instrumentManager.add(R.raw.steel_drum, getString(R.string.inst_steel_drum));
		_instrumentManager.add(R.raw.cat, getString(R.string.inst_cat));
		_recordNewIndex = _instrumentManager.add(getString(R.string.inst_new_recording));
		
		_instrumentManager.addEraserSound(R.raw.eraser1);
		_instrumentManager.addEraserSound(R.raw.eraser2);
		
		//Create other stuff.
		_song = new Song();
		_player = new Player(_song, _soundPlayer, _instrumentManager.getID(0));
		_player.setBpm(120);
		
		//Give stuff to display.
		Display.setSong(_song);
		Display.setSoundStuff(_player, _soundPlayer, _instrumentManager);
		
		_soundRecorder = new SoundRecorder();
		
		//Set up timer. Replace me.
		_timer = new Timer();
		_timer.scheduleAtFixedRate(new Updater(this), 0, FRAME_LENGTH);
		
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
				_progress = progress + 90;
				if (_progress != 0)
				{
					_player.setBpm(_progress);
					TextView bpmText = (TextView)findViewById(R.id.bpmLabel);
					bpmText.setText(_progress + " " + getString(R.string.label_bpm));
				}
			}
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				//if (_progress != 0)
				//	_player.setBpm(_progress);
			}
		});
		bpmBar.setProgress(30);
		
		//Set up drawer.
		setupDrawer(savedInstanceState);
		setupDirectory();
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
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        //This is where you can hide things on the action bar if needbe.
        boolean drawerOpen = _drawerLayout.isDrawerOpen(_drawerList);
        return super.onPrepareOptionsMenu(menu);
    }
    
    private void setupDrawer(Bundle savedInstanceState)
    {
    	_drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		_drawerList = (ListView) findViewById(R.id.left_drawer);

		//Set a custom shadow that overlays the main content when the drawer opens
        _drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        
        //Set up the drawer's list view with the instrument list and click listener
        _drawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, _instrumentManager.getNames()));
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
            selectItem(0, false);
    }
    
    private void setupDirectory()
    {
    	//Create fileDirector if loaded on first attempt
    	File folder = new File(Environment.getExternalStorageDirectory() +File.separator + "swifTone");
 
 		if (!folder.exists())
 		{ //Make Directory if it does not exist
 
 			Global.firstLoadTime = true;
 			onAboutAction();
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
	 * @param position The index of the item to select.
	 * @param silent Whether or not to play the instrument's sound.
	 */
	private void selectItem(int position, boolean playSound)
	{
		//Record new sound option.
		if (position == _recordNewIndex)
		{
			onPreRecording();
		}
		else		
		{
			int soundID = _instrumentManager.getID(position);
			_player.setInstrument(soundID);
			
			if (playSound && !_player.isPlaying())
			{
				_soundPlayer.demo(soundID);
				/*new AlertDialog.Builder(this)
					.setTitle("Woah!!")
				    .setMessage("You pressed " + position)
				    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
				    {
				        public void onClick(DialogInterface dialog, int which)
				        { 
				            //Something
				        }
				    })
				    .show();*/
			}
		}
	}
	
	/**
	 * Callback function for when an item is chosen from the drawer.
	 * @param position The index of the item to select.
	 */
	private void selectItem(int position)
	{
		selectItem(position, true);
	}
	
	/**
	 * Callback function for when items in the menu are selected.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
	    switch (item.getItemId())
	    {
	    	case R.id.action_new:
	    		onClearButton();
	    		return true;
	        /*case R.id.action_import:
	        	onImportAction();
	        	return true;
	        case R.id.action_export:
	        	onExportAction();
	        	return true;
	        case R.id.action_changeSignature:
	        	onChangeSignature();
	        	return true;*/
	        case R.id.action_changeLength:
	        	onChangeNoteLengthButton();
	        	return true;
	        //case R.id.action_settings:
	        //	onSettingsAction();
	        //    return true;
	        case R.id.action_help:
	        	onHelpAction();
	            return true;
	        case R.id.action_about:
	        	onAboutAction();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	/**
	 * This is the method called by java.util.Timer.
	 */
	public void run()
	{
		_player.update(FRAME_LENGTH_FLOAT);
		_soundRecorder.update(FRAME_LENGTH_FLOAT);
		
		//Ready for recording timer.
		if (_isReadyingRecorder)
		{
			_readyTimer += FRAME_LENGTH_FLOAT;
			if (_readyTimer >= RECORDING_READY_DURATION)
			{
				_readyTimer = 0.0f;
				_isReadyingRecorder = false;
				onStartRecording();
			}
		}
		
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
			Display.setScreen(0);
			
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
	 * Event called when the record button is clicked.
	 * @param view The view that was clicked.
	 */
	public void onRecordButton(View view)
	{
		//Confirm action
		
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
	/*public void stopRecording()
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
 	}*/
	
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
	//public void onBackButton(View view)
	//{
	//	Display.scoreBack();
	//}
	
	/**
	 * Event called when the Next button is pressed
	 * @param view The view that was clicked.
	 */
	//public void onNextButton(View view)
	//{
	//	Display.scoreNext();
	//}
	
	/**
	 * Event called when the Clear button is pressed
	 */
	public void onClearButton()
	{
		sharedStopStuff();
		new AlertDialog.Builder(this)
			.setTitle(R.string.popup_newTitle)
		    .setMessage(R.string.popup_newText)
		    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
		    {
		        public void onClick(DialogInterface dialog, int which)
		        { 
		        	Display.clear();
		        	Display.setScreen(0);
		        }
		    })
		    .setNegativeButton(android.R.string.no, null)
		    .show();
	}
	
	/**
	 * Event called when the Change length button is pressed
	 */
	public void onChangeNoteLengthButton()
	{
		Display.noteLengthMenu();
	}
	
	public void onChangeSignature()
	{
		new AlertDialog.Builder(this)
	    .setTitle("Choose signature")
	    .setItems(Display.getSignatures(), new DialogInterface.OnClickListener()
	    {
			public void onClick(DialogInterface dialog, int which)
			{
				if(!(which == 1 || which == 9))
				{
					Display.setSignature(which);
					
					//Big ugly switch statement to set the key signature.
					int root;
					int key = KeySignature.KEY_MAJOR;
					boolean hasSharps;
					if (which > 9)
						hasSharps = false;
					else
						hasSharps = true;
					
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
					case 7:
						root = KeySignature.ROOT_F_SHARP;
						break;
					case 8:
						root = KeySignature.ROOT_C_SHARP;
						break;
					case 10:
						root = KeySignature.ROOT_F;
						break;
					case 11:
						root = KeySignature.ROOT_A_SHARP;
						break;
					case 12:
						root = KeySignature.ROOT_D_SHARP;
						break;
					case 13:
						root = KeySignature.ROOT_C_SHARP;
						break;
					case 14:
						root = KeySignature.ROOT_F_SHARP;
						break;
					case 15:
						root = KeySignature.ROOT_G_SHARP;
						break;
					default:
						root = KeySignature.ROOT_C;
						break;
					}
					
					_song.getScore(0).getKeySignature().set(root, key, hasSharps);
				}
			}
	    })
	     .show();
	}
	
	public void onEraserButton(View view)
	{
		Display.setEraser(!Display.getEraser());
		Button button = (Button)view;
		if(Display.getEraser())
		{
			button.setTypeface(null, Typeface.BOLD);
		}
		else
		{
			button.setTypeface(null, Typeface.NORMAL);
		}
	}
	
	private void onSettingsAction()
	{
	}
	
	private void onAboutAction()
	{
		new AlertDialog.Builder(this)
			.setTitle("SwifTone v1.0")
		    .setMessage("SwifTone Music Composer\n\n" +
		    			"By Sean Latham, Josh Hadley and Shavarsh Movsesyan\n\n" +
		    			"\u00a9 2014")
		    .setNeutralButton(android.R.string.ok, null)
		    .show();
	}
	
	private void onHelpAction()
	{
		View image = findViewById(R.id.help_image);
		image.setVisibility(View.VISIBLE);
	}
	
	private void onImportAction()
	{
		_song = SongImporter.songImport("aSong.sts");
	}
	
	private void onExportAction()
	{
		SongExporter.songExport("aSong.sts", _song);
	}
	
	private void onPreRecording()
	{
		Log.d("SAVE", "pre recording");
		//Stop the player.
		_player.stop();
		
		//Create and display ready dialog.
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.popup_recording_title))
	    	.setMessage(getString(R.string.popup_recording_ready));
		_recordingDialog = builder.create();
		_recordingDialog.setCancelable(false);
		_recordingDialog.show();
		
		_isReadyingRecorder = true;
	}
	
	public void onStartRecording()
	{
		Log.d("SAVE", "start recording");
		//Re-purpose recording dialog so that it shows recording messages.
		/*_recordingDialog.setMessage(getString(R.string.popup_recording_recording));
		_recordingDialog.setButton(DialogInterface.BUTTON_POSITIVE,
				getString(R.string.popup_recording_stop),
				new DialogInterface.OnClickListener()
			    {
			        public void onClick(DialogInterface dialog, int which)
			        { 
			        	_soundRecorder.stop();
			        	onFinishRecording();
			        }
			    });*/
		
		runOnUiThread(new Runnable()
		{
			public void run()
			{
				_recordingDialog.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(that);
				builder.setTitle(getString(R.string.popup_recording_title))
			    	.setMessage(getString(R.string.popup_recording_recording))
			    	.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener()
				    {
				        public void onClick(DialogInterface dialog, int which)
				        { 
				        	_soundRecorder.stop();
				        	onFinishRecording();
				        }
				    });
				_recordingDialog = builder.create();
				_recordingDialog.setCancelable(false);
				_recordingDialog.show();
			}
		});
		//Create and display recording dialog.
		
		
		//Make callback function object.
		StopRecording stopRecording = new StopRecording(this);
		
		//Start recording.
		_soundRecorder.start("stTemp", stopRecording);
	}
	
	public void onFinishRecording()
	{
		//Cancel recording dialog.
		Log.d("SAVE", "finish recording");
		//_recordingDialog.dismiss();
		//_recordingDialog = null;
		
		//Remove record new item.
		_instrumentManager.remove(_recordNewIndex);
		
		//Add new items to list.
		int newItem = _instrumentManager.add(_soundRecorder.getFilePath(),
				getString(R.string.inst_recorded_sound) + _soundRecorder.getNumberOfRecordings());
		_recordNewIndex = _instrumentManager.add(getString(R.string.inst_new_recording));
		
		//Update the drawer.
		runOnUiThread(new Runnable()
		{
			public void run()
			{
				_drawerList.setAdapter(new ArrayAdapter<String>(that,
		                R.layout.drawer_list_item, _instrumentManager.getNames()));
			}
		});
		selectItem(newItem, false);
	}
	
	public void onHelpImageClick(View view)
	{
		view.setVisibility(View.INVISIBLE);
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
		_mainActivity.onFinishRecording();
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