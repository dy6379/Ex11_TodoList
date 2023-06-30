package com.busanit.ex11_todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

public class InputActivity extends AppCompatActivity {
    private EditText editWork;
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private Intent intent;
    private boolean update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        editWork = findViewById(R.id.editWork);

        intent = getIntent();
        update = intent.getBooleanExtra("update", false);
        if (update){
            String work = intent.getStringExtra("work");
            editWork.setText(work);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        String work = editWork.getText().toString();
        if(!work.equals("")){
            Intent resultIntent = new Intent();
            resultIntent.putExtra("work", work);

            dbHelper = new DBHelper(this);
            database = dbHelper.getWritableDatabase();
            if (update){
                // 수정
                int id = intent.getIntExtra("id",0);
                int position = intent.getIntExtra("position", 0);
                resultIntent.putExtra("id",id);
                resultIntent.putExtra("position",position);
                database.execSQL("update todo set work = '"+work+"' where _id = "+id);
            } else {
                // 입력
                database.execSQL("insert into todo (work) values ('"+work+"')");
            }

            database.close();
            setResult(RESULT_OK, resultIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}