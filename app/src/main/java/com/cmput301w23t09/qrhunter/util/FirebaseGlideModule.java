package com.cmput301w23t09.qrhunter.util;

import android.content.Context;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;
import java.io.InputStream;

/**
 * Registers Firebase for use with Glide
 *
 * <p>From:
 * https://github.com/firebase/snippets-android/blob/cb15737fe61389d2b58c65ae171cf83c26119cb3/storage/app/src/main/java/com/google/firebase/referencecode/storage/MyAppGlideModule.java
 * By: Sam Stern (https://github.com/samtstern) License: Apache 2.0
 */
@GlideModule
public class FirebaseGlideModule extends AppGlideModule {

  @Override
  public void registerComponents(Context context, Glide glide, Registry registry) {
    // Register FirebaseImageLoader to handle StorageReference
    registry.append(StorageReference.class, InputStream.class, new FirebaseImageLoader.Factory());
  }
}
