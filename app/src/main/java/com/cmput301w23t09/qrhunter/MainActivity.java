package com.cmput301w23t09.qrhunter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.ViewGroup;

import com.cmput301w23t09.qrhunter.navigation.NavigationControllerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * The Main Activity class handles displaying the fragments and navbar onscreen.
 */
public class MainActivity extends AppCompatActivity {

    private MainController controller;

    public MainController getController() {
        return controller;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        controller = new MainController(this);

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigation_bar);
        bottomNavigationView.setOnItemSelectedListener(new NavigationControllerAdapter(controller));
    }

    /**
     * Called when the fragment of the body should be updated.
     * @param fragment fragment to insert into the body.
     */
    void onControllerBodyUpdate(Fragment fragment) {
        FragmentTransaction bodyTransaction = getSupportFragmentManager().beginTransaction();
        bodyTransaction.replace(R.id.main_activity_fragment_body_host, fragment);
        bodyTransaction.commit();
    }

    /**
     * Called when the visibility of the navbar should be updated.
     * @param isEnabled if the navbar should be visible.
     */
    void onControllerNavbarVisibilityUpdate(boolean isEnabled) {
        BottomNavigationView menu = findViewById(R.id.navigation_bar);
        ViewGroup.LayoutParams params = menu.getLayoutParams();

        if (isEnabled) {
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            params.height = 0;
        }

        menu.setLayoutParams(params);
    }

}