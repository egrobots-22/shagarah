package com.egrobots.shagarah.data;

import android.net.Uri;

import com.egrobots.shagarah.data.models.CurrentUser;
import com.egrobots.shagarah.data.models.Image;
import com.egrobots.shagarah.data.models.QuestionAnalysis;
import com.egrobots.shagarah.data.models.Request;
import com.egrobots.shagarah.datasource.FirebaseDataSource;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Single;

public class DatabaseRepository {

    private FirebaseDataSource firebaseDataSource;

    @Inject
    public DatabaseRepository(FirebaseDataSource firebaseDataSource) {
        this.firebaseDataSource = firebaseDataSource;
    }

    public Single<CurrentUser> signIn(String email, String password) {
        return firebaseDataSource.signIn(email, password);
    }

    public Single<CurrentUser> signUp(String username, String email, String password) {
        return firebaseDataSource.signUp(username, email, password);
    }

    public Single<Boolean> addAnalysisAnswersToQuestion(String requestId, String requestUserId, QuestionAnalysis questionAnalysis) {
        return firebaseDataSource.addAnalysisAnswersToQuestion(requestId, requestUserId, questionAnalysis);
    }

    public Single<CurrentUser> getCurrentUser(String userId) {
        return firebaseDataSource.getCurrentUser(userId);
    }

    public Flowable<Request> getRequests(String userId) {
        return firebaseDataSource.getRequests(userId);
    }

    public Single<Request> getRequest(String requestId, String userId) {
        return firebaseDataSource.getRequest(requestId, userId);
    }

    public Single<Boolean> addNewRequest(String userId, List<Image> uploadedImagesUris, File audioRecordedFile, String questionText) {
        return firebaseDataSource.addNewRequest(userId, uploadedImagesUris, audioRecordedFile, questionText);
    }

    public Single<String> uploadImageToFirebaseStorage(Uri imageUri) {
        return firebaseDataSource.uploadImageToFirebaseStorage(imageUri);
    }
}