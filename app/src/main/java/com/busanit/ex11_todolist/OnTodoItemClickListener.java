package com.busanit.ex11_todolist;

import android.view.View;

public interface OnTodoItemClickListener {
    public void onItemClick(TodoAdaptor.ViewHolder holder, View view, int position);
}
