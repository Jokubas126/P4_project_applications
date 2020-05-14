int data;
void setup() {
  Serial.begin(9600);
}

void loop() {
  data = analogRead(A0);
}