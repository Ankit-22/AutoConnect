package com.ankit.autoconnect;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private int is_double_click = 0;
    private float initial_x, initial_y, start_x, start_y;
    private int mActivePointerId;
    private View touchPad;
    private int port = 0;
    private String server = "";
    private static Date prevDate = new Date();
    private ConnectionSocket connectionSocket;

    @SuppressLint("StaticFieldLeak")
    private void sendMessageToServer(final String message) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                return connectionSocket.sendSocketMessage(message);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if(!aBoolean) {
                    Toast.makeText(getApplicationContext(), "Cannot Connect To Server, Please Reconnect.", Toast.LENGTH_LONG).show();
                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... voids) {
                            try {
                                connectionSocket = new ConnectionSocket(server, port);
                            } catch (Exception e) {
                                Log.i("Error_Socket", "Error Creating Socket: " + e);
                            }
                            return null;
                        }
                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                super.onPostExecute(aBoolean);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent passedInfo = getIntent();
        server = passedInfo.getStringExtra("server");
        port = passedInfo.getIntExtra("port", 0);
        touchPad = findViewById(R.id.touchpad);
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    connectionSocket = new ConnectionSocket(server, port);
                } catch (Exception e) {
                    Log.i("Error_Socket", "Error Creating Socket: " + e);
                    touchPad = null;
                }
                if (touchPad != null) {
                    touchPad.setOnTouchListener(new View.OnTouchListener() {
                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public boolean onTouch(View view, MotionEvent ev) {
                            final int actionPerformed = ev.getAction();
                            mActivePointerId = ev.getPointerId(0);
                            int pointerIndex = ev.findPointerIndex(mActivePointerId);
                            Rect rect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());

                            switch (actionPerformed & MotionEvent.ACTION_MASK) {
                                case MotionEvent.ACTION_DOWN: {
                                    Log.i("Debug", "Pointer Down");
                                    initial_x = ev.getX(pointerIndex);
                                    initial_y = ev.getY(pointerIndex);
                                    start_x = initial_x;
                                    start_y = initial_y;
                                    prevDate = new Date();
                                    sendMessageToServer("Touch: " + initial_x + " " + initial_y);
                                    Log.i("Debug", "X: " + initial_x + " Y: " + initial_y);
                                    break;
                                }

                                case MotionEvent.ACTION_UP: {
                                    float x = ev.getX(pointerIndex);
                                    float y = ev.getY(pointerIndex);
                                    Date now = new Date();
                                    if (Math.abs(x - start_x) + Math.abs(y - start_y) < 5.0 && now.getTime() - prevDate.getTime() <= 600) {
                                        sendMessageToServer("Click:");
                                        Log.i("Debug", "Click: "+ev.getX()+" "+ev.getY());
                                    }
                                }

                                case MotionEvent.ACTION_MOVE: {
                                    if(!rect.contains(view.getLeft() + (int) ev.getX(), view.getTop() + (int) ev.getY()))
                                        break;
                                    float x = ev.getX(pointerIndex);
                                    float y = ev.getY(pointerIndex);
                                    if (Math.abs(x - initial_x) + Math.abs(y - initial_y) >= 5.0) {
                                        sendMessageToServer("Move: " + (x - initial_x) + " " + (y - initial_y));
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
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        ((Button)findViewById(R.id.left_click)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.i("Debug", "LeftClick Down");
                    sendMessageToServer("LeftClick: Down");
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Log.i("Debug", "LeftClick Up");
                    sendMessageToServer("LeftClick: Release");
                }
                return true;
            }
        });

        ((Button)findViewById(R.id.right_click)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.i("Debug", "RightClick Down");
                    sendMessageToServer("RightClick: Down");
                }
                else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Log.i("Debug", "RightClick Up");
                    sendMessageToServer("RightClick: Release");
                }
                return true;
            }
        });

    }

    @Override
    protected void onDestroy() {
        connectionSocket.finalize();
        super.onDestroy();
    }
}
