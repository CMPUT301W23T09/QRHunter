package com.cmput301w23t09.qrhunter.locationphoto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.smarteist.autoimageslider.SliderViewAdapter;
import java.util.ArrayList;
import java.util.List;

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
  private List<StorageReference> locationPhotosRefs;

  public LocationPhotoAdapter(Context ctx, QRCode qrCode) {
    this.ctx = ctx;
    this.qrCode = qrCode;
    renewLocationPhotos();
  }

  public void renewLocationPhotos() {
    locationPhotosRefs = new ArrayList<>();
    StorageReference qrCodeLocPhotos =
        FirebaseStorage.getInstance().getReference().child(qrCode.getHash() + "/");
    qrCodeLocPhotos
        .listAll()
        .addOnSuccessListener(
            new OnSuccessListener<ListResult>() {
              @Override
              public void onSuccess(ListResult listResult) {
                for (StorageReference photo : listResult.getItems()) locationPhotosRefs.add(photo);
                notifyDataSetChanged();
              }
            });
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
        .into(viewHolder.locationPhotoView);
  }

  @Override
  public int getCount() {
    return locationPhotosRefs.size();
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
