package com.cmput301w23t09.qrhunter.locationphoto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.google.firebase.storage.StorageReference;
import com.smarteist.autoimageslider.SliderViewAdapter;
import java.util.List;
import java.util.function.Consumer;

/**
 * Displays a QRCode's location photos inside a slider view.
 *
 * <p>Uses and adapts code from Android Image Slider
 * (https://github.com/smarteist/Android-Image-Slider) By: Ali Hosseini
 * (https://github.com/smarteist) License: Apache 2.0
 */
public class LocationPhotoAdapter
    extends SliderViewAdapter<LocationPhotoAdapter.LocationPhotoHolder> {

  private Context ctx;
  private QRCode qrCode;
  private LocationPhotoStorage locationPhotoStorage;
  private List<StorageReference> locationPhotosRefs;

  public LocationPhotoAdapter(Context ctx, QRCode qrCode) {
    this.ctx = ctx;
    this.qrCode = qrCode;
    locationPhotoStorage = LocationPhotoStorage.getInstance();
    renewLocationPhotos(unused -> {});
  }

  /** Fetches all location photos for the QRCode and updates the slider view */

  /**
   * Fetchs all location photos for the QRCode and updates the slider view
   *
   * @param callback A callback function to run after all photos have been fetched
   */
  public void renewLocationPhotos(Consumer<List<StorageReference>> callback) {
    locationPhotoStorage.getLocationPhotos(
        qrCode,
        (refs) -> {
          locationPhotosRefs = refs;
          notifyDataSetChanged();
          callback.accept(refs);
        });
  }

  /**
   * Gets the index of the player's location photo
   *
   * @param player The player we want to find the location photo of
   * @return The index of the player's location photo
   */
  public int getPlayerLocationPhoto(Player player) {
    if (locationPhotosRefs == null) return -1;
    for (int i = 0; i < locationPhotosRefs.size(); i++) {
      if (locationPhotosRefs.get(i).getName().equals(player.getDocumentId() + ".jpg")) return i;
    }
    return -1;
  }

  @Override
  public LocationPhotoHolder onCreateViewHolder(ViewGroup parent) {
    View inflate =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.location_photo_entry_view, null);
    return new LocationPhotoHolder(inflate);
  }

  @Override
  public void onBindViewHolder(LocationPhotoHolder viewHolder, int position) {
    StorageReference locationPhotoRef = locationPhotosRefs.get(position);
    Glide.with(viewHolder.itemView)
        .load(locationPhotoRef)
        .fitCenter()
        // Disable caching so location photo is updated properly
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .skipMemoryCache(true)
        .into(viewHolder.locationPhotoView);
  }

  @Override
  public int getCount() {
    return locationPhotosRefs != null ? locationPhotosRefs.size() : 0;
  }

  class LocationPhotoHolder extends SliderViewAdapter.ViewHolder {
    View itemView;
    ImageView locationPhotoView;

    public LocationPhotoHolder(View itemView) {
      super(itemView);
      locationPhotoView = itemView.findViewById(R.id.location_photo_view);
      this.itemView = itemView;
    }
  }
}
