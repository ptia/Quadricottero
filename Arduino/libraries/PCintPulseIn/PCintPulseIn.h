#include <Arduino.h>
#include <PCintBaseLib.h>

volatile long lastRisingMicros[19];
volatile long pulseLength[19];

void tick(uint8_t pin) {
	if (digitalRead(pin) == HIGH) {
    lastRisingMicros[pin] = micros();
  }
  else {
    pulseLength[pin] = micros() - lastRisingMicros[pin];
  }
}

long PCintPulseIn(uint8_t pin) {
	return pulseLength[pin];
}

void attachPulseIn(uint8_t pin) {
	pinMode(pin, INPUT);
	PCattachInterrupt(pin, tick, CHANGE);
}
