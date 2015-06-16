package it.ptia.quadricottero;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static it.ptia.quadricottero.BluetoothSerial.Communication.*;


public class MainActivity extends AppCompatActivity implements  BluetoothSerial.CommunicationReceiver{
    private static final String TAG = "MainActivity";
    BluetoothSerial bluetoothSerial = new BluetoothSerial(this);
    BluetoothAdapter bluetoothAdapter;
    MenuItem connectMenu;
    MenuItem saveLogMenu;
    TerminalFragment terminalFragment;
    FrameLayout fragmentContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        terminalFragment = new TerminalFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, terminalFragment).commit();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "È necessario attivare il bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onNewCommunicationReceived(BluetoothSerial.Communication communication) {
        if (communication == CONNECTION_SUCCESS) {
            terminalFragment.setBluetoothSerial(bluetoothSerial);
        }
        terminalFragment.onNewCommunicationReceived(communication);
    }

    private void chooseDevice() {
        final Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        final String[] deviceNames = new String[pairedDevices.size()];
        int i = 0;
        for (BluetoothDevice device : pairedDevices) {
            deviceNames[i] = device.getName();
            i++;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleziona dispositivo");
        builder.setItems(deviceNames, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals(deviceNames[which])) {
                        dialog.dismiss();
                        bluetoothSerial.begin(device);
                        break;
                    }
                }
            }
        });
        builder.create().show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!bluetoothAdapter.isEnabled()) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        connectMenu = menu.findItem(R.id.action_bt_connect);
        saveLogMenu = menu.findItem(R.id.action_save_log);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == R.id.action_bt_connect) {
            if(!bluetoothSerial.isConnected()){
                chooseDevice();
            }
            else {
                bluetoothSerial.close();
            }
            return true;
        }
        return false;
    }
}
