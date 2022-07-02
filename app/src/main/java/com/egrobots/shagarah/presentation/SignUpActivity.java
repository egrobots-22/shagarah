package com.egrobots.shagarah.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.egrobots.shagarah.R;
import com.egrobots.shagarah.presentation.helpers.ViewModelProviderFactory;
import com.egrobots.shagarah.presentation.viewmodels.AuthenticationViewModel;
import com.egrobots.shagarah.utils.Constants;

import javax.inject.Inject;

import androidx.lifecycle.ViewModelProvider;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.DaggerAppCompatActivity;

public class SignUpActivity extends DaggerAppCompatActivity {

    private AuthenticationViewModel authenticationViewModel;
    @Inject
    ViewModelProviderFactory providerFactory;
    @BindView(R.id.user_name_edit_text)
    EditText userNameEditText;
    @BindView(R.id.email_edit_text)
    EditText emailEditText;
    @BindView(R.id.password_edit_text)
    EditText passwordEditText;
    @BindView(R.id.sign_up_button)
    Button signUpButton;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        authenticationViewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(AuthenticationViewModel.class);
        observeSignUp();
        observeError();
    }

    private void observeSignUp() {
        authenticationViewModel.observeUserState().observe(this, user -> {
            if (user != null) {
                progressBar.setVisibility(View.GONE);
                signUpButton.setVisibility(View.VISIBLE);
                Intent intent = new Intent(SignUpActivity.this, RequestsActivity.class);
                intent.putExtra(Constants.USER_ID, user.getId());
                startActivity(intent);
            }
        });
    }

    private void observeError() {
        authenticationViewModel.observeErrorState().observe(this, error -> {
            Toast.makeText(SignUpActivity.this, error, Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            signUpButton.setVisibility(View.VISIBLE);
        });
    }

    @OnClick(R.id.sign_up_button)
    public void onSignUpButtonClicked() {
        String username = userNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.email_password_required_message, Toast.LENGTH_SHORT).show();
        } else {
            authenticationViewModel.signUp(username, email, password);
            progressBar.setVisibility(View.VISIBLE);
            signUpButton.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.sign_in_text_view)
    public void onSignInClicked() {
        startActivity(new Intent(this, SignInActivity.class));
    }
}