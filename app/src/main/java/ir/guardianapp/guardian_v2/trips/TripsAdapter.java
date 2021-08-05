package ir.guardianapp.guardian_v2.trips;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.osmdroid.util.GeoPoint;
import java.util.ArrayList;
import java.util.List;

import ir.guardianapp.guardian_v2.DrivingStatus.time.Month;
import ir.guardianapp.guardian_v2.DrivingStatus.time.PersianCalender;
import ir.guardianapp.guardian_v2.MainActivity;
import ir.guardianapp.guardian_v2.R;
import ir.guardianapp.guardian_v2.models.Trip;

public class TripsAdapter extends RecyclerView.Adapter<TripsAdapter.TripViewHolder> implements Filterable {
    private static final String TAG = "Filter";
    private List<Trip> trips;
    private List<Trip> tripsFull;
    public RecyclerViewClickListener clickListener;
    Context context;

    public TripsAdapter(List<Trip> trips, RecyclerViewClickListener listener, Context context) {
        this.trips = trips;
        tripsFull = new ArrayList<>(trips);
        this.clickListener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip_row,parent,false);
        return new TripViewHolder(view, clickListener);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = trips.get(position);
        holder.sourceName.setText(trip.getSourceName());
        holder.destinationName.setText(trip.getDestinationName());
        holder.startTime.setText(String.valueOf(trip.getStartDate().getHours() + ":" + trip.getStartDate().getMinutes()));
        holder.endTime.setText(String.valueOf(trip.getEndDate().getHours() + ":" + trip.getEndDate().getMinutes()));
        holder.distance.setText(String.valueOf(trip.getDistanceInKM() + " km"));
        holder.average.setText(String.valueOf(trip.getAverage() + "%"));
        holder.date.setText(String.valueOf((new PersianCalender.SolarCalendar(trip.getStartDate()).date) + Month.getPersianMonth((new PersianCalender.SolarCalendar(trip.getStartDate()).month))));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // click
            }
        });
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    @Override
    public Filter getFilter() {
        return tripFilter;
    }

    private Filter tripFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Trip> filteredList = new ArrayList<>();
            if(charSequence == null || charSequence.length() == 0)
                filteredList.addAll(tripsFull);
            else{
                String pattern = charSequence.toString().trim().toLowerCase();
                for (Trip trip : tripsFull) {
                    if(trip.getSourceName().trim().toLowerCase().contains(pattern))
                        filteredList.add(trip);
                    else if(trip.getDestinationName().trim().toLowerCase().contains(pattern))
                        filteredList.add(trip);
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            trips.clear();
            trips.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    class TripViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

//        private Button button;
        private RecyclerViewClickListener mListener;

        private TextView sourceName;
        private TextView destinationName;
        private TextView startTime;
        private TextView endTime;
        private TextView distance;
        private TextView date;
        private TextView average;

        public TripViewHolder(@NonNull View itemView, RecyclerViewClickListener listener) {
            super(itemView);
            mListener = listener;
//            button.setOnClickListener(this);
            sourceName = itemView.findViewById(R.id.source_name);
            destinationName = itemView.findViewById(R.id.destination_name);
            startTime = itemView.findViewById(R.id.start_time);
            endTime = itemView.findViewById(R.id.end_time);
            distance = itemView.findViewById(R.id.distance);
            date = itemView.findViewById(R.id.trip_date);
            average = itemView.findViewById(R.id.average);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    public void setTripsFull(List<Trip> tripsFull) {
        this.tripsFull = tripsFull;
    }
}