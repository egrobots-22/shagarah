package com.egrobots.shagarah.di.analysis;

import com.egrobots.shagarah.di.ViewModelKey;
import com.egrobots.shagarah.presentation.viewmodels.AnalysisViewModel;
import com.egrobots.shagarah.presentation.viewmodels.RequestsViewModel;

import androidx.lifecycle.ViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class AnalysisViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AnalysisViewModel.class)
    public abstract ViewModel bindViewModel(AnalysisViewModel viewModel);
}
