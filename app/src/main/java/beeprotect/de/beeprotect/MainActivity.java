package beeprotect.de.beeprotect;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
  private static final String TAG = "LEDOnOff";
  
  Button btnOn, btnOff;
  Button next;
  TextView text, bluetooth_val,mtext;


  private static final Random RANDOM = new Random();
  private LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
  private LineGraphSeries<DataPoint> seriesRight = new LineGraphSeries<DataPoint>();
  private int lastX = 0;
  List<Float> tempDiff = new ArrayList<>();

  private BluetoothAdapter btAdapter = null;
  private BluetoothSocket btSocket = null;
  private OutputStream outStream = null;
  private InputStream inStream = null;
  List<String> values = new ArrayList<String>();
  List<String> valuesRight = new ArrayList<String>();

  DataInputStream dataInputStream = null;
  // Intent request codes
  private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
  private static final int REQUEST_ENABLE_BT = 3;
  
  // Well known SPP UUID
  private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

  // Insert your bluetooth devices MAC address
  private static String address = "98:D3:51:F5:E1:45";
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mtext = findViewById(R.id.waitText);
    /*btnOn = (Button) findViewById(R.id.btnOn);
    btnOff = (Button) findViewById(R.id.btnOff);
    text = (TextView) findViewById(R.id.textEdit);
    bluetooth_val = (TextView) findViewById(R.id.bluetooth_values);
*/
    /*btnOn.setEnabled(false);
    btnOff.setEnabled(false);
*/
changeText(); // timer for the text below
      next = findViewById(R.id.temptotensor);

      next.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          double diff = calculateAverage(tempDiff);
          TestData.newInstance().setTemperatureDifference(Math.abs(diff));
          Log.d("testdata",""+Math.abs(diff));
          Toast.makeText(MainActivity.this, "temp diff : "+Math.abs(diff), Toast.LENGTH_SHORT).show();
          Intent intent=new Intent(getApplicationContext(), TensorflowActivity.class);
          startActivity(intent);
          finish();
        }

      });
      bluetooth_val = (TextView) findViewById(R.id.bluetooth_values);

      btAdapter = BluetoothAdapter.getDefaultAdapter();

    checkBTState();

    Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

    final Handler handler = new Handler();
    final Handler delayed = new Handler();
    final int delay = 6000; //milliseconds
    final GraphView graph = (GraphView) findViewById(R.id.graph);
    final GraphView graphRight = (GraphView) findViewById(R.id.graphright);

    //values.add("21");

    Runnable getTemp = new Runnable() {
      public void run() {
        //do something
        try {
          int bytes = 0;
          Integer[] arr = null;
          byte[] buffer = new byte[256];
          if (dataInputStream!=null)
          bytes = dataInputStream.read(buffer);
          String readMessage = new String(buffer, 0, bytes);
          String readMessageRight = new String(buffer, 0, bytes);
          Log.d("readmessage",readMessage);
            bluetooth_val.setText(readMessage);
            if (readMessage.contains("\n")) {
              readMessageRight = readMessage.split("\n")[1];
              readMessage = readMessage.split("\n")[0];
              Log.d("split message", readMessage);
            }
              Log.d("Read Message", readMessage);
              Log.d("Read MessageRight", readMessageRight);
            if (readMessage.trim().length()==5 && Float.valueOf(readMessage.trim())>0f && Float.valueOf(readMessage.trim())<=45f) {
              values.add(readMessage.trim());
            }
            if (readMessageRight.trim().length()==5 && Float.valueOf(readMessage.trim())>0f && Float.valueOf(readMessage.trim())<=45f ) {
              valuesRight.add(readMessageRight.trim());
            }
            if (readMessage.isEmpty())
            {}
            else
                tempDiff.add(Float.valueOf(readMessage.trim())-Float.valueOf(readMessageRight.trim()));
          //add_values_to_array(bytes);
          bytes = 0;
          //Toast.makeText(getApplicationContext(), readMessage, Toast.LENGTH_SHORT).show();
        } catch (IOException io) {
          io.printStackTrace();
        } catch (NullPointerException ne) {
          ne.printStackTrace();
        } catch( Exception e){
          e.printStackTrace();
        } catch (Error error){
            error.printStackTrace();
        }
        handler.postDelayed(this, delay);
      }
    };

    //if (btSocket!=null)
    (new Handler()).postDelayed(getTemp, 1000);

      if (values.size() > 0) {
        addEntry();
      }
      graph.addSeries(series);

    if (valuesRight.size() > 0) {
      addEntryRight();
    }
      graphRight.addSeries(seriesRight);

      Viewport viewport = graph.getViewport();
      viewport.setYAxisBoundsManual(true);
      viewport.setMinY(15);
      viewport.setMaxY(40);
      viewport.setMaxX(4);
      viewport.setScalable(true);

    Viewport viewportRight = graphRight.getViewport();
    viewportRight.setYAxisBoundsManual(true);
    viewportRight.setMinY(15);
    viewportRight.setMaxY(40);
    viewportRight.setMaxX(4);
    viewportRight.setScalable(true);

  }


  @Override
  protected void onResume() {
    super.onResume();

    new Thread(new Runnable() {
      @Override
      public void run() {
        for(int i=0; i<100;i++) {
          runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (values.size() > 0) {
                  addEntry();
                  addEntryRight();
                }
            }
          });
          try
          {
            Thread.sleep(10000);
          }
          catch (InterruptedException e){}

        }
      }
    }).start();
  }

  private double calculateAverage(List <Float> marks) {
    Float sum = 0f;
    if(!marks.isEmpty()) {
      for (Float mark : marks) {
        sum += mark;
      }
      return sum.doubleValue() / marks.size();
    }
    return sum;
  }

  private int counter = 0;
  private void addEntry() {
      Log.d("entry",values.toString());
      //if (values.size() > counter) {
      series.appendData(new DataPoint(lastX++, Double.valueOf(values.get(values.size() - 1))), true, 46);
          //Log.d("adding into graph","added"+values.get(values.size()-1));
      //}
  }

  private void addEntryRight() {
    Log.d("entry",values.toString());
    //if (values.size() > counter) {
    seriesRight.appendData(new DataPoint(lastX++, Double.valueOf(valuesRight.get(valuesRight.size() - 1))), true, 46);
    //Log.d("adding into graph","added"+values.get(values.size()-1));
    //}
  }

  public void ledOn(View v){

      for(int i=0; i<values.size(); i++)
      {
          bluetooth_val.setText(values.get(i));
      }

	  //sendData("1");
      //Toast msg = Toast.makeText(getBaseContext(), "LED is ON", Toast.LENGTH_SHORT);
      //msg.show();
  }
  
  public void ledOff(View v){
	  sendData("0");
      //Toast msg = Toast.makeText(getBaseContext(), "LED is OFF", Toast.LENGTH_SHORT);
      //msg.show();
  }



  public void connectToDevice(String adr) {
    super.onResume();
    
    //enable buttons once connection established.
   // btnOn.setEnabled(true);
    //btnOff.setEnabled(true);
    
    
    
    // Set up a pointer to the remote node using it's address.
    BluetoothDevice device = btAdapter.getRemoteDevice(adr);
    
    // Two things are needed to make a connection:
    //   A MAC address, which we got above.
    //   A Service ID or UUID.  In this case we are using the
    //     UUID for SPP.
    try {
      btSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
    } catch (IOException e) {
      errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
    }
  
    // Discovery is resource intensive.  Make sure it isn't going on
    // when you attempt to connect and pass your message.
    btAdapter.cancelDiscovery();
  
    // Establish the connection.  This will block until it connects.
    try {
      btSocket.connect();
    } catch (IOException e) {
      try {
        btSocket.close();
      } catch (IOException e2) {
        errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
      }
    }
    
    // Create a data stream so we can talk to server.
    try {
    outStream = btSocket.getOutputStream();
    inStream = btSocket.getInputStream();

    dataInputStream = new DataInputStream(inStream);

    } catch (IOException e) {
      errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
    }
  }
  
  private void checkBTState() {
    // Check for Bluetooth support and then check to make sure it is turned on

    // Emulator doesn't support Bluetooth and will return null
    if(btAdapter==null) { 
      errorExit("Fatal Error", "Bluetooth Not supported. Aborting.");
    } else {
      if (btAdapter.isEnabled()) {
        Log.d(TAG, "...Bluetooth is enabled...");
      } else {
        //Prompt user to turn on Bluetooth
        Intent enableBtIntent = new Intent(btAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
      }
    }
  }

  private void errorExit(String title, String message){
    Toast msg = Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_SHORT);
    msg.show();
    finish();
  }

  private void sendData(String message) {
    byte[] msgBuffer = message.getBytes();
    try {
      int bytes = 0;
      byte[] buffer = new byte[256];
      //outStream.write(msgBuffer);
      bytes = dataInputStream.read(buffer);
      String readMessage = new String(buffer, 0 , bytes);
      bytes = 0;
      //Toast.makeText(getApplicationContext(),readMessage,Toast.LENGTH_SHORT).show();
    } catch (IOException e) {
      String msg = "In onResume() and an exception occurred during write: " + e.getMessage();      
      errorExit("Fatal Error", msg);       
    }
  }
  
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
      switch (requestCode) {
      case REQUEST_CONNECT_DEVICE_SECURE:
          // When DeviceListActivity returns with a device to connect
          if (resultCode == Activity.RESULT_OK) {
              connectDevice(data, true);
          }
          break;
      }
  }
  
  private void connectDevice(Intent data, boolean secure) {
      // Get the device MAC address
      address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
      //text.setText("Device Address: " + address);
      connectToDevice(address);
      // Get the BluetoothDevice object
      BluetoothDevice device = btAdapter.getRemoteDevice(address);
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.option_menu, menu);
      return true;
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
      Intent serverIntent = null;
      switch (item.getItemId()) {
      case R.id.secure_connect_scan:
          // Launch the DeviceListActivity to see devices and do scan
          serverIntent = new Intent(this, DeviceListActivity.class);
          startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
          return true;
      }
      return false;
  }


  private void changeText() {
    int delayTime = 20000; // 2 min
    mtext.postDelayed(new Runnable() {
      @Override
      public void run() {
        mtext.setText("Please remove sensors \n and proceed with the next test");
      }
    }, delayTime);
  }
}
