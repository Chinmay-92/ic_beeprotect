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
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncContext;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.MobileServiceLocalStoreException;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore;
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import static beeprotect.de.beeprotect.ReportlisttabFragment.generateTable;
import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;
import com.microsoft.windowsazure.mobileservices.*;

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
    //public static AzureAdapter mAdapter;

    /**
     * Progress spinner to use for table operations
     */
    public static ProgressBar mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        try {
            mClient = new MobileServiceClient(
                    "https://beeprotectmobile.azurewebsites.net",
                    this
            );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        TestData item = new TestData("12","test","test","test");
        mClient.getTable(TestData.class).insert(item, new TableOperationCallback<TestData>() {
            public void onCompleted(TestData entity, Exception exception, ServiceFilterResponse response) {
                if (exception == null) {
                    // Insert succeeded
                    Log.d("success","success");
                } else {
                    Log.e("error",exception.getLocalizedMessage());
                    // Insert failed
                }
            }
        });
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
            /*mClient = new MobileServiceClient(
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
            });*/

//            AzureServiceAdapter.Initialize(this);

//            AzureServiceAdapter instance = AzureServiceAdapter.getInstance();

//            mClient = instance.getClient();

            // Get the Mobile Service Table instance to use

//            mToDoTable = mClient.getTable(TestData.class);

            // Offline Sync
            //mToDoTable = mClient.getSyncTable("TestData", TestData.class);

            //Init local storage
//            initLocalStore().get();

            //mTextNewToDo = (EditText) findViewById(R.id.textNewToDo);

            // Create an adapter to bind the items with the view
            //mAdapter = new AzureAdapter(this, R.layout.row_item);
            //ListView listViewToDo = (ListView) findViewById(R.id.listViewToDo);
            //listViewToDo.setAdapter(mAdapter);

            // Load the items from the Mobile Service
            //refreshItemsFromTable();

/*            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    addItem();
                }
            }, 2000);*/


            //refreshItemsFromTable();


            //AzureServiceAdapter.Initialize(this);

            //AzureServiceAdapter instance = AzureServiceAdapter.getInstance();



        } /*catch (MalformedURLException e) {
            Log.e("MalformedURL Error: ",e.getLocalizedMessage());
        } */catch (Exception e){
            Log.e("Error: ",e.getLocalizedMessage());
        }
    }

    private AsyncTask<Void, Void, Void> initLocalStore() throws MobileServiceLocalStoreException, ExecutionException, InterruptedException {

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    MobileServiceSyncContext syncContext = mClient.getSyncContext();

                    if (syncContext.isInitialized())
                        return null;

                    SQLiteLocalStore localStore = new SQLiteLocalStore(mClient.getContext(), "OfflineStore", null, 1);

                    Map<String, ColumnDataType> tableDefinition = new HashMap<String, ColumnDataType>();
                    tableDefinition.put("id", ColumnDataType.String);
                    tableDefinition.put("cancerProbability", ColumnDataType.String);
                    tableDefinition.put("temperatureDifference", ColumnDataType.String);
                    tableDefinition.put("painIntensity", ColumnDataType.String);
                    tableDefinition.put("response", ColumnDataType.String);
                    //tableDefinition.put("createdAt", ColumnDataType.Date);

                    localStore.defineTable("TestData", tableDefinition);

                    SimpleSyncHandler handler = new SimpleSyncHandler();

                    syncContext.initialize(localStore, handler).get();

                } catch (final Exception e) {
                    Log.e( "Error",e.getLocalizedMessage());
                }

                return null;
            }
        };

        return runAsyncTask(task);
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
                            //mAdapter.clear();

                            for (TestData item : results) {
                                //mAdapter.add(item);
                                if (!TestData.newInstance().getAllreports().contains(item))
                                {
                                    TestData.newInstance().addreport(item);
                                    if (!allreports.contains(item)) {
                                        allreports.add(item);
                                        if (adapter!=null)
                                        adapter.notifyDataSetChanged();
                                    }
                                }

                            }
                            generateTable();

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
        return mToDoTable.select("id","cancerProbability","temperatureDifference","painIntensity","response").execute().get();
    }

    public static void addItem() {
        if (mClient == null) {
            return;
        }

        // Create a new item
        String id = "123";
        //TestData.newInstance().setmId(id);
        final TestData item = TestData.newInstance();

        // Insert the new item
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final TestData testdata = addItemInTable(item);

                    UIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            //if(!entity.isComplete()){
                                //mAdapter.add(testdata);
                                //if (!item.getAllreports().contains(testdata))
                                {
                                    item.addreport(testdata);
                                    //if (!allreports.contains(item)) {
                                    //    allreports.add(item);
                                        /*if (adapter!=null)
                                            adapter.notifyDataSetChanged();*/
                                    //}
                                }
                            //}
                        }
                    });
                } catch (final Exception e) {
                    Log.e("Error: ",e.getLocalizedMessage());
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
