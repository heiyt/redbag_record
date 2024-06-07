package com.example.record_redbag;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase mDatabase;
    private EditText mEditTextSender;
    private EditText mEditTextAmount;
    private LinearLayout mLinearLayoutRecords;
    private TextView mTotalMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 创建或打开数据库
        RedEnvelopeDbHelper dbHelper = new RedEnvelopeDbHelper(this);
        mDatabase = dbHelper.getWritableDatabase();
        mEditTextSender = findViewById(R.id.editTextSender);
        mEditTextAmount = findViewById(R.id.editTextAmount);
        mEditTextAmount.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        mLinearLayoutRecords = findViewById(R.id.linearLayoutRecords);
        mTotalMoney = findViewById(R.id.total_money);

        Button buttonAdd = findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRedEnvelopeRecord();
            }
        });

        Button buttonDelete = findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {showConfirmationDialog();}
        });

        // 显示红包记录
        displayRedEnvelopeRecords();
    }

    private void addRedEnvelopeRecord() {
        String sender = mEditTextSender.getText().toString().trim();
        String amountStr = mEditTextAmount.getText().toString().trim();
        if (sender.isEmpty() || amountStr.isEmpty()) {
            return;
        }
        int amount = Integer.parseInt(amountStr);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm");
        Date date = new Date(System.currentTimeMillis());//获取系统时间
        String time = simpleDateFormat.format(date);
        // 插入红包记录到数据库
        ContentValues values = new ContentValues();
        values.put(RedEnvelopeContract.RedEnvelopeEntry.COLUMN_NAME, sender);
        values.put(RedEnvelopeContract.RedEnvelopeEntry.COLUMN_AMOUNT, amount);
        values.put(RedEnvelopeContract.RedEnvelopeEntry.COLUMN_TIME, time);
        mDatabase.insert(RedEnvelopeContract.RedEnvelopeEntry.TABLE_NAME, null, values);
        // 清空输入框
        mEditTextSender.getText().clear();
        mEditTextAmount.getText().clear();
        // 重新显示红包记录
        displayRedEnvelopeRecords();
    }

    private void deleteRedEnvelopRecord(){
        Cursor cursor = mDatabase.rawQuery("SELECT " + RedEnvelopeContract.RedEnvelopeEntry._ID +
                " FROM " + RedEnvelopeContract.RedEnvelopeEntry.TABLE_NAME +
                " ORDER BY " + RedEnvelopeContract.RedEnvelopeEntry._ID + " DESC LIMIT 1", null);

        if (cursor != null && cursor.moveToFirst()) {
            try {
                int lastRecordId = cursor.getInt(cursor.getColumnIndexOrThrow(RedEnvelopeContract.RedEnvelopeEntry._ID));
                mDatabase.delete(RedEnvelopeContract.RedEnvelopeEntry.TABLE_NAME,
                        RedEnvelopeContract.RedEnvelopeEntry._ID + "=?",
                        new String[]{String.valueOf(lastRecordId)});
            }catch (Exception e) {
                // 处理异常情况
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        displayRedEnvelopeRecords();
    }

    private void displayRedEnvelopeRecords() {
        mLinearLayoutRecords.removeAllViews();

        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + RedEnvelopeContract.RedEnvelopeEntry.TABLE_NAME, null);

        try {
            int nameColumnIndex = cursor.getColumnIndex(RedEnvelopeContract.RedEnvelopeEntry.COLUMN_NAME);
            int amountColumnIndex = cursor.getColumnIndex(RedEnvelopeContract.RedEnvelopeEntry.COLUMN_AMOUNT);
            int timeColumnIndex = cursor.getColumnIndex(RedEnvelopeContract.RedEnvelopeEntry.COLUMN_TIME);
            int total = 0;
            while (cursor.moveToNext()) {
                String currentName = cursor.getString(nameColumnIndex);
                int currentAmount = cursor.getInt(amountColumnIndex);
                total += currentAmount;
                String currentTime = cursor.getString(timeColumnIndex);
                TextView textViewRecord = new TextView(this);
                textViewRecord.setTextSize(20);
                textViewRecord.setTypeface(Typeface.DEFAULT_BOLD);
                String addText = currentName + ": " + currentAmount + "元" + "   " + currentTime;
                textViewRecord.setText(addText);
                mLinearLayoutRecords.addView(textViewRecord);
            }
            mTotalMoney.setText(total+"");
        } finally {
            cursor.close();
        }
    }

    // 弹出询问窗口的方法
    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认");
        builder.setMessage("你确定要删除最后一条红包记录吗？");

        // 添加确认按钮
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteRedEnvelopRecord();
            }
        });

        // 添加取消按钮
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 在这里执行取消操作
            }
        });

        // 创建并显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
