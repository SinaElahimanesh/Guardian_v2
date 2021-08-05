package ir.guardianapp.guardian_v2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

import ir.guardianapp.guardian_v2.map.search.ListViewAdapter;
import ir.guardianapp.guardian_v2.map.search.SearchPlaces;
import ir.guardianapp.guardian_v2.network.MapThreadGenerator;
import ir.guardianapp.guardian_v2.network.MessageResult;

public class SearchPlacesActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    ListView list;
    ListViewAdapter adapter;
    SearchView searchEditText;
    String[] placesList;
    ArrayList<SearchPlaces> arraylist = new ArrayList<>();

    private static final ArrayList<SearchPlaces> placesArray = new ArrayList<>();

    public static void setPlacesArray(ArrayList<SearchPlaces> placesArray) {
        SearchPlacesActivity.placesArray.clear();
        SearchPlacesActivity.placesArray.addAll(placesArray);
    }

    private Handler handler;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_places);

        progressBar = findViewById(R.id.progressBar);

        // Locate the ListView in listview_main.xml
        list = findViewById(R.id.listview);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchPlaces item = adapter.getItem(position);
                Intent intent = new Intent(SearchPlacesActivity.this, MainMenuActivity.class);

                HomeFragment.searchLatitude = item.getLatitude();
                HomeFragment.searchLongitude = item.getLongitude();
                HomeFragment.searchTitle = text;
                startActivity(intent);
            }
        });

        // Pass results to ListViewAdapter Class
        adapter = new ListViewAdapter(this, placesArray);

        // Binds the Adapter to the ListView
        list.setAdapter(adapter);

        // Locate the EditText in listview_main.xml
        searchEditText = findViewById(R.id.search);
        searchEditText.setOnQueryTextListener(this);

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MessageResult.SUCCESSFUL) {
                    ArrayList<String> placesArr = (ArrayList<String>) msg.obj;
                } else {
                    Toast.makeText(getBaseContext(), "Error: make sure your connection is stable", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    Thread requestThread;
    String text = "Unknown";

    @Override
    public boolean onQueryTextChange(String newText) {
        text = newText.trim();
        if (text.length() > 0) {
            progressBar.setVisibility(View.VISIBLE);
            if (requestThread != null)
                requestThread.interrupt();
            requestThread = MapThreadGenerator.getPlaces(this, newText, adapter, handler);
            requestThread.start();
        }
        return false;
    }
}