package com.ankit.autoconnect;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private int is_double_click = 0;
    private float initial_x, initial_y;
    private int mActivePointerId;
    private View touchPad;
    private ConnectionSocket connectionSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        touchPad = findViewById(R.id.touchpad);
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    connectionSocket = new ConnectionSocket("192.168.0.105", 6000);
                } catch (Exception e) {
                    Log.i("Error_Socket", "Error Creating Socket: " + e);
                    touchPad = null;
                }
                if (touchPad != null) {
                    touchPad.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View view, MotionEvent ev) {
                            final int actionPerformed = ev.getAction();
                            mActivePointerId = ev.getPointerId(0);
                            int pointerIndex = ev.findPointerIndex(mActivePointerId);

                            switch (actionPerformed & MotionEvent.ACTION_MASK) {
                                case MotionEvent.ACTION_DOWN: {
                                    Log.i("Debug", "Pointer Down");
                                    initial_x = ev.getX(pointerIndex);
                                    initial_y = ev.getY(pointerIndex);
                                    new AsyncTask<Void, Void, Boolean>() {

                                        @Override
                                        protected Boolean doInBackground(Void... voids) {
                                            return connectionSocket.sendSocketMessage("Touch: " + initial_x + " " + initial_y);
                                        }

                                        @Override
                                        protected void onPostExecute(Boolean aBoolean) {
                                            if(!aBoolean) {
                                                Toast.makeText(getApplicationContext(), "Cannot Connect To Server, Please Reconnect.", Toast.LENGTH_LONG).show();
                                                new AsyncTask<Void, Void, Void>() {

                                                    @Override
                                                    protected Void doInBackground(Void... voids) {
                                                        try {
                                                            connectionSocket = new ConnectionSocket("192.168.0.105", 6000);
                                                        } catch (Exception e) {
                                                            Log.i("Error_Socket", "Error Creating Socket: " + e);
                                                            touchPad = null;
                                                        }
                                                        return null;
                                                    }
                                                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
                                            }
                                            super.onPostExecute(aBoolean);
                                        }
                                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
                                    Log.i("Debug", "X: " + initial_x + " Y: " + initial_y);
                                    break;
                                }

                                case MotionEvent.ACTION_UP: {
                                    float x = ev.getX(pointerIndex);
                                    float y = ev.getY(pointerIndex);
                                    if (Math.abs(x - initial_x) + Math.abs(y - initial_y) < 10.0) {
                                        Log.i("Debug", "Click: "+ev.getX()+" "+ev.getY());
                                    }
                                }

                                case MotionEvent.ACTION_MOVE: {
                                    float x = ev.getX(pointerIndex);
                                    float y = ev.getY(pointerIndex);
                                    if (Math.abs(x - initial_x) + Math.abs(y - initial_y) >= 10.0) {
                                        initial_x = x;
                                        initial_y = y;
                                        Log.i("Debug", "Pointer Moved");
                                        Log.i("Debug", "X: " + initial_x + " Y: " + initial_y);
                                    }
                                    break;
                                }

                                case MotionEvent.ACTION_POINTER_DOWN: {
                                    if(is_double_click == 1)
                                        break;
                                    is_double_click = 1;
                                    Log.i("Debug", "Non primary pointer down: "+ev.getX()+" "+ev.getY());
                                    break;
                                }

                                case MotionEvent.ACTION_POINTER_UP: {
                                    is_double_click = 0;
                                    Log.i("Debug", "Non primary pointer removed");
                                    break;
                                }
                            }
                            return true;
                        }
                    });
                }
                return null;
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);
    }

    @Override
    protected void onDestroy() {
        connectionSocket.finalize();
        super.onDestroy();
    }

    public void leftClick(View view) {
        Log.i("Debug", "Left Click");
    }

    public void rightClick(View view) {
        Log.i("Debug", "Right Click");
    }
}
