package beeprotect.de.beeprotect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/*import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;*/

import java.util.Random;

public class GraphViewing extends AppCompatActivity {

    private static final Random RANDOM = new Random();
    //private LineGraphSeries<DataPoint> series;
    private int lastX = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_view);

        //GraphView graph = (GraphView) findViewById(R.id.graph);
        //series = new LineGraphSeries<DataPoint>();
        //addEntry();
        //graph.addSeries(series);

        //Viewport viewport = graph.getViewport();
        //viewport.setYAxisBoundsManual(true);
        //viewport.setMinY(0);
        //viewport.setMaxX(10);
        //viewport.setScalable(true);
    }

   /* @Override
    protected void onResume() {
        super.onResume();

        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<100;i++) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //addEntry();
                        }
                    });
                    try
                    {
                        Thread.sleep(600);
                    }
                    catch (InterruptedException e){}

                }
            }
        }).start();
    }*/
   /* private void addEntry(){
      //  series.appendData(new DataPoint(lastX++, RANDOM.nextDouble() * 10d), true, 10);
    }*/
}
