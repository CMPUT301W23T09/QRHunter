package com.cmput301w23t09.qrhunter.database;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import java.util.concurrent.Executor;

public class MockDocumentTask extends Task<DocumentReference> {

  private DocumentReference reference;

  @NonNull @Override
  public Task<DocumentReference> addOnCompleteListener(
      @NonNull OnCompleteListener<DocumentReference> onCompleteListener) {
    onCompleteListener.onComplete(this);
    return this;
  }

  @NonNull @Override
  public Task<DocumentReference> addOnFailureListener(
      @NonNull OnFailureListener onFailureListener) {
    return this;
  }

  @NonNull @Override
  public Task<DocumentReference> addOnFailureListener(
      @NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
    return this;
  }

  @NonNull @Override
  public Task<DocumentReference> addOnFailureListener(
      @NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
    return this;
  }

  @NonNull @Override
  public Task<DocumentReference> addOnSuccessListener(
      @NonNull OnSuccessListener<? super DocumentReference> onSuccessListener) {
    return this;
  }

  @NonNull @Override
  public Task<DocumentReference> addOnSuccessListener(
      @NonNull Activity activity,
      @NonNull OnSuccessListener<? super DocumentReference> onSuccessListener) {
    return this;
  }

  @NonNull @Override
  public Task<DocumentReference> addOnSuccessListener(
      @NonNull Executor executor,
      @NonNull OnSuccessListener<? super DocumentReference> onSuccessListener) {
    return this;
  }

  @Nullable @Override
  public Exception getException() {
    return null;
  }

  @Override
  public DocumentReference getResult() {
    return reference;
  }

  public void setReference(DocumentReference reference) {
    this.reference = reference;
  }

  @Override
  public <X extends Throwable> DocumentReference getResult(@NonNull Class<X> aClass) throws X {
    return null;
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
}
