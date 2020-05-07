
// import everything necessary to make sound.
import ddf.minim.*;
import ddf.minim.ugens.*;

// create all of the variables that will need to be accessed in
// more than one methods (setup(), draw(), stop()).
Minim minim;
AudioOutput out;
FilePlayer filePlayer;
AudioRecorder recorder;
boolean recorded;

String fileName = "defibrillator -60.wav";
String directionalDataFileName = "remapped_recording -60.csv";

BufferedReader reader;
String line;
int time = 0;
int iterator = 0;

int panSwitch;

String[] xValues = new String[50000];
float[] yValues = new float[50000];

// setup is run once at the beginning
void setup()
{
  // initialize the drawing window
  size( 512, 200, P2D );
  frameRate(1000);
   
  
  // initialize the minim and out objects
  minim = new Minim( this );
  // because we are using a Pan UGen, we need a stereo output.
  out = minim.getLineOut( Minim.STEREO, 1024 , 44100);
  
  // create new instances of any UGen objects as necessary
  filePlayer = new FilePlayer( minim.loadFileStream(fileName) );
  filePlayer.setSampleRate(44100);
  filePlayer.loop(0);
      
  // patch everything together up to the final output
  
  filePlayer.patch(out);
  out.setPan(1);
  panSwitch = 0;
  
  reader = createReader(directionalDataFileName); 
  
  
  boolean keepLooping = true;
  int lineNumber = 0;
  while(keepLooping)
  {
    try {
      line = reader.readLine();
    } catch (IOException e) {
      e.printStackTrace();
      line = null;
    }
    if (line == null) {
      // Stop reading because of an error or file is empty
      keepLooping = false;
    } else {
      String[] pieces = split(line, " ");
      xValues[lineNumber] = pieces[0];
      yValues[lineNumber] = float(pieces[1]);
      lineNumber++;
    }
  }
  
  recorder = minim.createRecorder(out, "myrecording.wav");
  recorder.beginRecord();
}

// draw is run many times
void draw()
{
  while(xValues[iterator] != null)
  {
    System.out.println("x: " + xValues[iterator] + " y: " + yValues[iterator] + " real time: " + millis());
    if(xValues[iterator].equals("time"))
    {
      time = int(yValues[iterator]);
      while(time > millis())
      {
         delay(1); 
      }
    }
    else if (xValues[iterator].equals("direction"))
    { 
      out.setPan(yValues[iterator]); 
      System.out.println(out.getBalance());
    }  
    iterator++;
  }
  recorder.endRecord();
  noLoop(); 
  //panSwitch++;    //used for testing panning
  //out.setPan((panSwitch % 100 - 50) / 50.0f); 


  /*
  // erase the window to black
  background( 0 );
  // draw using a white stroke
  stroke( 255 );
  // draw the waveforms
  for( int i = 0; i < out.bufferSize() - 1; i++ )
  {
    // find the x position of each buffer value
    float x1  =  map( i, 0, out.bufferSize(), 0, width );
    float x2  =  map( i+1, 0, out.bufferSize(), 0, width );
    // draw a line from one buffer position to the next for both channels
    line( x1, 50 + out.left.get(i)*50, x2, 50 + out.left.get(i+1)*50);
    line( x1, 150 + out.right.get(i)*50, x2, 150 + out.right.get(i+1)*50);
  }  
  */
}
