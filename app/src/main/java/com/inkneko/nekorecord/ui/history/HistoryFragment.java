package com.inkneko.nekorecord.ui.history;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.inkneko.nekorecord.R;
import com.inkneko.nekorecord.data.DailyRecord;
import com.inkneko.nekorecord.ui.record.RecordManageFragment;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;
    private LayoutInflater viewInflater;
    private ViewGroup viewContainer;
    private View viewRoot;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        historyViewModel = ViewModelProviders.of(this).get(HistoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_history, container, false);


        viewInflater = inflater;
        viewContainer = container;
        viewRoot = root;
        historyViewModel.fetchThisMonth().observe(this, this::showHistoryRecord);

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.history_set_range, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();

        MaterialDatePicker<Pair<Long, Long>> picker = builder.build();

        picker.show(getChildFragmentManager(), picker.toString());

        return true;

    }


    private void showHistoryRecord(List<DailyRecord> dailyRecords){
        LinearLayout historyRoot = viewRoot.findViewById(R.id.record_summary_host);
        TextView thisMonthTotal = historyRoot.findViewById(R.id.record_summary_total_this_month);
        Float total = 0f;
        String lastDay = "";
        Float lastDayPrice = 0.f;
        View summaryHostView = null;
        for (DailyRecord record : dailyRecords){
            View summaryItemView = viewInflater.inflate(R.layout.layout_record_summry_item, viewContainer, false);
            TextView date = summaryItemView.findViewById(R.id.record_summary_item_date);
            TextView type = summaryItemView.findViewById(R.id.record_summary_item_type);
            TextView comment = summaryItemView.findViewById(R.id.record_summary_item_comment);
            TextView price = summaryItemView.findViewById(R.id.record_summary_item_price);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(record.getTimestamp());
            SimpleDateFormat summaryHostFmt = new SimpleDateFormat("MM月dd日 EEEE");
            SimpleDateFormat summaryDateFmt = new SimpleDateFormat("HH:mm");

            String thisDay = summaryHostFmt.format(calendar.getTime());
            date.setText(summaryDateFmt.format(calendar.getTime()));
            type.setText(record.getEventType());
            comment.setText(record.getEvent());
            price.setText(String.format(Locale.CHINESE, "%.2f元", record.getPrice()));

            total += record.getPrice();

            if (!lastDay.equals(thisDay)){
                if (summaryHostView != null){
                    TextView hostDate = summaryHostView.findViewById(R.id.record_summary_host);
                    TextView hostPrice = summaryHostView.findViewById(R.id.record_summary_day_price);
                    hostDate.setText(lastDay);
                    hostPrice.setText(String.format(Locale.CHINESE, "%.2f元", lastDayPrice));
                    lastDayPrice=0.f;
                    historyRoot.addView(summaryHostView);
                }
                summaryHostView = viewInflater.inflate(R.layout.layout_record_sumary, viewContainer, false);
                lastDay = thisDay;
            }
            LinearLayout summaryItemList = summaryHostView.findViewById(R.id.record_summary_items);
            summaryItemList.addView(summaryItemView);
            lastDayPrice += record.getPrice();

        }
        if (summaryHostView != null){
            TextView hostDate = summaryHostView.findViewById(R.id.record_summary_host);
            hostDate.setText(lastDay);
            historyRoot.addView(summaryHostView);
            TextView hostPrice = summaryHostView.findViewById(R.id.record_summary_day_price);
            hostPrice.setText(String.format(Locale.CHINESE, "%.2f元", lastDayPrice));
        }

        thisMonthTotal.setText(String.format(Locale.CHINESE, "本月总计: %.2f元", total));

    }
}