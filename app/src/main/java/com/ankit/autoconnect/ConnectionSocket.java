package com.ankit.autoconnect;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by ankit on 13/1/18.
 */

public class ConnectionSocket {
    private DataOutputStream dataStream;
    private Socket socket;

    ConnectionSocket(String server, int port) throws Exception {
        try {
            socket = new Socket(server, port);
            dataStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            Log.i("Socket_Error", "Unable to connect: "+e);
            throw new Exception("Cannot Create Object");
        }
    }

    boolean sendSocketMessage(String message) {
        try {
            dataStream.writeUTF(message);
            Log.i("Debug", "Sent Message: "+message);
            return true;
        } catch (SocketException se) {
            Log.i("Socket_Error", "Unable to connect: "+se);
            return false;
        } catch (IOException e) {
            Log.i("Socket_Error", "Unable to connect: "+e);
            return sendSocketMessage(message);
        }
    }

    @Override
    public void finalize() {
        try {
            socket.shutdownInput();
            socket.shutdownOutput();
            dataStream.close();
            socket.close();
        } catch (IOException e) {
            Log.i("Error", ""+e);
        }
    }
}
