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
	 * Maximum length of recorded sounds in seconds.
	 */
	public static final float MAX_RECORDING_LENGTH = 2.0f;
	
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