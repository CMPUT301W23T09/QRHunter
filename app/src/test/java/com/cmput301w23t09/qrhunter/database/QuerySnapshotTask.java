package com.cmput301w23t09.qrhunter.database;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.concurrent.Executor;

/** A firebase task that returns a query snapshot. */
public class QuerySnapshotTask extends Task<QuerySnapshot> {
  private QuerySnapshot querySnapshot;

  @NonNull @Override
  public Task<QuerySnapshot> addOnCompleteListener(
      @NonNull OnCompleteListener<QuerySnapshot> onCompleteListener) {
    onCompleteListener.onComplete(this);
    return this;
  }

  @NonNull @Override
  public Task<QuerySnapshot> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
    return this;
  }

  @NonNull @Override
  public Task<QuerySnapshot> addOnFailureListener(
      @NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
    return this;
  }

  @NonNull @Override
  public Task<QuerySnapshot> addOnFailureListener(
      @NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
    return this;
  }

  @NonNull @Override
  public Task<QuerySnapshot> addOnSuccessListener(
      @NonNull OnSuccessListener<? super QuerySnapshot> onSuccessListener) {
    return this;
  }

  @NonNull @Override
  public Task<QuerySnapshot> addOnSuccessListener(
      @NonNull Activity activity,
      @NonNull OnSuccessListener<? super QuerySnapshot> onSuccessListener) {
    return this;
  }

  @NonNull @Override
  public Task<QuerySnapshot> addOnSuccessListener(
      @NonNull Executor executor,
      @NonNull OnSuccessListener<? super QuerySnapshot> onSuccessListener) {
    return this;
  }

  @Nullable @Override
  public Exception getException() {
    return null;
  }

  @Override
  public QuerySnapshot getResult() {
    return querySnapshot;
  }

  @Override
  public boolean isCanceled() {
    return false;
  }

  @Override
  public boolean isComplete() {
    return true;
  }

  @Override
  public boolean isSuccessful() {
    return true;
  }

  @Override
  public QuerySnapshot getResult(@NonNull Class aClass) {
    return querySnapshot;
  }

  public void setResult(QuerySnapshot querySnapshot) {
    this.querySnapshot = querySnapshot;
  }
}
