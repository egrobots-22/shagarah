package com.egrobots.shagarah.di.requests;

import com.egrobots.shagarah.di.ViewModelKey;
import com.egrobots.shagarah.presentation.viewmodels.NewRequestViewModel;

import androidx.lifecycle.ViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class NewRequestViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(NewRequestViewModel.class)
    public abstract ViewModel bindViewModel(NewRequestViewModel viewModel);
}
