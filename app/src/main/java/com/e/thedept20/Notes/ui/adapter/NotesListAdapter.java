package com.e.thedept20.Notes.ui.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.e.thedept20.Notes.model.Note;
import com.e.thedept20.Notes.util.AppUtils;
import com.e.thedept20.Notes.util.NoteDiffUtil;
import com.e.thedept20.R;

import java.util.List;

public class NotesListAdapter extends RecyclerView.Adapter<NotesListAdapter.CustomViewHolder> {

    private List<Note> notes;
    public NotesListAdapter(List<Note> notes) {
        this.notes = notes;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list_item, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Note note = getItem(position);

        holder.itemTitle.setText(note.getTitle());
        holder.itemTime.setText(AppUtils.getFormattedDateString(note.getCreatedAt()));

        if(note.isEncrypt()) {
            holder.itemTime.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_lock, 0);

        } else {
            holder.itemTime.setCompoundDrawablesWithIntrinsicBounds(0,0, 0, 0);
        }
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public Note getItem(int position) {
        return notes.get(position);
    }

    public void addTasks(List<Note> newNotes) {
        NoteDiffUtil noteDiffUtil = new NoteDiffUtil(notes, newNotes);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(noteDiffUtil);
        notes.clear();
        notes.addAll(newNotes);
        diffResult.dispatchUpdatesTo(this);
    }

    protected class CustomViewHolder extends RecyclerView.ViewHolder {

        private TextView itemTitle, itemTime;
        public CustomViewHolder(View itemView) {
            super(itemView);

            itemTitle = itemView.findViewById(R.id.item_title);
            itemTime = itemView.findViewById(R.id.item_desc);
        }
    }
}
