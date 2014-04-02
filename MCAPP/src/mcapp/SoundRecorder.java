package mcapp;

import java.io.IOException;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

/**
 * Records a sound from the microphone and saves it to the given file. This is
 * a static class.
 * @author Sean
 *
 */
public class SoundRecorder
{
	/**
 	 * Interface used for callback function.
 	 */
 	public interface SoundRecorderCallback
 	{
 		void callback();
 	}
	
	/**
	 * The object that handles sound recording.
	 */
	private MediaRecorder _recorder = null;
	
	/**
	 * Whether or not the SoundRecorder is recording.
	 */
	private boolean _isRecording = false;
	
	/**
 	 * How long the sound recording is.
 	 */
 	private float _timer = 0.0f;
	
	/**
	 * Absolute path of the file that has been recorded to.
	 */
	private String _filePath = "";
	
	/**
 	 * Holds callback object.
 	 */
 	private SoundRecorderCallback _callback = null;
 	
	
	public SoundRecorder()
	{
	}
	
	/**
	 * Accessor for the recording state.
	 * @return True if the SoundRecorder is recording, false otherwise.
	 */
	public boolean isRecording()
	{
		return _isRecording;
	}
	
	/**
	 * Accessor for the recording state.
	 * @return True if the SoundRecorder has finished recording to a file, false
	 * otherwise.
	 */
	public boolean hasFinishedRecording()
	{
		return _filePath != "" && !_isRecording;
	}
	
	/**
	 * Accessor for the file that has been recorded to.
	 * @return The absolute path of the file that has been recorded to.
	 */
	public String getFilePath()
	{
		return _filePath;
	}
	
	/**
	 * Starts recording data from the microphone, saving it as a .3GP file in
	 * the working directory..
	 * @param fileName The name of the file. No leading slashes and no
	 * trailing .3gp, please.
	 */
	public void start(String fileName, SoundRecorderCallback callback)
	{
		//Throw exception if we're already recording.
		if (_isRecording)
			throw new IllegalStateException("SoundRecorder started when " +
											"already recording.");
		
		_filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
		_filePath += "/" + fileName + ".3gp";
		Log.d("MCAPP", "filePath: " + _filePath);
		
		_recorder = new MediaRecorder();
		_recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		_recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		_recorder.setOutputFile(_filePath);
		_recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try
        {
        	_recorder.prepare();
        }
        catch (IOException e)
        {
            Log.e("MCAPP", "prepare() failed");
        }

        _recorder.start();
        _isRecording = true;
	}
	
	public void update(float deltaTime)
 	{
 		if (_isRecording)
 		{
 			_timer += deltaTime;
 			
 			if (_timer >= Global.MAX_RECORDING_LENGTH)
 				stop();
 		}
 	}
	
	
	/**
	 * Stops recording.
	 */
	public void stop()
	{
		//Throw exception if we're not recording.
		if (!_isRecording)
			throw new IllegalStateException("SoundRecorder stopped when not " +
											"recording.");
		
		_recorder.stop();
		_recorder.release();
		_recorder = null;
		_isRecording = false;
		_timer = 0.0f;
		 		
 		if (_callback != null)
 			_callback.callback();
 		_callback = null;
	}
}
