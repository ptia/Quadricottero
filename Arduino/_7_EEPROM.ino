#define PID_K_FIRST_ADDR 1

void readPIDk() {
  uint8_t addr = PID_K_FIRST_ADDR;
  for (int i = 0; i < 9; i++) {
    acroPIDk[i] = ((float)EEPROM.read(addr)) / 10.0;
    addr++;
  }
  for (int i = 0; i < 6; i++) {
    stabilizePIDk[i] = ((float)EEPROM.read(addr)) / 10.0;
    addr++;
  }
  setPIDk();
}

void savePIDk() {
  uint8_t addr = PID_K_FIRST_ADDR;
  for (int i = 0; i < 9; i++) {
    EEPROM.write(addr, (uint8_t)(acroPIDk[i] * 10));
    addr++;
  }
  for (int i = 0; i < 6; i++) {
    EEPROM.write(addr, (uint8_t)(stabilizePIDk[i] * 10));
    addr++;
  }
}