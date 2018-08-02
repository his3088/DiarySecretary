package com.project.bryan.diary_sample7.Diary;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.project.bryan.diary_sample7.R;

public class DiaryEditActivity extends AppCompatActivity {

    String date;
    Button btn_cancel, btn_save, btn_delete;
    EditText contents;
    private static SQLiteDatabase db;
    private static DiaryDBHelper dbHelper;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_edit);

        // MainActivity로부터 찾고자 하는 날짜를 받는다.
        Intent intent_getDate = getIntent();
        date = intent_getDate.getStringExtra("date");

        btn_cancel = (Button)findViewById(R.id.btn_diary_cancel);
        btn_delete = (Button)findViewById(R.id.btn_diary_delete);
        btn_save = (Button)findViewById(R.id.btn_diary_save);
        contents = (EditText)findViewById(R.id.editText_diaryEdit);


        dbHelper = new DiaryDBHelper(this);
        db = dbHelper.getReadableDatabase();
        String sql = "select * from DIARY_TB where DATE = " + date ;
        Cursor cursor = db.rawQuery(sql, null);

        // 기존에 저장된 내용이 있다면 setText 없다면 hint
        if(cursor.getCount() == 0) {
            contents.setHint("저장된 내용이 없습니다.");
            cursor.close();
        }
        else{
            cursor.moveToFirst();
            contents.setText(cursor.getString(cursor.getColumnIndex("CONTENTS")));
            cursor.close();
        }




        //---------------취소 버튼
        btn_cancel.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                db.close();
                finish();
            }
        });


        //----------------삭제 버튼
        btn_delete.setOnClickListener(new Button.OnClickListener(){
            Toast toast;
            @Override
            public void onClick(View v) {
                contents.setText("");
                db.execSQL("delete from DIARY_TB where date="+ date);
                db.close();
                toast.makeText(DiaryEditActivity.this,"삭제 실행 완료",Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK,null);
                finish();
            }
        });


        //----------------저장 버튼
        btn_save.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                Cursor cursor = db.rawQuery("select * from DIARY_TB where DATE = " + date, null);
                String contents_input = contents.getText().toString();

                if(contents_input.length() == 0 ){
                    Toast.makeText(DiaryEditActivity.this,"내용을 입력 후 저장하세요",Toast.LENGTH_SHORT).show();
                }else {
                    //데이터 없다면 바로 저장
                    if (cursor.getCount() == 0) {
                        db.execSQL("insert into DIARY_TB (DATE, CONTENTS) values ('" + date + "', '" + contents_input + "'); ");
                    }
                    //데이터 있다면 지우고 저장
                    else {
                        db.execSQL("delete from DIARY_TB where DATE = " + date + ";");
                        db.execSQL("insert into DIARY_TB (DATE, CONTENTS) values ('" + date + "', '" + contents_input + "'); ");
                    }

                    cursor.close();
                    db.close();
                    setResult(RESULT_OK,null);
                    finish();
                }
            }
        });
    }

}
