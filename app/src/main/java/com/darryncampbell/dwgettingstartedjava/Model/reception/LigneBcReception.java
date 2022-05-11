package com.darryncampbell.dwgettingstartedjava.Model.reception;

public class LigneBcReception {
    public String NoDoc;

    public String EAN;


    public String Article;
    public String Piece;
    public String Quantite;
    public String QuantiteScan;
    public String NbrExemplairePaquet;
    public String NbrPaquetCouche;
    public String Epaisseur;
    public String Poids;
    public String QuantiteTotal;

    public String getQuantiteTotal() {
        return QuantiteTotal;
    }

    public void setQuantiteTotal(String quantiteTotal) {
        QuantiteTotal = quantiteTotal;
    }

    public String getPoids() {
        return Poids;
    }

    public void setPoids(String poids) {
        Poids = poids;
    }

    public String getNbrExemplairePaquet() {
        return NbrExemplairePaquet;
    }

    public void setNbrExemplairePaquet(String nbrExemplairePaquet) {
        NbrExemplairePaquet = nbrExemplairePaquet;
    }

    public String getNbrPaquetCouche() {
        return NbrPaquetCouche;
    }

    public void setNbrPaquetCouche(String nbrPaquetCouche) {
        NbrPaquetCouche = nbrPaquetCouche;
    }

    public String getEpaisseur() {
        return Epaisseur;
    }

    public void setEpaisseur(String epaisseur) {
        Epaisseur = epaisseur;
    }

    public LigneBcReception(String noDoc, String EAN, String article, String piece, String quantite, String quantiteScan, String nbrExemplairePaquet, String nbrPaquetCouche, String epaisseur, String poids, String quantiteTotal) {
        NoDoc = noDoc;
        this.EAN = EAN;
        Article = article;
        Piece = piece;
        Quantite = quantite;
        QuantiteScan = quantiteScan;
        NbrExemplairePaquet = nbrExemplairePaquet;
        NbrPaquetCouche = nbrPaquetCouche;
        Epaisseur = epaisseur;
        Poids = poids;
        QuantiteTotal = quantiteTotal;
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

    public String getPiece() {
        return Piece;
    }

    public void setPiece(String piece) {
        Piece = piece;
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

    @Override
    public String toString() {
        return "LigneBcReception{" +
                "NoDoc='" + NoDoc + '\'' +
                ", EAN='" + EAN + '\'' +
                ", Article='" + Article + '\'' +
                ", Piece='" + Piece + '\'' +
                ", Quantite='" + Quantite + '\'' +
                ", QuantiteScan='" + QuantiteScan + '\'' +
                ", NbrExemplairePaquet='" + NbrExemplairePaquet + '\'' +
                ", NbrPaquetCouche='" + NbrPaquetCouche + '\'' +
                ", Epaisseur='" + Epaisseur + '\'' +
                ", Poids='" + Poids + '\'' +
                ", QuantiteTotal='" + QuantiteTotal + '\'' +
                '}';
    }
}
