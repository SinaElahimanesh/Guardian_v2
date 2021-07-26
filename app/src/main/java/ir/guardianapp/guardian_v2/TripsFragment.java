package ir.guardianapp.guardian_v2;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    static final int DEFAULT_THREAD_POOL_SIZE = 5;
    static ExecutorService executorService;
    private Handler handler;
    private ProgressBar progressBar;
    private TextView label;
    private int numberOfTrips = 5;
    private static boolean firstTime = true;

    public TripsFragment(DataBaseHelper dataBaseHelper) {
        // Required empty public constructor
        this.dataBaseHelper = dataBaseHelper;
    }

//    public static TripsFragment newInstance(String param1, String param2) {
//        TripsFragment fragment = new TripsFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

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

        executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);

        if(firstTime) {
            getTrips();
            firstTime = false;
        }

        Button refreshButton = view.findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(v -> {
            if(Trip.getTrips().size() == (numberOfTrips - 5)) {
                getTrips();
            }
        });

        return view;
    }

    private void getTrips() {

        executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MessageResult.SUCCESSFUL) {
                    progressBar.setVisibility(View.INVISIBLE);
                    progressBar.setProgress(progressBar.getProgress());
                    tripsAdapter.setTrips(Trip.getTrips());
                    tripsAdapter.setTripsFull(Trip.getTrips());
                    numberOfTrips += 5;
                    tripsAdapter.notifyDataSetChanged();
                    if(Trip.getTrips().size() != 0){
                        label.setVisibility(View.INVISIBLE);
                        label.setEnabled(false);
                    }

                } else if(msg.what == MessageResult.FAILED) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "لطفا از حساب کاربری خود خارج شوید و دوباره ورود کنید.", Toast.LENGTH_LONG).show();

                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getContext(), "لطفا دوباره تلاش کنید.", Toast.LENGTH_LONG).show();
                }
            }
        };
        if (Network.isNetworkAvailable(getActivity())) {   // connected to internet
            progressBar.setVisibility(View.VISIBLE);
            User user = User.getInstance();
            executorService.submit(ThreadGenerator.getRecentTrips(user.getUsername(), user.getToken(), numberOfTrips, handler));
        } else {
            Toast.makeText(getContext(), "اتصال شما به اینترنت برقرار نمی باشد.", Toast.LENGTH_LONG).show();
        }
    }
}