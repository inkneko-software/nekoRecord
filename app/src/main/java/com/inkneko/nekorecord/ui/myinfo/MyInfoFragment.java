package com.inkneko.nekorecord.ui.myinfo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.inkneko.nekorecord.R;
import com.inkneko.nekorecord.data.DailyRecordDatabase;
import com.inkneko.nekorecord.data.DailyRecordRepository;

public class MyInfoFragment extends Fragment {

    private MyInfoViewModel myInfoViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        myInfoViewModel =
                ViewModelProviders.of(this).get(MyInfoViewModel.class);
        View root = inflater.inflate(R.layout.fragment_myinfo, container, false);
        /*final TextView textView = root.findViewById(R.id.text_notifications);
        myInfoViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

         */
        Button saveBtn = root.findViewById(R.id.db_do_save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DailyRecordRepository(getActivity().getApplication()).backupToServer();
            }
        });

        Button fetchBtn = root.findViewById(R.id.db_do_load_btn);
        fetchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DailyRecordRepository(getActivity().getApplication()).fetchFromServer();
            }
        });
        return root;
    }



}