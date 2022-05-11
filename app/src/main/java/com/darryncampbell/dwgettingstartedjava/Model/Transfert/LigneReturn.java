package com.darryncampbell.dwgettingstartedjava.Model.Transfert;

public class LigneReturn {
    String NoClient;
    String Article;
    String Quantite;

    public String getNoClient() {
        return NoClient;
    }

    public void setNoClient(String noClient) {
        NoClient = noClient;
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

    public LigneReturn(String noClient, String article, String quantite) {
        NoClient = noClient;
        Article = article;
        Quantite = quantite;
    }
}
