package com.egrobots.shagarah.presentation.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.egrobots.shagarah.R;
import com.egrobots.shagarah.data.models.Planet;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class PlanetsAdapter extends RecyclerView.Adapter<PlanetsAdapter.PlanetViewHolder> implements Filterable {

    private List<Planet> plants;
    private List<Planet> plantsFull;
    private PlantsAdapterCallback plantsAdapterCallback;

    public PlanetsAdapter(List<Planet> plants, PlantsAdapterCallback plantsAdapterCallback) {
        this.plants = plants;
        this.plantsAdapterCallback = plantsAdapterCallback;
        plantsFull = new ArrayList<>(plants);
    }

    @NonNull
    @Override
    public PlanetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.planet_item_layout, parent, false);
        return new PlanetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanetViewHolder holder, int position) {
        Planet planet = plants.get(position);
        holder.planetNameTextView.setText(planet.getName());
        Glide.with(holder.itemView.getContext())
                .load(planet.getImage())
                .placeholder(R.drawable.tree_icon2)
                .into(holder.planetImageView);
        holder.itemView.setOnClickListener(v -> {
            plantsAdapterCallback.onPlanetItemClicked(planet.getName());
        });
    }

    @Override
    public int getItemCount() {
        return plants.size();
    }

    @Override
    public Filter getFilter() {
        return planetsFilter;
    }

    private Filter planetsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Planet> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(plantsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Planet item : plantsFull) {
                    if (item.getName().toLowerCase().contains(filterPattern)) {
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
            plants.clear();
            plants.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    static class PlanetViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.planet_img_view)
        ImageView planetImageView;
        @BindView(R.id.planet_name_text_view)
        TextView planetNameTextView;

        public PlanetViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface PlantsAdapterCallback {
        void onPlanetItemClicked(String selectedPlanet);
    }
}
