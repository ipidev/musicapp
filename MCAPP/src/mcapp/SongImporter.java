package mcapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.os.Environment;

/**
 * Imports songs.
 * @author Josh
 *
 */
public class SongImporter
{
	/**
	 * Import Data from a specified File path. Creates a Song instance loaded with the imported data
	 * @param fileName Name of Desired File
	 * @return Constructed Song Instance
	 */
	public static Song songImport(String fileName)
	{
		Song tSong = new Song();

		//Creates File Path for Song
		String _filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
		_filePath += "/" + fileName + ".txt";

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

		return tSong;
	}
}