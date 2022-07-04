package com.egrobots.shagarah.presentation.viewmodels;

import com.egrobots.shagarah.data.DatabaseRepository;
import com.egrobots.shagarah.data.models.Image;
import com.egrobots.shagarah.data.models.Request;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RequestsViewModel extends ViewModel {

    private DatabaseRepository databaseRepository;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MediatorLiveData<Integer> isDataExist = new MediatorLiveData<>();
    private MediatorLiveData<Request> requestLiveData = new MediatorLiveData<>();
    private MediatorLiveData<String> errorState = new MediatorLiveData<>();

    @Inject
    public RequestsViewModel(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    public void isDataExist(String userId) {
        SingleObserver<Integer> singleObserver
                = databaseRepository.isDataExist(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Integer size) {
                        isDataExist.setValue(size);
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorState.setValue(e.getMessage());
                    }
                });
        ;
    }

    public void getRequests(String userId) {
        databaseRepository.getRequests(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
                .subscribe(new Observer<Request>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(Request request) {
                        requestLiveData.setValue(request);
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorState.setValue(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        requestLiveData.setValue(null);
                    }
                });
    }

    public MediatorLiveData<Request> observeRequests() {
        return requestLiveData;
    }

    public MediatorLiveData<Integer> observeDataExist() {
        return isDataExist;
    }

    public MediatorLiveData<String> observeError() {
        return errorState;
    }
}
