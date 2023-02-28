package com.cmput301w23t09.qrhunter;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

import com.cmput301w23t09.qrhunter.navigation.NavigationControllerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * The GameActivity class handles displaying the fragments and navbar onscreen.
 */
public class GameActivity extends AppCompatActivity {

        private GameController controller;

    public GameController getController() {
        return controller;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        controller = new GameController(this);

        // Set navigation controller adapter
        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_bar);
        bottomNavigationView.setOnItemSelectedListener(new NavigationControllerAdapter(controller));
    }

    /**
     * Called when the fragment of the body should be updated.
     * @param fragment fragment to insert into the body.
     */
    void onControllerBodyUpdate(Fragment fragment) {
        FragmentContainerView fragmentContainerView = findViewById(R.id.main_activity_fragment_body_host);
        Fragment currentActiveFragment = fragmentContainerView.getFragment();

        FragmentTransaction bodyTransaction = getSupportFragmentManager().beginTransaction();
        if (fragment != null) {
            bodyTransaction.replace(R.id.main_activity_fragment_body_host, fragment);
        } else {
            bodyTransaction.remove(currentActiveFragment);
        }
        bodyTransaction.commit();
    }

    /**
     * Called when the activity should display a popup.
     * @param dialog popup to display on screen.
     * @throws IllegalArgumentException if setting the dialog to null while no dialog is on screen.
     */
    void onControllerPopupUpdate(DialogFragment dialog) {
        if (dialog != null) {
            dialog.show(getSupportFragmentManager(), "popup");
        } else {
            Fragment existingFragment = getSupportFragmentManager().findFragmentByTag("popup");

            if (!(existingFragment instanceof DialogFragment)) {
                throw new IllegalArgumentException("Attempted to dismiss dialog popup when none exists.");
            }
            ((DialogFragment) existingFragment).dismiss();
        }
    }

}
