package mcapp;

import java.util.Iterator;
import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.Log;

/**
 * Class that is responsible for loading and maintaining sounds and playing
 * them at various pitches. This class is implemented for use in an Android
 * activity - please create one upon instantiation of the activity.
 *
 * @author Sean
 *
 */
public class SoundPlayer
{
	/**
	 * Responsible for playing sounds.
	 */
	private SoundPool _soundPool = null;
	
	/**
	 * Reference to the activity this belongs to.
	 */
	private Activity _activity = null;
	
	/**
	 * Whether the object has been initialised or not.
	 */
	private boolean _isReady = false;
	
	
	/**
	 * Conversion table from notes to playback rate. This table assumes that
	 * the sample is at middle-C (C5).
	 */
	private static final float[] NOTE_FREQUENCIES =
	{
		/* C 4 */ 0.50001f, 0.52973f, 0.56122f, 0.59461f, 0.62997f, 0.66742f,
		/* F#4 */ 0.70710f, 0.74916f, 0.79369f, 0.84090f, 0.89089f, 0.94387f,
		/* C 5 */ 1.00000f, 1.05947f, 1.12247f, 1.18920f, 1.25991f, 1.33485f,
		/* F#5 */ 1.41422f, 1.49831f, 1.58741f, 1.68180f, 1.78181f, 1.88776f,
		/* C 6 */ 2.00000f
	};
	
	/**
	 * The list of IDs to samples that are currently loaded into the SoundPool.
	 */
	private LinkedList<Integer> _loadedSamples;
	
	
	/**
	 * Instantiates and initialises the sound player.
	 * @param activity The activity that this sound player belongs to.
	 */
	public SoundPlayer(Activity activity)
	{
		_activity = activity;
		
		//Let the hardware buttons control the volume.
		_activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		//Create the SoundPool.
		_soundPool = new SoundPool(Global.MAX_POLYPHONY,
								   AudioManager.STREAM_MUSIC, 0);
		
		//Callback function for when the SoundPool is created.
		_soundPool.setOnLoadCompleteListener(new OnLoadCompleteListener()
		{
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId,
					int status)
			{
				_isReady = true;
			}
		});
		
		_loadedSamples = new LinkedList<Integer>();
	}
	
	/**
	 * Gets whether or not the sound player is fully initialised.
	 * @return True if the sound player is ready, false otherwise.
	 */
	public boolean isReady()
	{
		return _isReady;
	}
	
	/**
	 * Loads the given sample into memory.
	 * @param resID The resource ID of the sample to load.
	 * @return The ID of the sample that was loaded - used to play or unload
	 * the sample.
	 */
	public int load(int resID)
	{
		int id = _soundPool.load(_activity, resID, 1);
		_loadedSamples.add(id);
		return id;
	}
	
	/**
	 * Loads the given sample into memory.
	 * @param path The path to the sample to load.
	 * @return The ID of the sample that was loaded - used to play or unload
	 * the sample.
	 */
	public int load(String path)
	{
		int id = _soundPool.load(path, 1);
		_loadedSamples.add(id);
		return id;
	}
	
	/**
	 * Plays a sample with the given settings.
	 * @param sampleID The ID of the sample to play (which was returned from a
	 * previous call to load()).
	 * @param pitch The number of semitones above or below middle-C. This value
	 * must be between -12 (C4) and 12 (C5).
	 * @param volume The volume of the sample. This value must be between 0.0
	 * and 1.0.
	 * @param panning The panning of the sample. This is unused, so use 0.0.
	 */
	public void play(int sampleID, int pitch, float volume, float panning)
	{
		//Calculate the volume to play the sample at.
        AudioManager am = (AudioManager)_activity.getSystemService(Context.AUDIO_SERVICE);
        float currentVolume = (float)am.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float)am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float finalVolume = (currentVolume / maxVolume) * volume;
        
        //Play!!
        _soundPool.play(sampleID, finalVolume, finalVolume, 1, 0,
        				NOTE_FREQUENCIES[pitch + 12]);
	}
	
	/**
	 * Plays a sample with the given settings.
	 * @param sampleID The ID of the sample to play (which was returned from a
	 * previous call to load()).
	 * @param pitch The number of semitones above or below middle-C. This value
	 * must be between -12 (C4) and 12 (C5).
	 */
	public void play(int sampleID, int pitch)
	{
		play(sampleID, pitch, 1.0f, 0.0f);
		Log.d("PLAYA", "PLAYING at " + pitch);
	}
	
	/**
	 * Unloads a sample with the given ID (which was returned from a previous
	 * call to load()).
	 * @param sampleID The ID of the sample to unload.
	 * @return False if the sample was already unloaded (or never loaded), true
	 * otherwise.
	 */
	public boolean unload(int sampleID)
	{
		//Search for and remove the ID.
		Iterator<Integer> it = _loadedSamples.iterator();
		while (it.hasNext())
		{
		   if (it.next() == sampleID)
		   {
			   it.remove();
			   /*
			    * Modifying a collection during iteration is legal if and only
			    * if it the loop is broken immediately!! Don't change this!!
			    */
			   break;
		   }
		}
		
		return _soundPool.unload(sampleID);
	}
	
	/**
	 * Unloads all loaded samples.
	 */
	public void unload()
	{	
		for (int i : _loadedSamples)
			_soundPool.unload(i);
		
		_loadedSamples.clear();
	}

	/**
	 * Destroys the sound player and releases all samples.
	 */
	public void release()
	{
		_soundPool.release();
		_activity = null;
		_loadedSamples = null;
	}
}
