void armMotors() {
  for (int i = 0; i < 4; i++) {
    motors[i].attach(motorPins[i]);
  }
  sendToMotors(MOTOR_ARM);
  delay(2500);
  sendToMotors(MOTOR_MIN_SPEED);
}

void sendMotorSpeeds() {
  int s0, s1, s2, s3;
  //Apply PRY delta to motor speeds
  //Apply     throttle   pitch       roll        yaw
  speeds[0] = throttle - rawPRY[0] + rawPRY[1] + rawPRY[2];
  speeds[1] = throttle + rawPRY[0] + rawPRY[1] - rawPRY[2];
  speeds[2] = throttle + rawPRY[0] - rawPRY[1] + rawPRY[2];
  speeds[3] = throttle - rawPRY[0] - rawPRY[1] - rawPRY[2];
  for (int i = 0; i < 4; i++) {
    motors[i].writeMicroseconds(MOTOR_MIN_SPEED + speeds[i]);
  }
}
void sendToMotors(int uS) {
  for (int i = 0; i < 4; i++) {
    motors[i].writeMicroseconds(uS);
  }
}
