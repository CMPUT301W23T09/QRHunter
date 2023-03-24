package com.cmput301w23t09.qrhunter.qrcode;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.cmput301w23t09.qrhunter.R;
import com.cmput301w23t09.qrhunter.map.LocationHandler;
import com.cmput301w23t09.qrhunter.player.Player;
import com.cmput301w23t09.qrhunter.scanqr.LocationPhotoController;
import com.cmput301w23t09.qrhunter.scanqr.LocationPhotoFragment;
import com.cmput301w23t09.qrhunter.scanqr.camera.CameraLocationPhotoController;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.concurrent.ExecutionException;

/**
 * Displays information about a specific QRCode. It also lets the user:
 *
 * <ul>
 *   <li>Add scanned QR code to profile
 *   <li>Record geolocation of scanned QR code
 *   <li>Take location photo of scanned qr code
 * </ul>
 */
public class AddQRCodeFragment extends QRCodeFragment {
    private ImageView locationPhoto;
    private QRCode qrCode;
    private Button takeLocationPhotoBtn;
    private CheckBox locationCheckbox;
    private LocationHandler locationHandler;
    private LocationPhotoFragment locationPhotoFragment;
    private Player activePlayer;
    private FloatingActionButton addButton;
    private FloatingActionButton deleteButton;
    private QRCodeDatabase qrCodeDatabase;

    /**
     * Creates a new QRCodeFragment with addition capabilities for a specific QR Code
     *
     * @param qrCode The QR code to view
     * @param activePlayer A player that has scanned the QR code
     * @return QRCodeFragment
     */
    public static AddQRCodeFragment newInstance(QRCode qrCode, Player activePlayer) {
        return (AddQRCodeFragment) QRCodeFragment.newInstance(qrCode, activePlayer);
    }

    @NonNull @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_qrcode, null);
        qrCode = (QRCode) getArguments().getSerializable("qrcode");
        activePlayer = (Player) getArguments().getSerializable("player");
        locationHandler = new LocationHandler(this);
        qrCodeDatabase = QRCodeDatabase.getInstance();
        try {
            setupViews(view);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        updateLocationPhoto();
        return createAlertDialog(view);
    }

    /**
     * Binds the UI components with the attributes of the QRCode
     *
     * @param view The view that displays fragment_qrcode.xml
     */
    @Override
    protected void setupViews(View view) throws ExecutionException, InterruptedException {
        // disable unused elements
        deleteButton = view.findViewById(R.id.deleteButton);
        deleteButton.setVisibility(View.GONE);

        // show qr code information
        setUpQRCodeView(view);

        // set up location photo-taker and location checkbox
        locationPhoto = view.findViewById(R.id.location_photo);
        locationCheckbox = view.findViewById(R.id.location_request_box);

        takeLocationPhotoBtn = view.findViewById(R.id.take_location_photo_btn);
        takeLocationPhotoBtn.setOnClickListener(
                v -> {
                    if (qrCode.getPhotos().size() > 0) {
                        qrCode.deletePhoto(qrCode.getPhotos().get(0));
                        updateLocationPhoto();
                    } else {
                        locationPhotoFragment = LocationPhotoFragment.newInstance(qrCode, this, activePlayer);
                        locationPhotoFragment.show(getParentFragmentManager(), "Take Location Photo");
                    }
                });
        locationCheckbox.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if (isChecked) {
                        locationHandler.setQrToLastLocation(qrCode);
                    } else {
                        qrCode.setLoc(null);
                    }
                });

        // implement the add button
        addButton = view.findViewById(R.id.addButton);
        updateAddButton();
        addButton.setOnClickListener(
                v -> {
                    qrCodeDatabase.addQRCode(qrCode);
                    qrCodeDatabase.addPlayerToQR(activePlayer, qrCode);
                    addButton.setVisibility(View.GONE);
                });
    }

    /**
     * Updates the locationPhoto image view to show the newly-captured location photo
     *
     * @see CameraLocationPhotoController
     * @see LocationPhotoController
     */
    public void updateLocationPhoto() {
        if (qrCode.getPhotos() != null && qrCode.getPhotos().size() > 0) {
            takeLocationPhotoBtn.setText(R.string.remove_location_photo);
            locationPhoto.setImageBitmap(qrCode.getPhotos().get(0).getPhoto());
        } else {
            takeLocationPhotoBtn.setText(R.string.take_location_photo);
            locationPhoto.setImageResource(android.R.color.transparent);
        }
    }

    /**
     * Disables the "Record QR Location" box if the user has not granted location permissions
     *
     * @param requestCode The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions which is either {@link
     *     android.content.pm.PackageManager#PERMISSION_GRANTED} or {@link
     *     android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LocationHandler.REQUEST_CODE_PERMISSIONS) {
            if (!locationHandler.locationPermissionsGranted()) {
                locationCheckbox.setEnabled(false);
            }
        }
    }

    /**
     * Display the add (+) QRCode button only if the player does not have the QRCode to their name
     */
    private void updateAddButton() {
        qrCodeDatabase.playerHasQRCode(
                activePlayer,
                qrCode,
                results -> {
                    if (results.isSuccessful()) {
                        if (results.getData()) {
                            // QR code hash is already added to the player's account
                            addButton.setVisibility(View.GONE);
                        } else {
                            // QR code hash is not yet added to the player's account
                            addButton.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.w("QRCodeFragment", "Error getting player by device ID.", results.getException());
                        Toast.makeText(getContext(), "Error getting player by device ID.", Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }

    /**
     * @return The location photo fragment
     */
    public LocationPhotoFragment getLocationPhotoFragment() {
        return locationPhotoFragment;
    }
}
