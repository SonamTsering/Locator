package com.sonam.gasstationlocator.locator;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ShareActionProvider;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by dolmabradach on 4/5/15.
 */



public class MapsActivity extends FragmentActivity implements OnMapLongClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private String[] places = {"Gas_Station", "Bank", "Cafe", "Fire-Station", "", "Pharmacy",
            "Police", "Restaurant", "Shopping Mall"};
    private String[] placesname = {"Gas_Station", "Bank", "Cafe", "Fire-Station", "Pharmacy",
            "Police", "Restaurant", "Shopping Mall"};
    private LocationManager locationManager;
    private Location loc;
    private Marker curlocmarker;
    private double lat;
    private double lng;
    private ShareActionProvider SAP;

    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //map setup
        setUpMapIfNeeded();

        Spinner spinnerPlace = (Spinner) findViewById(R.id.spinner_toolbar);
        ArrayAdapter<String> placeAdapter = new ArrayAdapter<String>(MapsActivity.this,
                android.R.layout.simple_spinner_item, placesname);

        placeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlace.setAdapter(placeAdapter);
        spinnerPlace.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

               //list of array that (places) change
                Log.e(TAG, places[position].toLowerCase().replace("-", "_"));
                if (loc != null) {

                    mMap.clear();
                    new GetPlaces(MapsActivity.this, places[position].toLowerCase().replace("-",
                            "_").replace(" ", "_")).execute();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        mMap.setOnMapLongClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user
     * to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call
     * this
     * method in {@link #onResume()} to guarantee that it will be called.
     */

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment)
                    getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
                currentLocation();
            }
        }
    }


    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        mMap.addMarker(new MarkerOptions().position(new LatLng(44.9778, -93.2650)).title("Minneapolis"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(44.977753, -93.26501080000003)).title("Minneapolis"));


        mMap.addMarker(new MarkerOptions().position(new LatLng(40.7127, -74.0059))

                .title("New York")
                .draggable(true));

