package com.egrobots.shagarah.presentation.viewmodels;

import com.egrobots.shagarah.data.DatabaseRepository;
import com.egrobots.shagarah.data.models.Request;
import com.egrobots.shagarah.data.models.TreeType;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AnalysisViewModel extends ViewModel {

    private DatabaseRepository databaseRepository;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MediatorLiveData<List<TreeType>> treeTypesLiveData = new MediatorLiveData<>();
    private MediatorLiveData<String> errorState = new MediatorLiveData<>();

    @Inject
    public AnalysisViewModel(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    public void getTreeTypes() {
        databaseRepository.getTreeTypes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
                .subscribe(new Observer<List<TreeType>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(List<TreeType> treeTypeList) {
                        treeTypesLiveData.setValue(treeTypeList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorState.setValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public MediatorLiveData<List<TreeType>> observeTreeTypes() {
        return treeTypesLiveData;
    }

    public MediatorLiveData<String> observeError() {
        return errorState;
    }
}
