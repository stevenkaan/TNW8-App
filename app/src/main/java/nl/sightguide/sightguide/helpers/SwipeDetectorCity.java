package nl.sightguide.sightguide.helpers;

import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import nl.sightguide.sightguide.activities.CityInfo;

public class SwipeDetectorCity implements View.OnTouchListener {

        static final String logTag = "ActivitySwipeDetector";
        private Activity activity;
        static final int MIN_DISTANCE = 100;
        private float downX, downY, upX, upY;
        private int img = 1;


        public SwipeDetectorCity(Activity activity){
            this.activity = activity;
        }

        public void onRightSwipe(View v, int img){
            Log.i(logTag, "RightToLeftSwipe!");
        }

        public void onLeftSwipe(View v, int img){
            Log.i(logTag, "LeftToRightSwipe!");
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
                            if(deltaX > 0) {
                                img++;
                                this.onRightSwipe(v,img);
                                return true;
                            }
                            if(deltaX < 0) {
                                img--;
                                this.onLeftSwipe(v,img);
                                return true;
                            }
                        }
                        else {
                            Log.i(logTag, "Horizontal Swipe was only " + Math.abs(deltaX) + " long, need at least " + MIN_DISTANCE);
                            return false; // We don't consume the event
                        }
                    }

                    return true;
                }
            }
            return false;
        }

    }
