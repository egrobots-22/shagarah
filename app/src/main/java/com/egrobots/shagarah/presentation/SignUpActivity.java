package com.egrobots.shagarah.presentation;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.egrobots.shagarah.R;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.user_name_edit_text)
    EditText userNameEditText;
    @BindView(R.id.email_edit_text)
    EditText emailEditText;
    @BindView(R.id.password_edit_text)
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.sign_up_button)
    public void onSignUpButtonClicked() {
        startActivity(new Intent(this, RequestsActivity.class));
    }

    @OnClick(R.id.sign_in_text_view)
    public void onSignInClicked() {
        startActivity(new Intent(this, SignInActivity.class));
    }
}