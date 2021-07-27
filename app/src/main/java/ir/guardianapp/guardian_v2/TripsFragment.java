package ir.guardianapp.guardian_v2;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import ir.guardianapp.guardian_v2.database.DataBaseHelper;
import ir.guardianapp.guardian_v2.extras.Network;
import ir.guardianapp.guardian_v2.models.Trip;
import ir.guardianapp.guardian_v2.models.User;
import ir.guardianapp.guardian_v2.network.MessageResult;
import ir.guardianapp.guardian_v2.network.ThreadGenerator;
import ir.guardianapp.guardian_v2.trips.RecyclerViewClickListener;
import ir.guardianapp.guardian_v2.trips.TripsAdapter;

public class TripsFragment extends Fragment {

    private RecyclerView tripsRecyclerView;
    private TripsAdapter tripsAdapter;

    private DataBaseHelper dataBaseHelper;

    private Handler handler;
    private ProgressBar progressBar;
    private TextView label;
    private int numberOfTrips = 5;
    private static boolean isFirstTime = true;
    private static boolean canUpdate = true;
    private static int requestLimit = 20;

    public TripsFragment(DataBaseHelper dataBaseHelper) {
        // Required empty public constructor
        this.dataBaseHelper = dataBaseHelper;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trips, container, false);

        progressBar = view.findViewById(R.id.progressBar);
        tripsRecyclerView = view.findViewById(R.id.trips_recyclerview);
        label = view.findViewById(R.id.no_trips_label);
        if(Trip.getTrips().size() != 0){
            label.setVisibility(View.INVISIBLE);
            label.setEnabled(false);
        }

        tripsAdapter = new TripsAdapter(Trip.getTrips(), new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                //
            }
        }, getContext());
        tripsAdapter.notifyDataSetChanged();
        tripsRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        tripsRecyclerView.setAdapter(tripsAdapter);

        if(isFirstTime) {
            getTrips();
            isFirstTime = false;
        }

        Button refreshButton = view.findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(v -> {
            if(Trip.getTrips().size() == (numberOfTrips - 5)) {
                getTrips();
            } else {
                Toast.makeText(getContext(), "سفر جدیدی برای نمایش وجود ندارد.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void getTrips() {

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MessageResult.SUCCESSFUL) {
                    progressBar.setVisibility(View.INVISIBLE);
                    canUpdate = true;
                    progressBar.setProgress(progressBar.getProgress());
                    tripsAdapter.setTrips(Trip.getTrips());
                    tripsAdapter.setTripsFull(Trip.getTrips());
                    numberOfTrips += 5;
                    tripsAdapter.notifyDataSetChanged();
                    if(Trip.getTrips().size() != 0){
                        label.setVisibility(View.INVISIBLE);
                        label.setEnabled(false);
                    }

                } else if(msg.what == MessageResult.AUTHENTICATION_FAILED) {
                    progressBar.setVisibility(View.INVISIBLE);
                    canUpdate = true;
                    Toast.makeText(getContext(), "لطفا از حساب کاربری خود خارج شوید و دوباره ورود کنید.", Toast.LENGTH_SHORT).show();

                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    canUpdate = true;
                    Toast.makeText(getContext(), "لطفا دوباره تلاش کنید.", Toast.LENGTH_SHORT).show();
                }
            }
        };
        if (Network.isNetworkAvailable(getActivity())) {   // connected to internet
            if(canUpdate && requestLimit!=0) {
                canUpdate = false;
                requestLimit--;
                progressBar.setVisibility(View.VISIBLE);
                User user = User.getInstance();
                MainActivity.executorService.submit(ThreadGenerator.getRecentTrips(user.getUsername(), user.getToken(), numberOfTrips, handler));
            } else if(requestLimit == 0) {
                Toast.makeText(getContext(), "لطفا بعدا تلاش کنید!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "اتصال شما به اینترنت برقرار نمی باشد.", Toast.LENGTH_SHORT).show();
        }
    }
}