package com.ankit.autoconnect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

class CallHandler {

	private static ConnectionSocket connectionSocket = null;
	private Context context;
	private String server;
	private int port;

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
                    Toast.makeText(context.getApplicationContext(), "Cannot Connect To Server, Please Reconnect.", Toast.LENGTH_LONG).show();
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

	CallHandler(Context context, String server, int port) throws Exception{
		if(connectionSocket == null) {
			try {
	            connectionSocket = new ConnectionSocket(server, port);
                this.server = server;
                this.port = port;
                this.context = context;
	        } catch (Exception e) {
	            Log.i("Error_Socket", "Error Creating Socket: " + e);
	            throw e;
	        }
	    }
	}

	void moveMouse(float dx, float dy) {
        sendMessageToServer("Move: " + dx + " " + dy);
    }

    void click() {
        sendMessageToServer("Click:");
    }

	void rightClickDown() {
        sendMessageToServer("RightClick: Down");
	}

	void rightClickUp() {
        sendMessageToServer("RightClick: Release");
    }

    void leftClickDown() {
        sendMessageToServer("LeftClick: Down");
    }

    void leftClickUp() {
        sendMessageToServer("LeftClick: Release");
    }

    @Override
    public void finalize() {
        connectionSocket.finalize();
        try {
            super.finalize();
        } catch (Throwable e) {
            Log.i("Debug", "Finalize Exception: "+e.toString());
        }
    }
}
