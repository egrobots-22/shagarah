package com.egrobots.shagarah.presentation.viewmodels;

import android.net.Uri;

import com.egrobots.shagarah.data.DatabaseRepository;
import com.egrobots.shagarah.data.models.Image;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NewRequestViewModel extends ViewModel {

    private DatabaseRepository databaseRepository;
    private CompositeDisposable disposable = new CompositeDisposable();
    private MediatorLiveData<Boolean> addRequestState = new MediatorLiveData<>();
    private MediatorLiveData<String> uploadImageState = new MediatorLiveData<>();
    private MediatorLiveData<String> errorState = new MediatorLiveData<>();

    @Inject
    public NewRequestViewModel(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    public void addNewRequest(String userId, List<Image> uploadedImagesUris, File audioRecordedFile, String questionText) {
        SingleObserver<Boolean> singleObserver
                = databaseRepository.addNewRequest(userId, uploadedImagesUris, audioRecordedFile, questionText)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SingleObserver<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Boolean success) {
                        addRequestState.setValue(success);
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorState.setValue(e.getMessage());
                    }
                });
        ;
    }

    public void uploadImageToFirebaseStorage(Uri imageUri) {
        SingleObserver<String> singleObserver = databaseRepository.uploadImageToFirebaseStorage(imageUri)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new SingleObserver<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(String downloadUrl) {
                        uploadImageState.setValue(downloadUrl);
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorState.setValue(e.getMessage());
                    }
                });
    }

    public MediatorLiveData<Boolean> observeAddingRequest() {
        return addRequestState;
    }

    public MediatorLiveData<String> observeUploadImage() {
        return uploadImageState;
    }

    public MediatorLiveData<String> observeError() {
        return errorState;
    }
}