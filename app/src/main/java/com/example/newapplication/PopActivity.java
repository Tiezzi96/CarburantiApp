package com.example.newapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;

import toan.android.floatingactionmenu.FloatingActionsMenu;

public class PopActivity extends Activity {
    private Button routeButton;
    private Button navigationButton;
    private static MainActivity m;
    private int key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop);

        TextView tv = (TextView)findViewById(R.id.text);
        TextView textNomeImpianto = (TextView) findViewById(R.id.TextNomeImpianto);
        TextView textBandiera = (TextView) findViewById(R.id.TextBandiera);
        TextView textGestore = (TextView) findViewById(R.id.TextGestore);
        TextView textBenzinaSelf = (TextView) findViewById(R.id.BenzinaSelf);
        TextView textBenzinaServita = (TextView) findViewById(R.id.BenzinaNoSelf);
        TextView textGasolioSelf = (TextView) findViewById(R.id.GasolioSelf);
        TextView textGasolioServito = (TextView) findViewById(R.id.GasolioNoSelf);
        TextView textMetano = (TextView) findViewById(R.id.MetanoNoSelf);
        TextView textGPL = (TextView) findViewById(R.id.GPLNoSelf);

        //tv.set
        Bundle b = getIntent().getExtras();
        String bandiera = b.getString("Bandiera");
        String nomeimpianto = b.getString("NomeImpianto");
        String gestore = b.getString("Gestore");
        String benzinaSelf = b.getString("BenzinaSelf");
        String benzinaServita = b.getString("BenzinaServita");
        String gasolioSelf = b.getString("GasolioSelf");
        String gasolioServito = b.getString("GasolioServito");
        String metano = b.getString("Metano");
        String gpl = b.getString("GPL");
        key = b.getInt("SymbolKey");
        /*
        tv.setText(//"\n" +
                //"Davanti a I'CIRRI di Montagnana c'è il TAMOIL BONO! ANDAECI!! \n" +
                //"\n Rubate a I'CIRRI che ci ha roba BONA ni GARAGE!\n" +
                "\n "+"Nome Impianto: "+nomeimpianto+"\n "+
                "\n "+"Bandiera: "+bandiera);
        tv.setTextColor(Color.BLACK);
         */
        textNomeImpianto.setText(nomeimpianto);
        textBandiera.setText(bandiera);
        textGestore.setText(gestore);


        if(benzinaSelf.equals("null")){
            benzinaSelf="assente";
        }
        if(benzinaServita.equals("null")){
            benzinaServita="assente";
        }
        if(gasolioSelf.equals("null")){
            gasolioSelf="assente";
        }
        if(gasolioServito.equals("null")){
            gasolioServito="assente";
        }
        if(metano.equals("null")){
            metano="assente";
        }
        if(gpl.equals("null")){
            gpl="assente";
        }
        if (benzinaSelf != null && benzinaSelf.length() >= 2
                && benzinaSelf.charAt(0) == '\"' && benzinaSelf.charAt(benzinaSelf.length() - 1) == '\"') {
            benzinaSelf = benzinaSelf.substring(1, benzinaSelf.length() - 1);
        }
        if(benzinaSelf.equals(null)){
            benzinaSelf="assente";
        }
        if (benzinaServita != null && benzinaServita.length() >= 2
                && benzinaServita.charAt(0) == '\"' && benzinaServita.charAt(benzinaServita.length() - 1) == '\"') {
            benzinaServita = benzinaServita.substring(1, benzinaServita.length() - 1);
        }
        if (gasolioSelf != null && gasolioSelf.length() >= 2
                && gasolioSelf.charAt(0) == '\"' && gasolioSelf.charAt(gasolioSelf.length() - 1) == '\"') {
            gasolioSelf = gasolioSelf.substring(1, gasolioSelf.length() - 1);
        }
        if (gasolioServito != null && gasolioServito.length() >= 2
                && gasolioServito.charAt(0) == '\"' && gasolioServito.charAt(gasolioServito.length() - 1) == '\"') {
            gasolioServito = gasolioServito.substring(1, gasolioServito.length() - 1);
        }
        if (metano != null && metano.length() >= 2
                && metano.charAt(0) == '\"' && metano.charAt(metano.length() - 1) == '\"') {
            metano = metano.substring(1, metano.length() - 1);
        }
        if (gpl != null && gpl.length() >= 2
                && gpl.charAt(0) == '\"' && gpl.charAt(gpl.length() - 1) == '\"') {
            gpl = gpl.substring(1, gpl.length() - 1);
        }

        textBenzinaSelf.setText(benzinaSelf);
        textBenzinaServita.setText(benzinaServita);
        textGasolioSelf.setText(gasolioSelf);
        textGasolioServito.setText(gasolioServito);
        textMetano.setText(metano);
        textGPL.setText(gpl);
        routeButton = findViewById(R.id.buttonRoute);
        navigationButton = findViewById(R.id.buttonStart);
        ConstraintLayout layout= findViewById(R.id.pop_layout);
        ViewGroup.LayoutParams params_pop = layout.getLayoutParams();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        System.out.println("Height is "+height);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.x = -20;
        params.height = dm.heightPixels/2;
        params.width = dm.widthPixels;
        params.y = dm.heightPixels/2;
        params_pop.width = dm.widthPixels;
        params_pop.height = dm.heightPixels/2;
