package com.egrobots.shagarah.presentation.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.egrobots.shagarah.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectedFruitsAdapter extends RecyclerView.Adapter<SelectedFruitsAdapter.SelectedFruitViewHolder> {

    private List<String> selectedFruits = new ArrayList<>();

    @NonNull
    @Override
    public SelectedFruitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.selected_fruit_item_layout, parent, false);
        return new SelectedFruitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedFruitViewHolder holder, int position) {
        String selectedFruit = selectedFruits.get(position);
        holder.selectedFruitTextView.setText(selectedFruit);
        holder.cancelButton.setOnClickListener(v -> {
            selectedFruits.remove(selectedFruit);
            notifyItemRemoved(position);
        });
    }

    @Override
    public int getItemCount() {
        return selectedFruits.size();
    }

    public void addItem(String fruit) {
        selectedFruits.add(fruit);
        notifyDataSetChanged();
    }

    public List<String> getSelectedFruits() {
        return selectedFruits;
    }

    static class SelectedFruitViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.selected_fruit_text_view)
        TextView selectedFruitTextView;
        @BindView(R.id.cancel_button)
        ImageButton cancelButton;

        public SelectedFruitViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
