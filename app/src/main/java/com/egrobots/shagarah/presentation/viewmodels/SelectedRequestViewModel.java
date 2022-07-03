package com.egrobots.shagarah.presentation.viewmodels;

import com.egrobots.shagarah.data.DatabaseRepository;
import com.egrobots.shagarah.data.models.CurrentUser;
import com.egrobots.shagarah.data.models.QuestionAnalysis;
import com.egrobots.shagarah.data.models.Request;

import javax.inject.Inject;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.Observer;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SelectedRequestViewModel extends ViewModel {

    private DatabaseRepository databaseRepository;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MediatorLiveData<Request> requestLiveData = new MediatorLiveData<>();
    private MediatorLiveData<Boolean> answerLiveData = new MediatorLiveData<>();
    private MediatorLiveData<String> errorState = new MediatorLiveData<>();

    @Inject
    public SelectedRequestViewModel(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    public void getRequest(String requestId, String userId) {
        SingleObserver<Request> singleObserver = databaseRepository.getRequest(requestId, userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SingleObserver<Request>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Request request) {
                        requestLiveData.setValue(request);
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorState.setValue(e.getMessage());
                    }
                });
    }

    public void addAnalysisAnswersToQuestion(String requestId, String requestUserId, QuestionAnalysis questionAnalysis) {
        SingleObserver<Boolean> singleObserver
                = databaseRepository.addAnalysisAnswersToQuestion(requestId, requestUserId, questionAnalysis)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SingleObserver<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Boolean success) {
                        answerLiveData.setValue(success);
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorState.setValue(e.getMessage());
                    }
                });
    }

    public MediatorLiveData<Request> observeRequest() {
        return requestLiveData;
    }

    public MediatorLiveData<String> observeError() {
        return errorState;
    }

    public MediatorLiveData<Boolean> observeAnalysisAnswer() {
        return answerLiveData;
    }
}
