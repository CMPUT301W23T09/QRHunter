package com.cmput301w23t09.qrhunter;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    private GridView qrCodeList;
    private QRCodeAdapter qrCodeAdapter;
    private ArrayList<QRCode> qrCodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        implementSpinners();
        implementQRCodeList();
    }

    private void implementSpinners() {
        // get spinners
        Spinner sortSpinner = findViewById(R.id.sort_spinner);
        Spinner orderSpinner = findViewById(R.id.order_spinner);

        // set array adapter for spinners
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this, R.array.sort_options, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> orderAdapter = ArrayAdapter.createFromResource(this, R.array.order_options, android.R.layout.simple_spinner_item);

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

    private void implementQRCodeList() {
        // get QR code list view
        qrCodeList = findViewById(R.id.code_list);

        // get data
        qrCodes = new ArrayList<>();
        qrCodes.add(new QRCode("10bx", "b", null, 10, null, null, null, null));
        qrCodes.add(new QRCode("000x", "a", null, 12, null, null, null, null));

        // set QR code list view adapter
        qrCodeAdapter = new QRCodeAdapter(this, qrCodes);
        qrCodeList.setAdapter(qrCodeAdapter);
    }
}
