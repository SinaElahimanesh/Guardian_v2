package ir.guardianapp.guardian_v2.OSRM;

import com.google.android.gms.maps.model.LatLng;

import java.lang.ref.WeakReference;

public class Line {
    Location beginning = new Location(0, 0);
    Location end = new Location(0, 0);
    WeakReference<Step> step = new WeakReference<>(new Step());

    public LatLng getBeginning() {
        return new LatLng(beginning.getLat(), beginning.getLon());
    }

    public LatLng getEnd() {
        return new LatLng(end.getLat(), end.getLon());
    }
}
