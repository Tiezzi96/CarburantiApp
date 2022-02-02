package com.example.newapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.Toast;

// Classes needed to add the location engine
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.PropertyName;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.ref.WeakReference;
//import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.*;
//import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.*;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolDragListener;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolLongClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.plugins.locationlayer.*;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.utils.BitmapUtils;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import toan.android.floatingactionmenu.FloatingActionButton;
import toan.android.floatingactionmenu.FloatingActionsMenu;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAnchor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconOffset;

public class MainActivity extends AppCompatActivity implements Serializable, OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener, LocationListener {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS = 123;
    private MapView mapView;
    private MapboxMap mapboxMap;
    private Button startButton;
    private SearchView simpleSearchView;
    // Variables needed to hanButtondle location permissions
    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;
    private static Location originLocationApriori;
    // Variables needed to add the location engine
    private LocationEngine locationEngine;
    private long DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L;
    private long DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5;
    // Variables needed to listen to location updates
    private MainActivityLocationCallback callback = new MainActivityLocationCallback(this);
    private Point originePosition;
    private Point destinationPosition;
    private Marker destinationMarker;
    private DirectionsRoute currentRoutes;
    private NavigationMapRoute navigationMapRoute;
    private static boolean symbolIsClicked =false;
    private Map<String, String> markers;
    private static final String TAG = "MainActivity";
    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String ICON_ID = "ICON_ID";
    private static final String ICON_ID_2 = "ICON_ID_2";
    private static final String ICON_ID_3 = "ICON_ID_3";
    private static final String ICON_ID_4 = "ICON_ID_4";
    private static final String LAYER_ID = "LAYER_ID";
    private static final String CALLOUT_LAYER_ID = "CALLOUT_LAYER_ID";
    private static final String CALLOUT_IMAGE_ID = "CALLOUT_IMAGE_ID";
    private static List<Symbol> symbols = new ArrayList<>();
    private SymbolManager symbolManager;
    private List<Feature> symbolLayerIconFeatureList = new ArrayList<>();
    private HashMap<Integer, List<String>> agipfilter = new HashMap<>();
    private HashMap<Integer, List<String>> Q8filter = new HashMap<>();
    private HashMap<Integer, List<String>> tamoilfilter= new HashMap<>();
    private HashMap<Integer, List<String>> benzinafilter= new HashMap<>();
    private HashMap<Integer, List<String>> gasoliofilter= new HashMap<>();
    private HashMap<Integer, List<String>> metanofilter= new HashMap<>();
    private HashMap<Integer, List<String>> gplfilter= new HashMap<>();
    private static Context context;
    private FloatingActionButton recenterbutton;
    private FloatingActionButton fab_agip;
    private FloatingActionButton fab_Q8;
    private FloatingActionButton fab_tamoil;
    private FloatingActionButton fab_allstation;
    private FloatingActionButton fab_benzina;
    private FloatingActionButton fab_gasolio;
    private FloatingActionButton fab_metano;
    private FloatingActionButton fab_gpl;
    private Utils utils;

    public static void setOriginLocationApriori(Location originLocationApriori) {
        MainActivity.originLocationApriori = originLocationApriori;
    }

    public MapboxMap getMapboxMap() {
        return mapboxMap;
    }

    public Marker getDestinationMarker() {
        return destinationMarker;
    }

    public DirectionsRoute getCurrentRoutes() {
        return currentRoutes;
    }

    public NavigationMapRoute getNavigationMapRoute() {
        return navigationMapRoute;
    }

    public SymbolManager getSymbolManager() {
        return symbolManager;
    }

    public static List<Symbol> getSymbols() {
        return symbols;
    }

    public void setSymbolIsClicked(boolean symbolclicked) {
        MainActivity.symbolIsClicked = symbolclicked;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        utils=new Utils();
        CommonModelClass commonModelClass = CommonModelClass.getSingletonObject();
        commonModelClass.setbaseActivity(MainActivity.this);

        fetchLocation();

        if (isOnline()) {
            //do whatever you want to do
        } else {
            try {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();

                alertDialog.setTitle("Attenzione");
                alertDialog.setMessage("Internet non attivo: per il funzionamento dell'applicazione si prega di consentire l'accesso ai servizi di rete.");
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();

                    }
                });

