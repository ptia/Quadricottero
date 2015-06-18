void readSerialPreferences() {
  while (Serial.available()) {
    char newByte = Serial.read();
    if (newByte == 'M') {
      flightMode = Serial.parseInt();
      setPIDk();
      digitalWrite(13, flightMode);
      continue;
    }
    if (newByte == 'L') {
      showLog = Serial.parseInt();
      continue;
    }
    if (newByte == 'O') {
      angleOffset[0] = angle[0];
      angleOffset[1] = angle[1];
      continue;
    }
    PIDprefManager(newByte);
  }
}

void PIDprefManager(char newByte) {
  int pidElement;
  if (newByte == 'S') {
    savePIDk();
    printPIDk();
    Serial.println("Saved");
    return;
  }

  if (newByte == 'P') {
    printPIDk();
  }

  if (newByte == '#') {
    pidElement = Serial.parseInt();
  }

  if (Serial.read() == '=') {
    if (pidElement < 9) {
      acroPIDk[pidElement] = (float) Serial.parseInt() / 10.0;
    }
    else {
      stabilizePIDk[pidElement - 9] = (float) Serial.parseInt() / 10.0;
    }
    setPIDk();
  }
}
