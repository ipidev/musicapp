package score.Objects;

//Class dedicated to storing individual notes of sound. Stores note name, note sound and note description.
public class Note 
{
	//Member Variables
	float _pitch;
	byte[] _sound;
	String _key;
	
	//Default Constructor
	public Note ()
	{
		_sound = null;
		_pitch = 0.0f;
		_key = "Default";
	}
	
	//Constructor for Importing
	public Note (byte[] sound, float pitch, String key)
	{
		_sound = sound;
		_pitch = pitch;
		_key = key;
	}
	
	//Setter for Note instance
	public void SetNote(byte[] sound, float pitch, String key)
	{
		_sound = sound;
		_pitch = pitch;
		_key = key;
	}
	
	public float GetPitch()
	{
		return _pitch;
	}
	
	public String GetKey()
	{
		return _key;
	}
	
	public void SetPitch(float pitch)
	{
		_pitch = pitch;
	}
	
	public void SetKey(String key)
	{
		_key = key;
	}
}
