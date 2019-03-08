package beeprotect.de.beeprotect;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class BeginFragment extends Fragment {
    public static BeginFragment newInstance() {
        BeginFragment fragment = new BeginFragment();
        return fragment;
    }
    Intent intent;
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