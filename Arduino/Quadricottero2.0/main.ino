void setup() {
  Serial.begin(115200);
  setupDMP();
  setupRX();
  readPIDk();
  armMotors();
  //Do not start with hight throttle
  while (!(lowThrottle | throttleLock)) {
    Serial.println(F("Can't start with high throttle"));
    readRX();
  }
  /*Pin 13 is used to notify
  * the current flight mode
  */
  pinMode(13, OUTPUT);
  digitalWrite(13, flightMode);
}

void loop() {
  /*Read IMU and stop if
  * there are errors
  */
  if (updateIMU() == 1) {
    return;
  }
  readRX();
  if (throttleLock) {
    sendToMotors(MOTOR_MIN_SPEED);
    readSerialPreferences();
    if(shouldPrintBookmark()) {
      printBookmark();
    }
    return;
  }
  computePID();
  sendMotorSpeeds();
  log();
}
