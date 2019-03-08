package beeprotect.de.beeprotect;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.rey.material.widget.ListView;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class ReportListActivity extends AppCompatActivity {

    ArrayList<ReportDataModel> ReportDataModels;
    ListView listView;
    private static ReportAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        listView=(ListView)findViewById(R.id.list);

        ReportDataModels= new ArrayList<>();

        ReportDataModels.add(new ReportDataModel("7 March, 15:00",10.00d,"50","20"));     //insert date time
        ReportDataModels.add(new ReportDataModel("2 March, 18:00",20.00d,"20","70"));     //insert date time
        ReportDataModels.add(new ReportDataModel("3 March, 17:00",30.00d,"10","60"));     //insert date time

        adapter= new ReportAdapter(ReportDataModels,getApplicationContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ReportDataModel ReportDataModel= ReportDataModels.get(position);

                /*Snackbar.make(view, ReportDataModel.getName()+"\n"+ReportDataModel.getType()+" API: "+ReportDataModel.getVersion_number(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();*/
                //Toast.makeText(ReportListActivity.this, ReportDataModel.getName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),ReportActivity.class);
                TestData.newInstance().setTemperatureDifference(ReportDataModel.tempDiff);
                TestData.newInstance().setCancerProbability(ReportDataModel.tensorflow);
                TestData.newInstance().setPainIntensity(ReportDataModel.pain);
                //intent.putExtra("report",ReportDataModel);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.report_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.delete) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
