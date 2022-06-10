package net.thebookofcode.www.amlnotes;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;

import androidx.appcompat.widget.SearchView;

import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import net.thebookofcode.www.amlnotes.Adapters.NoteAdapter;
import net.thebookofcode.www.amlnotes.Entities.Note;
import net.thebookofcode.www.amlnotes.Model.NoteViewModel;

import java.util.List;

public class NoteListFragment extends Fragment {
    private NoteViewModel noteViewModel;
    SearchView search_view;

    public NoteListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_note_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        search_view = view.findViewById(R.id.search_view);
        search_view.setImeOptions(EditorInfo.IME_ACTION_DONE);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        ImageView deleteNotes = view.findViewById(R.id.deleteNotes);
        FloatingActionButton fab = view.findViewById(R.id.fab);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        NoteAdapter adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        noteViewModel = new ViewModelProvider(getActivity()).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(getViewLifecycleOwner(), new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                // Update RecyclerView
                adapter.setNotes(notes);
            }
        });

        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.trim().isEmpty()) {
                    adapter.getFilter().filter(newText);
                }
                return false;
            }
        });

        deleteNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setMessage("Do you want to delete all notes?")
                        .setTitle("Warning!")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                noteViewModel.deleteAllNotes();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).create().show();

            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                noteViewModel.delete(adapter.getNote(viewHolder.getAdapterPosition()));
                Toast.makeText(getContext(), "Note Deleted!", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new NoteAdapter.NoteItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                noteViewModel.setNoteID(note.getId());
                //NavDirections action = NoteListFragmentDirections.actionNoteListFragmentToNoteFragment(note);
                NoteListFragmentDirections.ActionNoteListFragmentToAddEditNoteFragment action = NoteListFragmentDirections.actionNoteListFragmentToAddEditNoteFragment();
                action.setCurrentNote(note);
                Navigation.findNavController(recyclerView).navigate(action);
            }
        });

        fab.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_noteListFragment_to_addEditNoteFragment);
        });

    }
}