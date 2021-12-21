package com.inkneko.nekorecord.ui.history;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.inkneko.android.utils.ResourceManagement;
import com.inkneko.nekorecord.R;
import com.inkneko.nekorecord.data.model.relations.RecordInfo;
import com.inkneko.nekorecord.ui.component.RecordEditFragment;

import java.util.Calendar;
import java.util.List;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;
    private LayoutInflater inflater;
    private View rootView;

    private TextView recordTimeRangeTextView;
    private TextView recordSummaryTextView;
    private LinearLayout recordContainerLinearLayout;
    private LiveData<List<RecordInfo>> datasource;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        this.historyViewModel = ViewModelProviders.of(this).get(HistoryViewModel.class);
        this.inflater = inflater;
        this.rootView = inflater.inflate(R.layout.fragment_history, container, false);

        recordTimeRangeTextView = rootView.findViewById(R.id.history_date_range);
        recordContainerLinearLayout = rootView.findViewById(R.id.history_range_container);
        recordSummaryTextView = rootView.findViewById(R.id.history_total_summary);

        recordTimeRangeTextView.setText("当前帐期：本月");
        datasource = historyViewModel.fetchThisMonth();
        datasource.observe(getViewLifecycleOwner(), dataSourceObserver);


        setHasOptionsMenu(true);

        /**
         * TODO:
         * 实现当月记录显示
         * 实现范围记录显示
         * 实现收入、支出的类别统计，标签统计
         */
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.history_set_range, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        MaterialDatePicker<Pair<Long, Long>> picker = builder.build();
        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
                applyTimeRange(selection.first, selection.second);
                Toast.makeText(getContext(), "日期已切换", Toast.LENGTH_SHORT).show();
            }
        });
        picker.show(getChildFragmentManager(), picker.toString());
        return true;
    }

    private Observer<List<RecordInfo>> dataSourceObserver = new Observer<List<RecordInfo>>(){
        @Override
        public void onChanged(List<RecordInfo> recordInfos) {
            recordContainerLinearLayout.removeAllViews();
            Calendar lastRecordDate = Calendar.getInstance();
            lastRecordDate.setTimeInMillis(0);
            Calendar currentRecordDate = Calendar.getInstance();
            float incomeValue = 0;
            float outcomeValue = 0;
            if (recordInfos.size() != 0) {
                for (RecordInfo recordInfo : recordInfos) {
                    currentRecordDate.setTimeInMillis(recordInfo.recordDetail.getAddDate());
                    if (currentRecordDate.get(Calendar.DAY_OF_YEAR) != lastRecordDate.get(Calendar.DAY_OF_YEAR)){
                        TextView textView = new TextView(getContext());
                        textView.setText(new SimpleDateFormat("yyyy-MM-dd").format(recordInfo.recordDetail.getAddDate()));
                        recordContainerLinearLayout.addView(textView);
                        lastRecordDate.setTimeInMillis(currentRecordDate.getTimeInMillis());
                    }
                    View recordInfoView = createRecordDetailView(recordInfo, recordContainerLinearLayout);
                    recordInfoView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RecordEditFragment recordEditFragment = new RecordEditFragment(recordInfo, "修改记录");
                            recordEditFragment.show(getChildFragmentManager(), "RecordEditDialog");
                        }
                    });

                    if (recordInfo.category.getType().compareTo("支出") == 0){
                        outcomeValue += recordInfo.recordDetail.getValue();
                    }else{
                        incomeValue += recordInfo.recordDetail.getValue();
                    }
                    recordContainerLinearLayout.addView(recordInfoView);
                }
            }
            recordSummaryTextView.setText(String.format("当前总计支出：%.2f元，总计收入：%.2f元", outcomeValue, incomeValue));
        }
    };

    private View createRecordDetailView(RecordInfo recordInfo, ViewGroup parent){
        View recordInfoView = inflater.inflate(R.layout.layout_record_detail, parent, false);
        ImageButton categoryIcon = recordInfoView.findViewById(R.id.category_icon);
        TextView categoryNameTextView = recordInfoView.findViewById(R.id.category_name);
        TextView categoryTagsTextView = recordInfoView.findViewById(R.id.category_tags);
        TextView checkoutValueTextView = recordInfoView.findViewById(R.id.checkout_value);
        TextView recordTimeTextView = recordInfoView.findViewById(R.id.record_time);

        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
        recordTimeTextView.setText(fmt.format(recordInfo.recordDetail.getAddDate()));
        categoryIcon.setImageResource(ResourceManagement.getDrawableResourceId(recordInfo.category.getIconResourceName()));
        categoryNameTextView.setText(recordInfo.category.getName());
        categoryTagsTextView.setText(recordInfo.recordDetail.getRemark());
        checkoutValueTextView.setText(String.format("%.2f", recordInfo.recordDetail.getValue()));

        String remark = recordInfo.recordDetail.getRemark();
        String tags = TextUtils.join(" ", recordInfo.recordTags);
        if (remark.length() != 0){
            remark = remark.concat(" ").concat(tags);
        }else{
            remark = tags;
        }

        if (remark.length() != 0){
            categoryTagsTextView.setText(remark);
        }else {
            categoryTagsTextView.setVisibility(View.GONE);
        }

        if (recordInfo.category.getType().compareTo("收入") == 0){
            checkoutValueTextView.setText("+".concat(checkoutValueTextView.getText().toString()));
            checkoutValueTextView.setTextColor(getActivity().getColor(R.color.colorIncomeGreen));
        }
        return recordInfoView;
    }

    private void applyTimeRange(Long start, Long end){
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");
        recordTimeRangeTextView.setText(String.format("当前帐期：%s - %s", fmt.format(start), fmt.format(end)));
        datasource.removeObservers(getViewLifecycleOwner());
        datasource = historyViewModel.fetchRange(start, end);
        datasource.observe(getViewLifecycleOwner(), dataSourceObserver);
    }
}