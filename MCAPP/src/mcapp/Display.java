package mcapp;

import info.ipidev.mcapp.R;

import java.util.Vector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Responsible for... Perhaps also give this a better name. - Done.
 * @author Shavarsh, Sean
 *
 */
public class Display extends View 
{
	//It would be nice if this and the Score were united some day.
	static Vector<Pair<Integer, Integer, Integer>> _notePositions = new Vector<Pair<Integer, Integer, Integer>>();
	Canvas _canvas;
	Paint _paint = new Paint();
	
	Bitmap _score = BitmapFactory.decodeResource(getResources(), R.drawable.score);
	Bitmap _note = BitmapFactory.decodeResource(getResources(), R.drawable.note);
	Bitmap _tempNote = BitmapFactory.decodeResource(getResources(), R.drawable.note_temp);
	Bitmap _indicator = BitmapFactory.decodeResource(getResources(), R.drawable.indicator);
	Bitmap _noteSelection = BitmapFactory.decodeResource(getResources(), R.drawable.note_selection);
			
	Rect _bottombar = new Rect(0, 335, 760, 405);
	
	int _column = 1000;
	int _row = 1000;
	int _horStep = -1;
	int _vertStep = -1;
	float _eventX = 1000;
	float _eventY = 1000;
	//Initial indicator position
	static float _indicatorPositionX = 60;
	float _indicatorPositionY = 30;
	//the indicator of the "screen" currently viewed
	static int _screenPosition = 0;
	//Flags for additional menu
	boolean _isNoteChoosingActive = false;
	boolean _isMenuActive = false;
	//Menu position
	float _menuX = 130;
	float _menuY = 100;
	//Targeted note
	int _noteToChange;
	
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
    	
		//Bottom bar draw
    	_paint.setColor(Color.BLUE);    	    	
    	canvas.drawRect(_bottombar, _paint);    	
    	
    	//Score draw
    	canvas.drawBitmap(_score, 0, 30, null);
    	
