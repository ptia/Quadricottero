package it.ptia.quadricottero;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import static it.ptia.quadricottero.BluetoothSerial.Communication.*;

public class TerminalFragment extends Fragment implements BluetoothSerial.CommunicationReceiver{
    public static final String TAG = "TerminalFragment";
    private BluetoothSerial bluetoothSerial = new BluetoothSerial(this);
    Button sendButton;
    EditText sendText;
    TextView receivedTextView;
    ScrollView scroller;
    LogSaver logSaver;
    File logFile;
    String receivedData ="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout rootLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_terminal, null);
        if(bluetoothSerial == null) {
            bluetoothSerial = new BluetoothSerial(this);
        }
        sendButton = (Button) rootLayout.findViewById(R.id.send_button);
        sendText = (EditText) rootLayout.findViewById(R.id.send_message_edittext);
        receivedTextView = (TextView) rootLayout.findViewById(R.id.received_text_view);
        scroller = (ScrollView) rootLayout.findViewById(R.id.received_text_scroller);
        if(bluetoothSerial.isConnected()) {
            sendButton.setEnabled(true);
            sendText.setEnabled(true);
        }
        logFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                "LogQuadricottero.txt");
        try {
            logFile.createNewFile();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        logSaver = new LogSaver(logFile.getAbsolutePath());
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getBluetoothSerial().print(sendText.getText().toString());
                } catch (IOException e) {
                    bluetoothSerial.close();
                }
            }
        });
        return rootLayout;
    }

    @Override
    public void onNewCommunicationReceived(BluetoothSerial.Communication communication) {
        if (communication == CONNECTION_SUCCESS) {
            sendButton.setEnabled(true);
            sendText.setEnabled(true);
        }

        else if (communication == CONNECTION_CLOSED) {
            sendButton.setEnabled(false);
            sendText.setEnabled(false);
            logSaver.setRunning(false);
        }
        else if (communication == INCOMING_DATA) {
            String newString = null;
            try {
                newString = bluetoothSerial.readString()+"\n";
            } catch (IOException e) {
                e.printStackTrace();
                bluetoothSerial.close();
                return;
            }
            receivedData = receivedData.concat(newString);
            if(logSaver.isRunning()) {
                logSaver.appendString(newString);
            }
            //If the cumulative text to display is too long, remove its beginning
            if(receivedData.length()>2000) {
                receivedData = receivedData.substring(receivedData.length()-2000);
            }
            receivedTextView.setText(receivedData);
            scrollToBottom();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save_log) {
            new Thread(logSaver).start();
            Toast.makeText(getActivity(), "Salvando il log in: " + logFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
            getActivity().invalidateOptionsMenu();
            return true;
        }
        return false;
    }

    private void scrollToBottom()
    {
        scroller.post(new Runnable() {
            @Override
            public void run() {
                scroller.scrollTo(0, receivedTextView.getBottom());
            }
        });
    }

    public void closeLogSaver() {
        logSaver.setRunning(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem saveLogMenu = menu.findItem(R.id.action_save_log);
        if(!logSaver.isRunning() && bluetoothSerial.isConnected()) {
            saveLogMenu.setVisible(true);
        }
        else {
            saveLogMenu.setVisible(false);
        }
    }

    public BluetoothSerial getBluetoothSerial() {
        return bluetoothSerial;
    }

    public void setBluetoothSerial(BluetoothSerial bluetoothSerial) {
        this.bluetoothSerial = bluetoothSerial;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
}
