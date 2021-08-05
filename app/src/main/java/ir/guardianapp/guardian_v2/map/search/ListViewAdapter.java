package ir.guardianapp.guardian_v2.map.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ir.guardianapp.guardian_v2.R;

public class ListViewAdapter extends BaseAdapter {

    // Declare Variables

    Context mContext;
    LayoutInflater inflater;
    private List<SearchPlaces> placesList = null;
    private ArrayList<SearchPlaces> arraylist;

    public ListViewAdapter(Context context, List<SearchPlaces> placesList) {
        mContext = context;
        this.placesList = placesList;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<>();
        this.arraylist.addAll(placesList);
    }

    public void setArraylist(ArrayList<SearchPlaces> arraylist) {
        this.arraylist.clear();
        this.arraylist.addAll(arraylist);

        placesList.clear();
        placesList.addAll(arraylist);
    }

    public class ViewHolder {
        TextView name;
        TextView city;
        TextView address;
    }

    @Override
    public int getCount() {
        return placesList.size();
    }

    @Override
    public SearchPlaces getItem(int position) {
        return placesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.item_search_row, null);
            // Locate the TextViews in listview_item.xml
            holder.name = view.findViewById(R.id.neighbourhood);
            holder.city = view.findViewById(R.id.city);
            holder.address = view.findViewById(R.id.display_name);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        // Set the results into TextViews
        holder.name.setText(placesList.get(position).getSearchPlaceName());
        holder.city.setText(placesList.get(position).getCity());
        holder.address.setText(placesList.get(position).getAddress());
        return view;
    }

    public void filter() {
        notifyDataSetChanged();
    }
}

