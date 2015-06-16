package it.ptia.quadricottero;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by edoardo on 16/06/15.
 */
public class LogSaver implements Runnable {

    private boolean running = false;
    String newString = "";
    String file;

    public LogSaver(String file) {
        this.file = file;
    }

    @Override
    public void run() {
        FileWriter fileWriter = null;
        running = true;
        try {
            fileWriter = new FileWriter(file, true);
            while(running && !Thread.currentThread().isInterrupted()) {
                if(!newString.equals("")) {
                    fileWriter.write(newString);
                    fileWriter.flush();
                    newString = "";
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            setRunning(false);
        }
        try {
            fileWriter.close();
        }
        catch (IOException|NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void appendString(String newString) {
        this.newString += newString;
    }

    public boolean isRunning() {
        return running && !Thread.currentThread().isInterrupted();
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}