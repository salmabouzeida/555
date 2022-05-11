package com.darryncampbell.dwgettingstartedjava.Model;

import java.util.List;



public class ConsultArticle{
    //@JsonProperty("@odata.context")

    public List<Value> value;

      public List<Value> getValue() {
        return value;
    }

    public void setValue(List<Value> value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return "ConsultArticle{" +

                " value=" + value +
                '}';
    }
}
