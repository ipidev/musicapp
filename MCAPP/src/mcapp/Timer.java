package mcapp;

/**
 * Responsible for keeping track of the time between each update step.
 * @author Sean
 *
 */
public class Timer
{
	/**
	 * The time at which the current clock cycle was started.
	 */
	private long _startTime = 0;
	
	/**
	 * The difference in time between when the clock was started and stopped.
	 */
	private long _deltaTime = 0;
	
	/**
	 * Whether or not the clock has started.
	 */
	private boolean _hasStarted = false;
	
	
	public Timer()
	{
	}
	
	/**
	 * Whether or not the timer has been started.
	 * @return True if the timer has been started, false otherwise.
	 */
	public boolean hasStarted()
	{
		return _hasStarted;
	}
	
	/**
	 * Starts the timer.
	 */
	public void start()
	{
		_startTime = System.nanoTime();
		_hasStarted = true;
	}
	
	/**
	 * Stops the timer, calculating the delta time.
	 */
	public void stop()
	{
		_deltaTime = System.nanoTime() - _startTime;
		_hasStarted = false;
	}
	
	/**
	 * Accessor for the delta time.
	 * @return Returns the amount of time that the previous update loop took, in
	 * seconds.
	 */
	public float deltaTime()
	{
		return _deltaTime / 1000000000.0f;
	}
}
