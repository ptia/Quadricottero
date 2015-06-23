void log() {
  if (showLog) {
    printThrottle();
    if (!lowThrottle) {
      printTab();
      if (flightMode == STABILIZED_MODE) {
        printStabilizeSet();
        printTab();
        printPRangles();
        printTab();
      }
      printAcroSet();
      printTab();
      printGyroscopeData();
      printTab();
      printRawPRY();
    }
    printVcc();
    printNewLine();
  }
}

void printThrottle() {
  Serial.write('T');
  Serial.print(throttle);
  Serial.write(' ');
}

void printAcroSet() {
  //A 0-1.4-7.7
  Serial.write('A');
  Serial.write(' ');
  Serial.print(acroSet[0]);
  Serial.write('-');
  Serial.print(acroSet[1]);
  Serial.write('-');
  Serial.print(acroSet[2]);
  Serial.write(' ');
}

void printPRangles() {
  //I 1.5-3.7
  Serial.write('I');
  Serial.write(' ');
  Serial.print(angle[0]);
  Serial.write('-');
  Serial.print(angle[1]);
  Serial.write(' ');
}

void printStabilizeSet() {
  //S 3.0-2.1
  Serial.write('S');
  Serial.write(' ');
  Serial.print(stabilizeSet[0]);
  Serial.write('-');
  Serial.print(stabilizeSet[1]);
}

void printGyroscopeData() {
  Serial.write('G');
  Serial.write(' ');
  Serial.print(gyro[0]);
  Serial.write(' ');
  Serial.print(gyro[1]);
  Serial.write(' ');
  Serial.print(gyro[2]);
  Serial.write(' ');
}

void printRawPRY() {
  Serial.write('D');
  Serial.write(' ');
  Serial.print(rawPRY[0]);
  Serial.write('-');
  Serial.print(rawPRY[1]);
  Serial.write('-');
  Serial.print(rawPRY[2]);
  Serial.write(' ');
}

void printMotorSpeeds() {
  for (int i = 0; i < 4; i++) {
    Serial.write('D');
    Serial.print(i);
    Serial.write(':');
    Serial.print(speeds[i]);
    printTab();
  }
}

void printlnPIDk() {
  Serial.print(F("Acro: "));
  for (int i = 0; i < 9; i++) {
    Serial.print(acroPIDk[i]);
    printTab();
  }
  Serial.print(F("Stabilize: "));
  for (int i = 0; i < 6; i++) {
    Serial.print(stabilizePIDk[i]);
    printTab();
  }
  printNewLine();
}

void printPIDk() {
  Serial.print(F("Acro: "));
  for (int i = 0; i < 9; i++) {
    Serial.print(acroPIDk[i]);
    printTab();
  }
  Serial.print(F("Stabilize: "));
  for (int i = 0; i < 6; i++) {
    Serial.print(stabilizePIDk[i]);
    printTab();
  }
}

void printTab() {
  Serial.write(' ');
  Serial.write(' ');
  Serial.write(' ');
  Serial.write(' ');
}

void printNewLine() {
  Serial.write(10);
}

void printBookmark() {
  Serial.println(F("<bookmark>"));
}

int potLastVal = 1;
boolean shouldPrintBookmark() {
  /*Pot can be in 3 position: off, mid and on
  * a bookmark is sent when the signal goes to off
  * to mid, and then to on. This is to avoid sending
  * lots of bookmarks for every loop when the pot is read as on.
  * Position mid is needed because a pot is an analog
  * device, and if it's in the middle its reading can change
  * from on to off. By using a third, un-registered value (mid)
  * we avoid this problem. Think of it as a debouncing system for
  * an analog pot
  */
  int potVal = map(PCintPulseIn(RXpins[4]), signalMin[4], signalMax[4], 3, 0);
  /*Recording this value as the 'old' one for the next time
  * notice how the value 'mid' (1) is ignored
  */
  if (potVal == 0) {
    potLastVal = 0;
  }
  if (potVal == 3) {
    if (potLastVal == 0) {
      potLastVal = 3;
      return true;
    }
    potLastVal = 3;
  }
  return false;
}

/*Measure input voltage code from here:
* https://code.google.com/p/tinkerit/wiki/SecretVoltmeter
*/
long printVcc() {
  long result;
  // Read 1.1V reference against AVcc
  ADMUX = _BV(REFS0) | _BV(MUX3) | _BV(MUX2) | _BV(MUX1);
  delay(2); // Wait for Vref to settle
  ADCSRA |= _BV(ADSC); // Convert
  while (bit_is_set(ADCSRA, ADSC));
  result = ADCL;
  result |= ADCH << 8;
  result = 1126400L / result; // Back-calculate AVcc in mV
  Serial.write('V');
  Serial.print(result, DEC);
}
