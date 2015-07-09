void setup() {
  Serial.begin(115200);
  setupDMP();
  setupRX();
  readPIDk();
  armMotors();
  //Start only if the throttle lock is enabled
  while (throttle != THROTTLE_LOCK) {
    Serial.println(F("Insert the throttle lock to start"));
    readRX();
  }
  //Pin 13 is used to notify the current flight mode
  pinMode(13, OUTPUT);
  digitalWrite(13, flightMode);
  Serial.println(F("Launching loop"));
}

void loop() {
  //Read IMU and stop if there are errors
  if (updateIMU() == 1) {
    return;
  }
  readRX();
  if (throttle == THROTTLE_LOCK) {
    sendToMotors(MOTOR_MIN_SPEED);
    readSerialPreferences();
    if (shouldPrintBookmark()) {
      printBookmark();
    }
    printFreeRam();
    return;
  }
  if (throttle == LOW_THROTTLE) {
    //Do not apply PID
    rawPRY[0] = 0;
    rawPRY[1] = 0;
    rawPRY[2] = 0;
  }
  else {
    computePID();
  }
  sendMotorSpeeds();
  log();
}
