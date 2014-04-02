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
	public static final int BEATS_PER_SCORE = 32;
	
	/**
	 * Allows for conversion from the grid-based placing of notes to the pitches
	 * of the notes in the song.
	 */
	public static final int[] GRID_TO_PITCH =
	{
		/* B5 */ 11, 9, 7, 5, 4, 2, 0,
		/* B4 */ -1, -3, -5, -7, -8, -10, -12, 
		/* B3 */ -13,
	};
	
	/**
	 * The ID of the piano sample. Very very temporary.
	 */
	public static int pianoID = 0;
	
	/**
	 * The ID of the recorded sound. Just as temporary.
	 */
	public static int recordedID = 0;
	
	/**
	 * Whether or not to use the recorded sound.
	 */
	public static boolean useRecordedSound = false;
	
	
	private Global()
	{
		//Stops construction.
	}
}