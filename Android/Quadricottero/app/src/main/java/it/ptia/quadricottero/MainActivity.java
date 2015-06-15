package it.ptia.quadricottero;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

import static it.ptia.quadricottero.BluetoothSerial.Communication.*;


public class MainActivity extends AppCompatActivity implements BluetoothSerial.CommunicationReceiver {
    BluetoothSerial bluetoothSerial = new BluetoothSerial(this);
    BluetoothAdapter bluetoothAdapter;
    MenuItem connectMenu;
    Button sendButton;
    EditText sendText;
    TextView receivedTextView;
    ScrollView scroller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        sendButton = (Button) findViewById(R.id.send_button);
        sendText = (EditText) findViewById(R.id.send_message_edittext);
        receivedTextView = (TextView) findViewById(R.id.received_text_view);
        scroller = (ScrollView) findViewById(R.id.received_text_scroller);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothSerial.print(sendText.getText().toString());
            }
        });
    }

    @Override
    public void onNewCommunicationReceived(BluetoothSerial.Communication communication) {
        if (communication == CONNECTION_SUCCESS) {
            setTitle("Quadricottero @ "+bluetoothSerial.getDevice().getName());
            Toast.makeText(this,"Connesso",Toast.LENGTH_SHORT).show();
            sendButton.setEnabled(true);
            sendText.setEnabled(true);
            connectMenu.setIcon(getResources().getDrawable(R.drawable.ic_action_disconnect));
        }
        else if (communication == CONNECTION_ERROR) {
            Toast.makeText(this,
                    "Connessione con " +bluetoothSerial.getDevice().getName()+" fallita",
                    Toast.LENGTH_SHORT).show();
        }
        else if (communication == OUTPUT_ERROR) {
            bluetoothSerial.close();
        }
        else if (communication == CONNECTION_CLOSED) {
            connectMenu.setIcon(getResources().getDrawable(R.drawable.ic_bt_connect));
            sendButton.setEnabled(false);
            sendText.setEnabled(false);
            Toast.makeText(this,"Disconnesso",Toast.LENGTH_SHORT).show();
            setTitle("Quadricottero");
        }
        else if (communication == INCOMING_DATA) {
            receivedTextView.append(bluetoothSerial.readString()+"\n");
            scrollToBottom();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "È necessario attivare il bluetooth", Toast.LENGTH_SHORT).show();
            finish();
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

    private void scrollToBottom()
    {
        scroller.post(new Runnable()
        {
            public void run()
            {
                scroller.smoothScrollTo(0, receivedTextView.getBottom());
            }
        });
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        connectMenu = menu.findItem(R.id.action_bt_connect);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_bt_connect) {
            if(!bluetoothSerial.isConnected()){
                chooseDevice();
            }
            else {
                bluetoothSerial.close();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
