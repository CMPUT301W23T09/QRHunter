package com.cmput301w23t09.qrhunter.qrcode;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cmput301w23t09.qrhunter.R;
import java.util.ArrayList;

/** This is an array adapter for QRCode objects */
public class QRCodeAdapter extends ArrayAdapter<QRCode> {
  /** This is the list of QRCode objects the adapter converts into views */
  private ArrayList<QRCode> qrCodes;
  /** This is the context of the adapter */
  private Context context;

  /**
   * This initializes the adapter with a context and list of QRCode objects
   *
   * @param context This is the context of the adapter
   * @param qrCodes This is the list of QRCode objects th
   */
  public QRCodeAdapter(Context context, ArrayList<QRCode> qrCodes) {
    super(context, 0, qrCodes);
    this.qrCodes = qrCodes;
    this.context = context;
  }

  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    // get view of QR code
    View view = convertView;

    if (view == null) {
      view = LayoutInflater.from(context).inflate(R.layout.qrcode_view, parent, false);
    }

    // get QRCode data
    QRCode qrCode = qrCodes.get(position);

    // set visual representation of qr code
    ImageView visual = view.findViewById(R.id.visual_representation);
    try {
      visual.setImageBitmap(qrCode.getVisualRepresentation());
    } catch (Exception exception) {
      Log.e("QRCodeAdapter", "An exception occurred while fetching the QR image", exception);
      Toast.makeText(
              context, "An exception occurred while fetching the QR image...", Toast.LENGTH_SHORT)
          .show();
    }

    // set score field of view
    TextView score = view.findViewById(R.id.score);
    score.setText(String.valueOf(qrCode.getScore()) + " PTS");

    return view;
  }
}
