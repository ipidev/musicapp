package mcapp;

import java.util.LinkedList;

/**
 * Responsible for loading in instruments and storing their sound IDs.
 * @author Sean
 */
public class InstrumentManager
{
	/**
	 * SoundPlayer reference.
	 */
	SoundPlayer _soundPlayer = null;
	
	/**
	 * Collection of the instrument names.
	 */
	LinkedList<String> _instrumentNames = null;
	
	/**
	 * Collection of the instrument sound IDs.
	 */
	LinkedList<Integer> _instrumentIDs = null;
	
	/**
	 * Collection of the eraser sound IDs.
	 */
	LinkedList<Integer> _eraserIDs = null;
	
	
	public InstrumentManager(SoundPlayer soundPlayer)
	{
		_soundPlayer = soundPlayer;
		_instrumentNames = new LinkedList<String>();
		_instrumentIDs = new LinkedList<Integer>();
		_eraserIDs = new LinkedList<Integer>();
	}
	
	/**
	 * Loads a new instrument.
	 * @param resourceID The resource ID of the sound to load.
	 * @param name The name of the instrument.
	 * @return The index of the newly added sound.
	 */
	public int add(int resourceID, String name)
	{
		_instrumentNames.add(name);
		_instrumentIDs.add(_soundPlayer.load(resourceID));
		return _instrumentIDs.size() - 1;
	}
	
	/**
	 * Loads a new instrument.
	 * @param path The full path of the sound to load.
	 * @param name The name of the instrument.
	 * @return The index of the newly added sound.
	 */
	public int add(String path, String name)
	{
		_instrumentNames.add(name);
		_instrumentIDs.add(_soundPlayer.load(path));
		return _instrumentIDs.size() - 1;
	}
	
	/**
	 * Creates a new item in the instruments list.
	 * @param name The name of the item.
	 * @return The index of the newly added item.
	 */
	public int add(String name)
	{
		_instrumentNames.add(name);
		_instrumentIDs.add(-1);
		return _instrumentIDs.size() - 1;
	}
	
	/**
	 * Returns the ID of the instrument at the given index.
	 * @param index The instrument's index.
	 * @return The sound's ID for use with the SoundPlayer.
	 */
	public int getID(int index)
	{
		isIndexOutOfRange(index);
		return _instrumentIDs.get(index);
	}
	
	/**
	 * Returns the name of the instrument at the given index.
	 * @param index The instrument's index.
	 * @return The instrument's name.
	 */
	public String getName(int index)
	{
		isIndexOutOfRange(index);
		return _instrumentNames.get(index);
	}
	
	/**
	 * Returns the entire collection of instrument names.
	 * @return I'm not lying.
	 */
	public String[] getNames()
	{
		return _instrumentNames.toArray(new String[_instrumentNames.size()]);
	}
	
	/**
	 * Gets the index of the instrument with the given name.
	 * @param name The name of the instrument.
	 * @return Returns the index of the instrument, or -1 if it was not found.
	 */
	public int indexOf(String name)
	{
		for (int i = 0; i < _instrumentNames.size(); i++)
		{
			if (name == _instrumentNames.get(i))
				return i;
		}
		
		return -1;
	}
	
	/**
	 * Removes the instrument with the given index.
	 * @param index
	 */
	public void remove(int index)
	{
		isIndexOutOfRange(index);
		_soundPlayer.unload(_instrumentIDs.get(index));
		_instrumentNames.remove(index);
		_instrumentIDs.remove(index);
	}
	
	/**
	 * Loads a new eraser sound.
	 * @param resourceID The resource ID of the sound to load.
	 */
	public void addEraserSound(int resourceID)
	{
		_eraserIDs.add(_soundPlayer.load(resourceID));
	}
	
	/**
	 * Randomly returns one of the eraser sounds.
	 * @return A sound ID to an eraser sound effect.
	 */
	public int getEraserID()
	{
		return _eraserIDs.get(Global.randomInt(0, _eraserIDs.size() - 1));
	}
	
	private void isIndexOutOfRange(int index)
	{
		if (index < 0 || index >= _instrumentNames.size())
			throw new IndexOutOfBoundsException();
	}
}
