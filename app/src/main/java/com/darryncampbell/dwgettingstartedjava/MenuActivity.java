package com.darryncampbell.dwgettingstartedjava;

import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        CardView btn_consult = (CardView)   findViewById(R.id.btn_consult) ;
        btn_consult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),ConsultActivity.class);
                startActivity(intent);;
            }
        });

        CardView btn_prelevement = (CardView)   findViewById(R.id.btn_prelevement) ;
        btn_prelevement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), PrelevementActivity.class);
                startActivity(intent);;
            }
        });
        CardView btn_prelevement_lot = (CardView)   findViewById(R.id.btn_prelevement_lot) ;
        btn_prelevement_lot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), PrelevementLotActivity.class);
                startActivity(intent);;
            }
        });
        CardView btn_transfert = (CardView)   findViewById(R.id.btn_transfert) ;
        btn_transfert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),TransfertActivity.class);
                startActivity(intent);;
            }
        });
        CardView btn_test = (CardView)   findViewById(R.id.btn_test) ;
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);;
            }
        });
        CardView btn_reception = (CardView)   findViewById(R.id.btn_reception) ;
        btn_reception.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),ReceptionActivity.class);
                startActivity(intent);;
            }
        });
        CardView btn_scan_etiquette = (CardView)   findViewById(R.id.btn_scan_etiquette) ;
        btn_scan_etiquette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), ScanEtiquetteActivity.class);
                startActivity(intent);;
            }
        });
        CardView btn_colisage = (CardView)   findViewById(R.id.btn_colisage) ;
        btn_colisage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), ColisageActivity.class);
                startActivity(intent);;
            }
        });
        CardView btn_return = (CardView)   findViewById(R.id.btn_return) ;
        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), ReturnActivity.class);
                startActivity(intent);;
            }
        });
        //btn_stock
        CardView btn_stock = (CardView)   findViewById(R.id.btn_stock) ;
        btn_stock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), StockActivity.class);
                startActivity(intent);;
            }
        });
    }
}