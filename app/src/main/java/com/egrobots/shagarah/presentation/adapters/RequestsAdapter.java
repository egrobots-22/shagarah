package com.egrobots.shagarah.presentation.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.egrobots.shagarah.R;
import com.egrobots.shagarah.data.models.QuestionAnalysis;
import com.egrobots.shagarah.data.models.Request;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RequestsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int IN_PROGRESS_ITEM_VIEW = 0;
    private final static int DONE_ITEM_VIEW = 1;

    private List<Request> requestsList = new ArrayList<>();
    private OnRequestClickedCallback onRequestClickedCallback;
    private boolean isAdmin;

    public RequestsAdapter(boolean isAdmin, OnRequestClickedCallback onRequestClickedCallback) {
        this.isAdmin = isAdmin;
        this.onRequestClickedCallback = onRequestClickedCallback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == IN_PROGRESS_ITEM_VIEW) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_item_layout, parent, false);
            return new RequestViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_done_item_layout, parent, false);
            return new AnsweredRequestViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RequestViewHolder) {
            Request request = requestsList.get(position);
            ((RequestViewHolder)holder).requestStatusTextView.setText(request.getShownStatus());
            ((RequestViewHolder)holder).timestampTextView.setText(request.getFormattedDate());
            ((RequestViewHolder)holder).showRequestButton.setOnClickListener(v -> onRequestClickedCallback.onRequestClicked(request));
            ((RequestViewHolder)holder).itemView.setOnClickListener(v -> onRequestClickedCallback.onRequestClicked(request));
            Glide.with(holder.itemView.getContext())
                    .load(request.getImages().get(0).getUrl())
                    .placeholder(R.drawable.shagarah_logo)
                    .into(((RequestViewHolder)holder).requestThumbnailImgView);
        } else if (holder instanceof AnsweredRequestViewHolder) {
            Request request = requestsList.get(position);
            QuestionAnalysis questionAnalysis = request.getQuestionAnalysis();
            ((AnsweredRequestViewHolder)holder).treeTypeTextView.setText(questionAnalysis.getTreeType());
            ((AnsweredRequestViewHolder)holder).treeCodeTextView.setText(questionAnalysis.getTreeCode());
            ((AnsweredRequestViewHolder)holder).treeCategoryTextView.setText(questionAnalysis.getTreeCategory());
            ((AnsweredRequestViewHolder)holder).moreDetailsButton.setOnClickListener(v -> onRequestClickedCallback.onRequestClicked(request));
            ((AnsweredRequestViewHolder)holder).itemView.setOnClickListener(v -> onRequestClickedCallback.onRequestClicked(request));
            if (!isAdmin) {
                ((AnsweredRequestViewHolder) holder).ratingLayout.setVisibility(View.VISIBLE);
                ((AnsweredRequestViewHolder) holder).requestAnswerRating.setRating(questionAnalysis.getRating());
                ((AnsweredRequestViewHolder) holder).requestAnswerRating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> onRequestClickedCallback.onAddRating(request.getId(), rating));
            } else {
                ((AnsweredRequestViewHolder) holder).ratingLayout.setVisibility(View.GONE);
            }
            Glide.with(holder.itemView.getContext())
                    .load(request.getImages().get(0).getUrl())
                    .placeholder(R.drawable.shagarah_logo)
                    .into(((AnsweredRequestViewHolder)holder).requestThumbnailImgView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Request request = requestsList.get(position);
        if (request.getQuestionAnalysis() == null) {
            return IN_PROGRESS_ITEM_VIEW;
        } else {
            return DONE_ITEM_VIEW;
        }
    }

    @Override
    public int getItemCount() {
        return requestsList.size();
    }

    public void setItems(List<Request> requestList) {
        this.requestsList = requestList;
    }

    public void addRequest(Request request) {
        requestsList.add(request);
        notifyDataSetChanged();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {

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

    static class AnsweredRequestViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tree_type_value_text_view)
        TextView treeTypeTextView;
        @BindView(R.id.tree_code_value_text_view)
        TextView treeCodeTextView;
        @BindView(R.id.tree_status_value_text_view)
        TextView treeCategoryTextView;
        @BindView(R.id.more_details_button)
        Button moreDetailsButton;
        @BindView(R.id.request_thumbnail_img_view)
        ImageView requestThumbnailImgView;
        @BindView(R.id.request_answer_rating)
        RatingBar requestAnswerRating;
        @BindView(R.id.rating_layout)
        View ratingLayout;

        public AnsweredRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnRequestClickedCallback {
        void onRequestClicked(Request request);

        void onAddRating(String requestId, float rating);
    }
}
