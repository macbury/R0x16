var PIN_MOTOR = 4;

var Robot = function() {
  pinMode(PIN_MOTOR, INPUT);
}

Robot.prototype.loop = function() {
  digitalWrite(PIN_MOTOR, HIGH);
}