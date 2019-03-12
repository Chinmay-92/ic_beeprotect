package beeprotect.de.beeprotect;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.concurrent.TimeUnit;
import static beeprotect.de.beeprotect.ReportActivity.*;

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
        /*try {
            // Create the Mobile Service Client instance, using the provided
            *//*if (mAdapter == null )
            mAdapter = new AzureAdapter(getContext(), R.layout.row_item);*//*

            if (mClient == null) {
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
            }
        }catch (Exception ue){
            ue.printStackTrace();
        }*/

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



        return rootView;
    }
}