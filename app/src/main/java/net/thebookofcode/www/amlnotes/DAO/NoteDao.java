package net.thebookofcode.www.amlnotes.DAO;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import net.thebookofcode.www.amlnotes.Entities.Note;

import java.util.List;

@Dao
public interface NoteDao {
    @Insert
    void insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    @Query("SELECT * FROM note_table ORDER BY id DESC")
    LiveData<List<Note>> getAllNotes();

    @Query("DELETE FROM note_table")
    void  deleteAllNotes();


    /*
    @Query("SELECT * FROM note_table WHERE id = :id")
    Note getSpecificNote(int id);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void  insertNotes(note: Notes)



    @Query("DELETE FROM notes WHERE id = :id")
    void  deleteSpecificNote(id: Int)

     */

}
