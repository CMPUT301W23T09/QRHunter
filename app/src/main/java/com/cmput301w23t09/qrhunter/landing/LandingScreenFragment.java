package com.cmput301w23t09.qrhunter.landing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cmput301w23t09.qrhunter.BaseFragment;
import com.cmput301w23t09.qrhunter.MainController;
import com.cmput301w23t09.qrhunter.R;
import com.google.android.material.textfield.TextInputEditText;

public class LandingScreenFragment extends BaseFragment {

    private LandingScreenController controller;

    private TextInputEditText usernameInput;
    private TextInputEditText phoneInput;
    private TextInputEditText emailInput;

    public LandingScreenFragment(MainController mainController) {
        super(mainController);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        controller = new LandingScreenController(this);
        getMainController().setNavbarEnabled(false);

        View view = inflater.inflate(R.layout.fragment_landing, container, false);

        usernameInput = view.findViewById(R.id.landing_screen_usernameTextField);
        phoneInput = view.findViewById(R.id.landing_screen_phoneNoTextField);
        emailInput = view.findViewById(R.id.landing_screen_emailTextField);
        view.findViewById(R.id.landing_screen_register_button).setOnClickListener(this::onRegistrationClick);

        return view;
    }

    @Override
    public void onDestroyView() {
        getMainController().setNavbarEnabled(true);
        super.onDestroyView();
    }

    public void displayErrorMessage(String message) {
        Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void onRegistrationClick(View view) {
        String username = usernameInput.getText().toString();
        String phoneNo = phoneInput.getText().toString();
        String email = emailInput.getText().toString();

        controller.onRegistration(username, phoneNo, email);
    }

}
