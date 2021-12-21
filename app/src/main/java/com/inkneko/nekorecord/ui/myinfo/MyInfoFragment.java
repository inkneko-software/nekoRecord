package com.inkneko.nekorecord.ui.myinfo;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.inkneko.nekorecord.R;
import com.inkneko.nekorecord.data.localstorage.RecordDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyInfoFragment extends Fragment {

    private MyInfoViewModel myInfoViewModel;
    private final int DATABASE_BACKUP_REQUEST_CODE = 10001;
    private final int DATABASE_IMPORT_REQUEST_CODE = 10002;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        myInfoViewModel =
                ViewModelProviders.of(this).get(MyInfoViewModel.class);
        View root = inflater.inflate(R.layout.fragment_myinfo, container, false);


        /**
         * TODO:
         * 实现一个菜单。菜单项包括：类别管理，数据管理
         * 实现类别管理界面
         * 实现数据管理界面
         */

        TextView categoryManagementTextView = root.findViewById(R.id.myinfo_management_category);
        TextView exportDatabaseTextView = root.findViewById(R.id.myinfo_export_database);
        TextView importDatabaseTextView = root.findViewById(R.id.myinfo_import_database);

        exportDatabaseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd_HHmm");
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/octet-stream");
                intent.putExtra(Intent.EXTRA_TITLE, String.format("nekoRecord_%s.db", fmt.format(new Date())));
                startActivityForResult(intent, DATABASE_BACKUP_REQUEST_CODE);
            }
        });

        importDatabaseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/octet-stream");
                startActivityForResult(intent, DATABASE_IMPORT_REQUEST_CODE);
            }
        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        if (requestCode == DATABASE_BACKUP_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            RecordDatabase.getDatabase(getContext()).close();
            try {
                Uri uri = data.getData();
                ParcelFileDescriptor fileDescriptor = getActivity().getContentResolver().openFileDescriptor(uri, "w");
                FileOutputStream fileOutputStream = new FileOutputStream(fileDescriptor.getFileDescriptor());
                File databaseFile = getContext().getDatabasePath(RecordDatabase.databaseName);
                FileInputStream inputStream = new FileInputStream(databaseFile);

                int readNum = 0;
                byte[] buffer = new byte[65535];
                while ((readNum = inputStream.read(buffer, 0, 65535)) != -1){
                    fileOutputStream.write(buffer, 0, readNum);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                Toast.makeText(getContext(), "导出成功", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(getContext(), "导出失败", Toast.LENGTH_SHORT).show();
            }
            RecordDatabase.getDatabase(getContext());
        }else if (requestCode == DATABASE_IMPORT_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Uri uri = data.getData();
            try {
                ParcelFileDescriptor fileDescriptor = getActivity().getContentResolver().openFileDescriptor(uri, "r");
                FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
                RecordDatabase.restoreDatabase(getContext(), inputStream);
                Toast.makeText(getContext(), "导入成功", Toast.LENGTH_SHORT).show();
                inputStream.close();
            }catch (IOException e){
                Toast.makeText(getContext(), "导入失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}