package it.ptia.quadricottero;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class BTConnector extends AsyncTask<BluetoothDevice, Void, BluetoothSocket>{
    OnConnectedListener listener;
    public BTConnector(OnConnectedListener listener) {
        this.listener = listener;
    }

    @Override
    protected BluetoothSocket doInBackground(BluetoothDevice... devices) {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard //SerialPortService ID
        BluetoothSocket socket = null;
        try {
            socket = devices[0].createRfcommSocketToServiceRecord(uuid);
            socket.connect();
        }
        catch (IOException e) {
            e.printStackTrace();
            socket = null;
        }
        return socket;
    }

    @Override
    protected void onPostExecute(BluetoothSocket bluetoothSocket) {
        listener.onConnected(bluetoothSocket);
    }
    public interface OnConnectedListener {
        void onConnected(BluetoothSocket bluetoothSocket);
    }
}
