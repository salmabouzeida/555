package com.darryncampbell.dwgettingstartedjava.Model;

public class Value{

    public String EAN;

    public String Article;

    public String Piece;

    public String Entrepot;
    public String Quantite;
    public String Poids;

    public String getPoids() {
        return Poids;
    }

    public void setPoids(String poids) {
        Poids = poids;
    }

    public String getTitre() {
        return Titre;
    }

    public void setTitre(String titre) {
        Titre = titre;
    }

    public String getCapacite() {
        return Capacite;
    }

    public void setCapacite(String capacite) {
        Capacite = capacite;
    }

    public String Titre;
    public String Capacite;

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

    public String getPiece() {
        return Piece;
    }

    public void setPiece(String piece) {
        Piece = piece;
    }

    public String getEntrepot() {
        return Entrepot;
    }

    public void setEntrepot(String entrepot) {
        Entrepot = entrepot;
    }

    public int getSum_Qty_Base() {
        return Sum_Qty_Base;
    }

    public void setSum_Qty_Base(int sum_Qty_Base) {
        Sum_Qty_Base = sum_Qty_Base;
    }

    public String getQuantite() {
        return Quantite;
    }

    public void setQuantite(String quantite) {
        Quantite = quantite;
    }

    @Override
    public String toString() {
        return "Value{" +
                "EAN='" + EAN + '\'' +
                ", Item_No='" + Article + '\'' +
                ", Location_Code='" + Piece + '\'' +
                ", Bin_Code='" + Entrepot + '\'' +
                ", Sum_Qty_Base=" + Sum_Qty_Base +
                '}';
    }

    public int Sum_Qty_Base;
}