package com.egrobots.shagarah.datasource;

import android.text.TextUtils;

import com.egrobots.shagarah.data.models.CurrentUser;
import com.egrobots.shagarah.data.models.QuestionAnalysis;
import com.egrobots.shagarah.data.models.Request;
import com.egrobots.shagarah.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class FirebaseDataSource {

    private static final String TAG = "FirebaseDataSource";

    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private FirebaseMessaging firebaseMessaging;

    @Inject
    public FirebaseDataSource(StorageReference storageReference
            , FirebaseDatabase firebaseDatabase
            , FirebaseAuth firebaseAuth
            , FirebaseMessaging firebaseMessaging) {
        this.storageReference = storageReference;
        this.firebaseDatabase = firebaseDatabase;
        this.firebaseAuth = firebaseAuth;
        this.firebaseMessaging = firebaseMessaging;
    }

    public Single<CurrentUser> signIn(String email, String password) {
        return Single.create(emitter -> {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String userId = task.getResult().getUser().getUid();
                            firebaseDatabase.getReference(Constants.USERS_NODE)
                                    .child(userId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            CurrentUser user = snapshot.getValue(CurrentUser.class);
                                            user.setId(userId);
                                            emitter.onSuccess(user);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            emitter.onError(error.toException());
                                        }
                                    });
                        } else {
                            emitter.onError(task.getException());
                        }
                    });
        });
    }

    public Single<CurrentUser> signUp(String username, String email, String password) {
        return Single.create(emitter -> firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = task.getResult().getUser().getUid();
                        //get device token
                        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
                            if (!TextUtils.isEmpty(token)) {
                                DatabaseReference usersRef = firebaseDatabase.getReference(Constants.USERS_NODE);
                                CurrentUser user = new CurrentUser(username, password, email);
                                usersRef.child(userId).setValue(user).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        emitter.onSuccess(user);
                                    } else {
                                        emitter.onError(task1.getException());
                                    }
                                });
                            }
                        });
                    } else {
                        emitter.onError(task.getException());
                    }
                }));
    }

    public Single<Boolean> addAnalysisAnswersToQuestion(String requestId, String requestUserId, QuestionAnalysis questionAnalysis) {
        return Single.create(emitter -> {
            DatabaseReference answerRef = firebaseDatabase.getReference(Constants.REQUESTS_NODE)
                    .child(requestUserId)
                    .child(requestId);

            HashMap<String, Object> updates = new HashMap<>();
            updates.put(Constants.ANALYSIS_ANSWER, questionAnalysis);
            updates.put(Constants.STATUS, Request.RequestStatus.ANSWERED);
            answerRef.updateChildren(updates).addOnCompleteListener(task -> {
                emitter.onSuccess(task.isSuccessful());
            });
        });
    }

    public Single<CurrentUser> getCurrentUser(String userId) {
        return Single.create(emitter -> {
            firebaseDatabase.getReference(Constants.USERS_NODE)
                    .child(userId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            CurrentUser user = snapshot.getValue(CurrentUser.class);
                            user.setId(userId);
                            emitter.onSuccess(user);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            emitter.onError(error.toException());
                        }
                    });

        });
    }

    public Flowable<Request> getRequests(String userId) {
        return Flowable.create(emitter -> {
            DatabaseReference requestsRef;
            if (userId == null) {
                requestsRef = firebaseDatabase.getReference(Constants.REQUESTS_NODE);
            } else  {
                requestsRef = firebaseDatabase.getReference(Constants.REQUESTS_NODE).child(userId);
            }

            requestsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (userId == null) {
                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                            for (DataSnapshot requestSnapshot : userSnapshot.getChildren()) {
                                Request request = requestSnapshot.getValue(Request.class);
                                request.setId(requestSnapshot.getKey());
                                request.setUserId(userSnapshot.getKey());
                                emitter.onNext(request);
                            }
                        }
                    } else {
                        for (DataSnapshot requestSnapshot : snapshot.getChildren()) {
                            Request request = requestSnapshot.getValue(Request.class);
                            request.setId(requestSnapshot.getKey());
                            request.setUserId(snapshot.getKey());
                            emitter.onNext(request);
                        }
                    }
                    emitter.onComplete();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    emitter.onError(error.toException());
                }
            });
        }, BackpressureStrategy.BUFFER);
    }

    public Single<Request> getRequest(String requestId, String userId) {
        return Single.create(emitter -> {
            firebaseDatabase.getReference(Constants.REQUESTS_NODE)
                    .child(userId)
                    .child(requestId)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Request request = snapshot.getValue(Request.class);
                            request.setId(snapshot.getKey());
                            emitter.onSuccess(request);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            emitter.onError(error.toException());
                        }
                    });

        });

    }

}
