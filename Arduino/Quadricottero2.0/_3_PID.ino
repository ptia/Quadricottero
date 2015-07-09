void setPIDk() {
  //Stabilized PID
  for (int i = 0; i < 2; i++) {
    float pk = divideByTen(stabilizePIDk[i * 3]);
    float ik = divideByTen(stabilizePIDk[i * 3 + 1]);
    float dk = divideByTen(stabilizePIDk[i * 3 + 2]);
    stabilizePID[i].SetTunings(pk, ik, dk);
    stabilizePID[i].SetOutputLimits(-PR_ACRO_MAX, PR_ACRO_MAX);
    stabilizePID[i].SetSampleTime(PID_SAMPLE_TIME);
  }
  //Acro PID
  for (int i = 0; i < 3; i++) {
    float pk = divideByTen(acroPIDk[i * 3]);
    float ik = divideByTen(acroPIDk[i * 3 + 1]);
    float dk = divideByTen(acroPIDk[i * 3 + 2]);
    acroPID[i].SetTunings(pk, ik, dk);
    acroPID[i].SetOutputLimits(-MAX_MOTOR_DELTA, MAX_MOTOR_DELTA);
    acroPID[i].SetSampleTime(PID_SAMPLE_TIME);
  }
}

void computePID() {
  if (flightMode == STABILIZED_MODE) {
    //Compute angular velocities from stabilized PID
    acroSet[0] = stabilizePID[0].Compute(angle[0], stabilizeSet[0]);
    acroSet[1] = stabilizePID[1].Compute(angle[1], stabilizeSet[1]);
  }
  //Compute motor delta values from acro PID
  rawPRY[0] = acroPID[0].Compute(gyro[0], acroSet[0]);
  rawPRY[1] = acroPID[1].Compute(gyro[1], acroSet[1]);
  rawPRY[2] = acroPID[2].Compute(gyro[3], acroSet[2]);
}

float divideByTen(int n) {
  return ((float)n) / 10.0;
}

