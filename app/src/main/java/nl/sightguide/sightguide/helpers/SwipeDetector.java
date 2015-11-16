package nl.sightguide.sightguide.helpers;

import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import nl.sightguide.sightguide.activities.Route;

public class SwipeDetector implements View.OnTouchListener {

        static final String logTag = "ActivitySwipeDetector";
        private Activity activity;
        static final int MIN_DISTANCE = 100;
        private float downX, downY, upX, upY;

        public SwipeDetector(Activity activity){
            this.activity = activity;
        }

        public void onRightSwipe(View v){
            Log.i(logTag, "RightToLeftSwipe!");
            Route.swipeRight(v);
        }

        public void onLeftSwipe(View v){
            Log.i(logTag, "LeftToRightSwipe!");
            Route.swipeLeft(v);
        }

        public void onDownSwipe(View v){
            Log.i(logTag, "onTopToBottomSwipe!");
            Route.swipeDown(v);
        }

        public void onUpSwipe(View v){
            Log.i(logTag, "onBottomToTopSwipe!");
            Route.swipeUp(v);
        }

        public boolean onTouch(View v, MotionEvent event) {
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN: {
                    downX = event.getX();
                    downY = event.getY();
                    return true;
                }
                case MotionEvent.ACTION_UP: {
                    upX = event.getX();
                    upY = event.getY();

                    float deltaX = downX - upX;
                    float deltaY = downY - upY;

                    // swipe horizontal?
                    if(Math.abs(deltaX) > Math.abs(deltaY))
                    {
                        if(Math.abs(deltaX) > MIN_DISTANCE){
                            // left or right
                            if(deltaX > 0) { this.onRightSwipe(v); return true; }
                            if(deltaX < 0) { this.onLeftSwipe(v); return true; }
                        }
                        else {
                            Log.i(logTag, "Horizontal Swipe was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE);
                            return false; // We don't consume the event
                        }
                    }
                    // swipe vertical?
                    else
                    {
                        if(Math.abs(deltaY) > MIN_DISTANCE){
                            // top or down
                            if(deltaY < 0) { this.onDownSwipe(v); return true; }
                            if(deltaY > 0) { this.onUpSwipe(v); return true; }
                        }
                        else {
                            Log.i(logTag, "Vertical Swipe was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE);
                            return false; // We don't consume the event
                        }
                    }

                    return true;
                }
            }
            return false;
        }

    }
