package mcapp;

import info.ipidev.mcapp.MainActivity;
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
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

class showImage extends View 
{		
	Vector<Pair<Integer, Integer>> _notePositions = new Vector<Pair<Integer, Integer>>();
	Canvas _canvas;
	Paint _paint = new Paint();
	static MainActivity _main;
	
	Bitmap _score = BitmapFactory.decodeResource(getResources(), R.drawable.score);
	Bitmap _note = BitmapFactory.decodeResource(getResources(), R.drawable.note);
	Bitmap _tempNote = BitmapFactory.decodeResource(getResources(), R.drawable.note_temp);
	Bitmap _indicator = BitmapFactory.decodeResource(getResources(), R.drawable.indicator);
	Rect _bottombar = new Rect(0, 335, 760, 405);
	int _column = 1000;
	int _row = 1000;
	int _horStep = -1;
	int _vertStep = -1;
	float _eventX = 1000;
	float _eventY = 1000;
	
	public static void getIstance(MainActivity main) 
	{
		_main = main;		
	}
	
	public showImage (Context context) 
	{
		super(context);    
    }
 
    public showImage (Context context, AttributeSet attrs) 
    {
        super(context);
    }    

	@Override
    public void onDraw (Canvas canvas) 
    {		
		//Initialisation
		_canvas = canvas;
		//_notePositions = new Vector<Pair<Integer, Integer>>();
		//_paint = new Paint();
		//_score = BitmapFactory.decodeResource(getResources(), R.drawable.score);
		//_note = BitmapFactory.decodeResource(getResources(), R.drawable.note);
		//_tempNote = BitmapFactory.decodeResource(getResources(), R.drawable.note);
		//_bottombar = new Rect(0, 335, 760, 405);
    	
		//Bottom bar draw
    	_paint.setColor(Color.BLUE);    	    	
    	canvas.drawRect(_bottombar, _paint);    	
    	
    	//Score draw
    	canvas.drawBitmap(_score, 0, 30, null);
    	
    	noteDraw();
    }
    
	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		//Variables
		_eventX = event.getX() - 150;
	    _eventY = event.getY();
	    int upperDistance = 20;
	    int leftDistance = 80;
	    _vertStep = 18;
	    _horStep = 50;
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
		Log.d("ASD", String.valueOf(vertPosition));
	    _row = (int)(upperDistance + ( _vertStep * vertPosition ) );
	    _column = (int)(leftDistance + ( _horStep * horPosition ) );
	    
	    //Press down
		if(event.getAction() == MotionEvent.ACTION_DOWN)
		{
			
		}
		//Drag
		if(event.getAction() == MotionEvent.ACTION_MOVE)
		{
			//Log.d("ASD", String.valueOf(_eventX) + " " + String.valueOf(_eventY));
		}
		//Release
		if(event.getAction() == MotionEvent.ACTION_UP)
		{
		    Pair tempPair = new Pair(_column, _row);
		    //Check if there's already a note there		    
		    if(_notePositions.contains(tempPair))
		    {
		    	_notePositions.remove(tempPair);
		    	_main.removeFromGrid(horPosition - 1, vertPosition - 1);
		    }
		    else
		    {		    	
		    	_notePositions.add(tempPair);		    	
				_main.addToGrid(horPosition - 1, vertPosition - 1);
		    }		    
		    _column = 1000;
		    _row = 1000;
		    _eventX = 1000;
		    _eventY = 1000;
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
    		position = _notePositions.get(i).second / _vertStep;
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
    		//Log.d("ASD", "asd + _notePosition.get(i).first + _notePositions.get(i).second");
    		_canvas.drawBitmap(_note, _notePositions.get(i).first, _notePositions.get(i).second, null);
    	}
    	
    	//Temporary note draw    
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
	
	public void indicatorDraw()
	{
		
	}
}