//
//                Uri gmmIntentUri = Uri.parse("geo:0,0?z=14&q=gas_station");
//        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//        mapIntent.setPackage("com.google.android.apps.maps");
//        startActivity(mapIntent);


    }

    private class GetPlaces extends AsyncTask<Void, Void, ArrayList<Place>> {
        private ProgressDialog dialog;
        private Context context;
        private String places;

        public GetPlaces(Context context, String places) {
            this.context = context;
            this.places = places;
        }

        @Override
        protected void onPostExecute(ArrayList<Place> result) {
            super.onPostExecute(result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            LatLng currloc = new LatLng(loc.getLatitude(), loc.getLongitude());
            curlocmarker = mMap.addMarker(new MarkerOptions()
                            .position(currloc)
                            .title("My Location")
                   // .icon(BitmapDescriptorFactory.fromResource(R.drawable.mark))

            );
            for (int i = 0; i < result.size(); i++) {
                mMap.addMarker(new MarkerOptions()
                        .title(result.get(i).getName())
                        .position(
                                new LatLng(result.get(i).getLatitude(), result
                                        .get(i).getLongitude())) //geting lat and lng
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.pin))
                        .snippet(result.get(i).getVicinity()));
            }
            if (result.size() == 0) return;
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(result.get(0).getLatitude(), result
                            .get(0).getLongitude())) // Sets the center of the map to
                            // Mountain View
                    .zoom(14) // Sets the zoom
                    .tilt(30) // Sets the tilt of the camera to 30 degrees
                    .build(); // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setMessage("Loading..");
            dialog.isIndeterminate();
            dialog.show();
        }

        @Override
        protected ArrayList<Place> doInBackground(Void... arg0) {
            //use the browser api
            Service service = new Service("AIzaSyDPBxc_EiGJNg4kltUUbJW6bJj3QI-h2qA");
            ArrayList<Place> findPlaces = service.findPlaces(loc.getLatitude(), // 28.632808
                    loc.getLongitude(), places); // 77.218276
            for (int i = 0; i < findPlaces.size(); i++) {
                Place placeDetail = findPlaces.get(i);
                Log.e(TAG, "places : " + placeDetail.getName());
            }
            return findPlaces;

        }
    }

    // not use
    private void initCompo() {
        mMap = ((SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap();
    }

    private void currentLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //show normal map
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //location avaible
        mMap.setMyLocationEnabled(true);


        //zoom in button on the map
        mMap.getUiSettings().setZoomControlsEnabled(true);


        String provider = locationManager
                .getBestProvider(new Criteria(), false);
        Location location = locationManager.getLastKnownLocation(provider);
        if (location == null) {
            locationManager.requestLocationUpdates(provider, 0, 0, listener);
        } else {
            loc = location;
            new GetPlaces(MapsActivity.this, places[0].toLowerCase().replace(
                    "-", "_")).execute();
            Log.e(TAG, "location : " + location);
        }

//
//                Uri gmmIntentUri = Uri.parse("geo:0,0?z=14&q=gas_station");
//        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//        mapIntent.setPackage("com.google.android.apps.maps");
//        startActivity(mapIntent);
    }

    private LocationListener listener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }


        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "location update : " + location);
            loc = location;
            locationManager.removeUpdates(listener);
        }
    };

    public void GasLocate(View v) throws IOException {

        EditText etsearch = (EditText) findViewById(R.id.etsearch);
        String location = etsearch.getText().toString();
        //location api
        Geocoder gc = new Geocoder(this);

        List<Address> list = gc.getFromLocationName(location, 1);
        //adding the address
        Address add = list.get(0);

        //Address add1 = list.get(10);

        String locality = add.getLocality();
//
//        Uri gmmIntentUri = Uri.parse("geo:0,0?z=14&q=gas_station");
//        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//        mapIntent.setPackage("com.google.android.apps.maps");
//
//        startActivity(mapIntent);

        //pop up name the which enter in the search show on the map
        Toast.makeText(this, locality, Toast.LENGTH_LONG).show();

        //getting the lat and long for address that type in the search
        lat = add.getLatitude();
        lng = add.getLongitude();


        gotoLocation(lat, lng, 16);
        mMap.addMarker(new MarkerOptions().position(new LatLng(40.7127837, -74.00594130000002)).title("New York"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(44.977753, -93.26501080000003)).title("Minneapolis"));
    }

    private void gotoLocation(double lat, double lng, int i) {
        LatLng ll = new LatLng(lat, lng);
        CameraUpdate cameraupdate = CameraUpdateFactory.newLatLngZoom(ll, 14);
        mMap.moveCamera(cameraupdate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:

                return true;
            case R.id.help:
               // showhelp();

            return true;
            case R.id.share:
                SAP  = (ShareActionProvider) item.getActionProvider();


                return true;
            case R.id.currentlocation:
                mMap.setMyLocationEnabled(true);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }


    }
    @Override
    public void onMapLongClick(LatLng point) {

        mMap.addMarker(new MarkerOptions()
                .position(point)
                .title("You Click Here")
                .draggable(true)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));


    }


}




