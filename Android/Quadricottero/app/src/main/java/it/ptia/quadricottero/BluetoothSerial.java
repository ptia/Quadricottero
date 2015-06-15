package it.ptia.quadricottero;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothSerial implements BTConnector.OnConnectedListener {

    private final String TAG = "BluetoothSerial";
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private BluetoothDevice device;
    private Communicator communicator;

    public BluetoothSerial(Communicator communicator) {
        this.communicator = communicator;
    }
    public boolean isConnected() {
        try {
            return socket.isConnected();
        }
        catch (Exception e) {
            return false;
        }
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void begin(BluetoothDevice device) {
        this.device = device;
        BTConnector connector = new BTConnector(this);
        connector.execute(device);
    }

    public void close() {
        try {
            outputStream.close();
            inputStream.close();
            socket.close();
            communicator.communicate(Communication.CONNECTION_CLOSED);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void print(String msg) {
        try {
            if(isConnected()) {
                outputStream.write(msg.getBytes());
            }
        }
        catch (IOException | NullPointerException e) {
            e.printStackTrace();
            communicator.communicate(Communication.OUTPUT_ERROR);
        }

    }

    @Override
    public void onConnected(BluetoothSocket bluetoothSocket) {
        try {
            this.outputStream = bluetoothSocket.getOutputStream();
            this.inputStream = bluetoothSocket.getInputStream();
            this.socket = bluetoothSocket;
            Log.i(TAG,"Succesfully connected");
            communicator.communicate(Communication.CONNECTION_SUCCESS);
        }
        catch (IOException | NullPointerException e) {
            e.printStackTrace();
            Log.e(TAG, "Can't connect");
            communicator.communicate(Communication.CONNECTION_ERROR);
        }
    }

    private class DataListener implements Runnable {
        private boolean running = false;

        @Override
        public void run() {
            //Loop
            while (running && !Thread.currentThread().isInterrupted()) {

            }
        }

        public boolean isRunning() {
            return running;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }
    }

    public interface Communicator {
        void communicate(Communication communication);
    }
    public enum Communication {
        CONNECTION_SUCCESS, CONNECTION_ERROR, INPUT_ERROR, OUTPUT_ERROR, CONNECTION_CLOSED
    }
}
