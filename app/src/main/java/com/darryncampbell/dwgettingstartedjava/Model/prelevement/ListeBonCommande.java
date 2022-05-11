package com.darryncampbell.dwgettingstartedjava.Model.prelevement;

import java.util.List;

public class ListeBonCommande {


    public List<Value> value;



    public List<Value> getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "ListeBonCommande{" +

                ", value=" + value +
                '}';
    }

    public void setValue(List<Value> value) {
        this.value = value;
    }
}
