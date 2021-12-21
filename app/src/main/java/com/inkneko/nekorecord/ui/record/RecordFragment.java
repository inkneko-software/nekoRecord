package com.inkneko.nekorecord.ui.record;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.inkneko.android.utils.ResourceManagement;
import com.inkneko.nekorecord.R;
import com.inkneko.nekorecord.data.model.relations.RecordInfo;
import com.inkneko.nekorecord.ui.component.RecordEditFragment;


import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RecordFragment extends Fragment {

    private Calendar selectedDate;
    private RecordViewModel recordViewModel;
    private LiveData<List<RecordInfo>> datasource;
    private View root;
    private LayoutInflater inflater;

    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        recordViewModel = ViewModelProviders.of(this).get(RecordViewModel.class);
        this.inflater = inflater;
        root = inflater.inflate(R.layout.fragment_record, container, false);
        setHasOptionsMenu(true);
        TextView timeTitle = root.findViewById(R.id.record_today_time);
        timeTitle.setText(getTodayDateString());
        selectedDate = Calendar.getInstance();
        changeHeadSceneryImage();

        datasource = recordViewModel.getRecordInfosToday();
        datasource.observe(getViewLifecycleOwner(), recordDataSourceObserver);
        
        /**
         * TODO:
         * ✔ 实现一个记录编辑弹窗，可实现记录的添加和编辑
         * ✔ 点击悬浮按钮打开添加记录编辑弹窗
         * ✔ 点击记录，打开记录编辑弹窗
         * ✔ 当日收入支出的总量计算
         * ✔ 实现日期切换
         */

        FloatingActionButton floatingActionButton = root.findViewById(R.id.addRecordFloatButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                selectedDate.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY));
                selectedDate.set(Calendar.MINUTE, now.get(Calendar.MINUTE));
                RecordEditFragment recordEditFragment = new RecordEditFragment("添加记录", selectedDate);
                recordEditFragment.show(getChildFragmentManager(), "RecordEditDialog");
            }
        });
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.record_save_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Calendar calendar = Calendar.getInstance();
        CalendarConstraints.Builder constraintBuilder = new CalendarConstraints.Builder();
        CalendarConstraints calendarConstraints = constraintBuilder.setOpenAt(calendar.getTimeInMillis()).build();

        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setCalendarConstraints(calendarConstraints);
        MaterialDatePicker<Long> datePicker = builder.build();
        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                Long start = selection;
                selectedDate.setTimeInMillis(selection);
                selectedDate.add(Calendar.DAY_OF_MONTH, 1);
                Long end = selectedDate.getTimeInMillis();
                selectedDate.add(Calendar.DAY_OF_MONTH, -1);

                TextView timeTitle = root.findViewById(R.id.record_today_time);
                timeTitle.setText(getDateString(selectedDate.getTime()));

                datasource.removeObservers(getViewLifecycleOwner());
                datasource = recordViewModel.getRecordInfosByRange(start, end);
                datasource.observe(getViewLifecycleOwner(), recordDataSourceObserver);
                Toast.makeText(getContext(), "日期切换成功", Toast.LENGTH_SHORT).show();
            }
        });
        datePicker.show(getChildFragmentManager(), "datapicker");
        return true;
    }

    private String getTodayDateString(){
        return getDateString(new Date());
    }

    private String getDateString(Date date){
        SimpleDateFormat fmt = new SimpleDateFormat("MM月dd日 EEEE");
        return fmt.format(date);
    }

    private Observer<List<RecordInfo>> recordDataSourceObserver = new Observer<List<RecordInfo>>() {
        @Override
        public void onChanged(List<RecordInfo> recordInfos) {
            float incomeSum = 0;
            float outcomeSum = 0;
            View noRecordHint = root.findViewById(R.id.no_record_hint);
            if (recordInfos.size() != 0){
                LinearLayout recordLinearLayout = root.findViewById(R.id.record_container);
                recordLinearLayout.removeAllViews();
                for (RecordInfo recordInfo : recordInfos){
                    View recordInfoView = inflater.inflate(R.layout.layout_record_detail, recordLinearLayout, false);
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

                    recordInfoView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            RecordEditFragment recordEditFragment = new RecordEditFragment(recordInfo, "修改记录");
                            recordEditFragment.show(getChildFragmentManager(), "RecordEditDialog");
                        }
                    });
                    if (recordInfo.category.getType().compareTo("支出") == 0){
                        outcomeSum += recordInfo.recordDetail.getValue();
                    }else {
                        incomeSum += recordInfo.recordDetail.getValue();
                        checkoutValueTextView.setText("+".concat(checkoutValueTextView.getText().toString()));
                        checkoutValueTextView.setTextColor(getActivity().getColor(R.color.colorIncomeGreen));
                    }
                    applyBrief(incomeSum, outcomeSum);
                    recordLinearLayout.addView(recordInfoView);
                }
                noRecordHint.setVisibility(View.GONE);
            }else{
                noRecordHint.setVisibility(View.VISIBLE);
            }
        }
    };

    private void changeHeadSceneryImage(){
        ImageView headImageView = root.findViewById(R.id.head_scenery_image);
        int hour = selectedDate.get(Calendar.HOUR_OF_DAY);
        if (hour < 10){
            headImageView.setImageResource(R.drawable.morning);
        }else if (hour < 18){
            headImageView.setImageResource(R.drawable.noon);
        }else {
            headImageView.setImageResource(R.drawable.night);
        }
    }

    private void applyBrief(float incomeValue, float outcomeValue){
        TextView briefTextView = root.findViewById(R.id.total_price_title);
        briefTextView.setText(String.format("今日支出总计:%.2f, 收入总计: %.2f", outcomeValue, incomeValue));
    }
}