package beeprotect.de.beeprotect;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.rey.material.widget.ListView;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.fragment.app.Fragment;
import beeprotect.de.beeprotect.utils.AzureMLUtil;

//import static beeprotect.de.beeprotect.ReportActivity.mAdapter;
import static beeprotect.de.beeprotect.ReportActivity.mClient;
import static beeprotect.de.beeprotect.ReportActivity.mToDoTable;
import static beeprotect.de.beeprotect.ReportActivity.refreshItemsFromTable;

public class ReportlisttabFragment extends Fragment {
    public static ArrayList<ReportDataModel> ReportDataModels = new ArrayList<>();
    ListView listView;
    public static ReportAdapter adapter;
    public static List<Report> allreports;
    public static ReportlisttabFragment newInstance() {
        ReportlisttabFragment fragment = new ReportlisttabFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_report_list, container, false);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        listView = (ListView)rootView.findViewById(R.id.list);
        TextView emptyText = rootView.findViewById(R.id.empty);

        /*if (mClient != null ) {
            mToDoTable = mClient.getTable(TestData.class);
            ReportActivity.refreshItemsFromTable();
        }*/

        final Handler refreshhandler = new Handler();
        refreshhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                if (mClient != null && ReportActivity.mAdapter != null){
                    refreshItemsFromTable(getContext());
                }
            }
        }, 3000);


        /*ReportDataModels.add(new ReportDataModel("7 March, 15:00",10.00d,"50","20"));     //insert date time
        ReportDataModels.add(new ReportDataModel("2 March, 18:00",20.00d,"20","70"));     //insert date time
        ReportDataModels.add(new ReportDataModel("3 March, 17:00",30.00d,"10","60"));*/     //insert date time

        if (adapter==null)
            adapter= new ReportAdapter(ReportDataModels,getContext());

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                generateTable();
                if (emptyText != null)
                emptyText.setVisibility(View.GONE);
            }
        }, 2000);

        listView.setAdapter(adapter);
        listView.setEmptyView(emptyText);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ReportDataModel ReportDataModel= ReportDataModels.get(position);

                /*Snackbar.make(view, ReportDataModel.getName()+"\n"+ReportDataModel.getType()+" API: "+ReportDataModel.getVersion_number(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();*/
                //Toast.makeText(ReportListActivity.this, ReportDataModel.getName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(),ReportActivity.class);
                TestData.newInstance().setTemperatureDifference(ReportDataModel.tempDiff);
                TestData.newInstance().setCancerProbability(ReportDataModel.tensorflow);
                TestData.newInstance().setPainIntensity(ReportDataModel.pain);
                //intent.putExtra("report",ReportDataModel);
                startActivity(intent);
            }
        });




        /*cancerprob = rootView.findViewById(R.id.cancerchances);
        //tempdiff = rootView.findViewById(R.id.tempdifference);
        //pain = rootView.findViewById(R.id.painpercent);


        Button exit = rootView.findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), HomeActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        mlresponse = rootView.findViewById(R.id.mlresponse);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Log.d("testdata",TestData.newInstance().toString());
        cancerprob.setText(TestData.newInstance().getCancerProbability());
        tempdiff.setText(""+TestData.newInstance().getTemperatureDifference());
        pain.setText(TestData.newInstance().getPainIntensity());

        //JSONObject body = new JSONObject();
        try {
            String requestbody = "{\"Inputs\":{\"three_step_input\":{\"ColumnNames\":[\"temperature\",\"chances of cancer\",\"pain percent\"],\"Values\":[["+TestData.newInstance().getTemperatureDifference()+","+TestData.newInstance().getCancerProbability()+","+TestData.newInstance().getPainIntensity()+"]]}},\"GlobalParameters\":{}}";

            AzureMLUtil mlUtil = new AzureMLUtil(AzureMLUtil.endPointURL,AzureMLUtil.key);
            String response = mlUtil.requestResponse(requestbody);
            Log.d("AzureResponse",response);
            JSONObject responseJSON = new JSONObject(response);
            String suggestion = responseJSON.getJSONObject("Results").getJSONObject("final_output").getJSONObject("value").getJSONArray("Values").getJSONArray(0).getString(0);
            Log.d("from json",suggestion);
            mlresponse.setText(suggestion);
            TestData.newInstance().setResponse(suggestion);
        }catch (JSONException je){
            Log.d("json exception",je.getMessage());
        }catch (Exception ex){
            ex.printStackTrace();
            //Log.d("json exception",ex.getMessage());
        }
        */
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.report_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                refreshItemsFromTable(getContext());
                if (adapter !=null)
                    adapter.notifyDataSetChanged();
                return true;
            /*case R.id.delete:
                Toast.makeText(getContext(), "DELETE ALL RECORDS", Toast.LENGTH_SHORT).show();
                return true;*/
            default:
                return true;
        }
    }

    public static void generateTable() {
        allreports = TestData.newInstance().getAllreports();

        for (Report report:allreports) {
            ReportDataModel model = new ReportDataModel(report.getCreatedAt(), Double.valueOf(report.getTemperatureDifference()), report.getCancerProbability(), report.getPainIntensity());
            if (!ReportDataModels.contains(model)) {
                ReportDataModels.add(model);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
