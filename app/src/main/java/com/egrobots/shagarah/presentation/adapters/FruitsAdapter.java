package com.egrobots.shagarah.presentation.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.egrobots.shagarah.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FruitsAdapter extends RecyclerView.Adapter<FruitsAdapter.FruitViewHolder> implements Filterable {

    private List<String> fruits;
    private List<String> fruitsFull;
    private FruitsAdapterCallback fruitsAdapterCallback;

    public FruitsAdapter(List<String> fruits, FruitsAdapterCallback fruitsAdapterCallback) {
        this.fruits = fruits;
        this.fruitsAdapterCallback = fruitsAdapterCallback;
        fruitsFull = new ArrayList<>(fruits);
    }

    @NonNull
    @Override
    public FruitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fruit_item_layout, parent, false);
        return new FruitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FruitViewHolder holder, int position) {
        String fruit = fruits.get(position);
        holder.fruitNameTextView.setText(fruit);
        holder.itemView.setOnClickListener(v -> {
            fruitsAdapterCallback.onFruitItemClicked(fruit);
        });
    }

    @Override
    public int getItemCount() {
        return fruits.size();
    }

    @Override
    public Filter getFilter() {
        return fruitsFilter;
    }

    private Filter fruitsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<String> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(fruitsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (String item : fruitsFull) {
                    if (item.toLowerCase().contains(filterPattern)) {
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
            fruits.clear();
            fruits.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    static class FruitViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.fruit_img_view)
        ImageView fruitImageView;
        @BindView(R.id.fruit_name_text_view)
        TextView fruitNameTextView;

        public FruitViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface FruitsAdapterCallback {
        void onFruitItemClicked(String selectedFruit);
    }
}
