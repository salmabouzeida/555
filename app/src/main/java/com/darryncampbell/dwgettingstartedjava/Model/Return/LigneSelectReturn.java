package com.darryncampbell.dwgettingstartedjava.Model.Return;

public class LigneSelectReturn {
    public String NoDoc;

    public String EAN;
    public String Article;
    public String Quantite;
    public String QuantiteScan;
    public Boolean Rejection;
    public Boolean Damaged;

    public Boolean getDamaged() {
        return Damaged;
    }

    public void setDamaged(Boolean damaged) {
        Damaged = damaged;
    }

    public LigneSelectReturn(String noDoc, String EAN, String article, String quantite, String quantiteScan, Boolean rejection, Boolean damaged) {
        NoDoc = noDoc;
        this.EAN = EAN;
        Article = article;
        Quantite = quantite;
        QuantiteScan = quantiteScan;
        Rejection = rejection;
        Damaged = damaged;
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

    public String getQuantite() {
        return Quantite;
    }

    public void setQuantite(String quantite) {
        Quantite = quantite;
    }

    public String getQuantiteScan() {
        return QuantiteScan;
    }

    public void setQuantiteScan(String quantiteScan) {
        QuantiteScan = quantiteScan;
    }

    public Boolean getRejection() {
        return Rejection;
    }

    public void setRejection(Boolean rejection) {
        Rejection = rejection;
    }
}
