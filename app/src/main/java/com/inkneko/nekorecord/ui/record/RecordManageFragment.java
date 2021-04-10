package com.inkneko.nekorecord.ui.record;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.inkneko.nekorecord.R;
import com.inkneko.nekorecord.data.DailyRecord;

import java.security.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class RecordManageFragment extends Fragment {

    private RecordViewModel viewModel;
    private String mType;
    private List<Pair<View, DailyRecord>> recordViewList;
    private List<DailyRecord> removedRecords;
    private LayoutInflater inflater;
    private ViewGroup container;

    private MutableLiveData<Float> total;

    public RecordManageFragment(String type, RecordViewModel viewModel){
        recordViewList = new LinkedList<>();
        mType = type;
        this.viewModel = viewModel;
        removedRecords = new LinkedList<>();
        total = new MutableLiveData<>(0f);
    }

    public String getEventType(){return mType;}

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState){
        this.inflater = inflater;
        this.container = container;

        View root = inflater.inflate(R.layout.layout_record_manage_ui, container, false);

        TextView recordTypeTitle = root.findViewById(R.id.record_manager_type);
        recordTypeTitle.setText(mType);

        ImageButton addRecordBtn = root.findViewById(R.id.add_record_btn);


        addRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRecord();
            }
        });


        fetch();
        return root;
    }

    private void addRecord(){
        addRecord("", 0f);
    }

    private void addRecord(String comment, Float price){
        DailyRecord recordModel = new DailyRecord(comment, price, mType, (new Date()).getTime());
        addRecord(recordModel);
    }

    private void addRecord(DailyRecord recordModel){
        total.setValue(total.getValue() + recordModel.getPrice());

        View root = getView();
        final LinearLayout recordListLinearLayout = root.findViewById(R.id.record_manage_ui_record_list);
        final TextView noRecordHint = root.findViewById(R.id.record_manage_ui_nothing_hint);

        View recordView = inflater.inflate(R.layout.layout_record_detail, container, false);
        EditText eventComment = recordView.findViewById(R.id.record_comment);
        EditText eventPrice = recordView.findViewById(R.id.record_price);
        eventComment.setText(recordModel.getEvent());
        eventComment.setFocusable(false);
        eventComment.setOnClickListener((View parent)->{
            String[] types = {"早餐", "午餐", "晚餐", "手动输入"};

            AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
            builder.setTitle("选择备注");
            builder.setItems(types, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //to do: bug
                    if (types[which].equals("手动输入")){
                        eventComment.setFocusableInTouchMode(true);
                        eventComment.requestFocus();
                        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    } else {
                        eventComment.setText(types[which]);
                    }
                    // the user clicked on colors[which]
                }
            });
            builder.show();
        });

        eventComment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false){
                    v.setFocusable(false);
                }
            }
        });

        if (recordModel.getPrice() != 0f){
            eventPrice.setText(recordModel.getPrice().toString());
        }
        Button removeRecordBtn = recordView.findViewById(R.id.record_remove_btn);
        Pair<View, DailyRecord> record = new Pair<>(recordView, recordModel);
        removeRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(getContext())
                        .setTitle("删除以下记录?")
                        .setMessage(String.format(Locale.CHINESE, "事件：%s\n金额：%s", eventComment.getText().toString(), eventPrice.getText().toString()))
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeRecord(record);
                                removedRecords.add(record.second);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create()
                        .show();

            }
        });

        eventPrice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            private Float mLastValue = 0f;
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String priceText = eventPrice.getText().toString();
                if (priceText.length() == 0){
                    priceText = "0";
                }
                if (hasFocus){
                    mLastValue = Float.parseFloat(priceText);

                }else{
                    total.setValue(total.getValue() - mLastValue + Float.parseFloat(priceText));
                }
            }
        });
        //TODO:
        //add event_comment selection list
        //add activity for the creation of selectable event_comment

        noRecordHint.setVisibility(View.INVISIBLE);
        recordViewList.add(record);

        recordListLinearLayout.addView(recordView);
    }

    private void removeRecord(Pair<View, DailyRecord> record){
        total.setValue(total.getValue() - record.second.getPrice());
        View root = getView();
        final LinearLayout recordListLinearLayout = root.findViewById(R.id.record_manage_ui_record_list);
        final TextView noRecordHint = root.findViewById(R.id.record_manage_ui_nothing_hint);

        record.first.setVisibility(View.GONE);
        recordListLinearLayout.removeViewInLayout(record.first);
        recordViewList.remove(record);
        if (recordViewList.size() == 0){
            noRecordHint.setVisibility(View.VISIBLE);
        }
    }

    public Float save(){
        Float total = 0f;
        for(Pair<View, DailyRecord> record : recordViewList){
            EditText eventComment = record.first.findViewById(R.id.record_comment);
            EditText eventPrice = record.first.findViewById(R.id.record_price);

            Float eventPriceFloat = 0.f;
            try{
                eventPriceFloat = Float.valueOf(eventPrice.getText().toString());
            }catch (Exception ignore){}

            record.second.setEvent(eventComment.getText().toString());
            record.second.setPrice(eventPriceFloat);
            total += record.second.getPrice();
            viewModel.addRecord(record.second);
        }

        for(DailyRecord record : removedRecords){
            viewModel.removeRecord(record);
        }
        return total;
    }

    public MutableLiveData<Float> getTotalLiveData(){
        return this.total;
    }

    private void fetch(){
        LiveData<List<DailyRecord>> records = viewModel.getRecordsToday(mType);
        records.observe(this, new Observer<List<DailyRecord>>() {
            @Override
            public void onChanged(List<DailyRecord> dailyRecords) {
                for (Iterator<Pair<View, DailyRecord>> iter = recordViewList.iterator(); iter.hasNext(); ){
                    Pair<View, DailyRecord> record = iter.next();
                    iter.remove();
                    removeRecord(record);
                }

                for(DailyRecord record : dailyRecords){
                    addRecord(record);
                }
            }
        });
    }

}