    	noteDraw();
    	if(!_isNoteChoosingActive)
    	{
    		temporaryNoteDraw();
    	}
    	else
    	{
    		noteChooseMenuDraw();
    	}    	
    	indicatorDraw();
    	changeScore();
    }
    
	/**
	 * Touch event handling.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		Pair<Integer, Integer, Integer> tempPair;
		//Calculating horizontal and vertical displacement.
		_eventX = event.getX() - 150;
	    _eventY = event.getY();
	    int upperDistance = 20;
	    int leftDistance = 80;
	    _vertStep = 18;
	    _horStep = 75;
	    int vertPosition =  ( (int)_eventY - upperDistance ) / _vertStep;
	    if(vertPosition < 1)
		{
	    	vertPosition = 1;
		}
		if(vertPosition > 15)
		{
			vertPosition = 15;
		}
		int horPosition = ( (int)_eventX - leftDistance ) / _horStep;
		if(horPosition < 1)
		{
			horPosition = 1;
		}
		if(horPosition > 10)
		{
			horPosition = 10;
		}  
	    _column = (int)(leftDistance + ( _horStep * horPosition ) );
	    _row = (int)(upperDistance + ( _vertStep * vertPosition ) );
	    
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
			_eventX = event.getX();
			_eventY = event.getY();
			if(_eventX > _menuX && _eventX < _menuX + _noteSelection.getWidth()
					&& _eventY > _menuY && _eventY < _menuY + _noteSelection.getHeight()
					&& _isNoteChoosingActive && _isMenuActive)
			{
				int length = 1;
				switch(( (int)_eventX - (int)_menuX ) / 83) // 83 needs to be stored somewhere! Fix when addressing scaling
				{
					case 0:
						length = 2;
						break;
					case 1:
						length = 4;
						break;
					case 2:
						length = 8;
						break;
					case 3:
						length = 16;
						break;
					case 4:
						length = 32;
						break;
					case 5:
						length = 64;
						break;
					default:
						break;
				}
				_notePositions.get(_noteToChange).setR(length);
				_isMenuActive = false;
			}
			else
			{
				_isMenuActive = false;
			}
		    
			 //Note choosing menu initial check logic
			if(!_isNoteChoosingActive)
			{
				_eventX = event.getX();
			    _eventY = event.getY();
			    vertPosition =  ( (int)_eventY - upperDistance ) / _vertStep;
			    if(vertPosition < 1)
				{
			    	vertPosition = 1;
				}
				if(vertPosition > 15)
				{
					vertPosition = 15;
				}
				horPosition = ( (int)_eventX - leftDistance ) / _horStep;
				if(horPosition < 1)
				{
					horPosition = 1;
				}
				if(horPosition > 10)
				{
					horPosition = 10;
				}
				int tempColumn = (int)(leftDistance + ( _horStep * horPosition ) );
			    int tempRow = (int)(upperDistance + ( _vertStep * vertPosition ) );
			    
			    //Create a temporary pair
			    tempPair = new Pair<Integer, Integer, Integer>(tempColumn + ( _horStep * 5 * _screenPosition ), tempRow, 1);
			    
			    //Check if there's a note on that location	    
			    if(_notePositions.contains(tempPair))
			    {
			    	_isNoteChoosingActive = true;
			    	_isMenuActive = true;
			    	_noteToChange = _notePositions.indexOf(tempPair);
			    }
			    else
			    {
			    	_isMenuActive = false;
			    	
			    }
			}
			
		    //Draw a note if the note choosing menu is not active
			if(!_isNoteChoosingActive)
			{	
				//Create a temporary pair
			    tempPair = new Pair<Integer, Integer, Integer>(_column + ( _horStep * 5 * _screenPosition ), _row, 1);
			    
			    //Check if there's already a note there		    
			    if(_notePositions.contains(tempPair))
			    {
			    	_notePositions.remove(tempPair);
			    	_song.getScore(0).removeNote(horPosition - 1 + ( 5 * _screenPosition),
			    								 GRID_TO_PITCH[vertPosition - 1]);
			    }
			    else
			    {
			    	_notePositions.add(tempPair);		 
			    	if (_song.getScore(0).addNote(horPosition - 1 + ( 5 * _screenPosition),
							 					  GRID_TO_PITCH[vertPosition - 1],
							 					  0))
			    		Log.d("PLAYA", "NOTE ADDING SUCCESSFUL!!!");
			    }			    
			}
			
		    if(!_isMenuActive)
		    {
		    	_isNoteChoosingActive = false;
		    }
		    
		   
			_column = 1000;
		    _row = 1000;
		}
		
	    // Schedules a repaint.
		invalidate();
	    return true;
	}
	
	//Note drawing
	public void noteDraw()
	{		
		int position;
    	for(int i = 0; i < _notePositions.size(); i++)
    	{
    		position = _notePositions.get(i).getM() / _vertStep;
    		if(position < 2)
    		{
    			position = 2;
    		}
    		if(position > 16)
    		{
    			position = 16;
    		}
    		//Set bitmap & position
    		if(_notePositions.get(i).getR() != 1)
    		{
    			switch(_notePositions.get(i).getR())
    			{
    			case 2:
    				_note = BitmapFactory.decodeResource(getResources(), R.drawable.note_half);
    				break;
    			case 4:
    				_note = BitmapFactory.decodeResource(getResources(), R.drawable.note_quarter);
    				break;
    			case 8:
    				_note = BitmapFactory.decodeResource(getResources(), R.drawable.note_eighth);
    				break;
    			case 16:
    				_note = BitmapFactory.decodeResource(getResources(), R.drawable.note_sixteenth);
    				break;
    			case 32:
    				_note = BitmapFactory.decodeResource(getResources(), R.drawable.note_thirty_second);
    				break;
    			case 64:
    				_note = BitmapFactory.decodeResource(getResources(), R.drawable.note_sixty_fourth);
    				break;
    			default:
    				break;
    			}
    		}
    		else
    		{
	    		switch(position)
	    		{
	    			case 2:
	    				_note = BitmapFactory.decodeResource(getResources(), R.drawable.note_bottom);
	    				break;
	    			case 16:
	    				_note = BitmapFactory.decodeResource(getResources(), R.drawable.note_top);
	    				break;
	    			case 3:
	    			case 15:
	    				_note = BitmapFactory.decodeResource(getResources(), R.drawable.note_crossed);
	    				break;
	    			default:
	    				_note = BitmapFactory.decodeResource(getResources(), R.drawable.note);
	    				break;
	    		}
    		}
    		int positionY;
    		if(_notePositions.get(i).getR() != 1)
    		{
    			positionY = _notePositions.get(i).getM() - 90;
    		}
    		else
    		{
    			positionY = _notePositions.get(i).getM();
    		}
    		//Drawing
       		if(_screenPosition == 0 && _notePositions.get(i).getL() <= _horStep * 9)
    		{
    			_canvas.drawBitmap(_note, _notePositions.get(i).getL(), positionY, null);
    		}
    		else if(_notePositions.get(i).getL() > ((_horStep * 2 ) + (_horStep * 5 * _screenPosition )) && _notePositions.get(i).getL() < (( _horStep * 9 ) + ( _horStep * 5 * _screenPosition )))
    		{
    			_canvas.drawBitmap(_note, _notePositions.get(i).getL() - ( _horStep * 5 * _screenPosition ), positionY, null);
    		}
    	}
	}
	
	//Temporary note draw    
	public void temporaryNoteDraw()
	{
    	int position;
    	position = _row / _vertStep;
        if(position < 2)
    	{
    		position = 2;
    	}
    	if(position > 16)
    	{
    		position = 16;
    	}
    	switch(position)
    	{
    		case 2:
    			_tempNote = BitmapFactory.decodeResource(getResources(), R.drawable.note_bottom_temp);
    			break;
    		case 16:
    			_tempNote = BitmapFactory.decodeResource(getResources(), R.drawable.note_top_temp);
    			break;
    		case 3:
    		case 15:
    			_tempNote = BitmapFactory.decodeResource(getResources(), R.drawable.note_crossed_temp);
    			break;
    		default:
    			_tempNote = BitmapFactory.decodeResource(getResources(), R.drawable.note_temp);
    			break;
    	}
        _canvas.drawBitmap(_tempNote, _column, _row, null);  	
	}
	
	//Indicator drawing
	public void indicatorDraw()
	{
		_canvas.drawBitmap(_indicator, _indicatorPositionX, _indicatorPositionY, null);
		invalidate();
	}
	
	public void noteChooseMenuDraw()
	{
		_canvas.drawBitmap(_noteSelection, _menuX, _menuY, null);
		invalidate();
	}
	
	//Move indicator
	public static void moveIndicator(float beatProgress, float currentBeat)
	{
		currentBeat++;
		_indicatorPositionX = 60 + (75 * currentBeat) + (75 * beatProgress);
	}
	
	//Resets the indicator's position when the player is stopped
	public static void resetIndicatorPosition(float value)
	{
		_indicatorPositionX = 60;
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
	
	//Changes the score bitmap
	public void changeScore()
	{
		if(_screenPosition <= 0)
		{
			_score = BitmapFactory.decodeResource(getResources(), R.drawable.score);
			_screenPosition = 0;
		}
		else
		{
			if(_screenPosition % 2 != 0)
			{
				_score = BitmapFactory.decodeResource(getResources(), R.drawable.score_transition);
			}
			else
			{
				_score = BitmapFactory.decodeResource(getResources(), R.drawable.score_next);
			}
		}
	}
	
	public static void clear()
	{
		_song.getScore(0).clearScore();
		_notePositions.clear();
	}
}