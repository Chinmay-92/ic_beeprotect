package beeprotect.de.beeprotect;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.ArcMotion;
import androidx.transition.ChangeBounds;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;
import beeprotect.de.beeprotect.utils.AzureMLUtil;

import static beeprotect.de.beeprotect.ReportlisttabFragment.adapter;
import static beeprotect.de.beeprotect.ReportlisttabFragment.allreports;
import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;

public class ReportActivity extends AppCompatActivity {
    TextView mlresponse;
    TextView cancerprob,tempdiff,pain;

    public static Handler UIHandler;

    static
    {
        UIHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Mobile Service Client reference
     */
    public static MobileServiceClient mClient;
    public static MobileServiceTable<TestData> mToDoTable;
    public static AzureAdapter mAdapter;

    /**
     * Progress spinner to use for table operations
     */
    public static ProgressBar mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        mProgressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);

        // Initialize the progress bar
        mProgressBar.setVisibility(ProgressBar.GONE);

        cancerprob = findViewById(R.id.cancerchances);
        tempdiff = findViewById(R.id.tempdifference);
        pain = findViewById(R.id.painpercent);


        Button exit = findViewById(R.id.exit);
        /*final ViewGroup transitionsContainer = (ViewGroup) findViewById(R.id.reportLayout);
        Transition changeBounds = new ChangeBounds();
        changeBounds.setPathMotion(new ArcMotion());
        changeBounds.setDuration(500);
        TransitionManager.beginDelayedTransition(transitionsContainer, changeBounds);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) exit.getLayoutParams();
        boolean isReturnAnimation = true;
        *//*params.gra= isReturnAnimation ? (Gravity.LEFT | Gravity.TOP) :
                (Gravity.BOTTOM | Gravity.RIGHT);*//*
        exit.setLayoutParams(params);*/

        /*ShapeDrawable rounded_corners = (ShapeDrawable)exit.getBackground();
        rounded_corners.getPaint().setColor(Color.RED);*/
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mlresponse = findViewById(R.id.mlresponse);

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

        try {
            // Create the Mobile Service Client instance, using the provided

            // Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://beeprotectmobile.azurewebsites.net",
                    this).withFilter(new ProgressFilter());

            // Extend timeout from default of 10s to 20s
            mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(20, TimeUnit.SECONDS);
                    client.setWriteTimeout(20, TimeUnit.SECONDS);
                    return client;
                }
            });

            // Get the Mobile Service Table instance to use

            mToDoTable = mClient.getTable(TestData.class);

            addItem();
            // Offline Sync
            //mToDoTable = mClient.getSyncTable("TestData", TestData.class);

            //Init local storage
            //initLocalStore().get();

            //mTextNewToDo = (EditText) findViewById(R.id.textNewToDo);

            // Create an adapter to bind the items with the view
            mAdapter = new AzureAdapter(this, R.layout.row_item);
            //ListView listViewToDo = (ListView) findViewById(R.id.listViewToDo);
            //listViewToDo.setAdapter(mAdapter);

            // Load the items from the Mobile Service
            refreshItemsFromTable();

        } catch (MalformedURLException e) {
            Toast.makeText(this, ""+(new Exception("There was an error creating the Mobile Service. Verify the URL")), Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            Toast.makeText(this, "Error: "+e, Toast.LENGTH_SHORT).show();
        }
    }

    public static class ProgressFilter implements ServiceFilter {

        @Override
        public ListenableFuture<ServiceFilterResponse> handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback) {

            final SettableFuture<ServiceFilterResponse> resultFuture = SettableFuture.create();


            UIHandler.post(new Runnable() {

                @Override
                public void run() {
                    if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.VISIBLE);
                }
            });

            ListenableFuture<ServiceFilterResponse> future = nextServiceFilterCallback.onNext(request);

            Futures.addCallback(future, new FutureCallback<ServiceFilterResponse>() {
                @Override
                public void onFailure(Throwable e) {
                    resultFuture.setException(e);
                }

                @Override
                public void onSuccess(ServiceFilterResponse response) {
                    UIHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.GONE);
                        }
                    });

                    resultFuture.set(response);
                }
            });

            return resultFuture;
        }
    }

    /**
     * Refresh the list with the items in the Table
     */
    public static void refreshItemsFromTable() {

        // Get the items that weren't marked as completed and add them in the
        // adapter

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    final List<TestData> results = refreshItemsFromMobileServiceTable();

                    //Offline Sync
                    //final List<TestData> results = refreshItemsFromMobileServiceTableSyncTable();

                    UIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.clear();

                            for (TestData item : results) {
                                mAdapter.add(item);
                                if (!TestData.newInstance().getAllreports().contains(item))
                                {
                                    TestData.newInstance().addreport(item);
                                    allreports.add(item);
                                    adapter.notifyDataSetChanged();
                                }

                            }
                        }
                    });
                } catch (final Exception e){
                    Log.e("error Reportactivity",e.getLocalizedMessage());
                    /*Toast.makeText(getApplicationContext(), "Error: "+e, Toast.LENGTH_SHORT).show();*/
                }

                return null;
            }
        };

        runAsyncTask(task);
    }

    /**
     * Refresh the list with the items in the Mobile Service Table
     */

    public static List<TestData> refreshItemsFromMobileServiceTable() throws ExecutionException, InterruptedException {
        return mToDoTable.where().field("complete").
                eq(val(false)).execute().get();
    }

    public static void addItem() {
        if (mClient == null) {
            return;
        }

        // Create a new item
        final TestData item = TestData.newInstance();

        // Insert the new item
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final TestData entity = addItemInTable(item);

                    UIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //if(!entity.isComplete()){
                                mAdapter.add(entity);
                                if (!item.getAllreports().contains(entity))
                                {
                                    item.addreport(entity);
                                    allreports.add(item);
                                    adapter.notifyDataSetChanged();
                                }
                            //}
                        }
                    });
                } catch (final Exception e) {
/*                    Toast.makeText(getApplicationContext(), "Error: "+e, Toast.LENGTH_SHORT).show();*/
                }
                return null;
            }
        };

        runAsyncTask(task);

        //mTextNewToDo.setText("");
    }

    /**
     * Run an ASync task on the corresponding executor
     * @param task
     * @return
     */
    public static AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    /**
     * Add an item to the Mobile Service Table
     *
     * @param item
     *            The item to Add
     */
    public static TestData addItemInTable(TestData item) throws ExecutionException, InterruptedException {
        TestData entity = mToDoTable.insert(item).get();
        return entity;
    }
}
