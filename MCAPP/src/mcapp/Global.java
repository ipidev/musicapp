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
	 * of the corresponding MIDI note.
	 */
	public static final int[] GRID_TO_PITCH =
	{
		/* B5 */ 71, 69, 67, 65, 64, 62, 60,
		/* B4 */ 59, 57, 55, 53, 52, 50, 48, 
		/* B3 */ 47
	};
	
	/**
	 * The MIDI value for middle C (C5).
	 */
	public static final int MIDDLE_C = 60;
	
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
	
	/**
	 * * Check for first time application login
  	 */
  	public static boolean firstLoadTime = false;
  	
 	/**
 	 * Application Directory
 	 */
 	public static String fileDirectory = "default";
	
	/**
	 * Returns the positive modulo of two numbers.
	 */
	public static int dumbModuloPoopCheck(int dividend, int divisor)
	{
		if (dividend >= 0)
			return dividend % divisor;
		else
			return divisor + (dividend % divisor);
	}
	
	/**
	 * Returns a random integer within the given range, inclusive.
	 * @param min The minimum number.
	 * @param max The maximum number.
	 * @return A number within the specified range.
	 */
	public static int randomInt(int min, int max)
	{
		return min + (int)(Math.random() * ((max - min) + 1));
	}
	
	private Global()
	{
		//Stops construction.
	}
}