package com.cmput301w23t09.qrhunter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class QRCodeAdapter extends ArrayAdapter<QRCode>  {
    private ArrayList<QRCode> qrCodes;
    private Context context;

    public QRCodeAdapter(Context context, ArrayList<QRCode> qrCodes) {
        super(context, 0, qrCodes);
        this.qrCodes = qrCodes;
        this.context = context;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.qrcode_view, parent, false);
        }

        QRCode qrCode = qrCodes.get(position);

        TextView score = view.findViewById(R.id.score);
        score.setText(String.valueOf(qrCode.getScore()));
        return view;
    }
}
