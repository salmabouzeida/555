package com.darryncampbell.dwgettingstartedjava.Model.Etiquette;

public class LigneEtiquette {
    String Colis;
    String Article;
    String Quantite;

    public LigneEtiquette(String colis, String article, String quantite) {
        Colis = colis;
        Article = article;
        Quantite = quantite;
    }

    public String getColis() {
        return Colis;
    }

    public void setColis(String colis) {
        Colis = colis;
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
