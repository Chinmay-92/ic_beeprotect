package beeprotect.de.beeprotect;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class TutorialActivity extends AppCompatActivity {

    private ViewPager mPager;
    private int[] layouts = {R.layout.app_tutorial_slide_1, R.layout.app_tutorial_slide_2, R.layout.app_tutorial_slide_3, R.layout.app_tutorial_slide_4};
    private MpagerAdapter mpagerAdapter;
    private LinearLayout Dots_Layout;
    private ImageView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT >= 19)
        { getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); }
        else
        {getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); }
        setContentView(R.layout.activity_tutorial);

        // Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        mPager = (ViewPager) findViewById(R.id.viewPager);
        mpagerAdapter = new MpagerAdapter(layouts,this);
        mPager.setAdapter(mpagerAdapter);

        Dots_Layout = (LinearLayout) findViewById(R.id.dotsLayout);
        createDots(0);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                createDots(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    /*public void buttonOnClick(View v)
    {
        Button button =(Button)findViewById(R.id.getstarted);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(tutorialActivity.this,MainActivity.class);
                startActivity(i);
            }
        });
    }*/

    private  void createDots(int current_position)
    {
        if(Dots_Layout != null)
            Dots_Layout.removeAllViews();


        dots = new ImageView[layouts.length];

        for(int i=0; i<layouts.length; i++)
        {
            dots[i] = new ImageView(this);
            if(i == current_position)
            {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.active_dots));
            }
            else
            {
                dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_dots));
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4,0,4,0);

            Dots_Layout.addView(dots[i],params);

        }
    }


}
