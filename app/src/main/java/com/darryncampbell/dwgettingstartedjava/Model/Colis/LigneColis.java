package com.darryncampbell.dwgettingstartedjava.Model.Colis;

public class LigneColis {
    //{\"NoDoc\":\"PP000006\",\"NoColis\":\"001\",\"Article\":\"BORANG\",\"Quantite\":14.0,\"PoidsUnite\":2.0}
    String NoDoc;
    String NoColis;
    String Article;
    String Quantite;
    String PoidsUnite;
    String EAN;
    String QuantiteScan;
    String NoCommande;

    public LigneColis(String noDoc, String noColis, String article, String quantite, String poidsUnite, String EAN, String quantiteScan, String noCommande) {
        NoDoc = noDoc;
        NoColis = noColis;
        Article = article;
        Quantite = quantite;
        PoidsUnite = poidsUnite;
        this.EAN = EAN;
        QuantiteScan = quantiteScan;
        NoCommande = noCommande;
    }

    public String getNoCommande() {
        return NoCommande;
    }

    public void setNoCommande(String noCommande) {
        NoCommande = noCommande;
    }

    public String getEAN() {
        return EAN;
    }

    public void setEAN(String EAN) {
        this.EAN = EAN;
    }



    public String getNoDoc() {
        return NoDoc;
    }

    public void setNoDoc(String noDoc) {
        NoDoc = noDoc;
    }

    public String getNoColis() {
        return NoColis;
    }

    public void setNoColis(String noColis) {
        NoColis = noColis;
    }

    public String getArticle() {
        return Article;
    }

    public void setArticle(String article) {
        Article = article;
    }

    public String getQuantite() {
        return Quantite;
    }

    public void setQuantite(String quantite) {
        Quantite = quantite;
    }

    public String getPoidsUnite() {
        return PoidsUnite;
    }

    public void setPoidsUnite(String poidsUnite) {
        PoidsUnite = poidsUnite;
    }

    public String getQuantiteScan() {
        return QuantiteScan;
    }

    public void setQuantiteScan(String quantiteScan) {
        QuantiteScan = quantiteScan;
    }
}