                alertDialog.show();
            } catch (Exception e) {
                Log.d("ErrorMessage", "Show Dialog: " + e.getMessage());
            }
        }
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        //readCSVfile();
        setContentView(R.layout.activity_main);

        mapView = (MapView) findViewById(R.id.mapView);

        startButton = findViewById(R.id.startButton);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch Navigation UI
                NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                        .directionsRoute(currentRoutes)
                        .shouldSimulateRoute(false)
                        .build();
                NavigationLauncher.startNavigation(MainActivity.this, options);
            }

        });
        context = this;


        fab_agip = (FloatingActionButton) findViewById(R.id.fab_toggle_Agip);
        fab_agip.setColorNormal(Color.YELLOW);
        fab_agip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long time = System.currentTimeMillis();
                ArrayList<Symbol> s = new ArrayList<>();
                HashMap<Integer, List<String>> lista = new HashMap<>();
                if (!agipfilter.isEmpty()) {
                    lista = agipfilter;
                } else if (!Q8filter.isEmpty()) {
                    lista = Q8filter;
                    for (int i = 0; i < symbolLayerIconFeatureList.size(); i++) {
                        if (lista.containsKey(i)) {
                            if (symbolLayerIconFeatureList.get(i).getStringProperty("Bandiera").equals("Agip Eni") && !lista.get(i).get(1).equals("")) {
                                symbols.get(i).setIconImage(lista.get(i).get(0));
                                symbols.get(i).setTextField(lista.get(i).get(1));
                            }else if(symbolLayerIconFeatureList.get(i).getStringProperty("Bandiera").equals("Agip Eni") && lista.get(i).get(1).equals("")){
                                lista.get(i).add("Agip Eni");
                                agipfilter.put(i, lista.get(i));
                            } else {
                                agipfilter.put(i, lista.get(i));
                            }
                        } else {
                            Symbol symbol = symbols.get(i);
                            List<String> list = new ArrayList<>();
                            list.add(symbolLayerIconFeatureList.get(i).getStringProperty("icon-image"));
                            list.add(symbol.getTextField());
                            agipfilter.put(i, list);
                            symbol.setIconImage("");
                            symbol.setTextField("");
                            s.add(symbol);
                        }
                    }
                    fab_agip.setForeground(getResources().getDrawable(R.drawable.fab));
                    fab_Q8.setForeground(null);
                    fab_agip.setColorNormal(Color.YELLOW);
                    fab_Q8.setColorNormal(getResources().getColor(R.color.bluebright));
                    Q8filter = new HashMap<>();
                    symbolManager.update(symbols);

                } else if (!tamoilfilter.isEmpty()) {
                    lista = tamoilfilter;
                    for (int i = 0; i < symbolLayerIconFeatureList.size(); i++) {
                        if (lista.containsKey(i)) {
                            if (symbolLayerIconFeatureList.get(i).getStringProperty("Bandiera").equals("Agip Eni") && !(lista.get(i).get(1).equals(""))) {
                                symbols.get(i).setIconImage(lista.get(i).get(0));
                                symbols.get(i).setTextField(lista.get(i).get(1));
                            }else if(symbolLayerIconFeatureList.get(i).getStringProperty("Bandiera").equals("Agip Eni") && lista.get(i).get(1).equals("")){
                                List<String> l = new ArrayList<>();
                                l.add(lista.get(i).get(0));
                                l.add(lista.get(i).get(1));
                                l.add("Agip Eni");
                                System.out.println("Stream l: "+l.get(0)+"\n"+l.get(1)+"\n"+l.get(2));
                                agipfilter.put(i,l);
                            } else {
                                agipfilter.put(i, lista.get(i));
                            }
                        } else {
                            Symbol symbol = symbols.get(i);
                            List<String> list = new ArrayList<>();
                            list.add(symbolLayerIconFeatureList.get(i).getStringProperty("icon-image"));
                            list.add(symbol.getTextField());
                            agipfilter.put(i, list);
                            symbol.setIconImage("");
                            symbol.setTextField("");
                            s.add(symbol);
                        }
                    }
                    fab_agip.setForeground(getResources().getDrawable(R.drawable.fab));
                    fab_tamoil.setForeground(null);
                    fab_agip.setColorNormal(Color.YELLOW);
                    fab_tamoil.setColorNormal(getResources().getColor(R.color.redbright));
                    tamoilfilter = new HashMap<>();
                    symbolManager.update(symbols);
                } else {
                    for (int i = 0; i < symbolLayerIconFeatureList.size(); i++) {

                        String icon=symbols.get(i).getIconImage();
                        String icon2=symbolLayerIconFeatureList.get(i).getStringProperty("icon-image");
                        String field=symbols.get(i).getTextField();
                        String field2=symbolLayerIconFeatureList.get(i).getStringProperty("text-field");
                        if (!symbolLayerIconFeatureList.get(i).getStringProperty("Bandiera").equals("Agip Eni")) {
                            Symbol symbol = symbols.get(i);
                            List<String> list = new ArrayList<>();
                            try {
                                System.out.println(symbol.getTextField());
                            }catch(Exception e){
                                System.out.println(symbol.getLatLng());
                                System.out.println(symbol.getIconImage());
                            }
                            list.add(symbolLayerIconFeatureList.get(i).getStringProperty("icon-image"));
                            list.add(symbol.getTextField());
                            agipfilter.put(i, list);
                            symbol.setIconImage("");
                            symbol.setTextField("");
                            s.add(symbol);
                        }
                    }
                    fab_allstation.setForeground(null);
                    fab_agip.setForeground(getResources().getDrawable(R.drawable.fab));
                    fab_agip.setColorNormal(Color.YELLOW);
                    fab_allstation.setColorNormal(getResources().getColor(R.color.whitebright));
                    System.out.println(System.currentTimeMillis() - time);
                    symbolManager.update(s);
                }
                System.out.println(agipfilter);
                fab_allstation.setColorNormal(getResources().getColor(R.color.whitebright));
                fab_Q8.setColorNormal(getResources().getColor(R.color.bluebright));
                fab_tamoil.setColorNormal(getResources().getColor(R.color.redbright));
                fab_agip.setColorNormal(Color.YELLOW);
            }
        });

        fab_Q8 = (FloatingActionButton) findViewById(R.id.fab_toggle_Q8);
        fab_Q8.setColorNormal(Color.BLUE);
        fab_Q8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Symbol> s = new ArrayList<>();
                HashMap<Integer, List<String>> lista = new HashMap<>();
                if (!agipfilter.isEmpty()) {
                    lista = agipfilter;
                    for (int i = 0; i < symbolLayerIconFeatureList.size(); i++){
                        if(lista.containsKey(i)){
                           if(symbolLayerIconFeatureList.get(i).getStringProperty("Bandiera").equals("Q8") && !lista.get(i).get(1).equals("")){
                               symbols.get(i).setIconImage(lista.get(i).get(0));
                               symbols.get(i).setTextField(lista.get(i).get(1));
                           }else if(symbolLayerIconFeatureList.get(i).getStringProperty("Bandiera").equals("Q8") && lista.get(i).get(1).equals("")){
                               lista.get(i).add("Q8");
                               Q8filter.put(i, lista.get(i));
                           }else{
                               Q8filter.put(i, lista.get(i));
                           }
                        }else{
                            Symbol symbol = symbols.get(i);
                            List<String> list = new ArrayList<>();
                            list.add(symbolLayerIconFeatureList.get(i).getStringProperty("icon-image"));
                            list.add(symbol.getTextField());
                            Q8filter.put(i, list);
                            symbol.setIconImage("");
                            symbol.setTextField("");
                            s.add(symbol);
                        }
                    }
                    fab_agip.setForeground(null);
                    fab_Q8.setForeground(getResources().getDrawable(R.drawable.fab));
                    fab_Q8.setColorNormal(Color.BLUE);
                    fab_agip.setColorNormal(getResources().getColor(R.color.yellowbright));
                    agipfilter = new HashMap<>();
                    symbolManager.update(symbols);
                } else if (!Q8filter.isEmpty()) {
                    lista = Q8filter;
                } else if (!tamoilfilter.isEmpty()) {
                    lista = tamoilfilter;
                    for (int i = 0; i < symbolLayerIconFeatureList.size(); i++){
                        if(lista.containsKey(i)){
                            if(symbolLayerIconFeatureList.get(i).getStringProperty("Bandiera").equals("Q8") && !lista.get(i).get(1).equals("")){
                                symbols.get(i).setIconImage(lista.get(i).get(0));
                                symbols.get(i).setTextField(lista.get(i).get(1));
                            }else if(symbolLayerIconFeatureList.get(i).getStringProperty("Bandiera").equals("Q8") && lista.get(i).get(1).equals("")){
                                lista.get(i).add("Q8");
                                Q8filter.put(i, lista.get(i));
                            }
                            else{
                                Q8filter.put(i, lista.get(i));
                            }
                        }else{
                            Symbol symbol = symbols.get(i);
                            List<String> list = new ArrayList<>();
                            list.add(symbolLayerIconFeatureList.get(i).getStringProperty("icon-image"));
                            list.add(symbol.getTextField());
                            Q8filter.put(i, list);
                            symbol.setIconImage("");
                            symbol.setTextField("");
                            s.add(symbol);
                        }
                    }
                    fab_tamoil.setForeground(null);
                    fab_Q8.setForeground(getResources().getDrawable(R.drawable.fab));
                    fab_Q8.setColorNormal(Color.BLUE);
                    fab_tamoil.setColorNormal(getResources().getColor(R.color.redbright));

                    tamoilfilter = new HashMap<>();
                    symbolManager.update(symbols);
                } else {
                    for (int i = 0; i < symbolLayerIconFeatureList.size(); i++) {
                        if (!symbolLayerIconFeatureList.get(i).getStringProperty("Bandiera").equals("Q8")) {
                            Symbol symbol = symbols.get(i);
                            List<String> list = new ArrayList<>();
                            list.add(symbolLayerIconFeatureList.get(i).getStringProperty("icon-image"));
                            list.add(symbol.getTextField());
                            Q8filter.put(i, list);
                            symbol.setIconImage("");
                            symbol.setTextField("");
                            s.add(symbol);
                        }

                    }
                    fab_allstation.setForeground(null);
                    fab_Q8.setForeground(getResources().getDrawable(R.drawable.fab));
                    fab_Q8.setColorNormal(Color.BLUE);
                    fab_allstation.setColorNormal(getResources().getColor(R.color.whitebright));
                    symbolManager.update(s);

                }
                fab_agip.setColorNormal(getResources().getColor(R.color.yellowbright));
                fab_allstation.setColorNormal(getResources().getColor(R.color.whitebright));
                fab_tamoil.setColorNormal(getResources().getColor(R.color.redbright));
                fab_Q8.setColorNormal(Color.BLUE);
            }
        });

        fab_tamoil = (FloatingActionButton) findViewById(R.id.fab_toggle_Tamoil);
        fab_tamoil.setColorNormal(Color.RED);
        fab_tamoil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Symbol> s = new ArrayList<>();
                HashMap<Integer, List<String>> lista = new HashMap<>();
                if (!agipfilter.isEmpty()) {
                    lista = agipfilter;
                    for (int i = 0; i < symbolLayerIconFeatureList.size(); i++) {
                        if (lista.containsKey(i)) {
                            if (symbolLayerIconFeatureList.get(i).getStringProperty("Bandiera").equals("Tamoil") && !lista.get(i).get(1).equals("")) {
                                symbols.get(i).setIconImage(lista.get(i).get(0));
                                symbols.get(i).setTextField(lista.get(i).get(1));
                            }else if(symbolLayerIconFeatureList.get(i).getStringProperty("Bandiera").equals("Tamoil") && lista.get(i).get(1).equals("")){
                                lista.get(i).add("Tamoil");
                                tamoilfilter.put(i, lista.get(i));
                            }
                            else {
                                tamoilfilter.put(i, lista.get(i));
                            }
                        } else {
                            Symbol symbol = symbols.get(i);
                            List<String> list = new ArrayList<>();
                            list.add(symbolLayerIconFeatureList.get(i).getStringProperty("icon-image"));
                            list.add(symbol.getTextField());
                            tamoilfilter.put(i, list);
                            symbol.setIconImage("");
                            symbol.setTextField("");
                            s.add(symbol);
                        }
                    }
                    fab_agip.setForeground(null);
                    fab_tamoil.setForeground(getResources().getDrawable(R.drawable.fab));
                    fab_tamoil.setColorNormal(Color.RED);
                    fab_agip.setColorNormal(getResources().getColor(R.color.yellowbright));
                    agipfilter = new HashMap<>();
                    symbolManager.update(symbols);


                } else if (!Q8filter.isEmpty()) {
                    lista = Q8filter;
                    for (int i = 0; i < symbolLayerIconFeatureList.size(); i++) {
                        if (lista.containsKey(i)) {
                            if (symbolLayerIconFeatureList.get(i).getStringProperty("Bandiera").equals("Tamoil")  && !lista.get(i).get(1).equals("")) {
                                symbols.get(i).setIconImage(lista.get(i).get(0));
                                symbols.get(i).setTextField(lista.get(i).get(1));
                            }else if(symbolLayerIconFeatureList.get(i).getStringProperty("Bandiera").equals("Tamoil") && lista.get(i).get(1).equals("")){
                                lista.get(i).add("Tamoil");
                                tamoilfilter.put(i, lista.get(i));
                            }else {
                                tamoilfilter.put(i, lista.get(i));
                            }
                        } else {
                            Symbol symbol = symbols.get(i);
                            List<String> list = new ArrayList<>();
                            list.add(symbolLayerIconFeatureList.get(i).getStringProperty("icon-image"));
                            list.add(symbol.getTextField());
                            tamoilfilter.put(i, list);
                            symbol.setIconImage("");
                            symbol.setTextField("");
                            s.add(symbol);
                        }
                    }
                    fab_tamoil.setForeground(getResources().getDrawable(R.drawable.fab));
                    fab_Q8.setForeground(null);
                    fab_tamoil.setColorNormal(Color.RED);
                    fab_Q8.setColorNormal(getResources().getColor(R.color.bluebright));
                    Q8filter = new HashMap<>();
                    symbolManager.update(symbols);

                } else if (!tamoilfilter.isEmpty()) {
                    lista = tamoilfilter;
                } else {
                    for (int i = 0; i < symbolLayerIconFeatureList.size(); i++) {
                        if (!symbolLayerIconFeatureList.get(i).getStringProperty("Bandiera").equals("Tamoil")) {
                            System.out.println("tamoil");
                            List<String> list = new ArrayList<>();
                            Symbol symbol = symbols.get(i);
                            Symbol symbol2 = symbols.get(i);
                            list.add(symbolLayerIconFeatureList.get(i).getStringProperty("icon-image"));
                            list.add(symbol.getTextField());
                            tamoilfilter.put(i, list);
                            symbol.setIconImage("");
                            symbol.setTextField("");
                            System.out.println("list index 0 of"+i+" : "+list.get(0));
                            System.out.println("list index 1 of"+i+" : "+list.get(1));
                            s.add(symbol);
                        }

                    }
                    fab_allstation.setForeground(null);
                    fab_tamoil.setForeground(getResources().getDrawable(R.drawable.fab));
                    fab_tamoil.setColorNormal(Color.RED);
                    fab_allstation.setColorNormal(getResources().getColor(R.color.whitebright));
                    symbolManager.update(s);
                }
                fab_agip.setColorNormal(getResources().getColor(R.color.yellowbright));
                fab_Q8.setColorNormal(getResources().getColor(R.color.bluebright));
                fab_allstation.setColorNormal(getResources().getColor(R.color.whitebright));
                fab_tamoil.setColorNormal(Color.RED);
            }
        });

        fab_allstation = (FloatingActionButton) findViewById(R.id.fab_toggle_allstation);
        fab_allstation.setColorNormal(Color.WHITE);
        fab_allstation.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View v) {
                                              ArrayList<Symbol> s= new ArrayList<>();
                                              if(!benzinafilter.isEmpty()){
                                                  for(int j=0; j<symbolLayerIconFeatureList.size(); j++){
                                                      if(!tamoilfilter.isEmpty()&&tamoilfilter.containsKey(j)){
                                                          if(!benzinafilter.containsKey(j)){
                                                              System.out.println("!tamoilfilter.isEmpty()&&tamoilfilter.containsKey(j)&&!benzinafilter.containsKey(j)");
                                                              symbols.get(j).setIconImage(tamoilfilter.get(j).get(0));
                                                              symbols.get(j).setTextField(tamoilfilter.get(j).get(1));
                                                              tamoilfilter.remove(j);
                                                          }
                                                      }else if(!agipfilter.isEmpty()&&agipfilter.containsKey(j)){
                                                          if(!benzinafilter.containsKey(j)){
                                                              System.out.println("!agipfilter.isEmpty()&&agipfilter.containsKey(j)&&!benzinafilter.containsKey(j)");
                                                              symbols.get(j).setIconImage(agipfilter.get(j).get(0));
                                                              symbols.get(j).setTextField(agipfilter.get(j).get(1));
                                                              agipfilter.remove(j);
                                                          }
                                                      }else if(!Q8filter.isEmpty()&&Q8filter.containsKey(j)){
                                                          if(!benzinafilter.containsKey(j)){
                                                              symbols.get(j).setIconImage(Q8filter.get(j).get(0));
                                                              symbols.get(j).setTextField(Q8filter.get(j).get(1));
                                                              Q8filter.remove(j);
                                                          }
                                                      }
                                                  }
                                              }else if(!gasoliofilter.isEmpty()){
                                                  for(int j=0; j<symbolLayerIconFeatureList.size(); j++){
                                                      if(!tamoilfilter.isEmpty()&&tamoilfilter.containsKey(j)){
                                                          if(!gasoliofilter.containsKey(j)){

                                                              symbols.get(j).setIconImage(tamoilfilter.get(j).get(0));
                                                              symbols.get(j).setTextField(tamoilfilter.get(j).get(1));
                                                              tamoilfilter.remove(j);
                                                          }
                                                      }else if(!agipfilter.isEmpty()&&agipfilter.containsKey(j)){
                                                          if(!gasoliofilter.containsKey(j)){
                                                              symbols.get(j).setIconImage(agipfilter.get(j).get(0));
                                                              symbols.get(j).setTextField(agipfilter.get(j).get(1));
                                                              agipfilter.remove(j);
                                                          }
                                                      }else if(!Q8filter.isEmpty()&&Q8filter.containsKey(j)){
                                                          if(!gasoliofilter.containsKey(j)){
                                                              symbols.get(j).setIconImage(Q8filter.get(j).get(0));
                                                              symbols.get(j).setTextField(Q8filter.get(j).get(1));
                                                              Q8filter.remove(j);
                                                          }
                                                      }
                                                  }
                                              }else if(!gplfilter.isEmpty()){
                                                  for(int j=0; j<symbolLayerIconFeatureList.size(); j++){
                                                      if(!tamoilfilter.isEmpty()&&tamoilfilter.containsKey(j)){
                                                          if(!gplfilter.containsKey(j)){
                                                              symbols.get(j).setIconImage(tamoilfilter.get(j).get(0));
                                                              symbols.get(j).setTextField(tamoilfilter.get(j).get(1));
                                                              tamoilfilter.remove(j);
                                                          }
                                                      }else if(!agipfilter.isEmpty()&&agipfilter.containsKey(j)){
                                                          if(!gplfilter.containsKey(j)){
                                                              symbols.get(j).setIconImage(agipfilter.get(j).get(0));
                                                              symbols.get(j).setTextField(agipfilter.get(j).get(1));
                                                              agipfilter.remove(j);
                                                          }
                                                      }else if(!Q8filter.isEmpty()&&Q8filter.containsKey(j)){
                                                          if(!gplfilter.containsKey(j)){
                                                              symbols.get(j).setIconImage(Q8filter.get(j).get(0));
                                                              System.out.println("Q8filter.get(j).get(1): "+Q8filter.get(j).get(1)+" Q8filter.get(j).get(0): "+Q8filter.get(j).get(0));
                                                              symbols.get(j).setTextField(Q8filter.get(j).get(1));
                                                              Q8filter.remove(j);
                                                          }
                                                      }
                                                  }
                                              }else if(!metanofilter.isEmpty()){
                                                  for(int j=0; j<symbolLayerIconFeatureList.size(); j++){
                                                      if(!tamoilfilter.isEmpty()&&tamoilfilter.containsKey(j)){
                                                          if(!metanofilter.containsKey(j)){
                                                              symbols.get(j).setIconImage(tamoilfilter.get(j).get(0));
                                                              symbols.get(j).setTextField(tamoilfilter.get(j).get(1));
                                                              tamoilfilter.remove(j);
                                                          }
                                                      }else if(!agipfilter.isEmpty()&&agipfilter.containsKey(j)){
                                                          if(!metanofilter.containsKey(j)){
                                                              symbols.get(j).setIconImage(agipfilter.get(j).get(0));
                                                              symbols.get(j).setTextField(agipfilter.get(j).get(1));
                                                              agipfilter.remove(j);
                                                          }
                                                      }else if(!Q8filter.isEmpty()&&Q8filter.containsKey(j)){
                                                          if(!metanofilter.containsKey(j)){
                                                              symbols.get(j).setIconImage(Q8filter.get(j).get(0));
                                                              symbols.get(j).setTextField(Q8filter.get(j).get(1));
                                                              Q8filter.remove(j);
                                                          }
                                                      }
                                                  }
                                              }else {

                                                  for (int j = 0; j < symbols.size(); j++) {
                                                      if (!tamoilfilter.isEmpty() && tamoilfilter.containsKey(j)) {
                                                          symbols.get(j).setIconImage(tamoilfilter.get(j).get(0));
                                                          symbols.get(j).setTextField(tamoilfilter.get(j).get(1));
                                                      } else if (!agipfilter.isEmpty() && agipfilter.containsKey(j)) {
                                                          symbols.get(j).setIconImage(agipfilter.get(j).get(0));
                                                          symbols.get(j).setTextField(agipfilter.get(j).get(1));

                                                      } else if (!Q8filter.isEmpty() && Q8filter.containsKey(j)) {
                                                          symbols.get(j).setIconImage(Q8filter.get(j).get(0));
                                                          symbols.get(j).setTextField(Q8filter.get(j).get(1));
                                                      }
                                                  }


                                              }
                                              agipfilter = new HashMap<>();
                                              Q8filter = new HashMap<>();
                                              tamoilfilter = new HashMap<>();
                                              symbolManager.update(symbols);
                                              fab_agip.setForeground(null);
                                              fab_Q8.setForeground(null);
                                              fab_tamoil.setForeground(null);
                                              fab_allstation.setForeground(getResources().getDrawable(R.drawable.fab));
                                              fab_agip.setColorNormal(getResources().getColor(R.color.yellowbright));
                                              fab_Q8.setColorNormal(getResources().getColor(R.color.bluebright));
                                              fab_tamoil.setColorNormal(getResources().getColor(R.color.redbright));
                                              fab_allstation.setColorNormal(Color.WHITE);
                                          }

        });

        recenterbutton =(FloatingActionButton) findViewById(R.id.recenterbutton);
        recenterbutton.setColorNormal(Color.BLUE);
        recenterbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng position = new LatLng();
                position.setLatitude(originLocation.getLatitude());
                position.setLongitude(originLocation.getLongitude());
                CameraPosition p = new CameraPosition.Builder()
                        .target(position)
                        .zoom(11)
                        .build();
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(p), 100);

            }
        });

        FloatingActionsMenu menu = (FloatingActionsMenu) findViewById(R.id.Menu_Carburanti);
        fab_benzina = (FloatingActionButton) findViewById(R.id.fab_toggle_benzina);
        fab_benzina.setColorNormal(Color.GREEN);
        fab_benzina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Symbol> s= new ArrayList<>();
                if (!gasoliofilter.isEmpty()){
                    if(!agipfilter.isEmpty()){
                        utils.stationfilter(agipfilter,"Agip Eni", gasoliofilter, benzinafilter,
                                "BenzinaSelf", "BenzinaServita", Color.GREEN, symbolLayerIconFeatureList,
                                symbols, s);
                        gasoliofilter=new HashMap<>();
                    }else if(!Q8filter.isEmpty()){
                        utils.stationfilter(Q8filter,"Q8", gasoliofilter, benzinafilter,
                                "BenzinaSelf", "BenzinaServita", Color.GREEN, symbolLayerIconFeatureList,
                                symbols, s);
                        gasoliofilter=new HashMap<>();
                    }else if(!tamoilfilter.isEmpty()){
                        utils.stationfilter(tamoilfilter,"Tamoil", gasoliofilter, benzinafilter,
                                "BenzinaSelf", "BenzinaServita",Color.GREEN, symbolLayerIconFeatureList,
                                symbols, s);
                        gasoliofilter=new HashMap<>();
                    } else{
                        for (int i = 0; i < symbolLayerIconFeatureList.size(); i++) {
                            if (symbolLayerIconFeatureList.get(i).getStringProperty("BenzinaSelf") != null) {
                                if(gasoliofilter.containsKey(i)){
                                    symbols.get(i).setIconImage(gasoliofilter.get(i).get(1));
                                }
                                symbols.get(i).setTextField(symbolLayerIconFeatureList.get(i).getStringProperty("BenzinaSelf"));
                                symbols.get(i).setTextColor(Color.GREEN);
                                s.add(symbols.get(i));
                            } else if (symbolLayerIconFeatureList.get(i).getStringProperty("BenzinaServita") != null) {
                                if(gasoliofilter.containsKey(i)){
                                    symbols.get(i).setIconImage(gasoliofilter.get(i).get(1));
                                }
                                symbols.get(i).setTextField(symbolLayerIconFeatureList.get(i).getStringProperty("BenzinaServita"));
                                symbols.get(i).setTextColor(Color.GREEN);
                                s.add(symbols.get(i));
                            } else {
                                List<String> lista=new ArrayList<>();
                                lista.add(symbols.get(i).getTextField());
                                if(gasoliofilter.containsKey(i)){
                                    lista.add(gasoliofilter.get(i).get(1));
                                }else{
                                    lista.add(symbols.get(i).getIconImage());
                                }
                                symbols.get(i).setTextField("");
                                symbols.get(i).setIconImage("");
                                benzinafilter.put(i, lista);
                                s.add(symbols.get(i));
                            }
                        }
                        gasoliofilter = new HashMap<>();


                    }

                }else if(!metanofilter.isEmpty()){
                    if(!agipfilter.isEmpty()){
                        utils.stationfilter(agipfilter,"Agip Eni", metanofilter, benzinafilter,
                                "BenzinaSelf", "BenzinaServita",Color.GREEN,symbolLayerIconFeatureList,
                                symbols, s);
                        metanofilter=new HashMap<>();
                    }else if(!Q8filter.isEmpty()){
                        utils.stationfilter(Q8filter,"Q8", metanofilter, benzinafilter,
                                "BenzinaSelf", "BenzinaServita",Color.GREEN,symbolLayerIconFeatureList,
                                symbols, s);
                        metanofilter=new HashMap<>();
                    }else if(!tamoilfilter.isEmpty()){
                        utils.stationfilter(tamoilfilter,"Tamoil", metanofilter, benzinafilter,
                                "BenzinaSelf", "BenzinaServita",Color.GREEN,symbolLayerIconFeatureList,
                                symbols, s);
                        metanofilter=new HashMap<>();
                    } else {

                        for (int i = 0; i < symbolLayerIconFeatureList.size(); i++) {
                            if (symbolLayerIconFeatureList.get(i).getStringProperty("BenzinaSelf") != null) {
                                if (metanofilter.containsKey(i)) {
                                    symbols.get(i).setIconImage(metanofilter.get(i).get(1));
                                }
                                symbols.get(i).setTextField(symbolLayerIconFeatureList.get(i).getStringProperty("BenzinaSelf"));
                                symbols.get(i).setTextColor(Color.GREEN);
                                s.add(symbols.get(i));
                            } else if (symbolLayerIconFeatureList.get(i).getStringProperty("BenzinaServita") != null) {
                                if (metanofilter.containsKey(i)) {
                                    symbols.get(i).setIconImage(metanofilter.get(i).get(1));
                                }
                                symbols.get(i).setTextField(symbolLayerIconFeatureList.get(i).getStringProperty("BenzinaServita"));
                                symbols.get(i).setTextColor(Color.GREEN);
                                s.add(symbols.get(i));
                            } else {
                                List<String> lista = new ArrayList<>();
                                lista.add(symbols.get(i).getTextField());
                                if (metanofilter.containsKey(i)) {
                                    lista.add(metanofilter.get(i).get(1));
                                } else {
                                    lista.add(symbols.get(i).getIconImage());
                                }
                                symbols.get(i).setTextField("");
                                symbols.get(i).setIconImage("");
                                benzinafilter.put(i, lista);
                                s.add(symbols.get(i));
                            }
                        }
                        metanofilter = new HashMap<>();
                    }
                }else if(!gplfilter.isEmpty()){
                    if(!agipfilter.isEmpty()){
                        utils.stationfilter(agipfilter,"Agip Eni", gplfilter, benzinafilter,
                                "BenzinaSelf", "BenzinaServita",Color.GREEN,symbolLayerIconFeatureList,
                                symbols, s);
                        gplfilter=new HashMap<>();
                    }else if(!Q8filter.isEmpty()){
                        utils.stationfilter(Q8filter,"Q8", gplfilter, benzinafilter,
                                "BenzinaSelf", "BenzinaServita",Color.GREEN,symbolLayerIconFeatureList,
                                symbols, s);
                        gplfilter=new HashMap<>();
                    }else if(!tamoilfilter.isEmpty()){
                        utils.stationfilter(tamoilfilter,"Tamoil", gplfilter, benzinafilter,
                                "BenzinaSelf", "BenzinaServita",Color.GREEN,symbolLayerIconFeatureList,
                                symbols, s);
                        gplfilter=new HashMap<>();
                    } else {
                        for (int i = 0; i < symbolLayerIconFeatureList.size(); i++) {
                            if (symbolLayerIconFeatureList.get(i).getStringProperty("BenzinaSelf") != null) {
                                if (gplfilter.containsKey(i)) {
                                    symbols.get(i).setIconImage(gplfilter.get(i).get(1));
                                }
                                symbols.get(i).setTextField(symbolLayerIconFeatureList.get(i).getStringProperty("BenzinaSelf"));
                                symbols.get(i).setTextColor(Color.GREEN);
                                s.add(symbols.get(i));
                            } else if (symbolLayerIconFeatureList.get(i).getStringProperty("BenzinaServita") != null) {
                                if (gplfilter.containsKey(i)) {
                                    symbols.get(i).setIconImage(gplfilter.get(i).get(1));
                                }
                                symbols.get(i).setTextField(symbolLayerIconFeatureList.get(i).getStringProperty("BenzinaServita"));
                                symbols.get(i).setTextColor(Color.GREEN);
                                s.add(symbols.get(i));
                            } else {
                                List<String> lista = new ArrayList<>();
                                lista.add(symbols.get(i).getTextField());
                                if (gplfilter.containsKey(i)) {
                                    lista.add(gplfilter.get(i).get(1));
                                } else {
                                    lista.add(symbols.get(i).getIconImage());
                                }
                                symbols.get(i).setTextField("");
                                symbols.get(i).setIconImage("");
                                benzinafilter.put(i, lista);
                                s.add(symbols.get(i));
                            }
                        }
                        gplfilter = new HashMap<>();
                    }
                }else {
                    if (!agipfilter.isEmpty()) {
                        utils.stationfilter2(agipfilter, benzinafilter,
                                "BenzinaSelf", "BenzinaServita",Color.GREEN, symbolLayerIconFeatureList,
                                symbols, s);
                    } else if (!Q8filter.isEmpty()) {
                        utils.stationfilter2(Q8filter, benzinafilter,
                                "BenzinaSelf", "BenzinaServita",Color.GREEN, symbolLayerIconFeatureList,
                                symbols, s);
                    } else if (!tamoilfilter.isEmpty()) {
                        utils.stationfilter2(tamoilfilter, benzinafilter,
                                "BenzinaSelf", "BenzinaServita",Color.GREEN, symbolLayerIconFeatureList,
                                symbols, s);
                    } else {

                        for (int i = 0; i < symbolLayerIconFeatureList.size(); i++) {
                            if (symbolLayerIconFeatureList.get(i).getStringProperty("BenzinaSelf") != null) {
                                symbols.get(i).setTextField(symbolLayerIconFeatureList.get(i).getStringProperty("BenzinaSelf"));
                                symbols.get(i).setTextColor(Color.GREEN);
                                s.add(symbols.get(i));
                            } else if (symbolLayerIconFeatureList.get(i).getStringProperty("BenzinaServita") != null) {
                                symbols.get(i).setTextField(symbolLayerIconFeatureList.get(i).getStringProperty("BenzinaServita"));
                                symbols.get(i).setTextColor(Color.GREEN);
                                s.add(symbols.get(i));
                            } else {
                                List<String> lista = new ArrayList<>();
                                lista.add(symbols.get(i).getTextField());
                                lista.add(symbols.get(i).getIconImage());
                                symbols.get(i).setTextField("");
                                symbols.get(i).setIconImage("");
                                System.out.println("text field of "+i+" "+lista.get(0));
                                System.out.println("icon image of "+i+" "+lista.get(1));
                                benzinafilter.put(i, lista);
                                System.out.println(benzinafilter.values());
                                s.add(symbols.get(i));
                            }
                        }
                    }

                }

                symbolManager.update(s);
                fab_gasolio.setForeground(null);
                fab_metano.setForeground(null);
                fab_gpl.setForeground(null);
                fab_benzina.setForeground(getResources().getDrawable(R.drawable.fab));
                fab_gasolio.setColorNormal(getResources().getColor(R.color.whitebright));
                fab_metano.setColorNormal(getResources().getColor(R.color.greenbluebright));
                fab_gpl.setColorNormal(getResources().getColor(R.color.bluebright));
                fab_benzina.setColorNormal(Color.GREEN);
                //lp3.setMargins(0,0,(int)((float)((float)(dm.widthPixels)/((float)(dm.widthPixels)/150))*scale+0.5f), (int)((float)(dm.heightPixels/((float)(dm.heightPixels)/85))*scale+0.5f));
            }
        });

        fab_gasolio = (FloatingActionButton) findViewById(R.id.fab_toggle_gasolio);
        fab_gasolio.setColorNormal(Color.BLACK);
        fab_gasolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Symbol> s= new ArrayList<>();
                if (!benzinafilter.isEmpty()){
                if(!agipfilter.isEmpty()){
                    utils.stationfilter(agipfilter,"Agip Eni", benzinafilter, gasoliofilter,
                                "GasolioSelf", "GasolioServito",Color.BLACK,symbolLayerIconFeatureList,
                                symbols, s);
                    benzinafilter=new HashMap<>();
                }else if(!Q8filter.isEmpty()){
                    utils.stationfilter(Q8filter,"Q8", benzinafilter, gasoliofilter,
                                "GasolioSelf", "GasolioServito", Color.BLACK, symbolLayerIconFeatureList,
                                symbols, s);
                    benzinafilter=new HashMap<>();
                }else if(!tamoilfilter.isEmpty()){
                    utils.stationfilter(tamoilfilter,"Tamoil", benzinafilter, gasoliofilter,
                                "GasolioSelf", "GasolioServito",Color.BLACK,symbolLayerIconFeatureList,
                                symbols, s);
                    benzinafilter=new HashMap<>();
                } else {
                    for (int i = 0; i < symbolLayerIconFeatureList.size(); i++) {
                        if (symbolLayerIconFeatureList.get(i).getStringProperty("GasolioSelf") != null) {
                            if(benzinafilter.containsKey(i)){
                                symbols.get(i).setIconImage(benzinafilter.get(i).get(1));
                            }
                            symbols.get(i).setTextField(symbolLayerIconFeatureList.get(i).getStringProperty("GasolioSelf"));
                            symbols.get(i).setTextColor(Color.BLACK);
                            s.add(symbols.get(i));
                        } else if (symbolLayerIconFeatureList.get(i).getStringProperty("GasolioServito") != null) {
                            if(benzinafilter.containsKey(i)){
                                symbols.get(i).setIconImage(benzinafilter.get(i).get(1));
                            }
                            symbols.get(i).setTextField(symbolLayerIconFeatureList.get(i).getStringProperty("GasolioServito"));
                            symbols.get(i).setTextColor(Color.BLACK);
                            s.add(symbols.get(i));
                        } else {
                            List<String> lista=new ArrayList<>();
                            lista.add(symbols.get(i).getTextField());
                            if(benzinafilter.containsKey(i)){
                                lista.add(benzinafilter.get(i).get(1));
                            }else{
                                lista.add(symbols.get(i).getIconImage());
                            }
                            symbols.get(i).setTextField("");
                            symbols.get(i).setIconImage("");
                            gasoliofilter.put(i, lista);
                            s.add(symbols.get(i));
                        }
                    }
                    benzinafilter = new HashMap<>();
                    fab_benzina.setTitle("Benzina");
                }
                }else if(!metanofilter.isEmpty()){
                if(!agipfilter.isEmpty()){
                    utils.stationfilter(agipfilter,"Agip Eni", metanofilter, gasoliofilter,
                                "GasolioSelf", "GasolioServito",Color.BLACK, symbolLayerIconFeatureList,
                                symbols, s);
                    metanofilter=new HashMap<>();
                    }else if(!Q8filter.isEmpty()){
                    utils.stationfilter(Q8filter,"Q8", metanofilter, gasoliofilter,
                                "GasolioSelf", "GasolioServito",Color.BLACK,symbolLayerIconFeatureList,
                                symbols, s);
                    metanofilter=new HashMap<>();
                    }else if(!tamoilfilter.isEmpty()){
                    utils.stationfilter(tamoilfilter,"Tamoil", metanofilter, gasoliofilter,
                                "GasolioSelf", "GasolioServito",Color.BLACK, symbolLayerIconFeatureList,
                                symbols, s);
                    metanofilter=new HashMap<>();
                    } else {
                    for (int i = 0; i < symbolLayerIconFeatureList.size(); i++) {
                        if (symbolLayerIconFeatureList.get(i).getStringProperty("GasolioSelf") != null) {
                            if (metanofilter.containsKey(i)) {
                                symbols.get(i).setIconImage(metanofilter.get(i).get(1));
                            }
                            symbols.get(i).setTextField(symbolLayerIconFeatureList.get(i).getStringProperty("GasolioSelf"));
                            symbols.get(i).setTextColor(Color.BLACK);
                            s.add(symbols.get(i));
                        } else if (symbolLayerIconFeatureList.get(i).getStringProperty("GasolioServito") != null) {
                            if (metanofilter.containsKey(i)) {
                                symbols.get(i).setIconImage(metanofilter.get(i).get(1));
                            }
                            symbols.get(i).setTextField(symbolLayerIconFeatureList.get(i).getStringProperty("GasolioServito"));
                            symbols.get(i).setTextColor(Color.BLACK);
                            s.add(symbols.get(i));
                        } else {
                            List<String> lista = new ArrayList<>();
                            lista.add(symbols.get(i).getTextField());
                            if (metanofilter.containsKey(i)) {
                                lista.add(metanofilter.get(i).get(1));
                            } else {
                                lista.add(symbols.get(i).getIconImage());
                            }
                            symbols.get(i).setTextField("");
                            symbols.get(i).setIconImage("");
                            gasoliofilter.put(i, lista);
                            s.add(symbols.get(i));
                        }
                    }
                    metanofilter = new HashMap<>();

                }
                }else if(!gplfilter.isEmpty()){
                    if(!agipfilter.isEmpty()){
                        utils.stationfilter(agipfilter,"Agip Eni", gplfilter, gasoliofilter,
                                "GasolioSelf", "GasolioServito",Color.BLACK,symbolLayerIconFeatureList,
                                symbols, s);
                        gplfilter=new HashMap<>();
                    }else if(!Q8filter.isEmpty()){
                        utils.stationfilter(Q8filter,"Q8", gplfilter, gasoliofilter,
                                "GasolioSelf", "GasolioServito",Color.BLACK,symbolLayerIconFeatureList,
                                symbols, s);
                        gplfilter=new HashMap<>();
                    }else if(!tamoilfilter.isEmpty()){
                        utils.stationfilter(tamoilfilter,"Tamoil", gplfilter, gasoliofilter,
                                "GasolioSelf", "GasolioServito",Color.BLACK,symbolLayerIconFeatureList,
                                symbols, s);
                        gplfilter=new HashMap<>();
                    } else {
                        for (int i = 0; i < symbolLayerIconFeatureList.size(); i++) {
                            if (symbolLayerIconFeatureList.get(i).getStringProperty("GasolioSelf") != null) {
                                if (gplfilter.containsKey(i)) {
                                    symbols.get(i).setIconImage(gplfilter.get(i).get(1));
                                }
                                symbols.get(i).setTextField(symbolLayerIconFeatureList.get(i).getStringProperty("GasolioSelf"));
                                symbols.get(i).setTextColor(Color.BLACK);
                                s.add(symbols.get(i));
                            } else if (symbolLayerIconFeatureList.get(i).getStringProperty("GasolioServito") != null) {
                                if (gplfilter.containsKey(i)) {
                                    symbols.get(i).setIconImage(gplfilter.get(i).get(1));
                                }
                                symbols.get(i).setTextField(symbolLayerIconFeatureList.get(i).getStringProperty("GasolioServito"));
                                symbols.get(i).setTextColor(Color.BLACK);
                                s.add(symbols.get(i));
                            } else {
                                List<String> lista = new ArrayList<>();
                                lista.add(symbols.get(i).getTextField());
                                if (gplfilter.containsKey(i)) {
                                    lista.add(gplfilter.get(i).get(1));
                                } else {
                                    lista.add(symbols.get(i).getIconImage());
                                }
                                symbols.get(i).setTextField("");
                                symbols.get(i).setIconImage("");
                                gasoliofilter.put(i, lista);
                                s.add(symbols.get(i));
                            }
                        }
                        gplfilter = new HashMap<>();
                    }
                }else {
                    if (!agipfilter.isEmpty()) {
                        utils.stationfilter2(agipfilter, gasoliofilter,
                                "GasolioSelf", "GasolioServito", Color.BLACK, symbolLayerIconFeatureList,
                                symbols, s);
                    } else if (!Q8filter.isEmpty()) {
                        utils.stationfilter2(Q8filter, gasoliofilter,
                                "GasolioSelf", "GasolioServito", Color.BLACK, symbolLayerIconFeatureList,
                                symbols, s);
                    } else if (!tamoilfilter.isEmpty()) {
                        utils.stationfilter2(tamoilfilter, gasoliofilter,
                                "GasolioSelf", "GasolioServito", Color.BLACK, symbolLayerIconFeatureList,
                                symbols, s);
                    } else {


                        for (int i = 0; i < symbolLayerIconFeatureList.size(); i++) {
                            if (symbolLayerIconFeatureList.get(i).getStringProperty("GasolioSelf") != null) {
                                symbols.get(i).setTextField(symbolLayerIconFeatureList.get(i).getStringProperty("GasolioSelf"));
                                symbols.get(i).setTextColor(Color.BLACK);
                                s.add(symbols.get(i));
                            } else if (symbolLayerIconFeatureList.get(i).getStringProperty("GasolioServito") != null) {
                                symbols.get(i).setTextField(symbolLayerIconFeatureList.get(i).getStringProperty("GasolioServito"));
                                symbols.get(i).setTextColor(Color.BLACK);
                                s.add(symbols.get(i));
                            } else {
                                List<String> lista = new ArrayList<>();
                                lista.add(symbols.get(i).getTextField());
                                lista.add(symbols.get(i).getIconImage());
                                symbols.get(i).setTextField("");
                                symbols.get(i).setIconImage("");
                                gasoliofilter.put(i, lista);
                                s.add(symbols.get(i));
                            }

                        }

                    }
                }
                symbolManager.update(s);
                fab_benzina.setForeground(null);
                fab_metano.setForeground(null);
                fab_gpl.setForeground(null);
                fab_gasolio.setForeground(getResources().getDrawable(R.drawable.fab));
                fab_benzina.setColorNormal(getResources().getColor(R.color.greenbright));
                fab_metano.setColorNormal(getResources().getColor(R.color.greenbluebright));
                fab_gpl.setColorNormal(getResources().getColor(R.color.bluebright));
                fab_gasolio.setColorNormal(Color.BLACK);
                //lp3.setMargins(0,0,(int)((float)(dm.widthPixels)/((float)(dm.widthPixels)/170)*scale+0.5f), (int)((float)(dm.heightPixels/((float)(dm.heightPixels)/85))*scale+0.5f));
            }
        });
        fab_metano = (FloatingActionButton) findViewById(R.id.fab_toggle_metano);
        fab_metano.setColorNormal(getResources().getColor(R.color.greenblue));
        fab_metano.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Symbol> s= new ArrayList<>();
                if(!benzinafilter.isEmpty()){
                    if(!agipfilter.isEmpty()){
                        utils.stationfilterMetanoGPL(agipfilter, benzinafilter, metanofilter,"Agip Eni",
                                "Metano", getResources().getColor(R.color.greenblue), symbolLayerIconFeatureList,
                                symbols, s);
                        benzinafilter=new HashMap<>();
                    }else if(!Q8filter.isEmpty()){
                        utils.stationfilterMetanoGPL(Q8filter, benzinafilter, metanofilter,"Q8",
                                "Metano", getResources().getColor(R.color.greenblue), symbolLayerIconFeatureList,
                                symbols, s);
                        benzinafilter=new HashMap<>();
                    }else if(!tamoilfilter.isEmpty()){
                        utils.stationfilterMetanoGPL(tamoilfilter, benzinafilter, metanofilter,"Tamoil",
                                "Metano", getResources().getColor(R.color.greenblue), symbolLayerIconFeatureList,
                                symbols, s);
                        benzinafilter=new HashMap<>();
                    }else {
                        for (int i = 0; i < symbolLayerIconFeatureList.size(); i++) {
                            if (symbolLayerIconFeatureList.get(i).getStringProperty("Metano") != null) {
                                if (benzinafilter.containsKey(i)) {
                                    symbols.get(i).setIconImage(benzinafilter.get(i).get(1));
                                }
                                symbols.get(i).setTextField(symbolLayerIconFeatureList.get(i).getStringProperty("Metano"));
                                symbols.get(i).setTextColor(getResources().getColor(R.color.greenblue));
                                s.add(symbols.get(i));
                            } else {
                                List<String> lista = new ArrayList<>();
                                lista.add(symbols.get(i).getTextField());
                                if (benzinafilter.containsKey(i)) {
                                    lista.add(benzinafilter.get(i).get(1));
                                } else {
                                    lista.add(symbols.get(i).getIconImage());
                                }
                                symbols.get(i).setTextField("");
                                symbols.get(i).setIconImage("");
                                metanofilter.put(i, lista);
                                s.add(symbols.get(i));
                            }
                        }
                        benzinafilter = new HashMap<>();
                    }
                }else if(!gasoliofilter.isEmpty()){
                    if(!agipfilter.isEmpty()){
                        utils.stationfilterMetanoGPL(agipfilter, gasoliofilter, metanofilter,"Agip Eni",
                                "Metano", getResources().getColor(R.color.greenblue), symbolLayerIconFeatureList,
                                symbols, s);
                        gasoliofilter=new HashMap<>();
                    }else if(!Q8filter.isEmpty()){
                        utils.stationfilterMetanoGPL(Q8filter, gasoliofilter, metanofilter,"Q8",
                                "Metano", getResources().getColor(R.color.greenblue), symbolLayerIconFeatureList,
                                symbols, s);
                        gasoliofilter=new HashMap<>();
                    }else if(!tamoilfilter.isEmpty()){
                        utils.stationfilterMetanoGPL(tamoilfilter, gasoliofilter, metanofilter,"Tamoil",
                                "Metano", getResources().getColor(R.color.greenblue), symbolLayerIconFeatureList,
                                symbols, s);
                        gasoliofilter=new HashMap<>();
                    }else {


                        for (int i = 0; i < symbolLayerIconFeatureList.size(); i++) {
                            if (symbolLayerIconFeatureList.get(i).getStringProperty("Metano") != null) {
                                if (gasoliofilter.containsKey(i)) {
                                    System.out.println("gasoliofilter.get(i).get(1): "+gasoliofilter.get(i).get(1));
                                    symbols.get(i).setIconImage(gasoliofilter.get(i).get(1));
                                }
                                if(symbolLayerIconFeatureList.get(i).getStringProperty("Metano").equals("0.954")){
                                    System.out.println(i);
                                }
                                symbols.get(i).setTextField(symbolLayerIconFeatureList.get(i).getStringProperty("Metano"));
                                symbols.get(i).setTextColor(getResources().getColor(R.color.greenblue));
                                s.add(symbols.get(i));
                            } else {
                                List<String> lista = new ArrayList<>();
                                lista.add(symbols.get(i).getTextField());
                                if (gasoliofilter.containsKey(i)) {
                                    lista.add(gasoliofilter.get(i).get(1));
                                } else {
                                    lista.add(symbols.get(i).getIconImage());
                                }
                                symbols.get(i).setTextField("");
                                symbols.get(i).setIconImage("");
                                metanofilter.put(i, lista);
                                s.add(symbols.get(i));
                            }
                        }
                        gasoliofilter = new HashMap<>();
                    }
                }else if(!gplfilter.isEmpty()){
                    if(!agipfilter.isEmpty()){
                        utils.stationfilterMetanoGPL(agipfilter, gplfilter, metanofilter,"Agip Eni",
                                "Metano", getResources().getColor(R.color.greenblue), symbolLayerIconFeatureList,
                                symbols, s);
                        System.out.println(gplfilter.size());
                        gplfilter=new HashMap<>();
                    }else if(!Q8filter.isEmpty()){
                        utils.stationfilterMetanoGPL(Q8filter, gplfilter, metanofilter,"Q8",
                                "Metano", getResources().getColor(R.color.greenblue), symbolLayerIconFeatureList,
                                symbols, s);
                        gplfilter=new HashMap<>();
                    }else if(!tamoilfilter.isEmpty()){
                        utils.stationfilterMetanoGPL(tamoilfilter, gplfilter, metanofilter,"Tamoil",
                                "Metano", getResources().getColor(R.color.greenblue), symbolLayerIconFeatureList,
                                symbols, s);
                        gplfilter=new HashMap<>();
                    }else {

                        for (int i = 0; i < symbolLayerIconFeatureList.size(); i++) {
                            if (symbolLayerIconFeatureList.get(i).getStringProperty("Metano") != null) {
                                if (gplfilter.containsKey(i)) {
                                    symbols.get(i).setIconImage(gplfilter.get(i).get(1));
                                }
                                symbols.get(i).setTextField(symbolLayerIconFeatureList.get(i).getStringProperty("Metano"));
                                symbols.get(i).setTextColor(getResources().getColor(R.color.greenblue));
                                s.add(symbols.get(i));
                            } else {
                                List<String> lista = new ArrayList<>();
                                lista.add(symbols.get(i).getTextField());
                                if (gplfilter.containsKey(i)) {
                                    lista.add(gplfilter.get(i).get(1));
                                } else {
                                    lista.add(symbols.get(i).getIconImage());
                                }
                                symbols.get(i).setTextField("");
                                symbols.get(i).setIconImage("");
                                metanofilter.put(i, lista);
                                s.add(symbols.get(i));
                            }
                        }
                        gplfilter = new HashMap<>();
                    }
                }else {
                    if (!agipfilter.isEmpty()) {
                        utils.stationfilterMetanoGPL2(agipfilter, metanofilter,
                                "Metano", getResources().getColor(R.color.greenblue), symbolLayerIconFeatureList,
                                symbols, s);
                    } else if (!Q8filter.isEmpty()) {
                        utils.stationfilterMetanoGPL2(Q8filter, metanofilter,
                                "Metano", getResources().getColor(R.color.greenblue), symbolLayerIconFeatureList,
                                symbols, s);
                    } else if (!tamoilfilter.isEmpty()) {
                        utils.stationfilterMetanoGPL2(tamoilfilter, metanofilter,
                                "Metano", getResources().getColor(R.color.greenblue), symbolLayerIconFeatureList,
                                symbols, s);
                    } else {

                        for (int i = 0; i < symbolLayerIconFeatureList.size(); i++) {
                            if (symbolLayerIconFeatureList.get(i).getStringProperty("Metano") != null) {
                                symbols.get(i).setTextField(symbolLayerIconFeatureList.get(i).getStringProperty("Metano"));
                                symbols.get(i).setTextColor(getResources().getColor(R.color.greenblue));
                                s.add(symbols.get(i));
                            } else {
                                List<String> lista = new ArrayList<>();
                                lista.add(symbols.get(i).getTextField());
                                if(symbols.get(i).getIconImage().equals("")){
                                    System.out.println(i);
                                }
                                lista.add(symbols.get(i).getIconImage());
                                symbols.get(i).setTextField("");
                                symbols.get(i).setIconImage("");
                                metanofilter.put(i, lista);
                                s.add(symbols.get(i));
                            }
                        }


                    }
                }
                symbolManager.update(s);

                fab_gasolio.setForeground(null);
                fab_benzina.setForeground(null);
                fab_gpl.setForeground(null);
                fab_metano.setForeground(getResources().getDrawable(R.drawable.fab));
                fab_gasolio.setColorNormal(getResources().getColor(R.color.whitebright));
                fab_benzina.setColorNormal(getResources().getColor(R.color.greenbright));
                fab_gpl.setColorNormal(getResources().getColor(R.color.bluebright));
                fab_metano.setColorNormal(getResources().getColor(R.color.greenblue));
            }
        });

        fab_gpl = (FloatingActionButton) findViewById(R.id.fab_toggle_gpl);
        fab_gpl.setColorNormal(Color.BLUE);
        fab_gpl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Symbol> s= new ArrayList<>();
                if (!benzinafilter.isEmpty()){
                    if(!agipfilter.isEmpty()){
                        utils.stationfilterMetanoGPL(agipfilter, benzinafilter, gplfilter,"Agip Eni",
                                "GPL", Color.BLUE, symbolLayerIconFeatureList,
                                symbols, s);
                        benzinafilter=new HashMap<>();
                    }else if(!Q8filter.isEmpty()){
                        utils.stationfilterMetanoGPL(Q8filter, benzinafilter, gplfilter,"Q8",
                                "GPL", Color.BLUE, symbolLayerIconFeatureList,
                                symbols, s);
                        benzinafilter=new HashMap<>();
                    }else if(!tamoilfilter.isEmpty()){
                        utils.stationfilterMetanoGPL(tamoilfilter, benzinafilter, gplfilter,"Tamoil",
                                "GPL", Color.BLUE, symbolLayerIconFeatureList,
                                symbols, s);
                        benzinafilter=new HashMap<>();
                    }else {
                        for (int i = 0; i < symbolLayerIconFeatureList.size(); i++) {
                            if (symbolLayerIconFeatureList.get(i).getStringProperty("GPL") != null) {
                                if (benzinafilter.containsKey(i)) {
                                    System.out.println("benzinafilter.get(i).get(0): "+benzinafilter.get(i).get(0));
                                    symbols.get(i).setIconImage(benzinafilter.get(i).get(0));
                                }
                                symbols.get(i).setTextField(symbolLayerIconFeatureList.get(i).getStringProperty("GPL"));
                                symbols.get(i).setTextColor(Color.BLUE);
                                s.add(symbols.get(i));
                            } else {
                                List<String> lista = new ArrayList<>();
                                lista.add(symbols.get(i).getTextField());
                                if (benzinafilter.containsKey(i)) {
                                    lista.add(benzinafilter.get(i).get(1));
                                } else {
                                    lista.add(symbols.get(i).getIconImage());
                                }
                                symbols.get(i).setTextField("");
                                symbols.get(i).setIconImage("");
                                gplfilter.put(i, lista);
                                s.add(symbols.get(i));
                            }
                        }
                        benzinafilter = new HashMap<>();
                    }
                }else if(!gasoliofilter.isEmpty()){
                    if(!agipfilter.isEmpty()){
                        utils.stationfilterMetanoGPL(agipfilter, gasoliofilter, gplfilter,"Agip Eni",
                                "GPL", Color.BLUE, symbolLayerIconFeatureList,
                                symbols, s);
                        gasoliofilter=new HashMap<>();
                    }else if(!Q8filter.isEmpty()){
                        utils.stationfilterMetanoGPL(Q8filter, gasoliofilter, gplfilter,"Q8",
                                "GPL", Color.BLUE, symbolLayerIconFeatureList,
                                symbols, s);
                        gasoliofilter=new HashMap<>();
                    }else if(!tamoilfilter.isEmpty()){
                        utils.stationfilterMetanoGPL(tamoilfilter, gasoliofilter, gplfilter,"Tamoil",
                                "GPL", Color.BLUE, symbolLayerIconFeatureList,
                                symbols, s);
                        gasoliofilter=new HashMap<>();
                    }else {

                        for (int i = 0; i < symbolLayerIconFeatureList.size(); i++) {
                            if (symbolLayerIconFeatureList.get(i).getStringProperty("GPL") != null) {
                                if (gasoliofilter.containsKey(i)) {
                                    symbols.get(i).setIconImage(gasoliofilter.get(i).get(1));
                                }
                                symbols.get(i).setTextField(symbolLayerIconFeatureList.get(i).getStringProperty("GPL"));
                                symbols.get(i).setTextColor(Color.BLUE);
                                s.add(symbols.get(i));
                            } else {
                                List<String> lista = new ArrayList<>();
                                lista.add(symbols.get(i).getTextField());
                                if (gasoliofilter.containsKey(i)) {
                                    lista.add(gasoliofilter.get(i).get(1));
                                } else {
                                    lista.add(symbols.get(i).getIconImage());
                                }
                                symbols.get(i).setTextField("");
                                symbols.get(i).setIconImage("");
                                gplfilter.put(i, lista);
                                s.add(symbols.get(i));
                            }
                        }
                        gasoliofilter = new HashMap<>();
                    }
                }else if(!metanofilter.isEmpty()){
                    if(!agipfilter.isEmpty()){
                        utils.stationfilterMetanoGPL(agipfilter, metanofilter, gplfilter,"Agip Eni",
                                "GPL", Color.BLUE, symbolLayerIconFeatureList,
                                symbols, s);
                        metanofilter=new HashMap<>();
                    }else if(!Q8filter.isEmpty()){
                        utils.stationfilterMetanoGPL(Q8filter, metanofilter, gplfilter,"Q8",
                                "GPL", Color.BLUE, symbolLayerIconFeatureList,
                                symbols, s);
                        metanofilter=new HashMap<>();
                    }else if(!tamoilfilter.isEmpty()){
                        utils.stationfilterMetanoGPL(tamoilfilter, metanofilter, gplfilter,"Tamoil",
                                "GPL", Color.BLUE, symbolLayerIconFeatureList,
                                symbols, s);
                        metanofilter=new HashMap<>();
                    }else {

                        for (int i = 0; i < symbolLayerIconFeatureList.size(); i++) {
                            if (symbolLayerIconFeatureList.get(i).getStringProperty("GPL") != null) {
                                if (metanofilter.containsKey(i)) {
                                    symbols.get(i).setIconImage(metanofilter.get(i).get(1));
                                }
                                symbols.get(i).setTextField(symbolLayerIconFeatureList.get(i).getStringProperty("GPL"));
                                symbols.get(i).setTextColor(Color.BLUE);
                                symbols.get(i).setIconImage(symbolLayerIconFeatureList.get(i).getStringProperty("icon-image"));
                                s.add(symbols.get(i));
                            } else {
                                List<String> lista = new ArrayList<>();
                                lista.add(symbols.get(i).getTextField());
                                if (metanofilter.containsKey(i)) {
                                    lista.add(metanofilter.get(i).get(1));
                                } else {
                                    lista.add(symbols.get(i).getIconImage());
                                }
                                symbols.get(i).setTextField("");
                                symbols.get(i).setIconImage("");
                                gplfilter.put(i, lista);
                                s.add(symbols.get(i));
                            }
                        }
                        metanofilter = new HashMap<>();
                    }
                }else {
                    if (!agipfilter.isEmpty()) {
                        utils.stationfilterMetanoGPL2(agipfilter, gplfilter,
                                "GPL", Color.BLUE, symbolLayerIconFeatureList,
                                symbols, s);
                    } else if (!Q8filter.isEmpty()) {
                        utils.stationfilterMetanoGPL2(Q8filter, gplfilter,
                                "GPL", Color.BLUE, symbolLayerIconFeatureList,
                                symbols, s);
                    } else if (!tamoilfilter.isEmpty()) {
                        utils.stationfilterMetanoGPL2(tamoilfilter, gplfilter,
                                "GPL", Color.BLUE, symbolLayerIconFeatureList,
                                symbols, s);
                    } else {
                        for (int i = 0; i < symbolLayerIconFeatureList.size(); i++) {
                            if (symbolLayerIconFeatureList.get(i).getStringProperty("GPL") != null) {
                                symbols.get(i).setTextField(symbolLayerIconFeatureList.get(i).getStringProperty("GPL"));
                                symbols.get(i).setTextColor(Color.BLUE);
                                s.add(symbols.get(i));
                            } else {
                                List<String> lista = new ArrayList<>();
                                lista.add(symbols.get(i).getTextField());
                                lista.add(symbols.get(i).getIconImage());
                                symbols.get(i).setTextField("");
                                symbols.get(i).setIconImage("");
                                gplfilter.put(i, lista);
                                s.add(symbols.get(i));
                            }
                        }
                    }
                }


                symbolManager.update(s);

                fab_gasolio.setForeground(null);
                fab_metano.setForeground(null);
                fab_benzina.setForeground(null);
                fab_gpl.setForeground(getResources().getDrawable(R.drawable.fab));
                fab_gasolio.setColorNormal(getResources().getColor(R.color.whitebright));
                fab_metano.setColorNormal(getResources().getColor(R.color.greenbluebright));
                fab_benzina.setColorNormal(getResources().getColor(R.color.greenbright));
                fab_gpl.setColorNormal(Color.BLUE);
            }
        });
    }

    private void fetchLocation() {

        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(
                MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)
                + ContextCompat.checkSelfPermission(
                MainActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            // Do something, when permissions not granted
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    MainActivity.this,Manifest.permission.CAMERA)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    MainActivity.this,Manifest.permission.READ_CONTACTS)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                // If we should give explanation of requested permissions

                // Show an alert dialog here with request explanation
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Location, Read and Write External" +
                        " Storage permissions are required to do the task.");
                builder.setTitle("Please grant those permissions");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(
                                MainActivity.this,
                                new String[]{
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                },
                                MY_PERMISSIONS_REQUEST_ACCESS
                        );
                    }
                });
                builder.setNeutralButton("Cancel",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                // Directly request for required permissions, without explanation
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        },
                        MY_PERMISSIONS_REQUEST_ACCESS
                );
            }
        }else {
            // Do something, when permissions are already granted
            Toast.makeText(MainActivity.this,"Permissions already granted",Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(this, "No Internet connection!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
    


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void readCSVfile() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child("fuelstation");
        //reference.removeValue();
        InputStream is = getResources().openRawResource(R.raw.prezzo_alle_8);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        String line = "";
        try {
            int count = 0;
            ArrayList<String> carburanti = new ArrayList<>();
            HashMap<String, Integer> prezzocarburanti = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                //split by ';'
                Log.d("MainActivity", "Line: " + line);
                String[] tokens = line.split(";");
                if (count >= 2) {
                 /*
                    if(!carburanti.contains(tokens[1])){
                        carburanti.add(tokens[1]);
                        prezzocarburanti.put(tokens[1], 0);
                    }
                    prezzocarburanti.put(tokens[1], prezzocarburanti.get(tokens[1])+1);
                   */
                    //FeulStation station = new FeulStation();
                    //station.setIdImpianto(tokens[0]);
                    //station.setDescCarburante(tokens[1]);
                    //station.setPrezzo(tokens[2]);
                    //station.setIsSelf(tokens[3]);
                    //station.setDtComu(tokens[4]);
                    //Log.d("MainActivity", "Station: "+station.toString());

                    if(tokens[1].equals("Metano") && tokens[3].equals("0")){
                        DatabaseReference ref2 = reference.child(tokens[0]).getRef();
                        DatabaseReference ref3 = ref2.child("Carburante Servito").getRef();
                        ref3.child("Metano").setValue(tokens[2]);
                        Thread.sleep(100);
                    }

                    //DatabaseReference ref2 = reference.child(tokens[0]).getRef();

                    //ref2.child("Bandiera").setValue(tokens[2]);
                    //ref2.child("Gestore").setValue(tokens[1]);
                    //ref2.child("Indirizzo").setValue(tokens[5]);
                    //System.out.println("Indirizzo: " +tokens[5]);
                    //ref2.child("Nome Impianto").setValue(tokens[5]);
                    //ref2.child("Longitudine").setValue(tokens[9]);
                    //System.out.println(tokens[0]+";\n "+tokens[1]+";\n "+tokens[2]+";\n ");
                    //ref2.child("DescCarburante").setValue(station.getDescCarburante());
                    //ref2.child("Prezzo").setValue(station.getPrezzo());
                    //ref2.child("IsSelf").setValue(station.getIsSelf());
                    //ref2.child("DtComu").setValue(station.getDtComu());
                    //Thread.sleep(50);

                    if(count%100==0){
                        System.out.println("Count is "+count);
                    }
                }
                count++;


            }
            /*
            System.out.println("Lista carburanti: "+carburanti);
            Iterator it = prezzocarburanti.entrySet().iterator();
            while (it.hasNext()) {
                HashMap.Entry pair = (HashMap.Entry)it.next();
                //System.out.println(pair.getKey() + " = " + pair.getValue()+"\n");
                //System.out.println(pair.getKey() +", ");
                it.remove(); // avoids a ConcurrentModificationException
            }
            Thread.sleep(30000);*/
        } catch (Exception e) {

        }
    }

    @Override
    public void onMapReady(@NonNull @org.jetbrains.annotations.NotNull final MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            MainActivity.setOriginLocationApriori(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
        }
        if (MainActivity.originLocationApriori == null && (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)){
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.KEY_LOCATION_CHANGED, 0, 0, this);
            MainActivity.setOriginLocationApriori(locationManager.getLastKnownLocation(LocationManager.KEY_LOCATION_CHANGED));
        }
        if (MainActivity.originLocationApriori == null && (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)){
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            MainActivity.setOriginLocationApriori(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
        }

        mapboxMap.addOnMapClickListener(this);
        FirebaseDatabase.getInstance().getReference().child("fuelstation")
                .addListenerForSingleValueEvent(new ValueEventListener() {


                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int count=0;
                        markers = new HashMap<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            FuelStationLngLat fuelStation = snapshot.getValue(FuelStationLngLat.class);

                            if ((!fuelStation.getLatitudine().equals("NULL")) && (!fuelStation.getLongitudine().equals("NULL"))) {
                                if(originLocationApriori != null) {
                                    if (Double.valueOf(fuelStation.getLatitudine()) <= originLocationApriori.getLatitude() + 0.2 && Double.valueOf(fuelStation.getLatitudine()) >= originLocationApriori.getLatitude() - 0.2) {  //44.4724 N 42.3623295 S
                                        if (Double.valueOf(fuelStation.getLongitudine()) <= originLocationApriori.getLongitude() + 0.2 && Double.valueOf(fuelStation.getLongitudine()) >= originLocationApriori.getLongitude() - 0.2) {//12.3745984 E 9.6867561 O
                                            symbolLayerIconFeatureList.add(count, Feature.fromGeometry(
                                                    Point.fromLngLat(Double.valueOf(fuelStation.getLongitudine()), Double.valueOf(fuelStation.getLatitudine()))));
                                            String key = fuelStation.getLongitudine() + " " + fuelStation.getLatitudine();
                                            markers.put(key, "yes");
                                            if (!fuelStation.getBandiera().equals("NULL")) {
                                                System.out.println(fuelStation.getBandiera());
                                                JsonArray data = new JsonArray();
                                                JsonArray data1 = new JsonArray();
                                                try {
                                                    data1.add(fuelStation.getBandiera());
                                                    JsonObject o = new JsonObject();
                                                    o.add("Bandiera", data1);
                                                    data.add(o);
                                                } catch (Exception e) {

                                                }
                                                symbolLayerIconFeatureList.get(count).properties().addProperty("Bandiera", fuelStation.getBandiera());

                                            }
                                            if (!fuelStation.getNomeImpianto().equals("NULL")) {
                                                System.out.println("Nome Impianto: " + fuelStation.getNomeImpianto());
                                                symbolLayerIconFeatureList.get(count).properties().addProperty("NomeImpianto", fuelStation.getNomeImpianto());

                                            }
                                            if (!fuelStation.getGestore().equals("NULL")) {
                                                System.out.println("Gestore: " + fuelStation.getGestore());
                                                symbolLayerIconFeatureList.get(count).properties().addProperty("Gestore", fuelStation.getGestore());
                                            }
                                            if (fuelStation.getCarburanteSelf() != null) {
                                                if (fuelStation.getCarburanteSelf().containsKey("Benzina")) {
                                                    System.out.println("Benzina" + fuelStation.getCarburanteSelf().get("Benzina"));
                                                    symbolLayerIconFeatureList.get(count).properties().addProperty("BenzinaSelf", fuelStation.getCarburanteSelf().get("Benzina"));
                                                }
                                                if (fuelStation.getCarburanteSelf().containsKey("Gasolio")) {
                                                    System.out.println("Gasolio" + fuelStation.getCarburanteSelf().get("Gasolio"));
                                                    symbolLayerIconFeatureList.get(count).properties().addProperty("GasolioSelf", fuelStation.getCarburanteSelf().get("Gasolio"));
                                                }
                                            }
                                            if (fuelStation.getCarburanteServito() != null) {
                                                if (fuelStation.getCarburanteServito().containsKey("Benzina")) {
                                                    System.out.println("Benzina Servita:" + fuelStation.getCarburanteServito().get("Benzina"));
                                                    symbolLayerIconFeatureList.get(count).properties().addProperty("BenzinaServita", fuelStation.getCarburanteServito().get("Benzina"));
                                                }
                                                if (fuelStation.getCarburanteServito().containsKey("Gasolio")) {
                                                    System.out.println("Gasolio Servito:" + fuelStation.getCarburanteServito().get("Gasolio"));
                                                    symbolLayerIconFeatureList.get(count).properties().addProperty("GasolioServito", fuelStation.getCarburanteServito().get("Gasolio"));
                                                }
                                                if (fuelStation.getCarburanteServito().containsKey("Metano")) {
                                                    System.out.println("Metano:" + fuelStation.getCarburanteServito().get("Metano"));
                                                    symbolLayerIconFeatureList.get(count).properties().addProperty("Metano", fuelStation.getCarburanteServito().get("Metano"));
                                                }
                                                if (fuelStation.getCarburanteServito().containsKey("GPL")) {
                                                    System.out.println("GPL:" + fuelStation.getCarburanteServito().get("GPL"));
                                                    symbolLayerIconFeatureList.get(count).properties().addProperty("GPL", fuelStation.getCarburanteServito().get("GPL"));
                                                }
                                            }
                                            count++;
                                        }
                                    }
                                }else{//al momento dell'installazione i permessi non sono stati ancora accettati. Carico tutti i distributori della toscana
                                    if (Double.valueOf(fuelStation.getLatitudine()) <= 44.4724 && Double.valueOf(fuelStation.getLatitudine()) >= 42.3623295) {  //44.4724 N 42.3623295 S
                                        if (Double.valueOf(fuelStation.getLongitudine()) <= 12.3745984 && Double.valueOf(fuelStation.getLongitudine()) >= 9.6867561) {//12.3745984 E 9.6867561 O
                                            symbolLayerIconFeatureList.add(count, Feature.fromGeometry(
                                                    Point.fromLngLat(Double.valueOf(fuelStation.getLongitudine()), Double.valueOf(fuelStation.getLatitudine()))));
                                            String key = fuelStation.getLongitudine() + " " + fuelStation.getLatitudine();
                                            markers.put(key, "yes");
                                            if (!fuelStation.getBandiera().equals("NULL")) {
                                                System.out.println(fuelStation.getBandiera());
                                                JsonArray data = new JsonArray();
                                                JsonArray data1 = new JsonArray();
                                                try {
                                                    data1.add(fuelStation.getBandiera());
                                                    JsonObject o = new JsonObject();
                                                    o.add("Bandiera", data1);
                                                    data.add(o);
                                                } catch (Exception e) {

                                                }
                                                symbolLayerIconFeatureList.get(count).properties().addProperty("Bandiera", fuelStation.getBandiera());

                                            }
                                            if (!fuelStation.getNomeImpianto().equals("NULL")) {
                                                System.out.println("Nome Impianto: " + fuelStation.getNomeImpianto());
                                                symbolLayerIconFeatureList.get(count).properties().addProperty("NomeImpianto", fuelStation.getNomeImpianto());

                                            }
                                            if (!fuelStation.getGestore().equals("NULL")) {
                                                System.out.println("Gestore: " + fuelStation.getGestore());
                                                symbolLayerIconFeatureList.get(count).properties().addProperty("Gestore", fuelStation.getGestore());
                                            }
                                            if (fuelStation.getCarburanteSelf() != null) {
                                                if (fuelStation.getCarburanteSelf().containsKey("Benzina")) {
                                                    System.out.println("Benzina" + fuelStation.getCarburanteSelf().get("Benzina"));
                                                    symbolLayerIconFeatureList.get(count).properties().addProperty("BenzinaSelf", fuelStation.getCarburanteSelf().get("Benzina"));
                                                }
                                                if (fuelStation.getCarburanteSelf().containsKey("Gasolio")) {
                                                    System.out.println("Gasolio" + fuelStation.getCarburanteSelf().get("Gasolio"));
                                                    symbolLayerIconFeatureList.get(count).properties().addProperty("GasolioSelf", fuelStation.getCarburanteSelf().get("Gasolio"));
                                                }
                                            }
                                            if (fuelStation.getCarburanteServito() != null) {
                                                if (fuelStation.getCarburanteServito().containsKey("Benzina")) {
                                                    System.out.println("Benzina Servita:" + fuelStation.getCarburanteServito().get("Benzina"));
                                                    symbolLayerIconFeatureList.get(count).properties().addProperty("BenzinaServita", fuelStation.getCarburanteServito().get("Benzina"));
                                                }
                                                if (fuelStation.getCarburanteServito().containsKey("Gasolio")) {
                                                    System.out.println("Gasolio Servito:" + fuelStation.getCarburanteServito().get("Gasolio"));
                                                    symbolLayerIconFeatureList.get(count).properties().addProperty("GasolioServito", fuelStation.getCarburanteServito().get("Gasolio"));
                                                }
                                                if (fuelStation.getCarburanteServito().containsKey("Metano")) {
                                                    System.out.println("Metano:" + fuelStation.getCarburanteServito().get("Metano"));
                                                    symbolLayerIconFeatureList.get(count).properties().addProperty("Metano", fuelStation.getCarburanteServito().get("Metano"));
                                                }
                                                if (fuelStation.getCarburanteServito().containsKey("GPL")) {
                                                    System.out.println("GPL:" + fuelStation.getCarburanteServito().get("GPL"));
                                                    symbolLayerIconFeatureList.get(count).properties().addProperty("GPL", fuelStation.getCarburanteServito().get("GPL"));
                                                }
                                            }
                                            count++;
                                        }
                                    }
                                }
                            }


                            System.out.println(fuelStation.getLongitudine());
                            System.out.println(fuelStation.getLatitudine());
                            System.out.println("oi"+fuelStation.getCarburanteSelf());
                            System.out.println("oi"+fuelStation.getCarburanteServito());
                            System.out.println(fuelStation.getNomeImpianto());
                        }

                        // Adding a GeoJson source for the SymbolLayer icons.
                        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.map_marker_yellow, null);
                        Drawable drawable2 = ResourcesCompat.getDrawable(getResources(), R.drawable.map_marker_blue, null);
                        Drawable drawable3 = ResourcesCompat.getDrawable(getResources(), R.drawable.map_marker_red, null);
                        Drawable drawable4 = ResourcesCompat.getDrawable(getResources(), R.drawable.map_marker_white, null);
                        Bitmap mBitmap = BitmapUtils.getBitmapFromDrawable(drawable);
                        Bitmap mBitmap2 = BitmapUtils.getBitmapFromDrawable(drawable2);
                        Bitmap mBitmap3 = BitmapUtils.getBitmapFromDrawable(drawable3);
                        Bitmap mBitmap4 = BitmapUtils.getBitmapFromDrawable(drawable4);


                        mapboxMap.setStyle(new Style.Builder()
                                        .fromUri("mapbox://styles/mapbox/streets-v11")
                                        .withSource(new GeoJsonSource(SOURCE_ID,
                                               FeatureCollection.fromFeatures(symbolLayerIconFeatureList)))
                                        .withImage(ICON_ID, mBitmap)
                                        .withImage(ICON_ID_2, mBitmap2)
                                        .withImage(ICON_ID_3, mBitmap3)
                                        .withImage(ICON_ID_4, mBitmap4)



                                // Adding the actual SymbolLayer to the map style. An offset is added that the bottom of the red
                                        // marker icon gets fixed to the coordinate, rather than the middle of the icon being fixed to
                                        // the coordinate point. This is offset is not always needed and is dependent on the image
                                        // that you use for the SymbolLayer icon.
                                , new Style.OnStyleLoaded() {
                                    @Override
                                    public void onStyleLoaded(@NonNull Style style) {

                                        symbolManager = new SymbolManager(mapView, mapboxMap, style);

                                        symbolManager.setIconAllowOverlap(true);// settato a true consente la visualizzazione di tutte le icone seppur sovrapposte
                                        symbolManager.setTextAllowOverlap(true);
                                        // Set up a SymbolManager instance

                                        Float[] a= new Float[2];
                                        a[0]=2f;
                                        a[1]=2f;
                                        SymbolOptions s=new SymbolOptions()
                                                .withLatLng(new LatLng(0,0))
                                                .withIconImage(ICON_ID)
                                                .withIconSize(1.0f)
                                                .withTextField("oi")
                                                .withTextOffset(a)
                                                .withDraggable(true);



                                        for(int j=0; j<symbolLayerIconFeatureList.size(); j++) {
                                            symbolLayerIconFeatureList.get(j).properties().addProperty("is-draggable", false);
                                            symbolLayerIconFeatureList.get(j).properties().addProperty("icon-size", 1.0f);
                                            if(symbolLayerIconFeatureList.get(j).getStringProperty("Bandiera").equals("Agip Eni")){
                                                symbolLayerIconFeatureList.get(j).properties().addProperty("icon-image", ICON_ID);
                                            }
                                            else if(symbolLayerIconFeatureList.get(j).getStringProperty("Bandiera").equals("Q8")){
                                                symbolLayerIconFeatureList.get(j).properties().addProperty("icon-image", ICON_ID_2);
                                                //symbolLayerIconFeatureList.get(j).properties().addProperty("is-visible", "");

                                            }
                                            else if(symbolLayerIconFeatureList.get(j).getStringProperty("Bandiera").equals("Tamoil")){
                                                symbolLayerIconFeatureList.get(j).properties().addProperty("icon-image", ICON_ID_3);
                                            }else{
                                                symbolLayerIconFeatureList.get(j).properties().addProperty("icon-image", ICON_ID_4);
                                            }

                                            //serve per settare valori sul ping del simbolo
                                            String text_offset=symbolLayerIconFeatureList.get(j).getStringProperty("BenzinaSelf");
                                            if (text_offset==null){
                                                symbolLayerIconFeatureList.get(j).getStringProperty("BenzinaServita");
                                            }
                                            if (text_offset==null){
                                                text_offset="no value";
                                            }
                                            symbolLayerIconFeatureList.get(j).properties().addProperty("text-field", text_offset);
                                            JsonArray array = new JsonArray();
                                            try {
                                                JsonArray a2=new JsonArray();
                                                a2.add(0);
                                                a2.add(-1.8f);
                                                array.addAll(a2);

                                            } catch (Exception e) {

                                            }

                                            String[] listanchor  = new String[4];
                                            listanchor[0]=Property.TEXT_ANCHOR_TOP;
                                            listanchor[1]=Property.TEXT_ANCHOR_BOTTOM;
                                            listanchor[2]=Property.TEXT_ANCHOR_LEFT;
                                            listanchor[3]=Property.TEXT_ANCHOR_RIGHT;
                                            System.out.println();
                                            symbolLayerIconFeatureList.get(j).addProperty("text-offset", (JsonElement) array );
                                        }

                                        System.out.println("text-offset: "+symbolLayerIconFeatureList.get(1).properties());
                                        symbols=symbolManager.create(
                                               FeatureCollection.fromFeatures(symbolLayerIconFeatureList));
                                        for (int i=0; i<symbols.size(); i++){
                                            symbols.get(i).setTextHaloWidth(0.3f);
                                            symbols.get(i).setTextHaloColor(Color.BLACK);
                                            JsonArray data = new JsonArray();
                                            JsonArray data1 = new JsonArray();
                                            JsonArray data2 = new JsonArray();
                                            JsonArray data3 = new JsonArray();
                                            JsonArray data4 = new JsonArray();
                                            JsonArray data5 = new JsonArray();
                                            JsonArray data6 = new JsonArray();
                                            JsonArray data7 = new JsonArray();
                                            JsonArray data8 = new JsonArray();
                                            JsonArray data9 = new JsonArray();
                                            try {
                                                data1.add(symbolLayerIconFeatureList.get(i).getStringProperty("Bandiera"));
                                                data2.add(symbolLayerIconFeatureList.get(i).getStringProperty("NomeImpianto"));
                                                data3.add(symbolLayerIconFeatureList.get(i).getStringProperty("Gestore"));
                                                data4.add(symbolLayerIconFeatureList.get(i).getStringProperty("BenzinaSelf"));
                                                data5.add(symbolLayerIconFeatureList.get(i).getStringProperty("BenzinaServita"));
                                                data6.add(symbolLayerIconFeatureList.get(i).getStringProperty("GasolioSelf"));
                                                data7.add(symbolLayerIconFeatureList.get(i).getStringProperty("GasolioServito"));
                                                data8.add(symbolLayerIconFeatureList.get(i).getStringProperty("Metano"));
                                                data9.add(symbolLayerIconFeatureList.get(i).getStringProperty("GPL"));
                                                JsonObject o=new JsonObject();
                                                o.add("Bandiera", data1);
                                                o.add("NomeImpianto", data2);
                                                o.add("Gestore", data3);
                                                o.add("BenzinaSelf", data4);
                                                o.add("BenzinaServita", data5);
                                                o.add("GasolioSelf", data6);
                                                o.add("GasolioServito", data7);
                                                o.add("Metano", data8);
                                                o.add("GPL", data9);
                                                data.add(o);
                                            }catch (Exception e){

                                            }
                                            symbols.get(i).setData(data);
                                            LatLng featurelatlang=new LatLng(((Point)symbolLayerIconFeatureList.get(i).geometry()).coordinates().get(1),
                                                    ((Point)symbolLayerIconFeatureList.get(i).geometry()).coordinates().get(0));
                                            LatLng symbollatlang = symbols.get(i).getLatLng();
                                            if(symbollatlang.equals(featurelatlang)){
                                            }else{
                                                System.out.println("FALSE");
                                            }

                                        }
                                        symbolManager.addClickListener(new OnSymbolClickListener() {
                                            @Override
                                            public void onAnnotationClick(Symbol symbol){
                                                symbolIsClicked =true;
                                                startButton.setEnabled(false);
                                                for(int i=0; i<symbols.size(); i++){
                                                    if(symbol.equals(symbols.get(i))){
                                                        int key = i;
                                                        break;
                                                    }
                                                }
                                                symbol.setIconSize(1.5f);
                                                symbol.setTextOffset(new PointF(0.0f, -2.2f));
                                                symbolManager.update(symbol);
                                                System.out.println("currentroutes "+currentRoutes);
                                                System.out.println("navgiatio: " +navigationMapRoute);
                                                int key=symbolManager.getAnnotations().indexOfValue(symbol);
                                                System.out.println("key: "+key);
                                                mapboxMap.removeMarker(destinationMarker);
                                                Intent i = new Intent(getApplicationContext(), PopActivity.class);
                                                Bundle bundle = new Bundle();

                                                bundle.putString("Bandiera", (((JsonObject)symbol.getData().getAsJsonArray().get(0)).get("Bandiera")).getAsJsonArray().get(0).toString());
                                                bundle.putString("NomeImpianto", (((JsonObject)symbol.getData().getAsJsonArray().get(0)).get("NomeImpianto")).getAsJsonArray().get(0).toString());
                                                bundle.putString("Gestore", (((JsonObject)symbol.getData().getAsJsonArray().get(0)).get("Gestore")).getAsJsonArray().get(0).toString());
                                                bundle.putString("BenzinaSelf",(((JsonObject)symbol.getData().getAsJsonArray().get(0)).get("BenzinaSelf")).getAsJsonArray().get(0).toString());
                                                bundle.putString("BenzinaServita",(((JsonObject)symbol.getData().getAsJsonArray().get(0)).get("BenzinaServita")).getAsJsonArray().get(0).toString());
                                                bundle.putString("GasolioSelf",(((JsonObject)symbol.getData().getAsJsonArray().get(0)).get("GasolioSelf")).getAsJsonArray().get(0).toString());
                                                bundle.putString("GasolioServito",(((JsonObject)symbol.getData().getAsJsonArray().get(0)).get("GasolioServito")).getAsJsonArray().get(0).toString());
                                                bundle.putString("Metano",(((JsonObject)symbol.getData().getAsJsonArray().get(0)).get("Metano")).getAsJsonArray().get(0).toString());
                                                bundle.putString("GPL",(((JsonObject)symbol.getData().getAsJsonArray().get(0)).get("GPL")).getAsJsonArray().get(0).toString());
                                                bundle.putInt("SymbolKey", key);
                                                i.putExtras(bundle);
                                                System.out.println(navigationMapRoute);
                                                CameraPosition p = new CameraPosition.Builder()
                                                        .target(symbol.getLatLng())
                                                        .build();
                                                Projection projection = mapboxMap.getProjection();
                                                System.out.println("eila "+mapView.getHeight());

                                                //105 dimensione testata display, 897 altezza della parte di mappa non coperta dal popup

                                                PointF markerScreenPosition = projection.toScreenLocation(symbol.getLatLng());
                                                PointF pointHalfScreenAbove = new PointF(markerScreenPosition.x, (markerScreenPosition.y - (105 / 2)) + (897/2));

                                                LatLng aboveMarkerLatLng = projection.fromScreenLocation(pointHalfScreenAbove);
                                                CameraPosition p2 = new CameraPosition.Builder()
                                                        .target(aboveMarkerLatLng)
                                                        .build();

                                                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(p2), 10);
                                                i.putExtra("latlang", symbol.getLatLng());
                                                startActivity(i);
                                                /*
                                                Toast.makeText(MainActivity.this,
                                                        getString(R.string.clicked_symbol_toast), Toast.LENGTH_SHORT).show();
                                                symbolManager.update(symbol);*/
                                            }
                                        });

                                        // Add long click listener and change the symbol to an airport icon on long click
                                        symbolManager.addLongClickListener((new OnSymbolLongClickListener() {
                                            @Override
                                            public void onAnnotationLongClick(Symbol symbol) {
                                                Toast.makeText(MainActivity.this,
                                                        getString(R.string.long_clicked_symbol_toast), Toast.LENGTH_SHORT).show();
                                                //symbol.setIconImage(MAKI_ICON_AIRPORT);
                                                symbolManager.update(symbol);
                                            }
                                        }));

                                        symbolManager.addDragListener(new OnSymbolDragListener() {
                                            @Override
                                            // Left empty on purpose
                                            public void onAnnotationDragStarted(Symbol annotation) {
                                            }

                                            @Override
                                            // Left empty on purpose
                                            public void onAnnotationDrag(Symbol symbol) {
                                            }

                                            @Override
                                            // Left empty on purpose
                                            public void onAnnotationDragFinished(Symbol annotation) {
                                            }
                                        });
                                        Toast.makeText(MainActivity.this,
                                                getString(R.string.project_id), Toast.LENGTH_SHORT).show();

                                        enableLocationComponent(style);


                                        // Map is set up and the style has loaded. Now you can add additional data or make other map adjustments.


                                    }
                                });

                        mapboxMap.setInfoWindowAdapter(new MapboxMap.InfoWindowAdapter() {
                            @Nullable
                            @Override
                            public View getInfoWindow(@NonNull Marker marker) {
                                return null;

                                // return the view which includes the button

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }

                });


    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
                originLocationApriori = location;
    }



    public static Context getContext() {
        return context;
    }

    private static class MainActivityLocationCallback
            implements Serializable, LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<MainActivity> activityWeakReference;

        MainActivityLocationCallback(MainActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location has changed.
         *
         * @param result the LocationEngineResult object which has the last known location within it.
         */
        @Override
        public void onSuccess(LocationEngineResult result) {
            MainActivity activity = activityWeakReference.get();
            MainActivity.setOriginLocationApriori(result.getLastLocation());
            if (activity != null) {
                activity.originLocation = result.getLastLocation();

                if (activity.originLocation == null) {
                    return;
                }
// Pass the new location to the Maps SDK's LocationComponent
                if (activity.mapboxMap != null && result.getLastLocation() != null) {
                    activity.mapboxMap.getLocationComponent().forceLocationUpdate(result.getLastLocation());
                }
            }
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location can not be captured
         *
         * @param exception the exception message
         */
        @Override
        public void onFailure(@NonNull Exception exception) {
            Log.d("LocationChangeActivity", exception.getLocalizedMessage());
            MainActivity activity = activityWeakReference.get();
            if (activity != null) {
                //Toast.makeText(activity, exception.getLocalizedMessage(),
           //             Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
        mapView.onDestroy();
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            if (mapboxMap.getStyle() != null) {
                enableLocationComponent(mapboxMap.getStyle());
            }
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  //      permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS: {
                // Your code here
                if(
                        (grantResults.length >0) &&
                                (grantResults[0]
                                        + grantResults[1]
                                        + grantResults[2]
                                        == PackageManager.PERMISSION_GRANTED
                                )
                ){
                    // Permissions are granted
                    Toast.makeText(MainActivity.this,"Permissions granted.",Toast.LENGTH_SHORT).show();
                }else {
                    // Permissions are denied
                    Toast.makeText(MainActivity.getContext(),"Permissions denied.",Toast.LENGTH_SHORT).show();
                }
                return;

            }

        }

    }

    @Override
    public boolean onMapClick(@NonNull @NotNull LatLng point) {
        if (destinationMarker != null) {
            mapboxMap.removeMarker(destinationMarker);
        }
        String coordinates=String.valueOf(point.getLatitude())+" "+String.valueOf(point.getLongitude());
        if(!markers.containsKey(coordinates)){
            destinationMarker = mapboxMap.addMarker(new MarkerOptions().position(point));
        }
        destinationPosition = Point.fromLngLat(point.getLongitude(), point.getLatitude());
        originePosition = Point.fromLngLat(originLocation.getLongitude(), originLocation.getLatitude());
        getRoute(originePosition, destinationPosition);
        return false;
    }

    public boolean onMapClickWithSymbol(@NonNull @NotNull LatLng point){
        if (destinationMarker != null) {
            mapboxMap.removeMarker(destinationMarker);
        }
        String coordinates=String.valueOf(point.getLatitude())+" "+String.valueOf(point.getLongitude());
        if(!markers.containsKey(coordinates)){
            destinationMarker = mapboxMap.addMarker(new MarkerOptions().position(point));
        }
        destinationPosition = Point.fromLngLat(point.getLongitude(), point.getLatitude());
        originePosition = Point.fromLngLat(originLocation.getLongitude(), originLocation.getLatitude());
        getRouteSymbol(originePosition, destinationPosition);
        startButton.setEnabled(false);
        return false;
    }

    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if (response.body() == null) {
                            Log.e(TAG, "Not routes found, check right user and access token");
                            return;
                        } else if (response.body().routes().size() == 0) {
                            Log.e(TAG, "Not routes found");
                            return;
                        }
                        if (symbolIsClicked ==true){
                            if (navigationMapRoute != null) {
                                navigationMapRoute.removeRoute();
                            } else {
                                navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap);
                            }
                            currentRoutes=null;
                            return;
                        }else {


                            currentRoutes = response.body().routes().get(0);
                            if (navigationMapRoute != null) {
                                navigationMapRoute.removeRoute();
                            } else {
                                navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap);
                            }
                            navigationMapRoute.addRoute(currentRoutes);
                            startButton.setEnabled(true);
                            startButton.setBackgroundResource(R.color.mapbox_blue);
                        }

                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Log.e(TAG, "Error: " + t.getMessage());
                    }
                });
    }

    private void getRouteSymbol(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        if (response.body() == null) {
                            Log.e(TAG, "Not routes found, check right user and access token");
                            return;
                        } else if (response.body().routes().size() == 0) {
                            Log.e(TAG, "Not routes found");
                            return;
                        }
                        currentRoutes = response.body().routes().get(0);
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, mapboxMap);
                        }
                        navigationMapRoute.addRoute(currentRoutes);


                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                        Log.e(TAG, "Error: " + t.getMessage());
                    }
                });
    }

    /**
     * Initialize the Maps SDK's LocationComponent
     */
    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();

            // Set the LocationComponent activation options
            LocationComponentActivationOptions locationComponentActivationOptions =
                    LocationComponentActivationOptions.builder(this, loadedMapStyle)
                            .useDefaultLocationEngine(false)
                            .build();

            // Activate with the LocationComponentActivationOptions object
            locationComponent.activateLocationComponent(locationComponentActivationOptions);

            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);

            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);

            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);

            initLocationEngine();

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    /**
     * Set up the LocationEngine and the parameters for querying the device's location
     */
    @SuppressLint("MissingPermission")
    private void initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(this);

        LocationEngineRequest request = new LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build();

        locationEngine.requestLocationUpdates(request, callback, getMainLooper());
        locationEngine.getLastLocation(callback);
    }

}