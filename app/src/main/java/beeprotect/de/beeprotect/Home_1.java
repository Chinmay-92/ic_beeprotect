package beeprotect.de.beeprotect;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class Home_1 extends Fragment {

    private ViewPager mPager;
    private int[] layouts = {R.layout.app_tutorial_slide_1, R.layout.app_tutorial_slide_2, R.layout.app_tutorial_slide_3, R.layout.app_tutorial_slide_4};
    private MpagerAdapter mpagerAdapter;
    private LinearLayout Dots_Layout;
    private ImageView[] dots;


    public static Home_1 newInstance() {
        Home_1 fragment = new Home_1();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.activity_tutorial, container, false);

        mPager = rootView.findViewById(R.id.viewPager);
        mpagerAdapter = new MpagerAdapter(layouts,getContext());
        mPager.setAdapter(mpagerAdapter);

        Dots_Layout = rootView.findViewById(R.id.dotsLayout);
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
        return rootView;
    }


    private  void createDots(int current_position)
    {
        if(Dots_Layout != null)
            Dots_Layout.removeAllViews();


        dots = new ImageView[layouts.length];

        for(int i=0; i<layouts.length; i++)
        {
            dots[i] = new ImageView(getContext());
            if(i == current_position)
            {
                dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_dots));
            }
            else
            {
                dots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.default_dots));
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(4,0,4,0);

            Dots_Layout.addView(dots[i],params);

        }
    }
}