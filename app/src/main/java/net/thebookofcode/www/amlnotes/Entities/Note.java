package net.thebookofcode.www.amlnotes.Entities;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_table")
public class Note implements Parcelable {

    public void setId(int id) {
        this.id = id;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;

    private String content;

    @ColumnInfo(name = "date_time")
    private String dateTime;

    @ColumnInfo(name = "img_path")
    private String imgPath;

    private String todo;


    // a concatenated string of done or undone todos
    @ColumnInfo(name = "done_string")
    private String doneString;

    private int doneTodo;

    private int totalTodo;

    @ColumnInfo(name = "reminder_date_time")
    private String reminderDateTime;

    public Note(String title, String content, String dateTime, String imgPath, String todo, String doneString, int doneTodo, int totalTodo, String reminderDateTime) {
        this.title = title;
        this.content = content;
        this.dateTime = dateTime;
        this.imgPath = imgPath;
        this.todo = todo;
        this.doneString = doneString;
        this.doneTodo = doneTodo;
        this.totalTodo = totalTodo;
        this.reminderDateTime = reminderDateTime;
    }

    protected Note(Parcel in) {
        id = in.readInt();
        title = in.readString();
        content = in.readString();
        dateTime = in.readString();
        imgPath = in.readString();
        todo = in.readString();
        doneString = in.readString();
        doneTodo = in.readInt();
        totalTodo = in.readInt();
        reminderDateTime = in.readString();
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getTodo() {
        return todo;
    }

    public void setTodo(String todo) {
        this.todo = todo;
    }

    public int getDoneTodo() {
        return doneTodo;
    }

    public void setDoneTodo(int doneTodo) {
        this.doneTodo = doneTodo;
    }

    public int getTotalTodo() {
        return totalTodo;
    }

    public void setTotalTodo(int totalTodo) {
        this.totalTodo = totalTodo;
    }

    public String getReminderDateTime() {
        return reminderDateTime;
    }

    public void setReminderDateTime(String reminderDateTime) {
        this.reminderDateTime = reminderDateTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getDoneString() {
        return doneString;
    }

    public void setDoneString(String doneString) {
        this.doneString = doneString;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(dateTime);
        dest.writeString(imgPath);
        dest.writeString(todo);
        dest.writeString(doneString);
        dest.writeInt(doneTodo);
        dest.writeInt(totalTodo);
        dest.writeString(reminderDateTime);
    }
}
