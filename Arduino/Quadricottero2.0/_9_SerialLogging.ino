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
    printNewLine();
  }
}

void printThrottle() {
  Serial.print(F("Throttle:"));
  Serial.print(throttle);
}

void printAcroSet() {
  Serial.print(F("Acro:"));
  Serial.print(F(" P:"));
  Serial.print(acroSet[0]);
  Serial.print(F(" R:"));
  Serial.print(acroSet[1]);
  Serial.print(F(" Y:"));
  Serial.print(acroSet[2]);
}

void printPRangles() {
  Serial.print(F("Angles:"));
  Serial.print(F(" P:"));
  Serial.print(angle[0]);
  Serial.print(F(" R:"));
  Serial.print(angle[1]);
}

void printStabilizeSet() {
  Serial.print(F("Stabilize:"));
  Serial.print(F(" P:"));
  Serial.print(stabilizeSet[0]);
  Serial.print(F(" R:"));
  Serial.print(stabilizeSet[1]);
}

void printGyroscopeData() {
  Serial.print(F("Gyro:"));
  Serial.print(F(" P:"));
  Serial.print(gyro[0]);
  Serial.print(F(" R:"));
  Serial.print(gyro[1]);
  Serial.print(F(" Y:"));
  Serial.print(gyro[2]);
}

void printRawPRY() {
  Serial.print(F("Delta motori:"));
  Serial.print(F(" P:"));
  Serial.print(rawPRY[0]);
  Serial.print(F(" R:"));
  Serial.print(rawPRY[1]);
  Serial.print(F(" Y:"));
  Serial.print(rawPRY[2]);
}

void printMotorSpeeds() {
  for (int i = 0; i < 4; i++) {
    Serial.print(F("S"));
    Serial.print(i);
    Serial.print(F(":"));
    Serial.print(speeds[i]);
    printTab();
  }
}

void printPIDk() {
  Serial.print("Acro: ");
  for (int i = 0; i < 9; i++) {
    Serial.print(acroPIDk[i]);
    printTab();
  }
  Serial.print("Stabilize: ");
  for (int i = 0; i < 6; i++) {
    Serial.print(stabilizePIDk[i]);
    printTab();
  }
  printNewLine();
}

void printTab() {
  Serial.print(F("\t"));
}

void printNewLine() {
  Serial.print(F("\n"));
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
