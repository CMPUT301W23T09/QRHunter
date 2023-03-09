package com.cmput301w23t09.qrhunter.database;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import java.util.concurrent.Executor;

/** A firebase task that returns no results. */
public class VoidTask extends Task<Void> {

  @NonNull @Override
  public Task<Void> addOnCompleteListener(@NonNull OnCompleteListener<Void> onCompleteListener) {
    onCompleteListener.onComplete(this);
    return this;
  }

  @NonNull @Override
  public Task<Void> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
    return this;
  }

  @NonNull @Override
  public Task<Void> addOnFailureListener(
      @NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
    return this;
  }

  @NonNull @Override
  public Task<Void> addOnFailureListener(
      @NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
    return this;
  }

  @NonNull @Override
  public Task<Void> addOnSuccessListener(
      @NonNull OnSuccessListener<? super Void> onSuccessListener) {
    return this;
  }

  @NonNull @Override
  public Task<Void> addOnSuccessListener(
      @NonNull Activity activity, @NonNull OnSuccessListener<? super Void> onSuccessListener) {
    return this;
  }

  @NonNull @Override
  public Task<Void> addOnSuccessListener(
      @NonNull Executor executor, @NonNull OnSuccessListener<? super Void> onSuccessListener) {
    return this;
  }

  @Nullable @Override
  public Exception getException() {
    return null;
  }

  @Override
  public Void getResult() {
    return null;
  }

  @Override
  public <X extends Throwable> Void getResult(@NonNull Class<X> aClass) throws X {
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
