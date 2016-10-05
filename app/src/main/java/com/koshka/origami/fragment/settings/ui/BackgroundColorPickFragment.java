package com.koshka.origami.fragment.settings.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.koshka.origami.R;
import com.koshka.origami.activity.tutorial.FirstTimeLaunchPreferencesActivity;
import com.koshka.origami.fragment.before_main.ColorsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.koshka.origami.activity.main.MainActivity.SHARED_PREFS;

/**
 * Created by qm0937 on 10/3/16.
 */

public class BackgroundColorPickFragment extends Fragment implements AdapterView.OnItemClickListener {

    @BindView(R.id.colors_grid_view)
    GridView colorsGridView;

    private View previousView;

    private Drawable pickedDrawable;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private DatabaseReference backgroundColorRef;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ui_color_settings_fragment, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final String uid = mAuth.getCurrentUser().getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        backgroundColorRef = database.getReference().child("prefs").child(mAuth.getCurrentUser().getUid()).child("backgroundColor");

        colorsGridView.setAdapter(new ColorsAdapter(getContext()));
        colorsGridView.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (previousView != null) {
            previousView.setBackgroundColor(getResources().getColor(R.color.transparent6));
        }

        view.setBackgroundColor(getResources().getColor(R.color.transparent4));

        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.color_layout);
        pickedDrawable = layout.getBackground();

        view.getRootView().setBackground(pickedDrawable);
        previousView = view;

        backgroundColorRef.setValue(position).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });

        setSharedPref(position);

    }

    private void setSharedPref(int position){
        switch (position){
            case 0:{
                putIntInSharedPreference(R.style.amethist_theme,R.drawable.amethist_gradient);
                break;
            }
            case 1:{
                putIntInSharedPreference(R.style.bloody_mary__theme,R.drawable.bloody_mary_gradient );
                break;
            }
            case 2:{
                putIntInSharedPreference(R.style.influenza_theme, R.drawable.influenza_gradient);
                break;
            }
            case 3:{
                putIntInSharedPreference(R.style.shroom_theme, R.drawable.shroom_gradient);
                break;
            }
            case 4:{
                putIntInSharedPreference(R.style.kashmir_theme, R.drawable.kashmir_gradient);
                break;
            }
            case 5:{
                putIntInSharedPreference(R.style.grapefruit_sunset_theme, R.drawable.grapefruit_sunset_gradient);
                break;
            }
            case 6:{
                putIntInSharedPreference(R.style.moonrise_theme, R.drawable.moonrise_gradient);
                break;
            }
            case 7:{
                putIntInSharedPreference(R.style.purple_bliss_theme, R.drawable.purple_bliss_gradient);
                break;
            }
            case 8:{
                putIntInSharedPreference(R.style.passion_theme, R.drawable.passion_gradient);
                break;
            }
            case 9:{
                putIntInSharedPreference(R.style.little_leaf_theme, R.drawable.little_leaf_gradient);
                break;
            }
            case 10:{
                putIntInSharedPreference(R.style.reef_theme, R.drawable.reef_gradient);
                break;
            }
        }

    }

    private void putIntInSharedPreference(int themeCode, int gradientCode){
        SharedPreferences preferences = getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("theme", themeCode);
        editor.putInt("gradient", gradientCode);
        editor.commit();
        editor.apply();
    }


}