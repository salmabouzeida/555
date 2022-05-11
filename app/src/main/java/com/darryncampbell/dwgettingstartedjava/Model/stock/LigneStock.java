package com.darryncampbell.dwgettingstartedjava.Model.stock;

public class LigneStock {
    String FromBin;
    String Article;
    String Quantite;

    public LigneStock(String fromBin, String article, String quantite) {
        FromBin = fromBin;
        Article = article;
        Quantite = quantite;
    }

    public String getFromBin() {
        return FromBin;
    }

    public void setFromBin(String fromBin) {
        FromBin = fromBin;
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
}