//        layout.setLayoutParams(params_pop);

 //       this.getWindow().setAttributes(params);
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //getWindow().setLayout((int)(width), (int)(height/2));
        CommonModelClass commonModelClass = CommonModelClass.getSingletonObject();
        m = commonModelClass.getbaseActivity();
        //m.getNavigationMapRoute().removeRoute();
        routeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m.onMapClickWithSymbol(getIntent().getParcelableExtra("latlang"));
                m.getMapboxMap().removeMarker(m.getDestinationMarker());
                routeButton.setEnabled(false);
                routeButton.setVisibility(View.INVISIBLE);
                navigationButton.setVisibility(View.VISIBLE);
                navigationButton.setEnabled(true);
                //m.setSymbolIsClicked(false);

            }

        });

        routeButton.setEnabled(true);
        routeButton.setVisibility(View.VISIBLE);
        ConstraintLayout.LayoutParams lp =(ConstraintLayout.LayoutParams) routeButton.getLayoutParams();
        final float scale = getBaseContext().getResources().getDisplayMetrics().density;
        //lp.setMargins((int)((float)(dm.widthPixels)/4*scale+0.5f), (int)((float)(dm.heightPixels)/7.5*scale+0.5f), (int)(10*scale*0.5f), 0);
        //lp.setMargins((int)((float)(dm.widthPixels)/4*scale+0.5f), (int)((float)(dm.heightPixels)/7.5*scale+0.5f), (int)(10*scale*0.5f), 0);
        routeButton.setBackgroundResource(R.color.mapbox_navigation_route_layer_blue);
        System.out.println((float)(dm.widthPixels)/300+" , "+(float)(dm.heightPixels)/240);
        navigationButton.setVisibility(View.INVISIBLE);
        ConstraintLayout.LayoutParams lp2 =(ConstraintLayout.LayoutParams) navigationButton.getLayoutParams();
        System.out.println((float)(dm.widthPixels)/180+" , "+(float)(dm.heightPixels)/240);
        System.out.println("dm.heightPixels: "+(dm.heightPixels)+" dm.widthPixels: "+dm.widthPixels);
        System.out.println("scale: "+scale+" (float)(dm.widthPixels)/4*scale+0.5f: "+(int)((float)(dm.widthPixels)/4*scale+0.5F));
        System.out.println("(int)((float)(dm.heightPixels)/7.5*scale+0.5f): "+(int)((float)(dm.heightPixels)/7.5*scale+0.5f));
        //lp2.setMargins((int)((float)(dm.widthPixels)/7*scale+0.5f), (int)((float)(dm.heightPixels)/7.5*scale+0.5f), 0, 0);
        //lp2.setMargins((int)((float)(dm.widthPixels)/4*scale+0.5f), (int)((float)(dm.heightPixels)/7.5*scale+0.5f), (int)(10*scale*0.5f), 0);
        int pixelsleft = (int) (300 * scale + 0.5f);
        int pixelstop = (int) (240 * scale + 0.5f);
        //lp2.setMargins((int)pixelsleft, pixelstop, 0, 0);
        navigationButton.setBackgroundResource(R.color.mapbox_navigation_route_alternative_congestion_red);
        navigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                        .directionsRoute(m.getCurrentRoutes())
                        .shouldSimulateRoute(false)
                        .build();
                NavigationLauncher.startNavigation(PopActivity.this, options);
            }
        });









        setFinishOnTouchOutside(true);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction() && event.getY()<0) {
            System.out.println("event.getY(): "+event.getY());
            ViewGroup.LayoutParams param = this.getWindow().getAttributes();
            System.out.println(param.height);
            m.getSymbols().get(this.key).setIconSize(1.0f);
            m.getSymbols().get(this.key).setTextOffset(new PointF(0.0f, -1.8f));
            m.setSymbolIsClicked(false);
            m.getNavigationMapRoute().removeRoute();
            navigationButton.setEnabled(false);
            m.getSymbolManager().update(m.getSymbols());
            navigationButton.setVisibility(View.INVISIBLE);
            routeButton.setVisibility(View.VISIBLE);
            finish();
            return true;
        }

        return super.onTouchEvent(event);
    }

}