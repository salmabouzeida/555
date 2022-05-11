package com.darryncampbell.dwgettingstartedjava.Model.Colis;

public class LigneColisCreated {
    //{\"NoDoc\":\"PP000006\",\"NoCommande\":\"101011\",\"NoColis\":\"001\",\"PoidsMax\":14.5}
    String NoDoc;
    String NoCommande;
    String NoColis;
     String PoidsMax;

    public String getStatut() {
        return Statut;
    }

    public void setStatut(String statut) {
        Statut = statut;
    }

    String Statut;
    public String getNoDoc() {
        return NoDoc;
    }

    public void setNoDoc(String noDoc) {
        NoDoc = noDoc;
    }

    public String getNoCommande() {
        return NoCommande;
    }

    public void setNoCommande(String noCommande) {
        NoCommande = noCommande;
    }

    public String getNoColis() {
        return NoColis;
    }

    public void setNoColis(String noColis) {
        NoColis = noColis;
    }

    public String getPoidsMax() {
        return PoidsMax;
    }

    public void setPoidsMax(String poidsMax) {
        PoidsMax = poidsMax;
    }

    @Override
    public String toString() {
        return "LigneColisCreated{" +
                "NoDoc='" + NoDoc + '\'' +
                ", NoCommande='" + NoCommande + '\'' +
                ", NoColis='" + NoColis + '\'' +
                ", PoidsMax='" + PoidsMax + '\'' +
                '}';
    }
}
