package com.darryncampbell.dwgettingstartedjava.Model.reception;

public class Value {
    public String NoDoc;

    public String Client;
    public String AuxiliaryIndex1;

    public String getAuxiliaryIndex1() {
        return AuxiliaryIndex1;
    }

    public void setAuxiliaryIndex1(String auxiliaryIndex1) {
        AuxiliaryIndex1 = auxiliaryIndex1;
    }

    public String getNoDoc() {
        return NoDoc;
    }

    public void setNoDoc(String noDoc) {
        NoDoc = noDoc;
    }

    public String getClient() {
        return Client;
    }

    public void setClient(String client) {
        Client = client;
    }

    @Override
    public String toString() {
        return "Value{" +
                "NoDoc='" + NoDoc + '\'' +
                ", Client='" + Client + '\'' +
                '}';
    }
}
