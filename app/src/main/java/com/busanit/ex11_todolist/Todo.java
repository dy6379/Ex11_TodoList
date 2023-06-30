package com.busanit.ex11_todolist;

public class Todo {
    private int _id;
    private String work;

    public Todo(int _id, String work) {
        this._id = _id;
        this.work = work;
    }

    public int get_id() {
        return _id;
    }

    public String getWork() {
        return work;
    }
}
