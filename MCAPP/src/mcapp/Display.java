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
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

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
	//Signature strings
	static String[] _signatureList = {"NONE ( C Major / A Minor )",
									  "SHARPS:",
									  "G Major / E Minor",
									  "D Major / B Minor",
									  "A Major / F# Minor",
									  "E Major / C# Minor",
									  "B Major / G# Minor",
									  "F# Major / D# Minor",
									  "C# Major / A# Minor",
									  "FLATS:",
									  "F Major / D Minor",
									  "B" + "\u266D" + " Major / G Minor",
									  "E" + "\u266D" + " Major / C Minor",
									  "A" + "\u266D" + " Major / F Minor",
									  "D" + "\u266D" + " Major / B" + "\u266D" + " Minor",
									  "G" + "\u266D" + " Major / E" + "\u266D" + " Minor",
									  "A" + "\u266D" + " Major / A" + "\u266D" + " Minor",									  
									  };

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
	int _scaledScoreY = (_score.getHeight() * 4 * _scaledNoteHeight) / (_score.getHeight() / 2);
	Bitmap _scaledScore = getResizedBitmap(_score, (int)(_screenWidth * 0.94f), _scaledScoreY, false, false);

	Bitmap _scaledKey = getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.key_sol), 0, _scaledScore.getHeight(), true, false);

	Bitmap _scaledBar = getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.bar), 0, _scaledScore.getHeight() / 2, true, false);
	
	Bitmap _scaledArrowNext = getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.arrow_next), _scaledNote.getWidth() / 2, 4 * _scaledNoteHeight, false, false);
	
	Bitmap _scaledArrowBack = getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.arrow_back), _scaledNote.getWidth() / 2, 4 * _scaledNoteHeight, false, false);

	//Bar position
	int _barX;
	int _barY;

	Bitmap _indicator = BitmapFactory.decodeResource(getResources(), R.drawable.indicator);
	Bitmap _scaledIndicator = getResizedBitmap(_indicator, _indicator.getWidth(), _scaledScore.getHeight(), false, false);

	Bitmap _scaledMenu = getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.note_selection), 0, (int)(_screenHeight / 4), true, false);

	Bitmap _scaledSharp = getResizedBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.sharp),0, (int)(_scaledNoteHeight * 3), true, false);

	Bitmap _flat = BitmapFactory.decodeResource(getResources(), R.drawable.flat);
	Bitmap _scaledFlat = getResizedBitmap(_flat, 0, (int)( ((_scaledNoteHeight * 5) / 2) * 1.1 ), true, false);
	int _scaledFlatOffsetY = (_scaledFlat.getHeight() * 58) / _flat.getHeight();

	//Temporary note drawing variables
	int _column = 1000;
	int _row = 1000;

	//Note positioning variables
	int _eventDisplacementX = 50;
	int _horStep = _scaledNote.getWidth();
	int _vertStep = _scaledNoteHeight / 2;
	int _upperDistance = _screenCenterY - (int)(_scaledScoreY * 0.8) - _scaledNoteHeight / 2;
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
	float _menuX = _screenCenterX - (float)(_scaledMenu.getWidth() * 0.55);
	float _menuY = _screenCenterY - (float)(_scaledMenu.getHeight());

	//the indicator of the "screen" currently viewed
	static int _screenPosition = 0;
	static int _indicatorScreen = 0;

	//Flag for note choosing menu
	static boolean _isNoteChoosingActive = false;

	//Signature draw positions
	int _signaturePositionsY[] = { _screenCenterY - (int)(_scaledScore.getHeight() * 0.8) + 1 * (_scaledNoteHeight / 2),
								   _screenCenterY - (int)(_scaledScore.getHeight() * 0.8) + 4 * (_scaledNoteHeight / 2),
								   _screenCenterY - (int)(_scaledScore.getHeight() * 0.8) + 0 * (_scaledNoteHeight / 2),
								   _screenCenterY - (int)(_scaledScore.getHeight() * 0.8) + 3 * (_scaledNoteHeight / 2),
								   _screenCenterY - (int)(_scaledScore.getHeight() * 0.8) + 6 * (_scaledNoteHeight / 2),
								   _screenCenterY - (int)(_scaledScore.getHeight() * 0.8) + 2 * (_scaledNoteHeight / 2),
								   _screenCenterY - (int)(_scaledScore.getHeight() * 0.8) + 5 * (_scaledNoteHeight / 2),
								   _screenCenterY - (int)(_scaledScore.getHeight() * 0.8) + 4 * (_scaledNoteHeight / 2),
								   _screenCenterY - (int)(_scaledScore.getHeight() * 0.8) + 1 * (_scaledNoteHeight / 2),
								   _screenCenterY - (int)(_scaledScore.getHeight() * 0.8) + 5 * (_scaledNoteHeight / 2),
								   _screenCenterY - (int)(_scaledScore.getHeight() * 0.8) + 2 * (_scaledNoteHeight / 2),
								   _screenCenterY - (int)(_scaledScore.getHeight() * 0.8) + 6 * (_scaledNoteHeight / 2),
								   _screenCenterY - (int)(_scaledScore.getHeight() * 0.8) + 3 * (_scaledNoteHeight / 2),
								   _screenCenterY - (int)(_scaledScore.getHeight() * 0.8) + 7 * (_scaledNoteHeight / 2),
								   };

	//Active signature based on _signatureList
	static int	_currentSignature = 0;

	//Selected note
	static int _selectedNote = -1;
	static Note _selectedNoteObj = null;

	//Flag for deletion. This allows the note to be marked 1st and deleted 2nd.
	static boolean _isEraserActive = false;

	//Selected accidental
	int _accidentalToDraw = -1;

	//Toast duration
	int _duration = 0;
	//Tracking variable
	int _position = -1;
	//Toast to be drawn
	Toast _toast;
	//Toast char offset array
	int _charPositions[] = { 2,	//C
							0, //B
							-2,	//A
							3,	//G
							1,	//F
							-1,	//E
							-3,	//D
							};
	
	//Instrument demoing variables.
	int _previousVertPosition = -1;
	/**
	 * If this is true, the note is demoed upon changing the position of the
	 * note. If this is false, it is demoed when the note is placed.
	 */
	static final boolean DEMO_UPON_CHANGE = true;


	/**
	 * Reference to the current song.
	 */
	private static Song _song;
	
	/**
	 * Reference to the song player.
	 */
	private static Player _player = null;
	
	/**
	 * Reference to the sound player.
	 */
	private static SoundPlayer _soundPlayer = null;
	
	/**
	 * Reference to the instrument manager.
	 */
	private static InstrumentManager _instrumentManager = null;


	//Getter for the _screenPosition
	public static int getScreen()
	{
		return _screenPosition;
	}
	
	public static void setScreen(int screen)
	{
		_screenPosition = screen;
	}

	//Getter for the screen the indicator is currently in
	public static int getIndicatorScreen()
	{
		return _indicatorScreen;
	}

	//Setter for the screen the indicator is currently in
	public static void setIndicatorScreen(int screen)
	{
		_indicatorScreen = screen;
	}

	//Getter for the _signatureList
	public static String[] getSignatures()
	{
		return _signatureList;
	}
	
	//Getter for the eraser flag
	public static boolean getEraser()
	{
		return _isEraserActive;
	}
	
	//Setter for the eraser flag
	public static void setEraser(boolean value)
	{
		_isEraserActive = value;
	}
	
	//Constructors
	public Display(Context context) 
	{		
		super(context); 
		initialiseToast();
    }
 
    public Display(Context context, AttributeSet attrs) 
    {    	
        super(context);
        initialiseToast();
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
	
	public static void setSoundStuff(Player player, SoundPlayer soundPlayer,
			InstrumentManager instrumentManager)
	{
		_player = player;
		_soundPlayer = soundPlayer;
		_instrumentManager = instrumentManager;
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
    	if(!(!_isPlaying && _screenPosition != 0) && _screenPosition == _indicatorScreen)
    	{
    		indicatorDraw();
    	}
    	
    	//Draw signatures
    	drawSignatures();
    	
    	//Arrow drawing
    	drawArrows();
    	
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
	    
	    //Create a temporary pair
	    tempPair = new Pair<Integer, Integer, Integer>(_column + ( _horStep * 4 * _screenPosition ), _row, 1);
	    
	    //Press down event
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			//Right arrow check
			if(_eventX + _eventDisplacementX > _screenWidth * 0.94f - _scaledArrowNext.getWidth()
					&& _eventX + _eventDisplacementX < _screenWidth * 0.94f
					&& _eventY > _upperDistance + (int)(_scaledNoteHeight * 2.5)
					&& _eventY < _upperDistance + (int)(_scaledNoteHeight * 2.5) + _scaledArrowNext.getHeight())
			{
				scoreNext();
				_column = 1000;
				_row = 1000;
				return false;
			}
			
			//Left arrow check
			if(_eventX + _eventDisplacementX > 0
					&& _eventX + _eventDisplacementX < _scaledArrowBack.getWidth()
					&& _eventY > _upperDistance + (int)(_scaledNoteHeight * 2.5)
					&& _eventY < _upperDistance + (int)(_scaledNoteHeight * 2.5) + _scaledArrowNext.getHeight())
			{
				scoreBack();
				_column = 1000;
				_row = 1000;
				return false;
			}
		}
		//Drag event
		if(event.getAction() == MotionEvent.ACTION_MOVE)
		{
			if(_isEraserActive)
			{
				if(_notePositions.contains(tempPair))
				{
					if(_selectedNote == _notePositions.indexOf(tempPair))
					{
						_selectedNote = -1;		    
				    }
				    _notePositions.remove(tempPair);
				    _song.getScore(0).removeNote(horPosition - 1 + (4 * _screenPosition),
				    							 vertPosition - 1);
				    playEraserSound();
				}
			}
			else
			{
				//Demo note.
				if (DEMO_UPON_CHANGE && vertPosition != _previousVertPosition
						&& !_isNoteChoosingActive)
					demoNote(vertPosition - 1);
				
				_previousVertPosition = vertPosition;
			}
			//Log.d("ASD", String.valueOf(_eventX) + " " + String.valueOf(_eventY));			
		}
		//Release event
		if(event.getAction() == MotionEvent.ACTION_UP)
		{	
			_previousVertPosition = -1;
			
			//Note selection initial logic
			if(!_isNoteChoosingActive)
			{			   
			    //Check if there's a note on that location	    
			    if(_notePositions.contains(tempPair))
			    {
			    	//Select it
			    	_selectedNote = _notePositions.indexOf(tempPair);
			    	
			    	_selectedNoteObj = _song.getScore(0).getBeat(horPosition - 1 + (4 * _screenPosition))
			    		.getNoteAtPitch(vertPosition - 1);
			    }
			    //Draw note logic, if the note choosing menu is not active			    
			    //Check if there's already a note there			    
			    else
			    {
			    	Beat theBeat = _song.getScore(0).getBeat(horPosition - 1 + (4 * _screenPosition));
			    	
			    	if (!_isEraserActive && (theBeat == null || !theBeat.isFull()))
			    	{
			    		//Add the note graphic if it was added successfully.
			    		if (_song.getScore(0).addNote(horPosition - 1 + (4 * _screenPosition),
			    							  vertPosition - 1, 0))
			    		{
			    			_notePositions.add(tempPair);
			    			_selectedNote = -1;
			    			//Demo note.
							if (!DEMO_UPON_CHANGE)
								demoNote(vertPosition - 1);
			    		}
			    	}
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
					_selectedNoteObj.setLength(1 / (float)length);
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
    		position = ( (int)_notePositions.get(i).getM() - _upperDistance ) / _vertStep;
    		
    		//Set bitmap & position
    		int positionY = _notePositions.get(i).getM() - _scaledNote.getHeight() + _scaledNoteHeight;
    		
    		if(_notePositions.get(i).getR() != 1)
    		{
    			int noteLength = _notePositions.get(i).getR();
    			String noteName = "note_" + noteLength;
    			if(position < 8)
    			{
    				noteName = "note_" + noteLength + "_flipped";
    				positionY = _notePositions.get(i).getM();
    			}    			
    			int resID = getResources().getIdentifier(noteName, "drawable", getContext().getPackageName());
    		    note  = BitmapFactory.decodeResource(getResources(), resID);
    		}
    		else
    		{
	    		switch(position)
	    		{
	    			case 1:
	    				note = BitmapFactory.decodeResource(getResources(), R.drawable.note_bottom);
	    				break;
	    			case 15:
	    				note = BitmapFactory.decodeResource(getResources(), R.drawable.note_top);
	    				break;
	    			case 2:
	    			case 14:
	    				note = BitmapFactory.decodeResource(getResources(), R.drawable.note_crossed);
	    				break;
	    			default:
	    				break;
	    		}
    		}
    		
    		Bitmap scaledNote = getResizedBitmap(note, 0, (int)(_screenHeight / 4), true, false);
    		
    	
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
    	//position = _row / _vertStep;
    	position = ( _row - _upperDistance ) / _vertStep;
    	
    	
    	switch(position)
    	{
    		case 1:
    			note = BitmapFactory.decodeResource(getResources(), R.drawable.note_bottom);
    			break;
    		case 15:
    			note = BitmapFactory.decodeResource(getResources(), R.drawable.note_top);
    			break;
    		case 2:
    		case 14:
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
        
        //TOAST DRAW
        if(_row != 1000 && _column != 1000 && position != _position)
        {
        	_toast.cancel();
        	int offset = _charPositions[position % 7];
        	char letter = (char)('A' + position % 7 + offset);
        	//TO DO:
        	//Display key signature
        	//Setting text
        	_toast.setText(String.valueOf(letter));
        	//"Drawing"
	        _toast.show();
        }
        _position = position;
        _paint.setColorFilter(null);
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
		if(_screenPosition > (Global.BEATS_PER_SCORE - 8) / 4)
		{
			_screenPosition = (Global.BEATS_PER_SCORE - 8) / 4;
		}
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
			_barX = (_scaledScore.getWidth() - (int)((double)(_scaledScore.getWidth() * 0.48)));
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

	//Sets the current signature
	public static void setSignature(int position)
	{
		_currentSignature = position;
	}

	//Draws the signature
	public void drawSignatures()
	{
		if(_currentSignature == 0)
		{
			return;
		}
		else if(_currentSignature < 9)
		{
			int sharpStep = _scaledSharp.getWidth();
			_paint.setAlpha(100);
			for(int i = 2; i <= _currentSignature; i++)
			{
				_canvas.drawBitmap(_scaledSharp, _horStep + sharpStep * (i - 2), _signaturePositionsY[i - 2], _paint);
			}
			_paint.setAlpha(255);
		}
		else
		{
			int flatStep = _scaledFlat.getWidth();
			_paint.setAlpha(100);
			for(int i = 10; i <= _currentSignature; i++)
			{
				_canvas.drawBitmap(_scaledFlat, _horStep + flatStep * (i - 10), _signaturePositionsY[i - 3], _paint);
			}
			_paint.setAlpha(255);
		}
	}

	//Initial toast parameters
	public void initialiseToast()
	{
		Context context = getContext();
        CharSequence text = "Hello toast!";
        int duration = Toast.LENGTH_SHORT;
        _duration = duration;	
        _toast = Toast.makeText(context, text, duration);
        _toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
	}
	
	//Draws the arrows for changing the screen
	public void drawArrows()
	{
		_paint.setAlpha(100);
    	_canvas.drawBitmap(_scaledArrowNext, _screenWidth * 0.94f - _scaledArrowNext.getWidth(), _upperDistance + (int)(_scaledNoteHeight * 2.5), _paint);
    	_canvas.drawBitmap(_scaledArrowBack, 0, _upperDistance + (int)(_scaledNoteHeight * 2.5), _paint);
    	_paint.setAlpha(255);
	}
	
	private void demoNote(int verticalIndex)
	{
		_soundPlayer.demo(_player.getInstrument(),
				Global.GRID_TO_PITCH[verticalIndex]);
		
		//Need key sig at some point.
	}
	
	private void playEraserSound()
	{
		_soundPlayer.demo(_instrumentManager.getEraserID());
	}
}