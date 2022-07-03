package com.egrobots.shagarah.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.egrobots.shagarah.R;
import com.egrobots.shagarah.data.models.Request;
import com.egrobots.shagarah.presentation.adapters.RequestsAdapter;
import com.egrobots.shagarah.presentation.helpers.ViewModelProviderFactory;
import com.egrobots.shagarah.presentation.viewmodels.AuthenticationViewModel;
import com.egrobots.shagarah.presentation.viewmodels.RequestsViewModel;
import com.egrobots.shagarah.utils.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;

public class RequestsActivity extends DaggerAppCompatActivity implements RequestsAdapter.OnRequestClickedCallback {

    private List<Request> requestList = new ArrayList<>();

    @BindView(R.id.requests_recycler_view)
    RecyclerView requestsRecyclerView;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.add_request_fab)
    FloatingActionButton addRequestFab;
    @Inject
    ViewModelProviderFactory providerFactory;
    private RequestsViewModel requestsViewModel;
    private AuthenticationViewModel authenticationViewModel;
    private boolean isAdmin;
    private String userId;
    private RequestsAdapter requestsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);
        ButterKnife.bind(this);
        setTitle(getString(R.string.current_requests));
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        requestsAdapter = new RequestsAdapter(this);
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(RequestsActivity.this));
        requestsRecyclerView.setAdapter(requestsAdapter);

        authenticationViewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(AuthenticationViewModel.class);
        requestsViewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(RequestsViewModel.class);

        getCurrentUser();
        observeRequests();
        observeError();
    }

    private void getCurrentUser() {
        authenticationViewModel.getCurrentUser(userId);
        authenticationViewModel.observeUserState().observe(this, user -> {
            isAdmin = user.getRole() != null;
            if (isAdmin) {
                addRequestFab.setVisibility(View.GONE);
                requestsViewModel.getRequests(null);
            } else {
                addRequestFab.setVisibility(View.VISIBLE);
                requestsViewModel.getRequests(userId);
            }
        });
    }

    private void observeRequests() {
        requestsViewModel.observeRequests().observe(this, request -> {
            if (request != null) {
                requestList.add(request);
            } else {
                requestsAdapter.setItems(requestList);
                requestsAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void observeError() {
        requestsViewModel.observeError().observe(this, error -> {
            Toast.makeText(RequestsActivity.this, error, Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    public void onRequestClicked(Request request) {
        Intent intent;
        if (request.getQuestionAnalysis() != null) {
            intent = new Intent(this, AnsweredRequestViewActivity.class);
        } else {
            intent = new Intent(this, NotAnsweredRequestViewActivity.class);
        }
        intent.putExtra(Constants.REQUEST_ID, request.getId());
        intent.putExtra(Constants.REQUEST_USER_ID, request.getUserId());
        intent.putExtra(Constants.IS_ADMIN, isAdmin);
        startActivity(intent);
    }

    @OnClick(R.id.add_request_fab)
    public void onAddRequestClicked() {
        startActivity(new Intent(this, NewRequestActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.signout_action) {
            FirebaseAuth.getInstance().signOut();
            finish();
            startActivity(new Intent(this, SignInActivity.class));
        }
        return true;
    }

}