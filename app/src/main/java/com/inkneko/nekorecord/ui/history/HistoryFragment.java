package com.inkneko.nekorecord.ui.history;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.inkneko.nekorecord.R;
import com.inkneko.nekorecord.data.DailyRecord;
import com.inkneko.nekorecord.ui.record.RecordViewModel;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class HistoryFragment extends Fragment {

    private HistoryViewModel mHistoryViewModel;
    private LayoutInflater mViewInflater;
    private ViewGroup mViewContainer;
    private View mViewRoot;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mHistoryViewModel = ViewModelProviders.of(this).get(HistoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_history, container, false);

        mViewInflater = inflater;
        mViewContainer = container;
        mViewRoot = root;
        mHistoryViewModel.fetchThisMonth().observe(this, this::showHistoryRecord);

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


    //BUG: List<...>更新范围太大
    private void showHistoryRecord(List<DailyRecord> dailyRecords){
        LinearLayout historyRoot = mViewRoot.findViewById(R.id.record_summary_host);
        //historyRoot.removeAllViews();
        TextView thisMonthTotal = historyRoot.findViewById(R.id.record_summary_total_this_month);
        Float total = 0f;
        String lastDay = "";
        Float lastDayPrice = 0.f;
        Long lastDayTimestamp = 0L;
        View summaryHostView = null;
        //判断每一条记录的日期，然后添加到对应日期的summaryHostView中的summaryItemList
        for (DailyRecord record : dailyRecords){
            View summaryItemView = mViewInflater.inflate(R.layout.layout_record_summry_item, mViewContainer, false);
            TextView date = summaryItemView.findViewById(R.id.record_summary_item_time);
            TextView type = summaryItemView.findViewById(R.id.record_summary_item_type);
            TextView comment = summaryItemView.findViewById(R.id.record_summary_item_comment);
            TextView price = summaryItemView.findViewById(R.id.record_summary_item_price);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(record.getTimestamp());
            lastDayTimestamp = record.getTimestamp();
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
                    TextView hostDate = summaryHostView.findViewById(R.id.record_summary_date);
                    TextView hostPrice = summaryHostView.findViewById(R.id.record_summary_day_price);
                    hostDate.setText(lastDay);
                    hostPrice.setText(String.format(Locale.CHINESE, "%.2f元", lastDayPrice));
                    lastDayPrice=0.f;
                    hostDate.setOnClickListener(new HistoryModifier(summaryHostView, new DailyRecord("", 0.f, "", lastDayTimestamp), true));
                    historyRoot.addView(summaryHostView);
                }
                summaryHostView = mViewInflater.inflate(R.layout.layout_record_sumary, mViewContainer, false);
                lastDay = thisDay;
            }
            summaryItemView.setOnClickListener(new HistoryModifier(summaryItemView, record,false));
            LinearLayout summaryItemList = summaryHostView.findViewById(R.id.record_summary_items);
            summaryItemList.addView(summaryItemView);
            lastDayPrice += record.getPrice();
        }

        if (summaryHostView != null){
            TextView hostDate = summaryHostView.findViewById(R.id.record_summary_date);
            hostDate.setText(lastDay);
            historyRoot.addView(summaryHostView);
            TextView hostPrice = summaryHostView.findViewById(R.id.record_summary_day_price);
            hostDate.setOnClickListener(new HistoryModifier(summaryHostView, new DailyRecord("", 0.f, "", lastDayTimestamp), true));
            hostPrice.setText(String.format(Locale.CHINESE, "%.2f元", lastDayPrice));
        }

        thisMonthTotal.setText(String.format(Locale.CHINESE, "本月总计: %.2f元", total));

    }

    private class HistoryModifier implements View.OnClickListener{

        private View bindedHistoryItem;
        private DailyRecord bindedRecord;
        private boolean mNewRecord = false;
        public HistoryModifier(View historyItem, DailyRecord record, boolean newRecord){
            super();
            bindedHistoryItem = historyItem;
            bindedRecord = record;
            mNewRecord = newRecord;
        }
        @Override
        public void onClick(View v) {
            SimpleDateFormat dateFmt = new SimpleDateFormat("MM月dd日 EEEE");
            SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm");
            View modifyPanelView = mViewInflater.inflate(R.layout.layout_history_modify_panel, mViewContainer, false);
            LinearLayout recordList = modifyPanelView.findViewById(R.id.history_modify_selection_list);
            View historyItemView = mViewInflater.inflate(R.layout.layout_history_modify_item, mViewContainer, false);
            EditText timeEdit = historyItemView.findViewById(R.id.history_modify_item_time);
            EditText typeEdit = historyItemView.findViewById(R.id.history_modify_item_type);
            EditText commentEdit = historyItemView.findViewById(R.id.history_modify_item_comment);
            EditText priceEdit = historyItemView.findViewById(R.id.history_modify_item_price);
            if (!mNewRecord){
                timeEdit.setText(timeFmt.format(bindedRecord.getTimestamp()));
            }
            typeEdit.setText(bindedRecord.getEventType());
            commentEdit.setText(bindedRecord.getEvent());
            priceEdit.setText(bindedRecord.getPrice().toString());
            recordList.addView(historyItemView);
            new AlertDialog.Builder(getContext())
                    .setTitle(dateFmt.format(bindedRecord.getTimestamp()))
                    .setView(modifyPanelView)
                    .setPositiveButton("保存", new UpdateHistoryHelper(bindedHistoryItem, historyItemView, bindedRecord, mNewRecord))
                    .setNegativeButton("取消", null)
                    .setNeutralButton("删除", null)
                    .setCancelable(false)
                    .create().show();
        }
    }

    private class UpdateHistoryHelper implements DialogInterface.OnClickListener{

        private View mParent;
        private View mModifiedHistory;
        private DailyRecord mRecord;
        private boolean mNewRecord = false;
        public UpdateHistoryHelper(View parent, View modifiedHistory, DailyRecord record, boolean newRecord){
            mParent = parent;
            mModifiedHistory = modifiedHistory;
            mRecord = record;
            mNewRecord = newRecord;


        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            TextView newDate = mModifiedHistory.findViewById(R.id.history_modify_item_time);
            TextView newType = mModifiedHistory.findViewById(R.id.history_modify_item_type);
            TextView newComment = mModifiedHistory.findViewById(R.id.history_modify_item_comment);
            TextView newPrice = mModifiedHistory.findViewById(R.id.history_modify_item_price);


            SimpleDateFormat timeFmt = new SimpleDateFormat("HH:mm");
            try {
                Date modifiedTime = timeFmt.parse(newDate.getText().toString());
                Calendar inputTime = Calendar.getInstance();
                inputTime.setTime(modifiedTime);

                Calendar calendar = new GregorianCalendar();
                calendar.setTimeInMillis(mRecord.getTimestamp());
                calendar.set(Calendar.HOUR_OF_DAY, inputTime.get(Calendar.HOUR_OF_DAY));
                calendar.set(Calendar.MINUTE, inputTime.get(Calendar.MINUTE));
                calendar.set(Calendar.SECOND, inputTime.get(Calendar.SECOND));
                calendar.set(Calendar.MILLISECOND, 0);
                mRecord.setTimestamp(calendar.getTimeInMillis());
            }catch (ParseException e){
                mRecord.setTimestamp(0L);
            }
            mRecord.setEventType(newType.getText().toString());
            mRecord.setEvent(newComment.getText().toString());
            mRecord.setPrice(Float.parseFloat(newPrice.getText().toString()));

            if (mRecord.getTimestamp() == 0){
                new AlertDialog.Builder(getContext())
                        .setTitle("时间格式错误")
                        .setMessage("允许的格式为 小时:分钟")
                        .create().show();

                return;
            }

            mHistoryViewModel.saveRecord(mRecord);
            if (mNewRecord){
                View summaryItemView = mViewInflater.inflate(R.layout.layout_record_summry_item, mViewContainer, false);
                TextView date = summaryItemView.findViewById(R.id.record_summary_item_time);
                TextView type = summaryItemView.findViewById(R.id.record_summary_item_type);
                TextView comment = summaryItemView.findViewById(R.id.record_summary_item_comment);
                TextView price = summaryItemView.findViewById(R.id.record_summary_item_price);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(mRecord.getTimestamp());
                SimpleDateFormat summaryDateFmt = new SimpleDateFormat("HH:mm");

                date.setText(summaryDateFmt.format(mRecord.getTimestamp()));
                type.setText(mRecord.getEventType());
                comment.setText(mRecord.getEvent());
                price.setText(String.format(Locale.CHINESE, "%.2f元", mRecord.getPrice()));

                ((LinearLayout)mParent).addView(summaryItemView);
            }else {

                TextView date = mParent.findViewById(R.id.record_summary_item_time);
                TextView type = mParent.findViewById(R.id.record_summary_item_type);
                TextView comment = mParent.findViewById(R.id.record_summary_item_comment);
                TextView price = mParent.findViewById(R.id.record_summary_item_price);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(mRecord.getTimestamp());
                SimpleDateFormat summaryDateFmt = new SimpleDateFormat("HH:mm");

                date.setText(summaryDateFmt.format(mRecord.getTimestamp()));
                type.setText(mRecord.getEventType());
                comment.setText(mRecord.getEvent());
                price.setText(String.format(Locale.CHINESE, "%.2f元", mRecord.getPrice()));
            }
        }
    }
}