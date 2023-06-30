package com.busanit.ex11_todolist;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Todo> todoList;
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private int saveId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        // 1.조회 구현(select) DBHelper클래스 필요
        todoList = new ArrayList<Todo>();
        dbHelper = new DBHelper(this);
        database = dbHelper.getReadableDatabase();

        Cursor cursor = database.rawQuery("select _id, work from todo", null);
        int recordCount = cursor.getCount();
        for (int i=0; i<recordCount; i++){
            cursor.moveToNext();
            int id = cursor.getInt(0);
            saveId = id;
            String work = cursor.getString(1);
            Todo todo = new Todo(id, work);
            todoList.add(todo);
        }
        cursor.close();

        TodoAdaptor adaptor = new TodoAdaptor();
        adaptor.setItems(todoList);
        recyclerView.setAdapter(adaptor);

        // 입력 구현(insert) InputActivity 필요
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode()==RESULT_OK){
                            saveId++;
                            String work = result.getData().getStringExtra("work");
                            adaptor.addItem(new Todo(saveId,work));
                            adaptor.notifyDataSetChanged();
                        }
                    }
                });

        ExtendedFloatingActionButton btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), InputActivity.class);
                launcher.launch(intent);
            }
        });

        // 수정 구현(update) OnTodoItemClickListener 만들고, 구현
        ActivityResultLauncher<Intent> updateLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode()==RESULT_OK){
                            String work = result.getData().getStringExtra("work");
                            int id = result.getData().getIntExtra("id",0);
                            int position = result.getData().getIntExtra("position",0);
                            adaptor.setItem(position, new Todo(id, work));
                            adaptor.notifyDataSetChanged();
                        }
                    }
                });
        adaptor.setOnItemClickListener(new OnTodoItemClickListener() {
            @Override
            public void onItemClick(TodoAdaptor.ViewHolder holder, View view, int position) {
                Todo todo = adaptor.getItem(position);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("수정하시겠습니까?").setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent updateIntent = new Intent(getApplicationContext(), InputActivity.class);
                        updateIntent.putExtra("update",true);
                        updateIntent.putExtra("id", todo.get_id());
                        updateIntent.putExtra("work", todo.getWork());
                        updateIntent.putExtra("position", position);
                        updateLauncher.launch(updateIntent);
                    }
                }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        //삭제 구현 TodoAdaptor에서 구현
    }
}