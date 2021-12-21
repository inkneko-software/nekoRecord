package com.inkneko.nekorecord.ui.component;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.inkneko.android.utils.ResourceManagement;
import com.inkneko.nekorecord.R;
import com.inkneko.nekorecord.data.model.Category;
import com.inkneko.nekorecord.data.model.Record;
import com.inkneko.nekorecord.data.model.RecordTag;
import com.inkneko.nekorecord.data.model.Tag;
import com.inkneko.nekorecord.data.model.relations.RecordInfo;
import com.inkneko.nekorecord.data.model.relations.CategoryInfo;
import com.inkneko.nekorecord.ui.record.RecordFragment;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class RecordEditFragment extends DialogFragment {

    private  LayoutInflater inflater;
    private View rootView;
    androidx.appcompat.app.AlertDialog dialogInstance;

    private RecordEditViewModel recordEditViewModel;

    private Calendar date;
    private RecordInfo recordInfo;
    private String title;

    private Category selectedCategory;
    private List<Tag> selectedTags;

    public RecordEditFragment(String title, Calendar date) {
        this.title = title;
        selectedTags = new LinkedList<>();
        this.date = date;
    }

    public RecordEditFragment(RecordInfo recordInfo, String title) {
        this.recordInfo = recordInfo;
        this.title = title;
        selectedTags = new LinkedList<>();
        this.date = Calendar.getInstance();
        this.date.setTimeInMillis(recordInfo.recordDetail.getAddDate());
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        this.inflater = getActivity().getLayoutInflater();
        rootView =  inflater.inflate(R.layout.fragment_record_edit, null);
                recordEditViewModel = ViewModelProviders.of(this).get(RecordEditViewModel.class);

        RadioGroup radioGroup = rootView.findViewById(R.id.current_category_type_radio_group);
        RadioButton incomeRadioButton = rootView.findViewById(R.id.category_type_income);
        RadioButton outcomeRadioButton = rootView.findViewById(R.id.category_type_outcome);

        EditText currentPriceEditText = rootView.findViewById(R.id.checkout_value);
        EditText currentRemarkEditText = rootView.findViewById(R.id.remark);
        View timeLayoutView = rootView.findViewById(R.id.record_time_layout);


        if (recordInfo == null){
            recordEditViewModel.getCategories("支出").observe(this, new Observer<List<CategoryInfo>>() {
                @Override
                public void onChanged(List<CategoryInfo> categoryInfos) {
                    applyCategoryInfo(categoryInfos.get(0).category);
                    applyTagList(categoryInfos.get(0).tags);
                    applyCategoryList(categoryInfos);
                }
            });
            applyDateView(date.getTimeInMillis());
        }

        if (recordInfo != null){
            String recordCategoryType = recordInfo.category.getType();
            applyDateView(recordInfo.recordDetail.getAddDate());
            if (recordCategoryType.compareTo("支出") == 0){
                outcomeRadioButton.setChecked(true);
            }else {
                incomeRadioButton.setChecked(true);
            }
            recordEditViewModel.getCategories(recordCategoryType).observe(this, new Observer<List<CategoryInfo>>() {
                @Override
                public void onChanged(List<CategoryInfo> categoryInfos) {
                    applyCategoryList(categoryInfos);
                    for (CategoryInfo categoryInfo : categoryInfos){
                        if (categoryInfo.category.getCategoryId().equals(recordInfo.category.getCategoryId())){
                            applyCategoryInfo(recordInfo.category);
                            selectedTags = recordInfo.recordTags;
                            applyTagList(categoryInfo.tags);
                            break;
                        }
                    }
                }
            });

            //用户体验：如果数据为0，则使用EditText默认显示的数据，用户即可不用删掉0，点一下即可输入数据
            Float value = recordInfo.recordDetail.getValue();
            if (value != 0){
                currentPriceEditText.setText(String.format("%.2f", value));
            }
            currentRemarkEditText.setText(recordInfo.recordDetail.getRemark());
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String categoryType;
                if (checkedId == R.id.category_type_income){
                    categoryType = "收入";
                }else{
                    categoryType = "支出";
                }

                recordEditViewModel.getCategories(categoryType).observe(RecordEditFragment.this, new Observer<List<CategoryInfo>>() {
                    @Override
                    public void onChanged(List<CategoryInfo> categoryInfos) {
                        applyCategoryList(categoryInfos);
                        applyTagList(categoryInfos.get(0).tags);
                        applyCategoryInfo(categoryInfos.get(0).category);
                    }
                });
            }
        });

        timeLayoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialTimePicker.Builder builder =  new MaterialTimePicker.Builder();
                if (recordInfo != null){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(recordInfo.recordDetail.getAddDate());
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minutes = calendar.get(Calendar.MINUTE);
                    builder.setHour(hour);
                    builder.setMinute(minutes);
                }
                builder.setInputMode(MaterialTimePicker.INPUT_MODE_KEYBOARD);
                builder.setTimeFormat(TimeFormat.CLOCK_12H).build();

                MaterialTimePicker picker = builder.build();
                picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        date.set(Calendar.HOUR_OF_DAY, picker.getHour());
                        date.set(Calendar.MINUTE, picker.getMinute());
                        applyDateView(date.getTimeInMillis());
                    }
                });
                picker.show(getChildFragmentManager(), "timepicker");;
            }
        });

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle(title)
                .setView(rootView)
                .setPositiveButton("保存", saveRecordListener)
                .setNegativeButton("取消", null)
                .setNeutralButton("删除", saveRecordListener)
                .setCancelable(false);

        dialogInstance = builder.create();
        dialogInstance.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                if (recordInfo == null){
                    dialogInstance.getButton( androidx.appcompat.app.AlertDialog.BUTTON_NEUTRAL).setEnabled(false);
                }
            }
        });

        return dialogInstance;
    }

    /**
     * 更新类别信息的显示
     * @param category
     */
    private void applyCategoryInfo(Category category){
        selectedCategory = category;
        selectedTags.clear();
        ImageView currentCategoryImageView = rootView.findViewById(R.id.category_icon);
        TextView currentCategoryNameTextView = rootView.findViewById(R.id.category_name);
        currentCategoryImageView.setImageResource(ResourceManagement.getDrawableResourceId(category.getIconResourceName()));
        currentCategoryNameTextView.setText(category.getName());
    }

    /**
     * 根据所提供的类别信息，更新类别显示列表，并将
     * @param categories 所提供的类别信息
     */
    private void applyCategoryList(List<CategoryInfo> categories){
        LinearLayout categoryDisplayList = rootView.findViewById(R.id.category_display);
        categoryDisplayList.removeAllViews();
        for (CategoryInfo category : categories){
            View categoryDisplayView = inflater.inflate(R.layout.layout_category_display, categoryDisplayList, false);
            ImageView icon = categoryDisplayView.findViewById(R.id.category_icon);
            icon.setImageResource(ResourceManagement.getDrawableResourceId(category.category.getIconResourceName()));
            TextView name = categoryDisplayView.findViewById(R.id.category_name);
            name.setText(category.category.getName());
            categoryDisplayView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    applyCategoryInfo(category.category);
                    applyTagList(category.tags);
                }
            });
            categoryDisplayList.addView(categoryDisplayView);
        }
    }

    /**
     * 根据标签列表更新标签列表view
     * @param tags 所指定的标签
     */
    private void applyTagList(List<Tag> tags){
        LinearLayout tagDisplayList = rootView.findViewById(R.id.tag_display);
        tagDisplayList.removeAllViews();
        for(Tag tag : tags){
            View tagDisplayView = inflater.inflate(R.layout.layout_tag_display, tagDisplayList, false);
            TextView name = tagDisplayView.findViewById(R.id.tag_name);
            name.setText(tag.getName());
            tagDisplayView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View tagView) {
                    tagView.setSelected(!tagView.isSelected());
                    if (tagView.isSelected()){
                        selectedTags.add(tag);
                    }else{
                        selectedTags.remove(tag);
                    }
                }
            });
            if (selectedTags.contains(tag)){
                tagDisplayView.setSelected(true);
            }
            tagDisplayList.addView(tagDisplayView);
            //TODO: 记录选中状态
        }
    }

    private void applyDateView(Long timeMillis){
        TextView dateView = rootView.findViewById(R.id.record_time);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String dateString = fmt.format(timeMillis);
        dateView.setText(dateString);
    }

    private DialogInterface.OnClickListener saveRecordListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE){
                EditText valueEditText = rootView.findViewById(R.id.checkout_value);
                String valueText =valueEditText.getText().toString();
                Float value = Float.valueOf(valueText.length() == 0 ? "0" : valueText);
                EditText remarkEditText = rootView.findViewById(R.id.remark);
                String remark = remarkEditText.getText().toString();
                if (recordInfo == null){
                    recordEditViewModel
                            .createRecord(new Record(null, selectedCategory.getCategoryId(),value, date.getTimeInMillis(), remark))
                            .observe(RecordEditFragment.this, recordId -> {
                                for (Tag tag : selectedTags){
                                    recordEditViewModel.addRecordTag(new RecordTag(recordId, tag.getTagId()));
                                }
                                Toast.makeText(RecordEditFragment.this.getContext(), "保存成功", Toast.LENGTH_SHORT).show();
                            });
                }else{
                    recordInfo.recordDetail.setAddDate(date.getTimeInMillis());
                    recordInfo.recordDetail.setCategoryId(selectedCategory.getCategoryId());
                    recordInfo.recordDetail.setValue(value);
                    recordInfo.recordDetail.setRemark(remark.trim());
                    recordEditViewModel.updateRecord(recordInfo.recordDetail);
                    //异步过程，需要等待标签清空后再进行添加操作。
                    recordEditViewModel.clearRecordTags(recordInfo.recordDetail.getRecordId()).observe(RecordEditFragment.this, (signal) ->{
                        for (Tag tag : selectedTags){
                            recordEditViewModel.addRecordTag(new RecordTag(recordInfo.recordDetail.getRecordId(), tag.getTagId()));
                        }
                        Toast.makeText(RecordEditFragment.this.getContext(), "保存成功", Toast.LENGTH_SHORT).show();
                    });
                }
            }else if (which == DialogInterface.BUTTON_NEUTRAL){
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("确认删除")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("删除", (removeDialog, selection)->{
                            if (selection == DialogInterface.BUTTON_POSITIVE){
                                recordEditViewModel.clearRecordTags(recordInfo.recordDetail.getRecordId());
                                recordEditViewModel.removeRecord(recordInfo.recordDetail);
                            }
                        }).create().show();
            }
        }
    };
}