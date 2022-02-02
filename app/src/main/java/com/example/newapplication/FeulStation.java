package com.example.newapplication;

import com.google.firebase.database.PropertyName;

import java.io.Serializable;
import java.util.HashMap;

class FuelStationLngLat implements Serializable {
    private String Latitudine;
    private String Longitudine;
    private String Bandiera;
    private String Gestore;
    @PropertyName("Carburante Self")
    public HashMap<String, String> CarburanteSelf;
    @PropertyName("Carburante Servito")
    public HashMap<String, String> CarburanteServito;
    @PropertyName("Nome Impianto")
    public String NomeImpianto;


    public String getNomeImpianto() {
        return NomeImpianto;
    }

    public void setNomeImpianto(String nomeImpianto) {
        NomeImpianto = nomeImpianto;
    }

    public String getGestore() {
        return Gestore;
    }

    public void setGestore(String gestore) {
        Gestore = gestore;
    }

    public HashMap<String, String> getCarburanteSelf() {
        return CarburanteSelf;
    }

    public void setCarburanteSelf(HashMap<String, String> carburanteSelf) {
        CarburanteSelf = carburanteSelf;
    }

    public FuelStationLngLat() {
    }

    public String getLatitudine() {
        return Latitudine;
    }

    public String getBandiera() {
        return Bandiera;
    }

    public void setBandiera(String bandiera) {
        Bandiera = bandiera;
    }

    public void setLatitudine(String Latitudine) {
        this.Latitudine = Latitudine;
    }

    public String getLongitudine() {
        return Longitudine;
    }

    public void setLongitudine(String Longitudine) {
        this.Longitudine = Longitudine;
    }


    public HashMap<String, String> getCarburanteServito() {
        return CarburanteServito;
    }

    public void setCarburanteServito(HashMap<String, String> carburanteServito) {
        CarburanteServito = carburanteServito;
    }
}

;