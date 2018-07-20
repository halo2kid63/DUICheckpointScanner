package co.huy.android.duicheckpointscanner;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import co.huy.android.duicheckpointscanner.data.DUICheckPoint;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button updateBtn, btnDown, btnUp;
    private ProgressBar indeterminateBar;
    private TextView txtNumber;
    private LocalData database = new LocalData();
    private List<DUICheckPoint> dataList = new ArrayList<>();
    private int numberOfDay = 1;
    private Date dateCheck = new Date();
    private SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd, yyyy");
    private String ListAPI = "myAPILink";
    private String GoogleAPI = "https://maps.googleapis.com/maps/api/geocode/json?";
    private String GG_API_KEY = "MyGoogleAPIKey";
    private ObjectMapper objectMapper = new ObjectMapper();
    private boolean dbExisted = false;
    List<DUICheckPoint> tempList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        updateBtn = findViewById(R.id.updateButton);
        btnDown = findViewById(R.id.btnDown);
        btnUp = findViewById(R.id.btnUp);
        txtNumber = findViewById(R.id.txtNumber);
        indeterminateBar = findViewById(R.id.indeterminateBar);

        indeterminateBar.setVisibility(View.INVISIBLE);
        dateCheck = subtractDays(new Date(), numberOfDay);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberOfDay < 7) {
                    numberOfDay += 1;
                } else {
                    numberOfDay = 1;
                }

                txtNumber.setText("" + numberOfDay);
                dateCheck = subtractDays(new Date(), numberOfDay);
            }
        });

        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberOfDay > 1) {
                    numberOfDay -= 1;
                } else {
                    numberOfDay = 7;
                }

                txtNumber.setText("" + numberOfDay);
                dateCheck = subtractDays(new Date(), numberOfDay);
            }
        });

        File file = new File("database.txt");
        if (file.exists()) {
            dbExisted = true;
//            Toast.makeText(MapsActivity.this, "Da co database", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        indeterminateBar.setVisibility(View.INVISIBLE);
        LatLng la = new LatLng(34.0522342, -118.2436849);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(la));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(7), 2000, null);

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        if (dbExisted) {
            loadDatabase();
        }

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<DUICheckPoint> getList = new ArrayList<>();
                indeterminateBar.setVisibility(View.VISIBLE);
                @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> loadingTask = new AsyncTask<Void, Void, Void>() {
                    private String result;

                    @Override
                    protected Void doInBackground(Void... voids) {

                        HttpURLConnection urlConnection = null;
                        try {
                            URL url = new URL(ListAPI);
                            urlConnection = (HttpURLConnection) url.openConnection();
                            InputStream inputstream = new BufferedInputStream(urlConnection.getInputStream());

                            final int bufferSize = 1024;
                            final char[] buffer = new char[bufferSize];
                            final StringBuilder out = new StringBuilder();
                            Reader in = new InputStreamReader(inputstream, "UTF-8");
                            for (; ; ) {
                                int rsz = in.read(buffer, 0, buffer.length);
                                if (rsz < 0)
                                    break;
                                out.append(buffer, 0, rsz);
                            }
                            result = out.toString();
                        } catch (IOException e) {
//                            Log.e("TEST", "Failed to send the request.", e);
                        } catch (Exception e) {
//                            Log.e("TEST2", "Error in general: ", e);
                        } finally {
                            if (urlConnection != null) {
                                urlConnection.disconnect();
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);

                        try {
                            List<DUICheckPoint> data = objectMapper.readValue(result, new TypeReference<List<DUICheckPoint>>() {
                            });

                            for (DUICheckPoint cp : data) {
                                String ts = cp.getTimeString();
                                if (ts.contains("-")) {
                                    ts = ts.split("-")[ts.split("-").length - 1].trim();
                                }

                                Date cpDate = formatter.parse(ts);

                                if (cpDate.before(dateCheck)) {
                                    continue;
                                } else {
                                    getList.add(cp);
                                }
                            }

                            List<DUICheckPoint> updateList = new ArrayList<>();

                            if (!getList.isEmpty()) {

                                for (DUICheckPoint cp : getList) {
                                    updateList = compareDUICheckpoint(cp, dataList);
                                }

                                String jsonDb = objectMapper.writeValueAsString(updateList);

                                database.writeToFile(jsonDb, MapsActivity.this);
                            }

                            loadCheckpoint();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

//                        indeterminateBar.setVisibility(View.INVISIBLE);
                    }
                };

                loadingTask.execute();

//                loadCheckpoint();
            }
        });
    }

    private void loadDatabase() {
        String getResult = database.readFromFile(MapsActivity.this);

        if (getResult != null && !getResult.isEmpty()) {

            try {
                List<DUICheckPoint> checkpoints = objectMapper.readValue(getResult, new TypeReference<List<DUICheckPoint>>() {
                });

                for (DUICheckPoint cp : checkpoints) {
                    String ts = cp.getTimeString();
                    if (ts.contains("-")) {
                        ts = ts.split("-")[ts.split("-").length - 1].trim();
                    }

                    Date cpDate = formatter.parse(ts);

                    if (cpDate.before(dateCheck)) {
                        continue;
                    } else {
                        dataList.add(cp);
                    }
                }

                if (dataList != null && !dataList.isEmpty()) {
                    for (DUICheckPoint cp : dataList) {
                        if (cp.getLocationX() == 0 && cp.getLocationY() == 0) {
                            continue;
                        } else {
                            displayMarker(new LatLng(cp.getLocationX(), cp.getLocationY()), cp.getLocationOrigin());
                        }
                    }

                    String jsonDb = objectMapper.writeValueAsString(dataList);

                    database.writeToFile(jsonDb, MapsActivity.this);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private Date subtractDays(Date date, int days) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.add(Calendar.DATE, -days);

        return cal.getTime();
    }

    private List<DUICheckPoint> compareDUICheckpoint(DUICheckPoint cp, List<DUICheckPoint> list) {
        boolean found = false;
        for (DUICheckPoint data : list) {
            if (data.getCounty().equals(cp.getCounty()) && data.getCity().equals(cp.getCity())
                    && data.getLocationOrigin().equals(cp.getLocationOrigin()) && data.getTimeString().equals(cp.getTimeString())) {
                found = true;
                break;
            }
        }
        if (!found) {
            list.add(cp);
        }
        return list;
    }

    private void displayMarker(LatLng location, String titleLocation) {
        mMap.addMarker(new MarkerOptions().position(location).draggable(false).title(titleLocation));
    }

    private void loadCheckpoint() {

        String getListToCallAPI = database.readFromFile(MapsActivity.this);
        List<DUICheckPoint> finalData = new ArrayList<>();
        List<DUICheckPoint> updateData = new ArrayList<>();

        if (getListToCallAPI != null && !getListToCallAPI.isEmpty()) {

            try {
                List<DUICheckPoint> checkpoints = objectMapper.readValue(getListToCallAPI, new TypeReference<List<DUICheckPoint>>() {
                });
//                Log.i("inside loadCheckPoint", "loadCheckPoint");
                for (DUICheckPoint cp : checkpoints) {
                    String ts = cp.getTimeString();
                    if (ts.contains("-")) {
                        ts = ts.split("-")[ts.split("-").length - 1].trim();
                    }

                    Date cpDate = formatter.parse(ts);

                    if (cpDate.before(dateCheck)) {
                        continue;
                    } else if (cp.getLocationX() == 0 && cp.getLocationY() == 0) {
//                        updateData.add(callGoogleAPI(cp));
                        updateData.add(cp);
                    } else {
                        finalData.add(cp);
                    }
                }

                if (updateData.isEmpty()) {
                    //turn off loading bar
                    indeterminateBar.setVisibility(View.GONE);

                } else {
                    //update data async
                    callGoogleAPISuper(updateData, finalData);
                }

//                if (!finalData.isEmpty()) {
//                    // show cp have x,y
//
//                }
//                finalData.addAll(updateData);
//                String updateDatabase = objectMapper.writeValueAsString(finalData);

//                database.writeToFile(updateDatabase.toString(), MapsActivity.this);

            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


    private void callGoogleAPISuper(List<DUICheckPoint> checkpoints, List<DUICheckPoint> updatedList) {
        tempList.clear();

        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, Void> insideTask = new AsyncTask<Void, Void, Void>() {
            private String result;

            @Override
            protected Void doInBackground(Void... voids) {

                for (DUICheckPoint checkpoint : checkpoints) {
                    HttpURLConnection urlConnection = null;
//                    Log.i("update cp", "update this cp=" + checkpoint.getLocationOrigin());
                    try {
                        URL url = new URL(GoogleAPI + "address=" + checkpoint.getLocationOrigin().replace(" ", "+") + "&key=" + GG_API_KEY);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        InputStream inputstream = new BufferedInputStream(urlConnection.getInputStream());

                        final int bufferSize = 1024;
                        final char[] buffer = new char[bufferSize];
                        final StringBuilder out = new StringBuilder();
                        Reader in = new InputStreamReader(inputstream, "UTF-8");
                        for (; ; ) {
                            int rsz = in.read(buffer, 0, buffer.length);
                            if (rsz < 0)
                                break;
                            out.append(buffer, 0, rsz);
                        }
                        result = out.toString();
                        if (new JSONObject(result).getJSONArray("results").length() != 0) {


                            JSONObject obj = ((JSONObject) ((JSONObject) ((JSONObject) new
                                    JSONObject(result).getJSONArray("results").get(0)).get("geometry")).get("location"));

                            checkpoint.setLocationX((double) obj.get("lat"));
                            checkpoint.setLocationY((double) obj.get("lng"));

//                            displayMarker(new LatLng(checkpoint.getLocationX(), checkpoint.getLocationY()), checkpoint.getLocationOrigin());

                            tempList.add(checkpoint);
                        }

                    } catch (IOException e) {

//                        Log.e("TEST", "Failed to send the request.", e);
                    } catch (Exception e) {
//                        Log.e("TEST2", "Error in general: ", e);
                    } finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                for (DUICheckPoint cp : tempList) {
                    displayMarker(new LatLng(cp.getLocationX(), cp.getLocationY()), cp.getLocationOrigin());
                }
                tempList.addAll(updatedList);

                try {
                    String updateDatabase = objectMapper.writeValueAsString(tempList);
                    database.writeToFile(updateDatabase, MapsActivity.this);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }

                indeterminateBar.setVisibility(View.INVISIBLE);
                Toast.makeText(MapsActivity.this, "Updated", Toast.LENGTH_SHORT).show();

            }
        };

        insideTask.execute();

    }
}
