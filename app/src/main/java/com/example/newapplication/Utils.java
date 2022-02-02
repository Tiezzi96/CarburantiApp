package com.example.newapplication;

import android.graphics.Color;

import com.google.gson.JsonObject;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Utils {
    /*
    HashMap<Integer, List<String>> stationfilter : stazione di cui è stato eseguito il filtraggio
    HashMap<Integer, List<String>> fuelfilter: tipo di carburante precedentemente selezionato
    HashMap<Integer, List<String>> fuelSelectedFilter: tipo di carburante da selezionare
    String fuelself: carburante self
    String stationName: nome della stazione di cui è stato eseguito il filtraggio.
    String fuelservice: carburante servito
    ArrayList<Feature> data: caratteristiche dei symboli
    ArrayList<Symbol> symbols: tutti i symboli
    ArrayList<Symbol> symbolSelected: symboli selezionati
     */
    public Utils(){

    }
    public void stationfilter (HashMap<Integer, List<String>> stationfilter,String stationName, HashMap<Integer, List<String>> fuelfilter, HashMap<Integer, List<String>> fuelSelectedFilter, String fuelself, String fuelservice, int colore, List<Feature> data, List<Symbol> symbols, ArrayList<Symbol> symbolSelected){
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getStringProperty(fuelself) != null) {
                if(!stationfilter.containsKey(i)) {//agip, tamoil, q8, etc
                    if(fuelfilter.containsKey(i)){//gasolio, metano, benzina, gpl
                        symbols.get(i).setIconImage(fuelfilter.get(i).get(1));
                    }
                    System.out.println(symbols.get(i).getIconImage());
                    symbols.get(i).setTextField(data.get(i).getStringProperty(fuelself));
                    symbols.get(i).setTextColor(colore);
                    symbolSelected.add(symbols.get(i));
                }
                /*Aggiunto per riprendere quelle stazioni selezionate(es.Tamoil) che non hanno il precedente carburante(es. Metano) ma hanno il carburante selezionato ora (es.benzina )*/
                else if(stationfilter.containsKey(i) && stationfilter.get(i).contains(stationName)){
                    symbols.get(i).setTextField(data.get(i).getStringProperty(fuelself));
                    symbols.get(i).setTextColor(colore);
                    symbols.get(i).setIconImage(stationfilter.get(i).get(0));
                    symbolSelected.add(symbols.get(i));
                    stationfilter.remove(i);
                }/*Fine aggiunto*/
                else{
                    System.out.println("Station filter: "+stationfilter.get(i).get(1));
                    stationfilter.get(i).set(1, data.get(i).getStringProperty(fuelself));
                    symbols.get(i).setTextColor(colore);
                    symbolSelected.add(symbols.get(i));

                }
            } else if (data.get(i).getStringProperty(fuelservice) != null) {
                if(!stationfilter.containsKey(i)) {
                    if (fuelfilter.containsKey(i)) {
                        symbols.get(i).setIconImage(fuelfilter.get(i).get(1));
                    }
                    System.out.println(symbols.get(i).getIconImage());

                    symbols.get(i).setTextField(data.get(i).getStringProperty(fuelservice));
                    symbols.get(i).setTextColor(colore);
                    symbolSelected.add(symbols.get(i));

                }    /*Aggiunto per riprendere quelle stazioni selezionate(es.Tamoil) che non hanno il precedente carburante(es. Metano) ma hanno il carburante selezionato ora (es.benzina )*/
                else if(stationfilter.containsKey(i) && stationfilter.get(i).contains(stationName)){
                        symbols.get(i).setTextField(data.get(i).getStringProperty(fuelservice));
                        symbols.get(i).setTextColor(colore);
                        symbols.get(i).setIconImage(stationfilter.get(i).get(0));
                        symbolSelected.add(symbols.get(i));
                        stationfilter.remove(i);
                    }/*Fine aggiunto*/
                else{
                    System.out.println("Station filter: "+stationfilter.get(i).get(1));
                    stationfilter.get(i).set(1, data.get(i).getStringProperty(fuelservice));
                    symbols.get(i).setTextColor(colore);
                    symbolSelected.add(symbols.get(i));


                }
            } else {
                if(!stationfilter.containsKey(i)){
                    List<String> lista=new ArrayList<>();
                    lista.add(symbols.get(i).getTextField());
                    if(fuelfilter.containsKey(i)){
                        lista.add(fuelfilter.get(i).get(1));
                    }else{
                        lista.add(symbols.get(i).getIconImage());
                    }
                    System.out.println(symbols.get(i).getIconImage());
                    symbols.get(i).setTextField("");
                    symbols.get(i).setIconImage("");
                    fuelSelectedFilter.put(i, lista);
                    symbolSelected.add(symbols.get(i));
                }else{
                    System.out.println("Station filter: "+stationfilter.get(i).get(1));
                    /*Evita di avere icone senza testo quando clicco sul bottone allstation*/
                    List<String> lista=new ArrayList<>();
                    lista.add("");
                    lista.add(data.get(i).getStringProperty("icon-image"));
                    fuelSelectedFilter.put(i, lista);
                    /*Fine */
                    stationfilter.get(i).set(1, "");
                    symbols.get(i).setTextColor(colore);
                    symbolSelected.add(symbols.get(i));


                }
            }
        }
        for (int j=0; j<symbolSelected.size(); j++){
            String icon = symbolSelected.get(j).getIconImage();
            System.out.println("Bandiera "+j+" : "+data.get(j).getStringProperty("Bandiera")+
                    " Nome Impianto "+j+" : "+data.get(j).getStringProperty("NomeImpianto"));
            System.out.println("Bandiera Symbol "+j+" : "+(((JsonObject)symbolSelected.get(j).getData().getAsJsonArray().get(0)).get("Bandiera")).getAsJsonArray().get(0).toString()
                    +
                    " Nome Impianto "+j+" : "+(((JsonObject)symbolSelected.get(j).getData().getAsJsonArray().get(0)).get("NomeImpianto")).getAsJsonArray().get(0).toString());
            if(icon.equals("") && !symbolSelected.get(j).getTextField().equals("")){
                System.out.println(data.get(j).properties().get("icon-image").getAsString());
                symbolSelected.get(j).setIconImage(data.get(j).properties().get("icon-image").getAsString());
                if(fuelSelectedFilter.get(j)!=null){
                    List<String> lista2 = new ArrayList<>();
                    lista2.set(0, fuelSelectedFilter.get(j).get(0));
                    lista2.set(1, data.get(j).properties().get("icon-image").getAsString());
                    fuelSelectedFilter.remove(j);
                    fuelSelectedFilter.put(j, lista2);
                }
            }
        }
        fuelfilter = new HashMap<>();
    }

    public void stationfilter2 (HashMap<Integer, List<String>> stationfilter, HashMap<Integer, List<String>> fuelSelectedFilter, String fuelself, String fuelservice, int colore, List<Feature> data, List<Symbol> symbols, ArrayList<Symbol> symbolSelected){
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getStringProperty(fuelself) != null) {
                if(!stationfilter.containsKey(i)) {//agip, tamoil, q8, etc
                    symbols.get(i).setTextField(data.get(i).getStringProperty(fuelself));
                    symbols.get(i).setTextColor(colore);
                    symbolSelected.add(symbols.get(i));
                }else{
                    stationfilter.get(i).set(1, data.get(i).getStringProperty(fuelself));
                    symbols.get(i).setTextColor(colore);

                    symbolSelected.add(symbols.get(i));
                }
            } else if (data.get(i).getStringProperty(fuelservice) != null) {
                if(!stationfilter.containsKey(i)){
                    symbols.get(i).setTextField(data.get(i).getStringProperty(fuelservice));
                    symbols.get(i).setTextColor(colore);
                    symbolSelected.add(symbols.get(i));
                }else{
                    stationfilter.get(i).set(1, data.get(i).getStringProperty(fuelservice));
                    symbols.get(i).setTextColor(colore);

                    symbolSelected.add(symbols.get(i));
                }
            } else {
                if(!stationfilter.containsKey(i)){
                    List<String> lista=new ArrayList<>();
                    lista.add(symbols.get(i).getTextField());
                    lista.add(symbols.get(i).getIconImage());
                    System.out.println(symbols.get(i).getIconImage());
                    symbols.get(i).setTextField("");
                    symbols.get(i).setIconImage("");
                    fuelSelectedFilter.put(i, lista);
                    symbolSelected.add(symbols.get(i));
                }else{

                    /*Evita di avere icone senza testo quando clicco sul bottone allstation*/
                    List<String> lista=new ArrayList<>();
                    lista.add("");
                    //lista.add(symbols.get(i).getIconImage());
                    lista.add(data.get(i).getStringProperty("icon-image"));
                    fuelSelectedFilter.put(i, lista);
                    /*Fine */
                    stationfilter.get(i).set(1, "");
                    symbols.get(i).setTextColor(Color.GREEN);

                    symbolSelected.add(symbols.get(i));
                }

            }
        }
        System.out.println("data: "+data.size());
        System.out.println("Symbol Selected: "+symbolSelected.size());
    }


    public void stationfilterMetanoGPL (HashMap<Integer, List<String>> stationfilter, HashMap<Integer, List<String>> fuelfilter, HashMap<Integer, List<String>> fuelSelectedFilter,String stationName, String fuelservice, int colore, List<Feature> data, List<Symbol> symbols, ArrayList<Symbol> symbolSelected){
        int c=0;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getStringProperty(fuelservice) != null) {
                if(data.get(i).getStringProperty(fuelservice).equals("0.899")){
                    System.out.println(i);
                }
                if(!stationfilter.containsKey(i)){
                    if(fuelfilter.containsKey(i)){
                        System.out.println("fuelfilter.get(i).get(1): "+fuelfilter.get(i).get(1));
                        symbols.get(i).setIconImage(fuelfilter.get(i).get(1));
                    }
                    //System.out.println(symbols.get(i).getIconImage());

                    symbols.get(i).setTextField(data.get(i).getStringProperty(fuelservice));
                    symbols.get(i).setTextColor(colore);
                    symbolSelected.add(symbols.get(i));
                }/*Aggiunto per riprendere quelle stazioni selezionate(es.Tamoil) che non hanno il precedente carburante(es. Metano) ma hanno il carburante selezionato ora (es.benzina )*/
                else if(stationfilter.containsKey(i) && data.get(i).getStringProperty("Bandiera").equals(stationName)){
                    symbols.get(i).setTextField(data.get(i).getStringProperty(fuelservice));
                    symbols.get(i).setTextColor(colore);
                    symbols.get(i).setIconImage(data.get(i).getStringProperty("icon-image"));
                    System.out.println("stationfilter.get(i).get(0): "+stationfilter.get(i).get(0));
                    symbolSelected.add(symbols.get(i));
                    stationfilter.remove(i);
                }/*Fine aggiunto*/
                else{
                    System.out.println("Station filter: "+stationfilter.get(i).get(1));
                    stationfilter.get(i).set(1, data.get(i).getStringProperty(fuelservice));
                    symbols.get(i).setTextColor(colore);
                    symbolSelected.add(symbols.get(i));

                }
            } else {
                if(!stationfilter.containsKey(i)){
                    List<String> lista=new ArrayList<>();
                    lista.add(symbols.get(i).getTextField());
                    if(fuelfilter.containsKey(i)){
                        System.out.println("fuelfilter.get(i).get(1): "+fuelfilter.get(i).get(1));
                        lista.add(fuelfilter.get(i).get(1));
                    }else{
                        lista.add(symbols.get(i).getIconImage());
                    }
                    System.out.println(symbols.get(i).getIconImage());
                    symbols.get(i).setTextField("");
                    symbols.get(i).setIconImage("");
                    fuelSelectedFilter.put(i, lista);
                    symbolSelected.add(symbols.get(i));
                }else{
                    System.out.println("Station filter2: "+stationfilter.get(i).get(1));
                    /*Evita di avere icone senza testo quando clicco sul bottone allstation*/
                    List<String> lista=new ArrayList<>();
                    lista.add("");
                    lista.add(data.get(i).getStringProperty("icon-image"));
                    fuelSelectedFilter.put(i, lista);
                    /*Fine */
                    stationfilter.get(i).set(1, "");
                    symbols.get(i).setTextColor(colore);
                    symbols.get(i).setIconImage("");
                    symbolSelected.add(symbols.get(i));


                }
            }
        }
        System.out.println(data.size());
        System.out.println(symbolSelected.size());

        for (int j=0; j<symbolSelected.size(); j++){
            String icon = symbolSelected.get(j).getIconImage();
            System.out.println("Bandiera "+j+" : "+data.get(j).getStringProperty("Bandiera")+
                    " Nome Impianto "+j+" : "+data.get(j).getStringProperty("NomeImpianto"));
            System.out.println("Bandiera Symbol "+j+" : "+(((JsonObject)symbolSelected.get(j).getData().getAsJsonArray().get(0)).get("Bandiera")).getAsJsonArray().get(0).toString()
                    +
                    " Nome Impianto "+j+" : "+(((JsonObject)symbolSelected.get(j).getData().getAsJsonArray().get(0)).get("NomeImpianto")).getAsJsonArray().get(0).toString());
            if(icon.equals("") && !symbolSelected.get(j).getTextField().equals("")){
                System.out.println(data.get(j).properties().get("icon-image").getAsString());
                symbolSelected.get(j).setIconImage(data.get(j).properties().get("icon-image").getAsString());
                if(fuelSelectedFilter.get(j)!=null){
                    List<String> lista2 = new ArrayList<>();
                    lista2.set(0, fuelSelectedFilter.get(j).get(0));
                    lista2.set(1, data.get(j).properties().get("icon-image").getAsString());
                    fuelSelectedFilter.remove(j);
                    fuelSelectedFilter.put(j, lista2);
                }
            }
        }
        System.out.println("agipfilter: "+stationfilter.values());
        fuelfilter = new HashMap<>();
    }


    public void stationfilterMetanoGPL2 (HashMap<Integer, List<String>> stationfilter, HashMap<Integer, List<String>> fuelSelectedFilter, String fuelservice, int colore, List<Feature> data, List<Symbol> symbols, ArrayList<Symbol> symbolSelected){
        for (int i = 0; i < data.size(); i++) {
            String value = data.get(i).getStringProperty(fuelservice);
            String value2 = symbols.get(i).getTextField();
            String bandiera = data.get(i).getStringProperty("icon-image");
            String bandiera2 = symbols.get(i).getIconImage();
            if (data.get(i).getStringProperty(fuelservice) != null && data.get(i).getStringProperty(fuelservice) != "null") {
                if(!stationfilter.containsKey(i)){
                    symbols.get(i).setTextField(data.get(i).getStringProperty(fuelservice));
                    symbols.get(i).setTextColor(colore);
                    symbolSelected.add(symbols.get(i));
                }else{
                    stationfilter.get(i).set(1, data.get(i).getStringProperty(fuelservice));
                    symbols.get(i).setTextColor(colore);
                    symbolSelected.add(symbols.get(i));
                }
            } else {
                if(!stationfilter.containsKey(i)){
                    List<String> lista=new ArrayList<>();
                    lista.add(symbols.get(i).getTextField());
                    lista.add(symbols.get(i).getIconImage());
                    System.out.println(symbols.get(i).getIconImage());
                    symbols.get(i).setTextField("");
                    symbols.get(i).setIconImage("");
                    fuelSelectedFilter.put(i, lista);
                    symbolSelected.add(symbols.get(i));
                }else{
                    stationfilter.get(i).set(1, "");
                    /*Evita di avere icone senza testo quando clicco sul bottone allstation*/
                    List<String> lista=new ArrayList<>();
                    lista.add("");
                    lista.add(data.get(i).getStringProperty("icon-image"));
                    fuelSelectedFilter.put(i, lista);
                    /*Fine */
                    symbols.get(i).setTextColor(colore);
                    symbolSelected.add(symbols.get(i));
                }
            }
        }
        System.out.println(stationfilter);
        System.out.println(symbolSelected);
    }



}
