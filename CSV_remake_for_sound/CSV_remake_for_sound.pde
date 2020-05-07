BufferedReader reader;
String line;

float soundAngleStart = -60;    
float overallAngleChange = 0;  //Can be both positive and negative
float soundChangeStartTime = 93900;      //Milliseconds
float soundChangeEndTime = 101700;    //Milliseconds
float soundXAngle;     // angle that sound should come from when facing forward
int soundYAngle = 30;    // from 90 to -40

StringList timeList = new StringList();
StringList directionList = new StringList();

int counter = 0;

void setup() {
  frameRate(1000);
  reader = createReader("data/head_direction_recording.csv");
  System.out.println("Started");
  soundXAngle = soundAngleStart;
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
      //System.out.println(counter);
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
    if(int(pieces[1]) > soundChangeStartTime && int(pieces[1]) < soundChangeEndTime)
    {
      
      soundXAngle = soundAngleStart + ((int(pieces[1]) - soundChangeStartTime) / (soundChangeEndTime - soundChangeStartTime)) * overallAngleChange;
      System.out.println("soundXAngle:" + soundXAngle + " Applied change in degrees: " + (((int(pieces[1]) - soundChangeStartTime) / (soundChangeEndTime - soundChangeStartTime)) * overallAngleChange));
    }
    
    timeList.append("time " + float(pieces[1]));
  } else if (name.equals("direction")){
    float xValue = calculateXBias(float(pieces[1]) + soundXAngle); //<>//
    float yValue = calculateYBias(float(pieces[2]) + soundYAngle);
    directionList.append("direction " + xValue + " " + yValue); //<>//
  }
}

float calculateXBias(float value) {
  if (value > 180){
    value = value - 360;
  } else if (value < -180){
    value = value + 360;
  }
  return value;
}

// to be done
float calculateYBias(float value) {
  if (value > 90) {
    value =  90;
  } else if (value < -40) {
    value = -40;
  }
  return value;
}

void writeToFile() {
  Table table = new Table();
  table.addColumn("");
  for(int i = 0; i < timeList.size(); i++) {
    TableRow timeRow = table.addRow();
    timeRow.setString("", timeList.get(i));
    TableRow directionRow = table.addRow();
    directionRow.setString("", directionList.get(i));
  }
  saveTable(table, "data/sound_direction_recording.csv");
}
