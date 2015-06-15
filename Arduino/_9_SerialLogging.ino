void log() {
  if(showLog) {
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
