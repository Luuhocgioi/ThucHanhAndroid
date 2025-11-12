package clc65.hoangluu.fragmentex_replace;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class FooterFragment extends Fragment {

    public FooterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_footer, container, false);
        Button b1 = view.findViewById(R.id.button);
        Button b2 = view.findViewById(R.id.button2);
        Button b3 = view.findViewById(R.id.button3);


        FragmentManager fragmentManager = getParentFragmentManager();
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Code
                fragmentManager.beginTransaction().replace(R.id.fragmentContainerView_Content, new Fragment1()).commit();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Code
                fragmentManager.beginTransaction().replace(R.id.fragmentContainerView_Content, new Fragment2()).commit();


            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Code
                fragmentManager.beginTransaction().replace(R.id.fragmentContainerView_Content, new Fragment3()).commit();
            }
        });
        return view;
    }
}