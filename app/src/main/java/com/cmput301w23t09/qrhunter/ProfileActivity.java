package com.cmput301w23t09.qrhunter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Objects;

public class ProfileActivity extends Fragment {
    private GridView qrCodeList;
    private QRCodeAdapter qrCodeAdapter;
    private ArrayList<QRCode> qrCodes;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_activity, container, false);
        implementSpinners(view);
        implementQRCodeList(view);
        return view;
    }

    private void implementSpinners(View view) {
        // get spinners
        Spinner sortSpinner = view.findViewById(R.id.sort_spinner);
        Spinner orderSpinner = view.findViewById(R.id.order_spinner);

        // set array adapter for spinners
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(getContext(), R.array.sort_options, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> orderAdapter = ArrayAdapter.createFromResource(getContext(), R.array.order_options, android.R.layout.simple_spinner_item);

        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        orderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sortSpinner.setAdapter(sortAdapter);
        orderSpinner.setAdapter(orderAdapter);

        // handle item selection for sort type spinner
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selected = (String) parent.getItemAtPosition(pos);
                if (Objects.equals(selected, "Points")) {

                } else if (Objects.equals(selected, "Date Taken")) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // handle item selection for sort order spinner
        orderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String selected = (String) parent.getItemAtPosition(pos);
                if (Objects.equals(selected, "Descending")) {

                } else if (Objects.equals(selected, "Ascending")) {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void implementQRCodeList(View view) {
        // get QR code list view
        qrCodeList = view.findViewById(R.id.code_list);

        // get data
        qrCodes = new ArrayList<>();
        qrCodes.add(new QRCode("10bx", "b", null, 10, null, null, null, null));
        qrCodes.add(new QRCode("000x", "a", null, 12, null, null, null, null));

        // set QR code list view adapter
        qrCodeAdapter = new QRCodeAdapter(getContext(), qrCodes);
        qrCodeList.setAdapter(qrCodeAdapter);
    }
}
