package com.egrobots.shagarah.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
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

public class SignInActivity extends DaggerAppCompatActivity {

    private AuthenticationViewModel authenticationViewModel;
    @Inject
    ViewModelProviderFactory providerFactory;
    @BindView(R.id.email_edit_text)
    EditText emailEditText;
    @BindView(R.id.password_edit_text)
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);

        authenticationViewModel = new ViewModelProvider(getViewModelStore(), providerFactory).get(AuthenticationViewModel.class);
        observeSignIn();
        observeError();
    }

    private void observeSignIn() {
        authenticationViewModel.observeUserState().observe(this, user -> {
            if (user != null) {
                Intent intent = new Intent(this, RequestsActivity.class);
                intent.putExtra(Constants.USER_ID, user.getId());
                startActivity(intent);
                finish();
            }
        });
    }

    private void observeError() {
        authenticationViewModel.observeErrorState().observe(this, error -> {
            Toast.makeText(SignInActivity.this, error, Toast.LENGTH_SHORT).show();
        });
    }

    @OnClick(R.id.sign_in_button)
    public void onSingInButtonClicked() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.email_password_required_message, Toast.LENGTH_SHORT).show();
        } else {
            authenticationViewModel.signIn(email, password);
        }
    }

    @OnClick(R.id.sign_up_text_view)
    public void onSignUpNowClicked() {
        startActivity(new Intent(this, SignUpActivity.class));
    }
}