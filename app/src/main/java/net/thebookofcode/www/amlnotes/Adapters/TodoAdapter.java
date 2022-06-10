package net.thebookofcode.www.amlnotes.Adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.thebookofcode.www.amlnotes.R;

import java.util.ArrayList;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoHolder> {
    private ArrayList<String> todosArray = new ArrayList<>();
    private ArrayList<Integer> boolArray = new ArrayList<>();
    private int doneCount;

    @NonNull
    @Override
    public TodoAdapter.TodoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item, parent, false);
        return new TodoHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoAdapter.TodoHolder holder, int position) {
        holder.tvTodo.setText(todosArray.get(position));
        int status = boolArray.get(position);
        if (status == 0) {
            holder.check.setBackgroundResource(R.drawable.ic_undone);
            boolArray.set(position, 0);
        } else if (status == 1) {
            holder.check.setBackgroundResource(R.drawable.ic_done);
            boolArray.set(position, 1);
        } else {
            //Snackbar.make(holder.check,"An Error Occured",Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return todosArray.size();
    }

    public String getDoneString() {
        String doneString = "";
        for (int num : boolArray) {
            doneString = doneString + num + "\n";
        }
        return doneString;
    }

    public String getTodoString() {
        String todoString = "";
        try {
            for (String todo : todosArray) {
                todoString = todoString + todo + "\n";

            }
            return todoString;
        }catch (NullPointerException e){
            return "";
        }



    }

    class TodoHolder extends RecyclerView.ViewHolder {
        private TextView tvTodo;
        private ImageView check;
        private ImageView delete;

        public TodoHolder(@NonNull View itemView) {
            super(itemView);
            tvTodo = itemView.findViewById(R.id.tvTodo);
            check = itemView.findViewById(R.id.check);
            delete = itemView.findViewById(R.id.delete);

            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (boolArray.get(position) == 0) {
                        boolArray.set(position, 1);
                        check.setBackgroundResource(R.drawable.ic_done);
                    } else {
                        boolArray.set(position, 0);
                        check.setBackgroundResource(R.drawable.ic_undone);
                    }
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    boolArray.remove(position);
                    todosArray.remove(position);
                    notifyDataSetChanged();
                }
            });

            tvTodo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().isEmpty()) {
                        delete.setVisibility(View.GONE);
                    } else {
                        delete.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    public void setTodosArray(ArrayList<String> todosArray) {
        this.todosArray = todosArray;
        notifyDataSetChanged();
    }

    public void setDoneArray(ArrayList<Integer> boolArray) {
        this.boolArray = boolArray;
        notifyDataSetChanged();
    }

    public void setTodoBool(int done) {
        boolArray.add(done);
        notifyDataSetChanged();
    }

    public void setTodoString(String todoString) {
        todosArray.add(todoString);
        notifyDataSetChanged();
    }

    public int numDone() {
        for (int i = 0; i < boolArray.size(); i++) {
            if (boolArray.get(i) == 1) {
                doneCount++;
            }
        }
        return doneCount;
    }
}