//public class MapsActivity extends FragmentActivity {
////        implements
////        GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServices.OnConnectionFailListner
////{
//
//    int PLACE_PICKER_REQUEST = 1;
//    public double lng = 0.0;
//   public double lat = 0.0;
//    private final String TAG = getClass().getSimpleName();
//    private String[] places;
//   // private LocationManager locationManager;
//    private Location loc;
//     LocationListener locationListener;
//
//    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_maps);
//        setUpMapIfNeeded();
//        places = getResources().getStringArray(R.array.places);
//        showCurrentLocation();
//
//        final ActionBar actionBar = getActionBar();
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
//        actionBar.setListNavigationCallbacks(ArrayAdapter.createFromResource(
//                        this, R.array.places, android.R.layout.simple_list_item_1),
//                new ActionBar.OnNavigationListener() {
//
//                    @Override
//                    public boolean onNavigationItemSelected(int itemPosition,
//                                                            long itemId) {
//                        Log.e(TAG,
//                                places[itemPosition].toLowerCase().replace("-",
//                                        "_"));
//                        if (loc != null) {
//                            mMap.clear();
//                            new GetPlaces(MapsActivity.this,
//                                    places[itemPosition].toLowerCase().replace(
//                                            "-", "_").replace(" ", "_")).execute();
//                        }
//                        return true;
//                    }
//
//                });
//    }
//
//    private class GetPlaces extends AsyncTask<Void, Void, ArrayList<Place>> {
//
//
//        private ProgressDialog dialog;
//        private Context context;
//        private String places;
//
//        public GetPlaces(Context context, String places) {
//            this.context = context;
//            this.places = places;
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<Place> result) {
//            super.onPostExecute(result);
//            // super.onPostExecute(Place);
//            if (dialog.isShowing()) {
//                dialog.dismiss();
//            }
//            for (int i = 0; i < result.size(); i++) {
//                mMap.addMarker(new MarkerOptions()
//                        .title((String) result.get(i).getName())
//                        .position(
//                                new LatLng(result.get(i).getLatitude(), result
//                                        .get(i).getLongitude()))
//                                // .icon(BitmapDescriptorFactory
//                                //   .fromResource(R.drawable.pin))
//                        .snippet(result.get(i).getVicinity()));
//            }
//            CameraPosition cameraPosition = new CameraPosition.Builder()
//                    .target(new LatLng(result.get(0).getLatitude(), result
//                            .get(0).getLongitude())) // Sets the center of the map to
//                            // Mountain View
//                    .zoom(14) // Sets the zoom
//                    .tilt(30) // Sets the tilt of the camera to 30 degrees
//                    .build(); // Creates a CameraPosition from the builder
//            mMap.animateCamera(CameraUpdateFactory
//                    .newCameraPosition(cameraPosition));
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            dialog = new ProgressDialog(context);
//            dialog.setCancelable(false);
//            dialog.setMessage("Loading..");
//            dialog.isIndeterminate();
//            dialog.show();
//        }
//
//        @Override
//        protected ArrayList<Place> doInBackground(Void... arg0) {
//
//            Service service = new Service(
//
//                    "AIzaSyAWrELhvvpFBxadQACVMG26ngo6Nczpczc");
//            ArrayList<Place> findPlaces = service.findPlaces(loc.getLatitude(), // 28.632808
//                    loc.getLongitude(), places); // 77.218276
//
//            for (int i = 0; i < findPlaces.size(); i++) {
//
//                Place placeDetail = findPlaces.get(i);
//                Log.e(TAG, "places : " + placeDetail.getName());
//            }
//            //return findPlaces;
//            return null;
//        }
//    }
//
//
//
//
//
//    private void showCurrentLocation() {
//       LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//        String provider = locationManager
//                .getBestProvider(new Criteria(), false);
//
//        Location location = locationManager.getLastKnownLocation(provider);
//
//        if (location == null) {
//
//            locationManager.requestLocationUpdates(provider, 0, 0, locationListener);
//        } else {
//            loc = location;
//            new GetPlaces(MapsActivity.this, places[0].toLowerCase().replace(
//                    "-", "_")).execute();
//            Log.e(TAG, "location : " + location);
//        }
//
//
//    }
//
////    @Override
////    protected void onResume() {
////        super.onResume();
////        setUpMapIfNeeded();
////    }
////    @Override
////    protected void onPause(){
////        super.onPause();
////        setUpMapIfNeeded();
////    }
////    @Override
////    protected  void onDestroy(){
////        super.onDestroy();
////        setUpMapIfNeeded();
////    }
//
//
//
//    /**
//     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
//     * installed) and the map has not already been instantiated.. This will ensure that we only ever
//     * call {@link #setUpMap()} once when {@link #mMap} is not null.
//     * <p/>
//     * If it isn't installed {@link SupportMapFragment} (and
//     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
//     * install/update the Google Play services APK on their device.
//     * <p/>
//     * A user can return to this FragmentActivity after following the prompt and correctly
//     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
//     * have been completely destroyed during this process (it is likely that it would only be
//     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
//     * method in {@link #onResume()} to guarantee that it will be called.
//     */
//    private void setUpMapIfNeeded() {
//
//        // Do a null check to confirm that we have not already instantiated the map.
//        if (mMap == null) {
//            // Try to obtain the map from the SupportMapFragment.
//            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
//                    .getMap();
//            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//            //location avaible
//            mMap.setMyLocationEnabled(true);
//
//
//            //zoom in button on the map
//            mMap.getUiSettings().setZoomControlsEnabled(true);
//
//
//            // Check if we were successful in obtaining the map.
//            if (mMap != null) {
//                setUpMap();
//            }
//        }
//    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }
//
//    /**
//     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
//     * just add a marker near Africa.
//     * <p/>
//     * This should only be called once and when we are sure that {@link #mMap} is not null.
//     */
//    //show the current location
//    private void showCurrentLocation(Location location) {
//        //clear the early exist
//        mMap.clear();
//
//        //putting new location and position
//        LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
//
//        //putting the current location with markeroptions
//        mMap.addMarker(new MarkerOptions()
//                .position(currentPosition)
//                .snippet("Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude())
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.currentlocation))
//                .flat(true)
//                .draggable(true)
//                        //.getPosition(gas_station)
//                .title("I'm here!"));
//
////
////        Uri gmmIntentUri = Uri.parse("geo:0,0?z=14&q=gas_station");
////        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
////        mapIntent.setPackage("com.google.android.apps.maps");
////        startActivity(mapIntent);
//
//
//
//
//
//
//
////        Yelp yelp = new Yelp( lat, lng);
////        response = yelp.search("gas station", lat, lng);
////
//
//
//
//
//        // Zoom in, animating the camera.
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 14));
//    }
//
//    private void setUpMap() {
//
//        final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//
//        Criteria criteria = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_FINE);
//
//        String provider = locationManager.getBestProvider(criteria, true);
//
//     LocationListener locationListener = new LocationListener() {
//        LocationListener locationListener;
//
//
//
//            @Override
//            public void onStatusChanged(String s, int i, Bundle bundle) {
//
//            }
//
//            @Override
//            public void onProviderEnabled(String s) {
//            }
//
//            @Override
//            public void onProviderDisabled(String s) {
//            }
//            @Override
//            public void onLocationChanged(Location location) {
//                showCurrentLocation(location);
////                Log.e(TAG, "location update : " + location);
////                loc = location;
////               locationManager.removeUpdates(locationListener);
//            }
//        };
//
//        locationManager.requestLocationUpdates(provider, 2000, 0, locationListener);
//
//        // Getting initial Location
//        Location location = locationManager.getLastKnownLocation(provider);
//        // Show the initial location
//        if (location != null) {
//            showCurrentLocation(location);
//        }
//        mMap.addMarker(new MarkerOptions().position(new LatLng(44.9778, -93.2650)).title("Minneapolis"));
//
//    }
//
//



