BufferedReader reader;
String line;

StringList timeList = new StringList();
StringList directionList = new StringList();

int counter = 0;

String recordingName = "data/sound_direction_recording.csv";

void setup() {
  frameRate(1000);
  reader = createReader(recordingName);
  System.out.println("Started");
}

void draw() {
  try {
    line = reader.readLine();
    if (line == null || line == "") {
      writeToFile();
      System.out.println("Written to file");
      stop();
      noLoop();
    } else {
      System.out.println(counter);
      counter++;
      lineCheck(line);
    }
  } catch (IOException e) {
    e.printStackTrace();
    stop();
    noLoop();
  }
}

void lineCheck(String line) {
  String[] pieces = split(line, " ");
  String name = pieces[0];
  
  if (name.equals("time")) {
    timeList.append("time " + float(pieces[1]));
  } else if (name.equals("direction")){
    directionList.append("direction " + mapXAxis(float(pieces[1])) + " " + float(pieces[2]));
  }
}

float mapXAxis(float angle) {
  float singular = 0.0f;
  
  if (angle >= -180 && angle < -90)
    singular = -((angle + 180) / 90);
  else if (angle >= -90 && angle <= 90)
    singular = angle / 90;
  else if (angle > 90 && angle <= 180)
    singular = -((angle - 180) / 90);
  
  return singular;
}

void writeToFile() {
  Table table = new Table();
  table.addColumn("");
  for(int i = 0; i < timeList.size() - 1; i++) {
    TableRow timeRow = table.addRow();
    timeRow.setString("", timeList.get(i));
    TableRow directionRow = table.addRow();
    directionRow.setString("", directionList.get(i));
  }
  saveTable(table, "data/remapped_recording.csv");
}
