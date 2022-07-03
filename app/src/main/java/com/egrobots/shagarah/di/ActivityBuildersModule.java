package com.egrobots.shagarah.di;


import com.egrobots.shagarah.di.authentication.AuthenticationViewModelModule;
import com.egrobots.shagarah.di.requests.NewRequestViewModelModule;
import com.egrobots.shagarah.di.requests.RequestsViewModelModule;
import com.egrobots.shagarah.di.requests.SelectedRequestViewModelModule;
import com.egrobots.shagarah.presentation.AnsweredRequestViewActivity;
import com.egrobots.shagarah.presentation.NewRequestActivity;
import com.egrobots.shagarah.presentation.NotAnsweredRequestViewActivity;
import com.egrobots.shagarah.presentation.RequestsActivity;
import com.egrobots.shagarah.presentation.SignInActivity;
import com.egrobots.shagarah.presentation.SignUpActivity;
import com.egrobots.shagarah.presentation.SplashActivity;
import com.egrobots.shagarah.presentation.viewmodels.AuthenticationViewModel;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuildersModule {

    @ContributesAndroidInjector
    abstract SplashActivity contributeSplashActivity();

    @ContributesAndroidInjector(modules = AuthenticationViewModelModule.class)
    abstract SignInActivity contributeSignInActivity();

    @ContributesAndroidInjector(modules = AuthenticationViewModelModule.class)
    abstract SignUpActivity contributeSignUpActivity();

    @ContributesAndroidInjector(modules = {RequestsViewModelModule.class, AuthenticationViewModelModule.class})
    abstract RequestsActivity contributeRequestsActivity();

    @ContributesAndroidInjector(modules = SelectedRequestViewModelModule.class)
    abstract NotAnsweredRequestViewActivity contributeNotAnsweredRequestViewActivity();

    @ContributesAndroidInjector(modules = SelectedRequestViewModelModule.class)
    abstract AnsweredRequestViewActivity contributeAnsweredRequestViewActivity();

    @ContributesAndroidInjector(modules = NewRequestViewModelModule.class)
    abstract NewRequestActivity contributeNewRequestViewActivity();
}
