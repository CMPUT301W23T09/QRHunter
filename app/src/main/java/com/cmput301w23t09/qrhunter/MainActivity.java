package com.cmput301w23t09.qrhunter;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

/** The MainActivity handles displaying the landing and initial loading page. */
public class MainActivity extends AppCompatActivity {
  private MainController controller;

  private TextInputEditText usernameInput;
  private TextInputEditText phoneInput;
  private TextInputEditText emailInput;
  private Button registrationButton;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main_loading);

    controller = new MainController(this);
  }

  /** Switch the current view contents to the landing page. */
  public void showLandingPage() {
    setContentView(R.layout.activity_main_landing);

    usernameInput = findViewById(R.id.landing_screen_usernameTextField);
    phoneInput = findViewById(R.id.landing_screen_phoneNoTextField);
    emailInput = findViewById(R.id.landing_screen_emailTextField);
    registrationButton = findViewById(R.id.landing_screen_register_button);
    registrationButton.setOnClickListener(this::onRegistrationClick);
  }

  /**
   * Called when an error related to registration should be displayed to the user.
   *
   * @param message error message
   */
  public void displayRegistrationError(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    registrationButton.setEnabled(true);
  }

  /**
   * Called when the registration button is clicked.
   *
   * @param view
   */
  private void onRegistrationClick(View view) {
    String username = usernameInput.getText().toString();
    String phoneNo = phoneInput.getText().toString().replaceAll("\\s+", ""); // get rid of spaces
    String email = emailInput.getText().toString();

    registrationButton.setEnabled(false);
    controller.onRegistration(username, phoneNo, email);
  }

  /**
   * Called when a toast should be displayed by the main activity.
   *
   * @param message message to display
   */
  public void displayToast(String message) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
  }
}
