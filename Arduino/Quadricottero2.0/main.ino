void setup() {
  Serial.begin(115200);
  setupDMP();
  setupRX();
  readPIDk();
  armMotors();
  //Start only if the throttle lock is enabled
  while (!throttleLock) {
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
  if (throttleLock) {
    sendToMotors(MOTOR_MIN_SPEED);
    readSerialPreferences();
    if (shouldPrintBookmark()) {
      printBookmark();
    }
    return;
  }
  if(lowThrottle) {
    //Do not apply PID
    sendToMotors(throttle);
  }
  else {
    computePID();
  }
  sendMotorSpeeds();
  log();
}
