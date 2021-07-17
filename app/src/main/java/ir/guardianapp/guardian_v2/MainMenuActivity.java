package ir.guardianapp.guardian_v2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;

import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;

public class MainMenuActivity extends AppCompatActivity {

    public static Fragment CURRENT_FRAGMENT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, HomeFragment.class, null)
                    .commit();
        }

        BubbleNavigationLinearView bubbleNavigationLinearView = findViewById(R.id.bottom_navigation_view_linear);
        bubbleNavigationLinearView.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                //navigation changed, do something
                switch (position) {
                    case 0:
                        if(!(CURRENT_FRAGMENT instanceof TripsFragment)) {
                            removeAllFragments();
                            CURRENT_FRAGMENT = new TripsFragment();
                            loadFragment(CURRENT_FRAGMENT);
                        }
                        break;
                    case 1:
                        if(!(CURRENT_FRAGMENT instanceof DrivingFragment)) {
                            removeAllFragments();
                            CURRENT_FRAGMENT = new DrivingFragment();
                            loadFragment(CURRENT_FRAGMENT);
                        }
                        break;
//                    case 2:
//                        if(!(CURRENT_FRAGMENT instanceof HomeFragment)) {
//                            removeAllFragments();
//                            CURRENT_FRAGMENT = new HomeFragment();
//                            loadFragment(CURRENT_FRAGMENT);
//                        }
//                        break;
                    case 3:
                        if(!(CURRENT_FRAGMENT instanceof SettingsFragment)) {
                            removeAllFragments();
                            CURRENT_FRAGMENT = new SettingsFragment();
                            loadFragment(CURRENT_FRAGMENT);
                        }
                        break;
                    case 4:
                        if(!(CURRENT_FRAGMENT instanceof ProfileFragment)) {
                            removeAllFragments();
                            CURRENT_FRAGMENT = new ProfileFragment();
                            loadFragment(CURRENT_FRAGMENT);
                        }
                        break;
                    default:
                        if(!(CURRENT_FRAGMENT instanceof HomeFragment)) {
                            removeAllFragments();
                            CURRENT_FRAGMENT = new HomeFragment();
                            loadFragment(CURRENT_FRAGMENT);
                        }
                        break;
                }
            }
        });
    }

    private void removeAllFragments() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_view, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}