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
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
    @BindView(R.id.emptyView)
    View emptyView;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;
    @Inject
    ViewModelProviderFactory providerFactory;
    private RequestsViewModel requestsViewModel;
    private AuthenticationViewModel authenticationViewModel;
    private boolean isAdmin;
    private String userId;
    private RequestsAdapter requestsAdapter;
    private Integer requestsNum = 0;
    private boolean dataNotRetrievedYetFirstTime = true;

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
        observeExistData();
        observeRequests();
        observeError();
    }

    private void getCurrentUser() {
        authenticationViewModel.getCurrentUser(userId);
        authenticationViewModel.observeUserState().observe(this, user -> {
            isAdmin = user.getRole() != null;
            if (isAdmin) {
                addRequestFab.setVisibility(View.GONE);
                requestsViewModel.isDataExist(null);
            } else {
                addRequestFab.setVisibility(View.VISIBLE);
                requestsViewModel.isDataExist(userId);
            }

            refreshLayout.setOnRefreshListener(() -> {
                refreshData();
                refreshLayout.setRefreshing(false);
            });
        });
    }

    private void refreshData() {
        if (isAdmin) {
            requestsViewModel.isDataExist(null);
        } else {
            requestsViewModel.isDataExist(userId);
        }
    }

    private void observeExistData() {
        requestsViewModel.observeDataExist().observe(this, size -> {
            progressBar.setVisibility(View.GONE);
            if (size == 0) {
                emptyView.setVisibility(View.VISIBLE);
                requestsRecyclerView.setVisibility(View.GONE);
            } else {
                requestList = new ArrayList<>();
                requestsNum = size;
                emptyView.setVisibility(View.GONE);
                requestsRecyclerView.setVisibility(View.VISIBLE);
                //get requests
                if (isAdmin) {
                    requestsViewModel.getRequests(null);
                } else {
                    requestsViewModel.getRequests(userId);
                }
            }
        });
    }

    private void observeRequests() {
        requestsViewModel.observeRequests().observe(this, request -> {
            progressBar.setVisibility(View.GONE);
            if (request != null) {
                requestList.add(request);
                if (requestList.size() == requestsNum) {
                    Collections.reverse(requestList);
                    reOrderList();
                    requestsAdapter.setItems(requestList);
                    requestsAdapter.notifyDataSetChanged();
                    dataNotRetrievedYetFirstTime = false;
                }
            }
        });
    }

    private void reOrderList() {
        ArrayList<Request> inProgressItems = new ArrayList<>();
        ArrayList<Request> answeredItems = new ArrayList<>();
        for (Request request : requestList) {
            if (request.getStatus() == Request.RequestStatus.IN_PROGRESS.value) {
                inProgressItems.add(request);
            } else {
                answeredItems.add(request);
            }
        }
        requestList.clear();
        requestList.addAll(inProgressItems);
        requestList.addAll(answeredItems);
    }

    private void observeError() {
        requestsViewModel.observeError().observe(this, error -> {
            Toast.makeText(RequestsActivity.this, error, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!dataNotRetrievedYetFirstTime) {
            refreshData();
        }
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