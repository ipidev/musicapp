package mcapp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import android.os.Environment;
import android.util.Log;

/**
 * Exports songs.
 * @author Josh
 *
 */
public class SongExporter
{
	private static final int NOTE_SIZE = 2;
	
	/**
	 * Exports the Song Object to a file for storage.
	 * @param fileName Name of File
	 * @param song Current Song instance
	 */
	public static void songExport(String fileName, Song song)
	{
		//Creates File Path for Song
		String _filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
		_filePath += "/" + fileName + ".txt";
		Log.d("SAVE", "Saving " + _filePath);

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
		    if (beat == null)
		    	continue;
		    		    
		    Note[] notes = beat.getNotes();

		    int j = 0;

		    //Converts Note Information into Bytes
		    for (Note n : notes)
		    {
		    	if (n == null)
		    		continue;
		    	
		    	Log.d("SAVE", "Note " + j + "; " + n);
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
	
	private byte[] outputBeat(Beat beat)
	{
		byte[] output = new byte[Global.MAX_POLYPHONY * NOTE_SIZE];
		
		//If beat is null, just fill the output array with zeros.
		if (beat == null)
		{
			for (int i = 0; i < output.length; i++)
				output[i] = 0;
			return output;
		}
		
		//Otherwise get the data of each note from the beat and fill the array
		//with that.
		for (int i = 0; i < Global.MAX_POLYPHONY; i++)
		{
			Note note = beat.getNote(i);
			
			if (note == null)
			{
				for (int j = 0; j < NOTE_SIZE; j++)
					output[(i * NOTE_SIZE) + j] = 0;
			}
			else
			{
				output[(i * NOTE_SIZE)] = (byte)note.getPitch();
				output[(i * NOTE_SIZE) + 1] = (byte)note.getInstrument();
			}
		}
		
		return output;
	}

}