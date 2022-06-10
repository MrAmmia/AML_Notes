package net.thebookofcode.www.amlnotes.Model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import net.thebookofcode.www.amlnotes.Entities.Note;
import net.thebookofcode.www.amlnotes.Repository.NoteRepository;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private NoteRepository repository;
    private LiveData<List<Note>> allNotes;
    private final MutableLiveData<Integer> mNoteID = new MutableLiveData<>();

    public NoteViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application);
        allNotes = repository.getAllNotes();
    }

    public void insert(Note note){
        repository.insert(note);
    }

    public void update(Note note){
        repository.update(note);
    }

    public void delete(Note note){
        repository.delete(note);
    }

    public void deleteAllNotes(){
        repository.deleteAllNotes();
    }

    public LiveData<List<Note>> getAllNotes(){
        return allNotes;
    }

    /*public Note getSpecificNote(int id) {
        return repository.getSpecificNote(id);
    }*/

    public void setNoteID(int noteID) {
        mNoteID.setValue(noteID);
    }
    public LiveData<Integer> getNoteID() {
        return mNoteID;
    }

}


