package com.inkneko.nekorecord.ui.record;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.inkneko.nekorecord.R;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RecordFragment extends Fragment {

    private RecordViewModel recordViewModel;
    private List<RecordManageFragment> recordFragments;
    private Map<String, Float> recordPriceTotal;

    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        recordViewModel = ViewModelProviders.of(this).get(RecordViewModel.class);
        View root = inflater.inflate(R.layout.fragment_record, container, false);
        TextView timeTitle = root.findViewById(R.id.record_fragment_time);
        timeTitle.setText(getTime());
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.record_save_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        for(RecordManageFragment recordInterface : recordFragments){
            recordInterface.save();
        }
        Toast.makeText(
                getActivity().getApplicationContext(),
                "保存成功",
                Toast.LENGTH_LONG).show();
        return true;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        recordPriceTotal = new HashMap<>();
        recordFragments = new LinkedList<>();
        List<String> typeList = new LinkedList<String>();
        typeList.add("早上");
        typeList.add("中午");
        typeList.add("晚上");

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        for (String type : typeList){
            RecordManageFragment recordManagerInterface = new RecordManageFragment(type, recordViewModel);
            recordFragments.add(recordManagerInterface);
            transaction.add(R.id.record_manage_fragment_container, recordManagerInterface, type);
        }

        transaction.commit();
        bindTotalPriceListener();
    }

    private String getTime(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat fmt = new SimpleDateFormat("MM月dd日 EEEE");
        return fmt.format(calendar.getTime());
    }

    private void updateTotalText(){
        Float priceTotal = recordPriceTotal.values().stream().reduce(0f, Float::sum);

        TextView totalText = getView().findViewById(R.id.total_price_title);
        totalText.setText(String.format(Locale.CHINESE, "今日总计: %.2f 元", priceTotal));
    }

    private void bindTotalPriceListener(){
        for(RecordManageFragment recordInterface : recordFragments){
            recordInterface.getTotalLiveData().observe(this, total->{
                recordPriceTotal.put(recordInterface.getEventType(), total);
                updateTotalText();
            });
        }
    }
}