package beeprotect.de.beeprotect;

import android.app.AlertDialog;
import android.content.Context;
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
import java.util.ArrayList;
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
import me.itangqi.waveloadingview.WaveLoadingView;

import static beeprotect.de.beeprotect.ReportlisttabFragment.adapter;
import static beeprotect.de.beeprotect.ReportlisttabFragment.allreports;
import static beeprotect.de.beeprotect.ReportlisttabFragment.generateTable;
import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;
import com.microsoft.windowsazure.mobileservices.*;

public class ReportActivity extends AppCompatActivity {
    TextView mlresponse;
    TextView tempdiff;
    TextView duration;
    WaveLoadingView waveLoadingCancer, waveLoadingPain;

    public static Handler UIHandler;
    /**
     * Mobile Service Client reference
     */
    public static MobileServiceClient mClient;

    /**
     * Mobile Service Table used to access data
     */
    public static  MobileServiceTable<Report> mToDoTable;

    //Offline Sync
    /**
     * Mobile Service Table used to access and Sync data
     */
    //private MobileServiceSyncTable<Report> mToDoTable;

    /**
     * Adapter to sync the items list with the view
     */
    public static AzureAdapter mAdapter;

    static
    {
        UIHandler = new Handler(Looper.getMainLooper());
    }

    //public static AzureAdapter mAdapter;

