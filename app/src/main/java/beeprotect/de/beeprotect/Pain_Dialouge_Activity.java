package beeprotect.de.beeprotect;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.rey.material.widget.Slider;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import androidx.annotation.Nullable;


public class Pain_Dialouge_Activity extends Activity {
    ListView list;
    String[] web = {
            "leftbottom",
            "lefttop",
            "rightbottom",
            "righttop"
    } ;
    Integer[] imageId = {
            R.drawable.femalebody_leftbottom,
            R.drawable.femalebody_lefttop,
            R.drawable.femalebody_rightbottom,
            R.drawable.femalebody_rightop
    };
    boolean[] selectedParts = {
        false,
        false,
        false,
        false
    };
    Activity dialogContext;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pain_intensity);
        dialogContext = this;

        GridView bodyparts = findViewById(R.id.gridview);
        bodyparts.setSelector(new ColorDrawable(Color.BLACK));


        ImageList listAdapter = new
                ImageList(dialogContext, web, imageId);
        //list=(ListView)findViewById(R.id.list);
        bodyparts.setAdapter(listAdapter);
        bodyparts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (!selectedParts[position])
                    view.setBackground(getResources().getDrawable(R.drawable.hightlight_image));
                else
                    view.setBackground(null);
                if(selectedParts[position])
                    selectedParts[position] = false;
                else
                    selectedParts[position] = true;

            }
        });
        /*ListAdapter bodyimagesAdapter = new ArrayAdapter<Drawable>();
        bodyparts.setAdapter(bodyimagesAdapter);*/
        /*final Slider painIntensity = findViewById(R.id.slider1);*/

        Button submit = findViewById(R.id.submit);

        ProgressBar progressBar = findViewById(R.id.PROGRESS_BAR);

        /*painIntensity.setOnPositionChangeListener(new Slider.OnPositionChangeListener() {
            @Override
            public void onPositionChanged(Slider view, boolean fromUser, float oldPos, float newPos, int oldValue, int newValue) {
                progressBar.setProgress(painIntensity.getValue());
            }
        });*/

        DiscreteSeekBar painIntensity = findViewById(R.id.slider1);

        painIntensity.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                progressBar.setProgress(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });


        progressBar.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                Toast.makeText(Pain_Dialouge_Activity.this, "i"+i+"i1"+i1+"i2"+i2+"i3"+i3, Toast.LENGTH_SHORT).show();
            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pain = painIntensity.getProgress();
                Log.d("testdata",""+pain);
                TestData.newInstance().setPainIntensity(""+pain);
                TestData.setTestdata(TestData.testdata);
                Intent intent=new Intent(getApplicationContext(), ReportActivity.class);
                //intent.putExtra("testData",TestData.testdata);
                startActivity(intent);
                finish();
            }
        });
    }

    public class ImageList extends ArrayAdapter<String>{
        private final Activity context;
        private final String[] web;
        private final Integer[] imageId;
        public ImageList(Activity context,
                          String[] web, Integer[] imageId) {
            super(context, R.layout.list_single, web);
            this.context = context;
            this.web = web;
            this.imageId = imageId;
        }
        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView= inflater.inflate(R.layout.list_single, null, true);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
            imageView.setImageDrawable(getDrawable(imageId[position]));
            return rowView;
        }
    }
}
