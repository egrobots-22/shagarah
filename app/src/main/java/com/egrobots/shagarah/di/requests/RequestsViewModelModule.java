package com.egrobots.shagarah.di.requests;

import com.egrobots.shagarah.di.ViewModelKey;
import com.egrobots.shagarah.presentation.viewmodels.RequestsViewModel;

import androidx.lifecycle.ViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class RequestsViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(RequestsViewModel.class)
    public abstract ViewModel bindViewModel(RequestsViewModel viewModel);
}
