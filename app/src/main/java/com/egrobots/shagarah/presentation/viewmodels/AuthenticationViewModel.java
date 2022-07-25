package com.egrobots.shagarah.presentation.viewmodels;

import com.egrobots.shagarah.data.DatabaseRepository;
import com.egrobots.shagarah.data.models.CurrentUser;

import javax.inject.Inject;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AuthenticationViewModel extends ViewModel {

    private DatabaseRepository databaseRepository;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MediatorLiveData<CurrentUser> userState = new MediatorLiveData<>();
    private MediatorLiveData<String> errorState = new MediatorLiveData<>();

    @Inject
    public AuthenticationViewModel(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    public void signIn(String email, String password) {
        SingleObserver<CurrentUser> singleObserver = databaseRepository.signIn(email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SingleObserver<CurrentUser>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(CurrentUser user) {
                        userState.setValue(user);
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorState.setValue(e.getMessage());
                    }
                });
    }

    public void signInAnonymously() {
        SingleObserver<CurrentUser> singleObserver = databaseRepository.signInAnonymously()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SingleObserver<CurrentUser>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(CurrentUser user) {
                        userState.setValue(user);
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorState.setValue(e.getMessage());
                    }
                });
    }

    public void signUp(String username, String email, String password) {
        SingleObserver<CurrentUser> singleObserver = databaseRepository.signUp(username, email, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SingleObserver<CurrentUser>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(CurrentUser currentUser) {
                        userState.setValue(currentUser);
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorState.setValue(e.getMessage());
                    }
                });
    }

    public void getCurrentUser(String userId) {
        SingleObserver<CurrentUser> singleObserver = databaseRepository.getCurrentUser(userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SingleObserver<CurrentUser>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(CurrentUser currentUser) {
                        userState.setValue(currentUser);
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorState.setValue(e.getMessage());
                    }
                });
    }

    public MediatorLiveData<CurrentUser> observeUserState() {
        return userState;
    }

    public MediatorLiveData<String> observeErrorState() {
        return errorState;
    }

}
