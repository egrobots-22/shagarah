package com.egrobots.shagarah.presentation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.egrobots.shagarah.R;
import com.egrobots.shagarah.models.Request;
import com.egrobots.shagarah.presentation.adapters.RequestsAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RequestsActivity extends AppCompatActivity implements RequestsAdapter.OnRequestClickedCallback {

    private List<Request> requestList;

    @BindView(R.id.requests_recycler_view)
    RecyclerView requestsRecyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        ButterKnife.bind(this);
        setTitle(getString(R.string.current_requests));

        RequestsAdapter requestsAdapter = new RequestsAdapter(this);
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(RequestsActivity.this));
        requestsRecyclerView.setAdapter(requestsAdapter);

        FirebaseDatabase.getInstance()
                .getReference("requests")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        requestList = new ArrayList<>();
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            for (DataSnapshot requestSnapshot : userSnapshot.getChildren()) {
                                Request request = requestSnapshot.getValue(Request.class);
                                request.setId(requestSnapshot.getKey());
                                request.setUserId(userSnapshot.getKey());
                                requestList.add(request);
                            }
                        }
                        requestsAdapter.setItems(requestList);
                        requestsAdapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public void onRequestClicked(Request request) {
        Intent intent;
        if (request.getStatus().equals("answered")) {
            intent = new Intent(this, AnsweredRequestViewActivity.class);
        } else {
            intent = new Intent(this, NotAnsweredRequestViewActivity.class);
        }
        intent.putExtra("request_id", request.getId());
        intent.putExtra("request_user_id", request.getUserId());
        startActivity(intent);
    }

    @OnClick(R.id.add_request_fab)
    public void onAddRequestClicked() {
        startActivity(new Intent(this, NewRequestActivity.class));
    }

}