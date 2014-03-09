package mcapp;

import info.ipidev.mcapp.R;

import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Responsible for drawing the UI.
 * @author Shavarsh, Sean
 *
 */
public class Display extends View 
{
	//It would be nice if this and the Score were united some day.
	//List with note coordinates and note length
	static Vector<Pair<Integer, Integer, Integer>> _notePositions = new Vector<Pair<Integer, Integer, Integer>>();
	Canvas _canvas;
	Paint _paint = new Paint();
	
	//Screen measurements
	DisplayMetrics _metrics = getResources().getDisplayMetrics();
	int _screenWidth = _metrics.widthPixels;
	int _screenHeight = _metrics.heightPixels;
	int _screenCenterX = _screenWidth / 2;
	int _screenCenterY = _screenHeight / 2;
	
	//Bitmap initialisation
	Bitmap _scaledNote = getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.note), 0, (int)(_screenHeight / 4), true, false);	
	int _scaledNoteHeight = (int)(_scaledNote.getHeight() * 0.24);
	
	Bitmap _score = BitmapFactory.decodeResource(getResources(), R.drawable.score);
	int scaledScoreY = (_score.getHeight() * 4 * _scaledNoteHeight) / (_score.getHeight() / 2);
	Bitmap _scaledScore = getResizedBitmap(_score, (int)(_screenWidth * 0.9), scaledScoreY, false, false);
	
	Bitmap _scaledKey = getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.key_sol), 0, _scaledScore.getHeight(), true, false);
	
	Bitmap _scaledBar = getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bar), 0, _scaledScore.getHeight() / 2, true, false);
	//Bar position
	int _barX;
	int _barY;
	
	Bitmap _indicator = BitmapFactory.decodeResource(getResources(), R.drawable.indicator);
	Bitmap _scaledIndicator = getResizedBitmap(_indicator, _indicator.getWidth(), _scaledScore.getHeight(), false, false);
	
	Bitmap _scaledMenu = getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.note_selection), 0, (int)(_screenHeight / 4), true, false);
	
	//Temporary note drawing variables
	int _column = 1000;
	int _row = 1000;
	
	//Note positioning variables
	int _eventDisplacementX = 150;
	int _horStep = _scaledNote.getWidth();
	int _vertStep = _scaledNoteHeight / 2;
	int _upperDistance = _screenCenterY - (int)(_scaledScore.getHeight() * 0.8) - _scaledNoteHeight / 2;
	int _leftDistance = _screenWidth / 30;
	
	//Touch event coordinates
	float _eventX;
	float _eventY;
	
	//Indicator variables
	float _defaultIndicatorPositionX = _horStep / 2;
	float _indicatorPositionX = _defaultIndicatorPositionX;
	float _indicatorPositionY = (float)(_screenHeight * 0.13);
	static boolean _toBeReset = false; 
	static float _currentBeat = 0;
	static float _beatProgress = 0;
	static boolean _isPlaying = false;
	
	//Menu position
	float _menuX = _screenCenterX - (float)(_scaledMenu.getWidth() * 0.6);
	float _menuY = _screenCenterY - (float)(_scaledMenu.getHeight());
	
	//the indicator of the "screen" currently viewed
	static int _screenPosition = 0;
	
	//Flags for additional menu
	static boolean _isNoteChoosingActive = false;
	
	//Selected note
	static int _selectedNote = -1;
	
	//Flag for deletion. This allows the note to be marked 1st and deleted 2nd.
	boolean _toDelete = false;
	
	/**
	 * Reference to the current song.
	 */
	private static Song _song;
	
	/**
	 * Allows for conversion from the grid-based placing of notes to the pitches
	 * of the notes in the song.
	 */
	private static final int[] GRID_TO_PITCH =
	{
		/* A5 */ 9, 7, 5, 4, 2, 0, -1,
		/* A4 */ -3, -5, -7, -8, -10, -12, -13,
		/* A3 */ -15
	};
	
	//Getter for the _screenPosition
	public static int getScreen()
	{
		return _screenPosition;
	}
	
	//Constructors
	public Display(Context context) 
	{		
		super(context);   
    }
 
    public Display(Context context, AttributeSet attrs) 
    {    	
        super(context);
    }    
    
    /**
	 * Sets the song. In the future this will also need to display all of the
	 * notes. This has to be static because otherwise you get a null reference
	 * exception when the app is created.
	 * @param song
	 */
	public static void setSong(Song song)
	{
		_song = song;
	}
	
	/**
	 * Main draw function.
	 */
	@Override
    public void onDraw (Canvas canvas) 
    {		
		//Initialisation
		_canvas = canvas;
		
    	//Score draw
    	canvas.drawBitmap(_scaledScore, 0, _screenCenterY - (int)(_scaledScore.getHeight() * 0.8), null);
    	
    	//Key draw
    	if(_screenPosition == 0)
    	{
    		canvas.drawBitmap(_scaledKey, 0, _screenCenterY - (int)(_scaledKey.getHeight() * 0.8), null);
    	}
    	
    	//Bar draw
    	barDraw();
    	
    	//NoteDraw
    	noteDraw();
    	
    	//Temporary note draw if menu is not shown
    	if(!_isNoteChoosingActive)
    	{    		
    		temporaryNoteDraw();
    	}
    	//Draw menu
    	else
    	{
    		noteChooseMenuDraw();
    	}    	
    	
    	//Indicator draw
    	animateIndicator();
    	if(!(!_isPlaying && _screenPosition != 0))
    	{
    		indicatorDraw();
    	}
    	
    	//Issue a redraw
    	invalidate();
    }
    
	/**
	 * Touch event handling.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		Pair<Integer, Integer, Integer> tempPair;
		
		//Calculating horizontal and vertical displacement.
		_eventX = event.getX() - _eventDisplacementX;
	    _eventY = event.getY();
	    
	    int vertPosition =  ( (int)_eventY - _upperDistance ) / _vertStep;
	    if(vertPosition < 1)
		{
	    	vertPosition = 1;
		}
		if(vertPosition > 15)
		{
			vertPosition = 15;
		}
		int horPosition = ( (int)_eventX - _leftDistance ) / _horStep;
		if(horPosition < 1)
		{
			horPosition = 1;
		}
		if(horPosition > 8)
		{
			horPosition = 8;
		}  
	    _column = (int)(/*_leftDistance + */( _horStep * horPosition ) );
	    _row = (int)(_upperDistance + ( _vertStep * vertPosition ) );
	    
	    //Press down event
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			
		}
		//Drag event
		if(event.getAction() == MotionEvent.ACTION_MOVE)
		{
			//Log.d("ASD", String.valueOf(_eventX) + " " + String.valueOf(_eventY));			
		}
		//Release event
		if(event.getAction() == MotionEvent.ACTION_UP)
		{	
			//Note selection initial logic
			if(!_isNoteChoosingActive)
			{
			    //Create a temporary pair
			    tempPair = new Pair<Integer, Integer, Integer>(_column + ( _horStep * 4 * _screenPosition ), _row, 1);
			    //Check if there's a note on that location	    
			    if(_notePositions.contains(tempPair))
			    {
			    	if(_selectedNote == _notePositions.indexOf(tempPair))
			    	{
			    		_toDelete = true;				    
			    	}
			    	else
			    	{
			    		_selectedNote = _notePositions.indexOf(tempPair);
			    	}			    	
			    }			    		
			
			    //Draw note logic, if the note choosing menu is not active			    
			    //Check if there's already a note there			    
			    if(_notePositions.contains(tempPair))
			    {
			    	if(_toDelete)
			    	{
				    	_notePositions.remove(tempPair);
				    	_song.getScore(0).removeNote(horPosition - 1 + ( 4 * _screenPosition),
				    								 GRID_TO_PITCH[vertPosition - 1]);
				    	_selectedNote = -1;
				    	_toDelete = false;
			    	}
			    }
			    else
			    {
			    	_notePositions.add(tempPair);		 
			    	if (_song.getScore(0).addNote(horPosition - 1 + ( 4 * _screenPosition),
							 					  GRID_TO_PITCH[vertPosition - 1],
							 					  0))
			    		Log.d("PLAYA", "NOTE ADDING SUCCESSFUL!!!");
			    }			    
			}		
			//Choose note length if menu is active
			else
			{
				int correctX = (int)(_eventX + _eventDisplacementX);
				if(correctX > _menuX && correctX < _menuX + _scaledMenu.getWidth()
						&& _eventY > _menuY && _eventY < _menuY + _scaledMenu.getHeight())
				{					
					int length = (int)Math.pow(2, (correctX - (int)_menuX) / _scaledNote.getWidth());
					
					_notePositions.get(_selectedNote).setR(length);
					_isNoteChoosingActive = false;
				}
				else
				{
					_isNoteChoosingActive = false;
				}
			}
			_column = 1000;
			_row = 1000;
		}		
		
	    return true;
	}
	
	//Note drawing
	public void noteDraw()
	{		
		int position;
    	for(int i = 0; i < _notePositions.size(); i++)
    	{
    		Bitmap note = BitmapFactory.decodeResource(getResources(), R.drawable.note);
    		position = _notePositions.get(i).getM() / _vertStep;
    		
    		//Set bitmap & position
    		if(_notePositions.get(i).getR() != 1)
    		{
    			int noteLength = _notePositions.get(i).getR();
    			String noteName = "note_" + noteLength;
    			int resID = getResources().getIdentifier(noteName, "drawable", getContext().getPackageName());
    		    note  = BitmapFactory.decodeResource(getResources(), resID);
    		}
    		else
    		{
	    		switch(position)
	    		{
	    			case 4:
	    				note = BitmapFactory.decodeResource(getResources(), R.drawable.note_bottom);
	    				break;
	    			case 18:
	    				note = BitmapFactory.decodeResource(getResources(), R.drawable.note_top);
	    				break;
	    			case 5:
	    			case 17:
	    				note = BitmapFactory.decodeResource(getResources(), R.drawable.note_crossed);
	    				break;
	    			default:
	    				break;
	    		}
    		}
    		
    		Bitmap scaledNote = getResizedBitmap(note, 0, (int)(_screenHeight / 4), true, false);
    		int positionY = _notePositions.get(i).getM() - _scaledNote.getHeight() + _scaledNoteHeight;
    	
    		//Drawing
    		if(i == _selectedNote)
    		{
    			ColorFilter filter = new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
    	    	_paint.setColorFilter(filter);
    		}
    		else
    		{
    			_paint = new Paint();
    		}
       		if(_screenPosition == 0 && _notePositions.get(i).getL() <= _horStep * 8)
    		{
    			_canvas.drawBitmap(scaledNote, _notePositions.get(i).getL(), positionY, _paint);
    		}
    		else if(_notePositions.get(i).getL() > (/*(_horStep * 1 ) +*/ (_horStep * 4 * _screenPosition )) && _notePositions.get(i).getL() < (( _horStep * 9 ) + ( _horStep * 4 * _screenPosition )))
    		{
    			_canvas.drawBitmap(scaledNote, _notePositions.get(i).getL() - ( _horStep * 4 * _screenPosition ), positionY, _paint);
    		}
    	}
	}
	
	//Temporary note draw    
	public void temporaryNoteDraw()
	{
		Bitmap note = BitmapFactory.decodeResource(getResources(), R.drawable.note);
    	int position;
    	position = _row / _vertStep;
    	
    	switch(position)
    	{
    		case 4:
    			note = BitmapFactory.decodeResource(getResources(), R.drawable.note_bottom);
    			break;
    		case 18:
    			note = BitmapFactory.decodeResource(getResources(), R.drawable.note_top);
    			break;
    		case 5:
    		case 17:
    			note = BitmapFactory.decodeResource(getResources(), R.drawable.note_crossed);
    			break;
    		default:    			
    			break;
    	}
    	Bitmap scaledNote = getResizedBitmap(note, 0, (int)(_screenHeight / 4), true, false);
    	int positionY = _row - _scaledNote.getHeight() + _scaledNoteHeight;
    	
    	ColorFilter filter = new PorterDuffColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
    	_paint.setColorFilter(filter);    	
        _canvas.drawBitmap(scaledNote, _column, positionY, _paint);
	}
	
	//Indicator drawing
	public void indicatorDraw()
	{
		_canvas.drawBitmap(_scaledIndicator, _indicatorPositionX, _indicatorPositionY, null);
	}
	
	//Note choosing menu drawing
	public void noteChooseMenuDraw()
	{
		_canvas.drawBitmap(_scaledMenu, _menuX, _menuY, null);
	}
	
	//Move indicator
	public static void passPlayProgress(float currentBeat, float beatProgress)
	{
		currentBeat++;
		_currentBeat = currentBeat;
		_beatProgress = beatProgress;
	}
	
	//This is how the Display class knows whether the player is currently active
	public static void passPlayerStatus(boolean status)
	{
		_isPlaying = status;
	}
	
	//Indicator movement
	public void animateIndicator()
	{
		//Reset indicator
    	if(_toBeReset)
    	{
    		_indicatorPositionX = _defaultIndicatorPositionX;
    		_toBeReset = false;
    	}
    	if(_isPlaying)
    	{
    		_indicatorPositionX = _defaultIndicatorPositionX + (_horStep * _currentBeat) + (_horStep * _beatProgress);
    	}
	}
	
	//Resets the indicator's position when the player is stopped
	public static void resetIndicatorPosition()
	{
		_toBeReset = true;
	}
	
	//Called when the score is "scrolled"
	public static void scoreBack()
	{
		_screenPosition--;
	}
	
	//Called when the score is "scrolled"
	public static void scoreNext()
	{
		_screenPosition++;
	}
	
	//Responsible for displaying the note length menu
	public static void noteLengthMenu()
	{
		//If there's not a selected note don't show the menu
		if(_selectedNote != -1)
		{
			_isNoteChoosingActive = !_isNoteChoosingActive;
		}
	}
	
	//Changes the score bitmap
	public void barDraw()
	{
		if(_screenPosition < 0)
		{			
			_screenPosition = 0;
		}		
		if(_screenPosition % 2 != 0)
		{
			_barX = (_scaledScore.getWidth() - (int)((double)(_scaledScore.getWidth() * 0.46)));
			_barY = _screenCenterY - (int)((double)(_scaledScore.getHeight() * 0.8) / 1.48);
		}
		else
		{
			_barX = 0;
			_barY = _screenCenterY - (int)((double)(_scaledScore.getHeight() * 0.8) / 1.48);			
			_canvas.drawBitmap(_scaledBar, _barX, _barY, null);
			_barX = _scaledScore.getWidth() - _scaledBar.getWidth();
			_barY = _screenCenterY - (int)((double)(_scaledScore.getHeight() * 0.8) / 1.48);
			
		}
		_canvas.drawBitmap(_scaledBar, _barX, _barY, null);
	}
	
	//Clears all notes
	public static void clear()
	{
		if(!_isNoteChoosingActive)
		{
			_song.getScore(0).clearScore();
			_notePositions.clear();
			_selectedNote = -1;
		}
	}
	
	//Method returns resized bitmap
	
	public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight, boolean widthToHeightRatio, boolean heightToWidthRatio)
	{		 
		int width = bm.getWidth();		 
		int height = bm.getHeight();
		float scaleWidth = 0;		 
		float scaleHeight = 0;
		if(widthToHeightRatio && heightToWidthRatio)
		{
			Log.d("ASD", "Bitmap scaling is failing!");
		}
		else if(widthToHeightRatio)
		{
			scaleHeight = ((float) newHeight) / height;
			scaleWidth = scaleHeight;
		}
		else if(heightToWidthRatio)
		{
			scaleWidth = ((float) newWidth) / width;
			scaleHeight = scaleWidth;
		}
		else
		{
			scaleWidth = ((float) newWidth) / width;
			scaleHeight = ((float) newHeight) / height;
		}		
		 
		// CREATE A MATRIX FOR THE MANIPULATION		 
		Matrix matrix = new Matrix();
		 
		// RESIZE THE BIT MAP		 
		matrix.postScale(scaleWidth, scaleHeight);
		 
		// RECREATE THE NEW BITMAP		 
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
		 
		return resizedBitmap;		 
	}
}