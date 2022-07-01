package com.egrobots.shagarah.presentation.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.egrobots.shagarah.R;
import com.egrobots.shagarah.models.Request;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RequestViewHolder> {

    private List<Request> requestsList = new ArrayList<>();
    private OnRequestClickedCallback onRequestClickedCallback;

    public RequestsAdapter(OnRequestClickedCallback onRequestClickedCallback) {
        this.onRequestClickedCallback = onRequestClickedCallback;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_item_layout, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Request request = requestsList.get(position);
        holder.requestStatusTextView.setText(request.getStatus());
        holder.timestampTextView.setText(request.getTimestamp());
        Glide.with(holder.itemView.getContext())
                .load(request.getImages().get(0).getUrl())
                .placeholder(R.drawable.shagarah_logo)
                .into(holder.requestThumbnailImgView);
        holder.showRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRequestClickedCallback.onRequestClicked(request);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestsList.size();
    }

    public void setItems(List<Request> requestList) {
        this.requestsList = requestList;
    }

    class RequestViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.request_status_value_text_view)
        TextView requestStatusTextView;
        @BindView(R.id.timestamp_value_text_view)
        TextView timestampTextView;
        @BindView(R.id.show_request_button)
        Button showRequestButton;
        @BindView(R.id.request_thumbnail_img_view)
        ImageView requestThumbnailImgView;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnRequestClickedCallback {
        void onRequestClicked(Request request);
    }
}
