package ir.guardianapp.guardian_v2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.gauravk.bubblenavigation.BubbleNavigationLinearView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;

import java.util.Date;
import java.util.concurrent.Executors;

import ir.guardianapp.guardian_v2.database.DataBaseHelper;
import ir.guardianapp.guardian_v2.extras.Network;
import ir.guardianapp.guardian_v2.models.ThisTripData;
import ir.guardianapp.guardian_v2.models.Trip;
import ir.guardianapp.guardian_v2.models.User;
import ir.guardianapp.guardian_v2.network.ThreadGenerator;

public class MainMenuActivity extends AppCompatActivity {

    public static Fragment CURRENT_FRAGMENT;

    public DataBaseHelper dbHelper;
    protected SQLiteDatabase db;

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

        // MAKE IT FULL SCREEN
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.appThemeColor));
        }

//        dbHelper.getAllTrips();
    }

    @Override
    protected void onResume() {
        super.onResume();

        newDB();
        HomeFragment homeFragment = new HomeFragment();

        BubbleNavigationLinearView bubbleNavigationLinearView = findViewById(R.id.bottom_navigation_view_linear);
        bubbleNavigationLinearView.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                //navigation changed, do something
                switch (position) {
                    case 0:
                        if(!(CURRENT_FRAGMENT instanceof TripsFragment)) {
                            removeAllFragments();
                            CURRENT_FRAGMENT = new TripsFragment(dbHelper);
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
                            CURRENT_FRAGMENT = homeFragment;
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

    static final int DEFAULT_THREAD_POOL_SIZE = 10;
    private void newDB() {
        dbHelper = new DataBaseHelper(this, Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE));
        db = dbHelper.db;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Handler handler2 = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {

            }
        };
        MainActivity.executorService.submit(ThreadGenerator.postATripInformation(User.getInstance().getUsername(), User.getInstance().getToken(),
                "tehran", 1, 1, "qom", 2, 2, 3,
                (new Date()).toString(), (new Date()).toString(), 4, 5, handler2));

        ThisTripData thisTripData = ThisTripData.getInstance();
//        dbHelper.addTrip(new Trip(thisTripData.getSourceName(), thisTripData.getDestName(),
//                thisTripData.getStartDate(), thisTripData.getEndDate(), thisTripData.getDistance(), thisTripData.getAverage()));

//        if (Network.isNetworkAvailable(this)) {   // connected to internet
//            if(thisTripData.getEnable()) {
//                MainActivity.executorService.submit(ThreadGenerator.postATripInformation(thisTripData.getUsername(), thisTripData.getToken(),
//                        thisTripData.getSourceName(), thisTripData.getSourceLongitude(), thisTripData.getSourceLatitude(),
//                        thisTripData.getDestName(), thisTripData.getDestLongitude(), thisTripData.getDestLatitude(),
//                        thisTripData.getDuration(), thisTripData.getStartDate(), thisTripData.getEndDate(),
//                        thisTripData.getAverage(), thisTripData.getDistance(), handler2));
//            }
//        } else {
//            Toast.makeText(this, "اتصال شما به اینترنت برقرار نمی باشد.", Toast.LENGTH_SHORT).show();
//        }
    }
}