int data;
int time = 0;
void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:
  data = analogRead(A0);
  Serial.print(String(data);
}