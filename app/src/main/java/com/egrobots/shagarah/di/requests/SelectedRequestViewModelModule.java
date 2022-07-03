package com.egrobots.shagarah.di.requests;

import com.egrobots.shagarah.di.ViewModelKey;
import com.egrobots.shagarah.presentation.viewmodels.RequestsViewModel;
import com.egrobots.shagarah.presentation.viewmodels.SelectedRequestViewModel;

import androidx.lifecycle.ViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class SelectedRequestViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SelectedRequestViewModel.class)
    public abstract ViewModel bindViewModel(SelectedRequestViewModel viewModel);
}
