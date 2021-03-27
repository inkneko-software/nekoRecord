package com.inkneko.nekorecord.ui.history;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.inkneko.nekorecord.R;
import com.inkneko.nekorecord.data.DailyRecord;

import java.util.Calendar;
import java.util.Locale;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        historyViewModel =
                ViewModelProviders.of(this).get(HistoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_history, container, false);
        LinearLayout historyRoot = root.findViewById(R.id.record_summary_host);
        TextView thisMonthTotal = historyRoot.findViewById(R.id.record_summary_total_this_month);

        historyViewModel.fetchThisMonth().observe(this, dailyRecords -> {
            Float total = 0f;
            String lastDay = "";
            Float lastDayPrice = 0.f;
            View summaryHostView = null;
            for (DailyRecord record : dailyRecords){
                View summaryItemView = inflater.inflate(R.layout.layout_record_summry_item, container, false);
                TextView date = summaryItemView.findViewById(R.id.record_summary_item_date);
                TextView type = summaryItemView.findViewById(R.id.record_summary_item_type);
                TextView comment = summaryItemView.findViewById(R.id.record_summary_item_comment);
                TextView price = summaryItemView.findViewById(R.id.record_summary_item_price);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(record.getTimestamp());
                SimpleDateFormat summaryHostFmt = new SimpleDateFormat("MM月dd日 EEEE");
                SimpleDateFormat summaryDateFmt = new SimpleDateFormat("HH:ss");

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
                    summaryHostView = inflater.inflate(R.layout.layout_record_sumary, container, false);
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

        });
        return root;
    }


}