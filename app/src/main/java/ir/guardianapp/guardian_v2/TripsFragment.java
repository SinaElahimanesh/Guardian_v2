package ir.guardianapp.guardian_v2;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import ir.guardianapp.guardian_v2.database.DataBaseHelper;
import ir.guardianapp.guardian_v2.models.Trip;
import ir.guardianapp.guardian_v2.trips.RecyclerViewClickListener;
import ir.guardianapp.guardian_v2.trips.TripsAdapter;

public class TripsFragment extends Fragment {

    private RecyclerView tripsRecyclerView;
    private TripsAdapter tripsAdapter;

    private DataBaseHelper dataBaseHelper;

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
        if(Trip.getTrips().size() != 0){
            TextView label = view.findViewById(R.id.no_trips_label);
            label.setVisibility(View.INVISIBLE);
            label.setEnabled(false);

            tripsRecyclerView = view.findViewById(R.id.trips_recyclerview);
            tripsAdapter = new TripsAdapter(Trip.getTrips(), new RecyclerViewClickListener() {
                @Override
                public void onClick(View view, int position) {
                   //
                }
            }, getContext());
            tripsAdapter.notifyDataSetChanged();
            tripsRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
            tripsRecyclerView.setAdapter(tripsAdapter);

        }
        return view;
    }
}