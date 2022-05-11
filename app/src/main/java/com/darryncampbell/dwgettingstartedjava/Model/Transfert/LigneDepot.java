package com.darryncampbell.dwgettingstartedjava.Model.Transfert;

public class LigneDepot {
    String Piece;
    String Quantite;

    public LigneDepot(String piece, String quantite) {
        Piece = piece;
        Quantite = quantite;
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
}