//        Yelp yelp = new Yelp(lat, lng);
//        String json = yelp.search("gas_station", lat, lng);


        // mMap.addMarker(json);


//
//        CircleOptions circleOptions = new CircleOptions()
//                .center(new LatLng(lat, lng))
//                .radius(1000); // In meters

// Get back the mutable Circle
//        Circle circle = mMap.addCircle(circleOptions);


//    }
//    public void Gas(View v) {
//
//        // setcurrentplace(gas station);
//
//        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//
//        Context context = getApplicationContext();
//        try {
//            startActivityForResult(builder.build(context), PLACE_PICKER_REQUEST);
//
//        } catch (GooglePlayServicesRepairableException e) {
//
//            e.printStackTrace();
//        } catch (GooglePlayServicesNotAvailableException e) {
//            e.printStackTrace();
//        }
//    }


//
//        Yelp yelp = new Yelp(lat, lng);
//       //String searchResults =
//           yelp.search("gas station", lat, lng);


        //this is getting the location






//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == PLACE_PICKER_REQUEST) {
//            if (resultCode == RESULT_OK) {
//                Place place = PlacePicker.getPlace(data, this);
//                String toastMsg = String.format("Place: %s", place.getName());
//                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
//
//               // CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(lat, lng, 14);
//               // mMap.moveCamera(cameraUpdate);
//            }
//
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//
//    }
////    Place.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
////            .setResultCallback(new ResultCallback<PlaceBuffer>() {
////        @Override
////        public void onResult(PlaceBuffer places) {
////            if (places.getStatus().isSuccess()) {
////                final Place myPlace = places.get(0);
////                //Log.i(TAG, "Place found: " + myPlace.getName());
////            }
////            places.release();
////        }
////    });
//}




