package it.ptia.quadricottero;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Set;

import static it.ptia.quadricottero.BluetoothSerial.Communication.CONNECTION_CLOSED;
import static it.ptia.quadricottero.BluetoothSerial.Communication.CONNECTION_ERROR;
import static it.ptia.quadricottero.BluetoothSerial.Communication.CONNECTION_SUCCESS;

public class MainActivity extends AppCompatActivity implements  BluetoothSerial.CommunicationReceiver{
    private static final String TAG = "MainActivity";

    BluetoothSerial bluetoothSerial = new BluetoothSerial(this);
    BluetoothAdapter bluetoothAdapter;

    MenuItem connectMenu;
    MenuItem disconnectMenu;

    String currentFragment;
    TerminalFragment terminalFragment;
    PIDSettingsFragment pidSettingsFragment;

    TabLayout tabLayout;
    TabLayout.Tab terminalTab;
    TabLayout.Tab pidTab;

    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        terminalFragment = new TerminalFragment();
        pidSettingsFragment = new PIDSettingsFragment();
        terminalFragment.setBluetoothSerial(bluetoothSerial);
        pidSettingsFragment.setBluetoothSerial(bluetoothSerial);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, terminalFragment).commit();
        currentFragment = TerminalFragment.TAG;

        tabLayout = (TabLayout) findViewById(R.id.tab_view);
        terminalTab = tabLayout.newTab().setText("Terminale");
        pidTab = tabLayout.newTab().setText("Configura PID");
        tabLayout.addTab(pidTab);
        tabLayout.addTab(terminalTab, true);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.equals(pidTab)) {
                    terminalFragment.closeLogSaver();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, pidSettingsFragment).commit();
                    currentFragment = PIDSettingsFragment.TAG;
                }
                else if (tab.equals(terminalTab)) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, terminalFragment).commit();
                    currentFragment = TerminalFragment.TAG;
                }
                invalidateOptionsMenu();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
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
            getSupportActionBar().setTitle("Quadricottero @ " + bluetoothSerial.getDevice().getName());
            Toast.makeText(this, "Connesso", Toast.LENGTH_SHORT).show();
            invalidateOptionsMenu();
        }
        if(communication == CONNECTION_CLOSED) {
            getSupportActionBar().setTitle("Quadricottero");
            invalidateOptionsMenu();
        }
        if(communication == CONNECTION_ERROR) {
            Toast.makeText(this,
                    "Connessione con " +bluetoothSerial.getDevice().getName()+" fallita",
                    Toast.LENGTH_SHORT).show();
        }
        if(currentFragment.equals(TerminalFragment.TAG)) {
            terminalFragment.onNewCommunicationReceived(communication);
        }
        if(currentFragment.equals(PIDSettingsFragment.TAG)) {
            pidSettingsFragment.onNewCommunicationReceived(communication);
        }
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
        disconnectMenu = menu.findItem(R.id.action_bt_disconnect);
        if (bluetoothSerial.isConnected()) {
            connectMenu.setVisible(false);
            disconnectMenu.setVisible(true);
        }
        else {
            connectMenu.setVisible(true);
            disconnectMenu.setVisible(false);
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == R.id.action_bt_connect) {
            chooseDevice();
            invalidateOptionsMenu();
            return true;
        }
        if(item.getItemId() == R.id.action_bt_disconnect) {
            bluetoothSerial.close();
            invalidateOptionsMenu();
            return true;
        }
        return false;
    }
}
