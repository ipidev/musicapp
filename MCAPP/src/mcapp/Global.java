package mcapp;

/**
 * Stores global constants.
 * @author Sean
 *
 */
public class Global
{
	/**
	 * The maximum number of sounds that can be played at one time.
	 */
	public static final int MAX_POLYPHONY = 3;
	
	/**
	 * The maximum number of beats permitted for each Score instance.
	 */
	public static final int BEATS_PER_SCORE = 16;
	
	/**
	 * The ID of the piano sample. Very very temporary.
	 */
	public static int pianoID;
	
	private Global()
	{
		//Stops construction.
	}
}