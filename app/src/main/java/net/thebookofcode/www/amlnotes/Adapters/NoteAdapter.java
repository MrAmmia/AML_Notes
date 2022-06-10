package net.thebookofcode.www.amlnotes.Adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.thebookofcode.www.amlnotes.Entities.Note;
import net.thebookofcode.www.amlnotes.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteHolder> implements Filterable {
    private List<Note> notes = new ArrayList<>();
    private ArrayList<Note> mNotesFull;
    private NoteItemClickListener listener;

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new NoteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        Note currentNote = notes.get(position);
        if (!currentNote.getTitle().isEmpty()) {
            holder.tvTitle.setVisibility(View.VISIBLE);
            holder.tvTitle.setText(currentNote.getTitle());
        }
        if (!currentNote.getContent().isEmpty()) {
            holder.tvBody.setVisibility(View.VISIBLE);
            holder.tvBody.setText(currentNote.getContent());
        }
        holder.tvDateTime.setText(currentNote.getDateTime());
        if (!currentNote.getReminderDateTime().isEmpty()) {
            holder.reminderLayout.setVisibility(View.VISIBLE);
            holder.imgReminder.setVisibility(View.VISIBLE);
            holder.reminderTime.setText(currentNote.getReminderDateTime());
        }
        if (currentNote.getTotalTodo() != 0) {
            String total = Integer.toString(currentNote.getTotalTodo());
            String done = Integer.toString(currentNote.getDoneTodo());
            holder.todoLayout.setVisibility(View.VISIBLE);
            holder.tvTodoDone.setText(done);
            holder.tvTodoTotal.setText(total);
        }
        if (!currentNote.getImgPath().isEmpty()) {
            String path = currentNote.getImgPath();
            holder.imageNote.setVisibility(View.VISIBLE);
            holder.imageNote.setImageBitmap(getImageFromPath(path));
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes;
        mNotesFull = new ArrayList<>(notes);
        notifyDataSetChanged();
    }

    public Note getNote(int position) {
        return notes.get(position);
    }

    @Override
    public Filter getFilter() {
        return mNotesFilter;
    }

    private Filter mNotesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Note> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0 || constraint.equals("")) {
                filteredList.addAll(mNotesFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Note item : mNotesFull) {
                    if (item.getTitle().toLowerCase().contains(filterPattern) || item.getContent().toLowerCase().contains(filterPattern)
                    || item.getTodo().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notes.clear();
            notes.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };

    class NoteHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvBody;
        private TextView tvDateTime;
        private TextView tvTodoDone;
        private TextView tvTodoTotal;
        private ImageView imgReminder;
        private ImageView imgTodo;
        private LinearLayout todoLayout;
        private LinearLayout reminderLayout;
        private TextView reminderTime;
        private ImageView imageNote;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvDateTime = itemView.findViewById(R.id.note);
            tvTodoDone = itemView.findViewById(R.id.tvTodoDone);
            tvTodoTotal = itemView.findViewById(R.id.tvTodoTotal);
            imgReminder = itemView.findViewById(R.id.imgReminder);
            imgTodo = itemView.findViewById(R.id.imgTodo);
            todoLayout = itemView.findViewById(R.id.todoLayout);
            reminderLayout = itemView.findViewById(R.id.reminderLayout);
            reminderTime = itemView.findViewById(R.id.reminderTime);
            imageNote = itemView.findViewById(R.id.imageNote);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(notes.get(position));
                    }
                }
            });
        }
    }

    private Bitmap getImageFromPath(String path) {
        Bitmap myBitmap = null;
        File imgFile = new File(path);

        Size size = new Size(100, 100);
        if (imgFile.exists()) {
            if (Build.VERSION.SDK_INT >= 29) {
                try {
                    myBitmap = ThumbnailUtils.createImageThumbnail(imgFile, size, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                myBitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path), 100, 100);
            }
        }
        return myBitmap;
    }

    public interface NoteItemClickListener {
        void onItemClick(Note note);
    }

    public void setOnItemClickListener(NoteItemClickListener listener) {
        this.listener = listener;
    }
}
