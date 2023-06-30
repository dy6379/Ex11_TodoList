package com.busanit.ex11_todolist;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TodoAdaptor extends RecyclerView.Adapter<TodoAdaptor.ViewHolder> implements OnTodoItemClickListener{
    ArrayList<Todo> items = new ArrayList<Todo>();
    OnTodoItemClickListener listener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.todo_item, parent,false);
        return new ViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Todo item = items.get(position);
        holder.todoText.setText(item.getWork());
        // 삭제 구현
        holder.btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper dbHelper = new DBHelper(v.getContext());
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                database.execSQL("delete from todo where _id = "+item.get_id());
                database.close();
                items.remove(position);
                notifyItemRemoved(position);
                notifyItemChanged(position, items.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Todo item){
        items.add(item);
    }

    public void setItems(ArrayList<Todo> items){
        this.items = items;
    }

    public Todo getItem(int position){
        return items.get(position);
    }

    public void setItem(int position, Todo item){
        items.set(position, item);
    }

    public void setOnItemClickListener(OnTodoItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if (listener!=null){
            listener.onItemClick(holder, view, position);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView todoText;
        Button btnDel;

        public ViewHolder(@NonNull View itemView, final OnTodoItemClickListener listener) {
            super(itemView);
            todoText = itemView.findViewById(R.id.todoText);
            btnDel = itemView.findViewById(R.id.btnDel);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener!=null){
                        listener.onItemClick(ViewHolder.this, v, position);
                    }
                }
            });
        }
    }
}
