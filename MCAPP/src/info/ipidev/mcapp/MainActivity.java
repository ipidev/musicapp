package info.ipidev.mcapp;

import info.ipidev.mcapp.R;
import java.util.Random;

import mcapp.SoundPlayer;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {
	
	//Plays sounds.
	private SoundPlayer _soundPlayer = null;
	
	//ID for the piano sound.
	private int _pianoID;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		//Android creation stuff.
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//Set up sound player and load the sample.
		_soundPlayer = new SoundPlayer(this);
		_pianoID = _soundPlayer.load(R.raw.piano);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	//Method that plays the sound at different pitches when a button is pressed.
	public void onKeyPress(View view)
	{
		switch (view.getId())
		{
		case R.id.keyC:
			_soundPlayer.play(_pianoID, 0);
			break;
		case R.id.keyD:
			_soundPlayer.play(_pianoID, 2);
			break;
		case R.id.keyE:
			_soundPlayer.play(_pianoID, 4);
			break;
		case R.id.keyF:
			_soundPlayer.play(_pianoID, 5);
			break;
		case R.id.keyG:
			_soundPlayer.play(_pianoID, 7);
			break;
		case R.id.keyA:
			_soundPlayer.play(_pianoID, 9);
			break;
		case R.id.keyB:
			_soundPlayer.play(_pianoID, 11);
			break;
		case R.id.keyCup:
			_soundPlayer.play(_pianoID, 12);
			break;
		default:
			throw new RuntimeException("Unknown button ID.");
		}
		
		//Debug
		findViewById(R.id.testText).setBackgroundColor(Color.argb(255, randInt(0, 255), randInt(0, 255), randInt(0, 255)));
	}
	
	public static int randInt(int min, int max)
	{
	    // Usually this can be a field rather than a method variable
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
}
