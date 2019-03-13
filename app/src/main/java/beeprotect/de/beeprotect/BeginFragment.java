package beeprotect.de.beeprotect;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import static beeprotect.de.beeprotect.ReportActivity.*;
import static beeprotect.de.beeprotect.ReportlisttabFragment.ReportDataModels;
import static beeprotect.de.beeprotect.ReportlisttabFragment.allreports;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BeginFragment extends Fragment {
    public static BeginFragment newInstance() {
        BeginFragment fragment = new BeginFragment();
        return fragment;
    }
    Intent intent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_home_2, container, false);

        intent = new Intent(getActivity(), MainActivity.class);
        final Button button = (Button) rootView.findViewById(R.id.begin);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(intent);
            }
        });


        try {
            // Create the Mobile Service Client instance, using the provided

            // Mobile Service URL and key
            mClient = new MobileServiceClient(
                    "https://beeprotectmobile.azurewebsites.net",
                    getContext()).withFilter(new ReportActivity.ProgressFilter());

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
            initLocalStore(getContext()).get();

            if (ReportlisttabFragment.adapter ==null)
            ReportlisttabFragment.adapter = new ReportAdapter(ReportDataModels,getContext());

            // Create an adapter to bind the items with the view
            if (mAdapter==null)
            mAdapter = new AzureAdapter(getContext(), R.layout.row_item);
            //ListView listViewToDo = (ListView) findViewById(R.id.listViewToDo);
            //listViewToDo.setAdapter(mAdapter);

            // Load the items from the Mobile Service
            refreshItemsFromTable(getContext());

        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Error",getContext());
        } catch (Exception e){
            createAndShowDialog(e, "Error",getContext());
        }


        return rootView;
    }
}