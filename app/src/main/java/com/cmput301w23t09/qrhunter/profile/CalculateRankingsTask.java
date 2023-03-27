package com.cmput301w23t09.qrhunter.profile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class CalculateRankingsTask extends AsyncTask<Void, Void, String> {

    private ProfileController controller;
    private Context context;
    private AlertDialog dialog;
    private ProgressDialog progressDialog;

    public CalculateRankingsTask(ProfileController controller, Context context) {
        this.controller = controller;
        this.context = context;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Calculating rankings...");
    }

    @Override
    protected void onPreExecute() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("Your Rankings");
//        builder.setMessage("Calculating rankings...");
//        builder.setCancelable(false); // Disable cancel button to prevent user from closing dialog before calculation completes
//        dialog = builder.create();
//        dialog.show();
          progressDialog.show();

    }

    @Override
    protected String doInBackground(Void... voids) {
        controller.calculateRankOfHighestQRScore();
        return controller.getFormattedQRPercentile();
    }

    @Override
    protected void onPostExecute(String rankingsMessage) {
//        if (dialog != null && dialog.isShowing()) {
//            dialog.dismiss(); // Close the dialog if it's still showing
//        }
        progressDialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Your Rankings");
        builder.setMessage(rankingsMessage);
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}