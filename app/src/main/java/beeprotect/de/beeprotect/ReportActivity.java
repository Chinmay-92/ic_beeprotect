package beeprotect.de.beeprotect;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.os.StrictMode;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.transition.ArcMotion;
import androidx.transition.ChangeBounds;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;
import beeprotect.de.beeprotect.utils.AzureMLUtil;

public class ReportActivity extends AppCompatActivity {
    TextView mlresponse;
    TextView cancerprob,tempdiff,pain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

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
    }
}
