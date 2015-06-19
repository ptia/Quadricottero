package it.ptia.quadricottero;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;

import static it.ptia.quadricottero.BluetoothSerial.Communication.*;

public class PIDSettingsFragment extends Fragment implements BluetoothSerial.CommunicationReceiver {
    public static final String TAG = "PIDSettingsFragment";

    private BluetoothSerial bluetoothSerial = new BluetoothSerial(this);

    EditText[] pidEditText = new EditText[15];
    Spinner flightModeSpinner;
    SwipeRefreshLayout refresher;

    boolean saveMenuVisible = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");
        View rootLayout= inflater.inflate(R.layout.fragment_pid_settings, null);
        refresher = (SwipeRefreshLayout) rootLayout.findViewById(R.id.pull_to_refresh_container);
        refresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(bluetoothSerial.isConnected())
                refreshData();
            }
        });
        pidEditText[0] = (EditText) rootLayout.findViewById(R.id.acro_pitch_p);
        pidEditText[1] = (EditText) rootLayout.findViewById(R.id.acro_pitch_i);
        pidEditText[2] = (EditText) rootLayout.findViewById(R.id.acro_pitch_d);
        pidEditText[3] = (EditText) rootLayout.findViewById(R.id.acro_roll_p);
        pidEditText[4] = (EditText) rootLayout.findViewById(R.id.acro_roll_i);
        pidEditText[5] = (EditText) rootLayout.findViewById(R.id.acro_roll_d);
        pidEditText[6] = (EditText) rootLayout.findViewById(R.id.acro_yaw_p);
        pidEditText[7] = (EditText) rootLayout.findViewById(R.id.acro_yaw_i);
        pidEditText[8] = (EditText) rootLayout.findViewById(R.id.acro_yaw_d);
        pidEditText[9] = (EditText) rootLayout.findViewById(R.id.stable_pitch_p);
        pidEditText[10] = (EditText) rootLayout.findViewById(R.id.stable_pitch_i);
        pidEditText[11] = (EditText) rootLayout.findViewById(R.id.stable_pitch_d);
        pidEditText[12] = (EditText) rootLayout.findViewById(R.id.stable_roll_p);
        pidEditText[13] = (EditText) rootLayout.findViewById(R.id.stable_roll_i);
        pidEditText[14] = (EditText) rootLayout.findViewById(R.id.stable_roll_d);
        flightModeSpinner = (Spinner) rootLayout.findViewById(R.id.flight_mode_spinner);
        ArrayAdapter<CharSequence> flightModeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.flight_modes,
                android.R.layout.simple_spinner_item);
        flightModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        flightModeSpinner.setAdapter(flightModeAdapter);
        if(bluetoothSerial.isConnected()) {
            refresher.setEnabled(true);
            refresher.post(new Runnable() {
                @Override public void run() {
                    refresher.setRefreshing(true);
                }
            });
            refreshData();
        }
        else {
            refresher.setEnabled(false);
            setEnabled(false);
        }
        return rootLayout;
    }

    @Override
    public void onNewCommunicationReceived(BluetoothSerial.Communication communication) {
        if(communication == CONNECTION_SUCCESS) {
            refreshData();
            refresher.setEnabled(true);
            refresher.setRefreshing(true);
        }
        if(communication == CONNECTION_CLOSED) {
            refresher.setEnabled(false);
            setEnabled(false);
        }
        //Acro: 1    2    3    1    2    3    1    2    3    stabilize: 1    2    3    1    2    3    M1
        if(communication == INCOMING_DATA) {
            String newString = null;
            try {
                newString = bluetoothSerial.readString().toLowerCase();
            } catch (IOException e) {
                e.printStackTrace();
                bluetoothSerial.close();
                return;
            }
            if (newString.startsWith("acro")) {
                String[] valuesArray = newString.replace("acro: ", "").replace("stabilize: ","").split("    ");
                for (int i = 0; i < pidEditText.length; i++) {
                    pidEditText[i].setText(valuesArray[i]);
                }
                flightModeSpinner.setSelection(Integer.parseInt(newString.split("m")[1]));
                refresher.setRefreshing(false);
                setEnabled(true);
            }
            else if(!newString.startsWith("fifo")){
                Toast.makeText(getActivity(), "Inserisci il blocco del throttle", Toast.LENGTH_SHORT).show();
                setEnabled(false);
                refresher.setRefreshing(false);
            }
        }
    }

    private void sendData() {
        try {
            String message = "";
            for(int i = 0; i < pidEditText.length; i++) {
                Integer val = (int) (Float.parseFloat(pidEditText[i].getText().toString()) * 10);
                message = message.concat("#"+i+"="+val+"||");
            }
            message = message.concat("M"+flightModeSpinner.getSelectedItemPosition()+"||");
            Log.i(TAG, "sending "+message);
            bluetoothSerial.print(message);
        } catch (IOException e) {
            e.printStackTrace();
            bluetoothSerial.close();
        }
    }

    private void refreshData() {
        try {
            bluetoothSerial.print("P");
        } catch (IOException e) {
            e.printStackTrace();
            bluetoothSerial.close();
        }
        setEnabled(false);
    }

    public void setEnabled(boolean enabled) {
        for (EditText editText : pidEditText) {
            editText.setEnabled(enabled);
        }
        flightModeSpinner.setEnabled(enabled);
        saveMenuVisible = enabled;
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_send_pid) {
            sendData();
            return true;
        }
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (saveMenuVisible && bluetoothSerial.isConnected()) {
            menu.findItem(R.id.action_send_pid).setVisible(true);
        }
        else {
            menu.findItem(R.id.action_send_pid).setVisible(false);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void setBluetoothSerial(BluetoothSerial bluetoothSerial) {
        this.bluetoothSerial = bluetoothSerial;
    }
}
