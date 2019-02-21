package beeprotect.de.beeprotect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.rey.material.widget.Slider;

import androidx.annotation.Nullable;


public class Pain_Dialouge_Activity extends Activity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pain_intensity);

        final Slider painIntensity = findViewById(R.id.slider1);

        Button submit = findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pain = painIntensity.getValue();
                Log.d("testdata",""+pain);
                TestData.newInstance().setPainIntensity(""+pain);
                TestData.setTestdata(TestData.testdata);
                Intent intent=new Intent(getApplicationContext(), ReportActivity.class);
                intent.putExtra("testData",TestData.testdata);
                startActivity(intent);
                finish();
            }
        });
    }
}
