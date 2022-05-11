package com.darryncampbell.dwgettingstartedjava.Model.Colis;

public class LigneEcartColisage {
    String NoCommande;
    String Article;
    String Quantite;
    String Ecart;
    String QuantiteColis;
    String NoColis;

    public String getNoCommande() {
        return NoCommande;
    }

    public void setNoCommande(String noCommande) {
        NoCommande = noCommande;
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

    public String getEcart() {
        return Ecart;
    }

    public void setEcart(String ecart) {
        Ecart = ecart;
    }

    public String getQuantiteColis() {
        return QuantiteColis;
    }

    public void setQuantiteColis(String quantiteColis) {
        QuantiteColis = quantiteColis;
    }

    public String getNoColis() {
        return NoColis;
    }

    public void setNoColis(String noColis) {
        NoColis = noColis;
    }
}
