package com.darryncampbell.dwgettingstartedjava.Model.Return;

public class LigneEcartReturn {
    public String NoDoc;

    public String EAN;
    public String Article;
    public String QuantiteRejected;
    public String QuantiteScan;
    public String QuantitePrevue;


    public LigneEcartReturn(String noDoc, String EAN, String article, String quantite, String quantiteScan, String quantitePrevue) {
        NoDoc = noDoc;
        this.EAN = EAN;
        Article = article;
        QuantiteRejected = quantite;
        QuantiteScan = quantiteScan;
        QuantitePrevue =quantitePrevue;
    }

    public String getNoDoc() {
        return NoDoc;
    }

    public void setNoDoc(String noDoc) {
        NoDoc = noDoc;
    }

    public String getEAN() {
        return EAN;
    }

    public void setEAN(String EAN) {
        this.EAN = EAN;
    }

    public String getArticle() {
        return Article;
    }

    public void setArticle(String article) {
        Article = article;
    }

    public String getQuantiteRejected() {
        return QuantiteRejected;
    }

    public void setQuantiteRejected(String quantiteRejected) {
        QuantiteRejected = quantiteRejected;
    }

    public String getQuantiteScan() {
        return QuantiteScan;
    }

    public void setQuantiteScan(String quantiteScan) {
        QuantiteScan = quantiteScan;
    }

    public String getQuantitePrevue() {
        return QuantitePrevue;
    }

    public void setQuantitePrevue(String quantitePrevue) {
        QuantitePrevue = quantitePrevue;
    }
}
