package com.cmput301w23t09.qrhunter;

import android.util.Log;
import android.widget.Toast;

import com.cmput301w23t09.qrhunter.qrcode.QRCode;
import com.cmput301w23t09.qrhunter.qrcode.ScoreComparator;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class TempRankingClass {

    /**
     * Finds the position of the user's top QR code relative to all QR codes
     * @param queryDocumentSnapshots Documents for all QR codes
     * @param topQR The user's highest scoring QR code
     * @return -1 if the user's top QR code was not found in the collection
     * @return The user's top QR position relative to all the other QR code positions
     */
    public int getTopQRPosition(QuerySnapshot queryDocumentSnapshots, QRCode topQR) {
        int position = 1;

        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
            String qrHash = documentSnapshot.getString("hash");
            int score = documentSnapshot.getLong("score").intValue();

            if (qrHash.equals(topQR.getHash())) {
                return position;
            }

            position++;
        }

        return -1;
    }

    /**
     * Calculates the percentile rank of the user's top QR code by score relative to all QR codes
     */
    private void calculateRankOfHighestQRScore() {
        if (qrcodes.size() < 0) {
            return;
        }

        qrCodes.sort(new ScoreComparator().reversed());
        QRCode topQR = qrCodes.get(0);
        Query query = qrcodeCollection.orderBy("score", Query.Direction.DESCENDING);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int topQRPosition = getTopQRPosition(queryDocumentSnapshots, topQR);
                int totalNumQRCodes = queryDocumentSnapshots.size();

                if (topQRPosition == -1) {
                    return;
                }

                float percentileRank = ((topQRPosition - 1) / totalNumQRCodes) * 100;
                displayHighestQRScoreToast(percentileRank);
            }
        });
    }

    /**
     * Displays the percentile rank of the user's top QR code by score relative to all QR codes
     * @param percentile Percentile value for the user's top QR code
     */
    private void displayHighestQRScoreToast(float percentile) {
        int duration = Toast.LENGTH_SHORT;
        Context context = getApplicationContext();
        String formattedPercentile = String.format("%2f", 100 - percentile);
        String message = "Your highest scoring unique QR code is in the top ${formattedPercentile} in terms of points.";
        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }
}
