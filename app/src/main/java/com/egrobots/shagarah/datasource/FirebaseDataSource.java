package com.egrobots.shagarah.datasource;

import android.net.Uri;
import android.text.TextUtils;

import com.egrobots.shagarah.data.models.CurrentUser;
import com.egrobots.shagarah.data.models.Image;
import com.egrobots.shagarah.data.models.QuestionAnalysis;
import com.egrobots.shagarah.data.models.Request;
import com.egrobots.shagarah.data.models.RequestSurveyQuestion;
import com.egrobots.shagarah.data.models.TreeType;
import com.egrobots.shagarah.utils.Constants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.StreamSupport;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;

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

    public Single<CurrentUser> signInAnonymously() {
        return Single.create(emitter -> {
            firebaseAuth.signInAnonymously()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            //get device token
                            FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
                                if (!TextUtils.isEmpty(token)) {
                                    String userId = task.getResult().getUser().getUid();
                                    CurrentUser user = new CurrentUser();
                                    user.setUsername("Anonymous");
                                    user.setEmail("Anonymous");
                                    user.setId(userId);
                                    user.setToken(token);
                                    DatabaseReference usersRef = firebaseDatabase.getReference(Constants.USERS_NODE);
                                    usersRef.child(userId).setValue(user).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            emitter.onSuccess(user);
                                            //save user locally on device

                                        } else {
                                            emitter.onError(task1.getException());
                                        }
                                    });
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
                                CurrentUser user = new CurrentUser(username, password, email, token);
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

    public Single<Boolean> addAnalysisAnswersToQuestion(String requestId, QuestionAnalysis questionAnalysis) {
        return Single.create(emitter -> {
            DatabaseReference answerRef = firebaseDatabase.getReference(Constants.REQUESTS_NODE)
                    .child(requestId);

            HashMap<String, Object> updates = new HashMap<>();
            updates.put(Constants.ANALYSIS_ANSWER, questionAnalysis);
            updates.put(Constants.STATUS, Request.RequestStatus.ANSWERED.value);
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

    public Single<Integer> isDataExist(String userId) {
        return Single.create(emitter -> {
            Query requestsRef;
            if (userId == null) {
                requestsRef = firebaseDatabase.getReference(Constants.REQUESTS_NODE);
            } else {
                requestsRef = firebaseDatabase.getReference(Constants.REQUESTS_NODE).orderByChild("userId").equalTo(userId);
            }
            //check if there are current requests
            requestsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChildren()) {
                        int size = Long.valueOf(StreamSupport.stream(snapshot.getChildren().spliterator(), false).count()).intValue();
                        emitter.onSuccess(size);
                    } else {
                        emitter.onSuccess(0);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });
    }

    public Flowable<Request> getRequests(String userId) {
        return Flowable.create(emitter -> {
            Query requestsRef;
            if (userId == null) {
                requestsRef = firebaseDatabase.getReference(Constants.REQUESTS_NODE);
            } else {
                requestsRef = firebaseDatabase.getReference(Constants.REQUESTS_NODE).orderByChild("userId").equalTo(userId);
            }

            requestsRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Request request = snapshot.getValue(Request.class);
                    request.setId(snapshot.getKey());
                    request.setUserId(userId);
                    emitter.onNext(request);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Request request = snapshot.getValue(Request.class);
                    request.setId(snapshot.getKey());
                    request.setUserId(userId);
                    request.setFlag(Constants.UPDATED);
//                    emitter.onNext(request);
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }, BackpressureStrategy.BUFFER);
    }

    public Single<Request> getRequest(String requestId) {
        return Single.create(emitter -> {
            firebaseDatabase.getReference(Constants.REQUESTS_NODE)
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

    public Single<String> uploadImageToFirebaseStorage(Uri imageUri) {
        return Single.create(emitter -> {
            StorageReference reference = storageReference.child(Constants.REQUESTS_REF + System.currentTimeMillis() + Constants.IMAGE_FILE_TYPE);
            UploadTask uploadFileTask = reference.putFile(imageUri);
            uploadFileTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    emitter.onError(task.getException());
                } else {
                    // Continue with the fileTask to get the download URL
                    reference.getDownloadUrl().addOnCompleteListener(task1 -> {
                        String downloadUrl = task1.getResult().toString();
                        emitter.onSuccess(downloadUrl);
                    });
                }
                return reference.getDownloadUrl();
            });
        });
    }

    private void saveRequestToFirebaseDatabase(String userId
            , String token
            , List<Image> uploadedImagesUris
            , String audioUrl
            , String problemDesc
            , String type
            , String cat
            , List<RequestSurveyQuestion> surveyQuestions
            , SingleEmitter<Boolean> emitter) {
        DatabaseReference requestsRef = firebaseDatabase.getReference(Constants.REQUESTS_NODE);
        Request request = new Request();
        request.setUserId(userId);
        request.setToken(token);
        request.setTimestamp(System.currentTimeMillis());
        request.setTimestampToOrder(-System.currentTimeMillis());
        request.setImages(uploadedImagesUris);
        request.setStatus(Request.RequestStatus.IN_PROGRESS.value);
        request.setAudioQuestion(audioUrl);
        request.setTextQuestion(problemDesc);
        request.setType(type);
        request.setCat(cat);
        request.setSurveyQuestions(surveyQuestions);

        requestsRef.push()
                .setValue(request)
                .addOnCompleteListener(task -> {
                    emitter.onSuccess(task.isSuccessful());
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        emitter.onError(e);
                    }
                });
    }

    public Single<Boolean> addNewRequest(String userId, String token,
                                         List<Image> uploadedImagesUris,
                                         File audioRecordedFile,
                                         String problemDesc,
                                         String type,
                                         String cat,
                                         List<RequestSurveyQuestion> surveyQuestions) {
        return Single.create(emitter -> {
            if (audioRecordedFile != null) {
                //upload audio firstly
                Uri audioFile = Uri.fromFile(audioRecordedFile);
                StorageReference audioReference = storageReference.child(Constants.REQUESTS_REF)
                        .child(Constants.AUDIO_ANSWERS + System.currentTimeMillis() + Constants.AUDIO_FILE_TYPE);

                audioReference.putFile(audioFile).addOnSuccessListener(success -> {
                    Task<Uri> audioUrl = success.getStorage().getDownloadUrl();
                    audioUrl.addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String downloadUrl = task.getResult().toString();
                            saveRequestToFirebaseDatabase(userId, token, uploadedImagesUris, downloadUrl, problemDesc, type, cat, surveyQuestions, emitter);
                        }
                    });
                });
            } else {
                saveRequestToFirebaseDatabase(userId, token, uploadedImagesUris, null, problemDesc, type, cat, surveyQuestions, emitter);
            }
        });
    }

    public Flowable<List<TreeType>> getTreeTypes() {
        return Flowable.create(emitter -> {
            DatabaseReference treeTypesRef = firebaseDatabase.getReference(Constants.ANALYSIS_NODE).child(Constants.TREE_TYPES_NODE);
            treeTypesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    List<TreeType> treeTypeList = new ArrayList<>();
                    for (DataSnapshot typeSnapshot : snapshot.getChildren()) {
                        TreeType type = typeSnapshot.getValue(TreeType.class);
                        treeTypeList.add(type);
                    }
                    emitter.onNext(treeTypeList);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    emitter.onError(error.toException());
                }
            });
        }, BackpressureStrategy.BUFFER);
    }

    public void setRequestRating(String requestId, float rating) {
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("rating", rating);
        firebaseDatabase.getReference(Constants.REQUESTS_NODE)
                .child(requestId)
                .child("questionAnalysis")
                .updateChildren(updates);
    }
}