    /**
     * Progress spinner to use for table operations
     */
    public static ProgressBar mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        duration = findViewById(R.id.duration);
        mProgressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);
        // Initialize the progress bar
        mProgressBar.setVisibility(ProgressBar.GONE);

        /*cancerprob = findViewById(R.id.cancerchances);
        tempdiff = findViewById(R.id.tempdifference);*/
        //pain = findViewById(R.id.painpercent);
        tempdiff = findViewById(R.id.tempdifference);

        waveLoadingCancer = (WaveLoadingView)findViewById(R.id.waveLoadingCancer);
        //waveLoadingCancer.setProgressValue(75);
        waveLoadingCancer.setBottomTitle("");
        //waveLoadingCancer.setCenterTitle(String.format("%d%%",75));
        waveLoadingCancer.setTopTitle("");
        waveLoadingPain = (WaveLoadingView)findViewById(R.id.waveLoadingPain);
        //waveLoadingPain.setProgressValue(25);
        waveLoadingPain.setBottomTitle("");
        //waveLoadingPain.setCenterTitle(String.format("%d%%",25));
        waveLoadingPain.setTopTitle("");

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
        waveLoadingPain.setCenterTitle(String.format("%d%%",Integer.parseInt(TestData.newInstance().getPainIntensity())));
        waveLoadingPain.setProgressValue(Integer.parseInt(TestData.newInstance().getPainIntensity()));

        waveLoadingCancer.setCenterTitle(String.format("%d%%",Integer.parseInt(TestData.newInstance().getCancerProbability())));
        waveLoadingCancer.setProgressValue(Integer.parseInt(TestData.newInstance().getCancerProbability()));
        tempdiff.setText(""+TestData.newInstance().getTemperatureDifference());
        duration.setText(TestData.newInstance().getDuration());

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

            allreports = allreports = TestData.newInstance().getAllreports();
            if(allreports == null)
                allreports = new ArrayList<>();
            // Get the Mobile Service Table instance to use

            mToDoTable = mClient.getTable(Report.class);

            // Offline Sync
            //mToDoTable = mClient.getSyncTable("Report", Report.class);

            //Init local storage
            initLocalStore(getApplicationContext()).get();


            // Create an adapter to bind the items with the view
            mAdapter = new AzureAdapter(this, R.layout.row_item);
            //ListView listViewToDo = (ListView) findViewById(R.id.listViewToDo);
            //listViewToDo.setAdapter(mAdapter);

            // Load the items from the Mobile Service
            refreshItemsFromTable(getApplicationContext());


            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    addItem(null,getApplicationContext());
                }
            }, 2000);

        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error",getApplicationContext());
        } catch (Exception e){
            createAndShowDialog(e, "Error",getApplicationContext());
        }
    }

    /**
     * Add a new item
     *
     * @param view
     *            The view that originated the call
     */
    public static void addItem(View view, Context context) {
        if (mClient == null) {
            return;
        }

        // Create a new item
        final Report item = new Report();
        item.cancerProbability = TestData.newInstance().cancerProbability;
        item.temperatureDifference = TestData.newInstance().temperatureDifference;
        item.painIntensity = TestData.newInstance().painIntensity;
        item.response= TestData.newInstance().response;

        //item.setText(mTextNewToDo.getText().toString());
        item.setComplete(false);

        // Insert the new item
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final Report entity = addItemInTable(item);

                    UIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(!entity.isComplete()){
                                mAdapter.add(entity);
                                if (!TestData.newInstance().getAllreports().contains(entity))
                                {
                                    TestData.newInstance().addreport(entity);
                                    if (!allreports.contains(item)) {
                                        allreports.add(item);
                                        if (adapter!=null)
                                            adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                    });
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error", context);
                }
                return null;
            }
        };

        runAsyncTask(task);

        //mTextNewToDo.setText("");
    }

    /**
     * Add an item to the Mobile Service Table
     *
     * @param item
     *            The item to Add
     */
    public static Report addItemInTable(Report item) throws ExecutionException, InterruptedException {
        Report entity = mToDoTable.insert(item).get();
        return entity;
    }

    /**
     * Refresh the list with the items in the Table
     */
    public static void refreshItemsFromTable(Context context) {

        // Get the items that weren't marked as completed and add them in the
        // adapter

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    final List<Report> results = refreshItemsFromMobileServiceTable();

                    //Offline Sync
                    //final List<Report> results = refreshItemsFromMobileServiceTableSyncTable();

                    UIHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.clear();

                            for (Report item : results) {
                                mAdapter.add(item);
                                if (!TestData.newInstance().getAllreports().contains(item))
                                {
                                    TestData.newInstance().addreport(item);
                                    if (!allreports.contains(item)) {
                                        allreports.add(item);
                                        Log.d("item pain",item.painIntensity);
                                        if (adapter!=null)
                                            adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                    });
                } catch (final Exception e){
                    createAndShowDialogFromTask(e, "Error",context);
                }

                return null;
            }
        };

        runAsyncTask(task);
    }

    /**
     * Refresh the list with the items in the Mobile Service Table
     */

    public static List<Report> refreshItemsFromMobileServiceTable() throws ExecutionException, InterruptedException {
        return mToDoTable.where().field("complete").
                eq(val(false)).execute().get();
    }

    //Offline Sync
    /**
     * Refresh the list with the items in the Mobile Service Sync Table
     */
    /*private List<Report> refreshItemsFromMobileServiceTableSyncTable() throws ExecutionException, InterruptedException {
        //sync the data
        sync().get();
        Query query = QueryOperations.field("complete").
                eq(val(false));
        return mToDoTable.read(query).get();
    }*/

    /**
     * Initialize local storage
     * @return
     * @throws MobileServiceLocalStoreException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static AsyncTask<Void, Void, Void> initLocalStore(Context context) throws MobileServiceLocalStoreException, ExecutionException, InterruptedException {

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
                    tableDefinition.put("text", ColumnDataType.String);
                    tableDefinition.put("createdAt", ColumnDataType.Date);
                    tableDefinition.put("complete", ColumnDataType.Boolean);

                    localStore.defineTable("Reports", tableDefinition);

                    SimpleSyncHandler handler = new SimpleSyncHandler();

                    syncContext.initialize(localStore, handler).get();

                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error", context);
                }

                return null;
            }
        };

        return runAsyncTask(task);
    }

    //Offline Sync
    /**
     * Sync the current context and the Mobile Service Sync Table
     * @return
     */
    /*
    private AsyncTask<Void, Void, Void> sync() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    MobileServiceSyncContext syncContext = mClient.getSyncContext();
                    syncContext.push().get();
                    mToDoTable.pull(null).get();
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };
        return runAsyncTask(task);
    }
    */

    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    public static void createAndShowDialogFromTask(final Exception exception, String title, Context context) {
        UIHandler.post(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, "Error", context);
            }
        });
    }


    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    public static void createAndShowDialog(Exception exception, String title, Context context) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title, context);
    }

    /**
     * Creates a dialog and shows it
     *
     * @param message
     *            The dialog message
     * @param title
     *            The dialog title
     */
    public static void createAndShowDialog(final String message, final String title, Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
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
}
