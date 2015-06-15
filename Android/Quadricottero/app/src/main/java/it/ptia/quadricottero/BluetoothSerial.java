package it.ptia.quadricottero;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothSerial implements BTConnector.OnConnectedListener {

    private final String TAG = "BluetoothSerial";
    private static final byte ASCII_NEWLINE = 10;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private DataListener dataListener;
    private BluetoothDevice device;
    private CommunicationReceiver communicationReceiver;

    public BluetoothSerial(CommunicationReceiver communicator) {
        this.communicationReceiver = communicator;
    }

    public void begin(BluetoothDevice device) {
        this.device = device;
        BTConnector connector = new BTConnector(this);
        connector.execute(device);
    }

    @Override
    public void onConnected(BluetoothSocket bluetoothSocket) {
        try {
            this.outputStream = bluetoothSocket.getOutputStream();
            dataListener = new DataListener(bluetoothSocket.getInputStream());
            Thread dataReceiverThread = new Thread(dataListener);
            this.socket = bluetoothSocket;
            Log.i(TAG,"Succesfully connected");
            communicationReceiver.onNewCommunicationReceived(Communication.CONNECTION_SUCCESS);
        }
        catch (IOException | NullPointerException e) {
            e.printStackTrace();
            Log.e(TAG, "Can't connect");
            communicationReceiver.onNewCommunicationReceived(Communication.CONNECTION_ERROR);
        }
    }

    public void close() {
        try {
            outputStream.close();
            socket.close();
            dataListener.setRunning(false);
            communicationReceiver.onNewCommunicationReceived(Communication.CONNECTION_CLOSED);
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
            communicationReceiver.onNewCommunicationReceived(Communication.OUTPUT_ERROR);
        }

    }

    public String readString() {
        if(dataListener.getInputException() == null) {
            return dataListener.getData();
        }
        else {
            communicationReceiver.onNewCommunicationReceived(Communication.INPUT_ERROR);
            return null;
        }
    }

    public boolean isConnected() {
        try {
            return socket.isConnected() && dataListener.isRunning();
        }
        catch (Exception e) {
            return false;
        }
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    private class DataListener implements Runnable {
        private boolean running = false;
        private InputStream inputStream;
        private byte[] readBuffer;
        private String data;
        private IOException inputException = null;

        public DataListener(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            readBuffer = new byte[1024];
            int readBufferPosition = 0;
            //Loop
            while (isRunning() && !Thread.currentThread().isInterrupted()) {
                try {
                    int bytesAvailable = inputStream.available();
                    if(bytesAvailable > 0) {
                        byte[] receivedPacket = new byte[bytesAvailable];
                        inputStream.read(receivedPacket);
                        for (int i = 0; i < receivedPacket.length; i++) {
                            byte b = receivedPacket[i];
                            if(b==ASCII_NEWLINE) {
                                byte[] encodedBytes = new byte[readBufferPosition];
                                System.arraycopy(readBuffer, 0, encodedBytes, 0,encodedBytes.length);
                                data = new String(encodedBytes, "US-ASCII");
                                readBufferPosition = 0;
                            }
                            else {
                                readBuffer[readBufferPosition++] = b;
                            }
                        }
                    }
                    inputException = null;
                }
                catch (IOException e) {
                    e.printStackTrace();
                    running = false;
                    inputException = e;
                }
                catch (ArrayIndexOutOfBoundsException e) {
                    readBufferPosition = 0;
                }
            }
            //Quando abbiamo finito
            try {
                inputStream.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean isRunning() {
            return running && !Thread.currentThread().isInterrupted();
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        public String getData() {
            return data;
        }

        public IOException getInputException() {
            return inputException;
        }
    }

    public interface CommunicationReceiver {
        void onNewCommunicationReceived(Communication communication);
    }
    public enum Communication {
        CONNECTION_SUCCESS, CONNECTION_ERROR, INPUT_ERROR, OUTPUT_ERROR, CONNECTION_CLOSED
    }
}
