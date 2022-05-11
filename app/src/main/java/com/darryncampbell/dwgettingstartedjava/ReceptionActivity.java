package com.darryncampbell.dwgettingstartedjava;

import static android.view.KeyEvent.KEYCODE_DEL;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.darryncampbell.dwgettingstartedjava.LocalBase.Helper;


import com.darryncampbell.dwgettingstartedjava.Model.reception.LigneBcReception;
import com.darryncampbell.dwgettingstartedjava.Model.reception.ListBonCommandAchat;
import com.darryncampbell.dwgettingstartedjava.Model.reception.ListLigneBcReception;
import com.darryncampbell.dwgettingstartedjava.Model.reception.Value;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReceptionActivity extends AppCompatActivity implements View.OnTouchListener {
    final Context co = this;
    GridView grid_reception;
    ProgressBar progressBar;
    Helper helper;
    LinearLayout layout_valider;
    Button bt_view, btAdd, bt_valid_list, bt_scan, bt_view_ligne, btnDelete, bt_create;
    String searchScan = "";
    String baseUrlLigneBC ="";
    TextView output;
    String baseUrlCreateReception = "";
    String baseUrlListBc = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reception);
          baseUrlCreateReception = getResources().getString(R.string.base_url) + "WmsApp_ValiderReception?$format=application/json;odata.metadata=none";
          baseUrlLigneBC = getResources().getString(R.string.base_url) + "WmsApp_ReturnSelectedPurchaseLigne";
          baseUrlListBc = getResources().getString(R.string.base_url) + "WmsApp_ReturnPurchaseHead?$format=application/json;odata.metadata=none";

        grid_reception = (GridView) findViewById(R.id.grid_reception);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        layout_valider = (LinearLayout) findViewById(R.id.layout_valider);
        output = findViewById(R.id.txtOutput);
        helper = new Helper(getApplicationContext());
        btAdd = (Button) findViewById(R.id.bt_add);
        bt_view_ligne = (Button) findViewById(R.id.bt_view_ligne);
        bt_create = (Button) findViewById(R.id.btn_create);
        bt_scan = (Button) findViewById(R.id.btnScanprev);
        bt_valid_list = (Button) findViewById(R.id.bt_valid_list);
        DWUtilities.CreateDWProfile(co, "com.reception.action");
        Button btnScan = findViewById(R.id.btnScanprev);
        btnScan.setOnTouchListener(this);


        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FillListBonCommande();
                layout_valider.setVisibility(View.GONE);
                bt_view.setVisibility(View.VISIBLE);
            }
        });
        bt_view = (Button) findViewById(R.id.bt_view);
        bt_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout_valider.setVisibility(View.VISIBLE);
                bt_view.setVisibility(View.GONE);
                FillListBcSelect fillListBcSelect = new FillListBcSelect();
                fillListBcSelect.execute("");


            }
        });
        bt_view_ligne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FillListLigneReception fillList = new FillListLigneReception();
                fillList.execute("");


            }
        });

        layout_valider.setVisibility(View.GONE);
        bt_view.setVisibility(View.VISIBLE);
        bt_valid_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if( helper.getListCommandReception().getCount()>0)
                    {helper.AddValideCommandeReception("test");
                    bt_scan.setVisibility(View.VISIBLE);
                    bt_view_ligne.setVisibility(View.VISIBLE);
                    bt_view.setVisibility(View.GONE);
                    layout_valider.setVisibility(View.GONE);
                    btAdd.setVisibility(View.GONE);
                    bt_valid_list.setVisibility(View.GONE);

                    FillLigneBonCommande();}else{
                        Toast.makeText(getApplicationContext(),"selectionner une ou plusieures lignes ",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        Cursor c = helper.getListLigneCommandReception();
        if (c.getCount() > 0) {
            FillListLigneReception fillListLigneReception = new FillListLigneReception();
            fillListLigneReception.execute("");
            bt_valid_list.setVisibility(View.GONE);
            bt_view.setVisibility(View.GONE);

        } else {
            FillListBonCommande();
            btnScan.setVisibility(View.GONE);
            bt_view_ligne.setVisibility(View.GONE);

        }
        btnDelete = (Button) findViewById(R.id.bt_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder alt = new AlertDialog.Builder(co);
                alt.setIcon(R.drawable.icon_reception);
                alt.setTitle("Reception");
                alt.setMessage("Voulez-vous vraiment annuler ce reception?");
                alt.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface di, int i) {

                                helper.DeleteListBonCommandReception();
                                helper.DeleteLigneBonCommandReception();
                                helper.DeleteValideBonCommandReception();
                                layout_valider.setVisibility(View.GONE);
                                bt_view.setVisibility(View.VISIBLE);
                                finish();
                                startActivity(getIntent());
                            }
                        })
                        .setNegativeButton("Annuler",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface di, int i) {
                                        di.cancel();
                                    }
                                });

                final AlertDialog d = alt.create();
                d.show();

            }
        });
        bt_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Cursor c = helper.getListLigneCommandReception();
                if (c.getCount() > 0) {

                    CreateReception();

                } else {
                    Toast.makeText(getApplicationContext(), "scanner des article d'abord", Toast.LENGTH_SHORT).show();

                }
//                  output.setText("3274080005003")         ;
//                // FillListConsult("3274080005003");
//                searchScan = "3274080005003";
//
//
//                Toast.makeText(getApplicationContext(), searchScan, Toast.LENGTH_SHORT).show();
//                FillListLigneReceptionSearch fillListLigneReceptionSearch = new FillListLigneReceptionSearch();
//                fillListLigneReceptionSearch.execute("");

            }
        });


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        displayScanResult(intent);
    }

    private void displayScanResult(Intent scanIntent) {

        String decodedSource = scanIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_source));
        String decodedData = scanIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_data));
        String decodedLabelType = scanIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_label_type));
        // String scan = decodedData + " [" + decodedLabelType + "]\n\n";
        String scan = decodedData;
        searchScan = scan;

        output.setText(scan);
        Toast.makeText(getApplicationContext(), searchScan, Toast.LENGTH_SHORT).show();
        FillListLigneReceptionSearch fillListLigneReceptionSearch = new FillListLigneReceptionSearch();
        fillListLigneReceptionSearch.execute("");


    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view.getId() == R.id.btnScanprev) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                //  Button pressed, start scan
                Intent dwIntent = new Intent();
                dwIntent.setAction("com.symbol.datawedge.api.ACTION");
                dwIntent.putExtra("com.symbol.datawedge.api.SOFT_SCAN_TRIGGER", "START_SCANNING");
                sendBroadcast(dwIntent);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                //  Button released, end scan
                Intent dwIntent = new Intent();
                dwIntent.setAction("com.symbol.datawedge.api.ACTION");
                dwIntent.putExtra("com.symbol.datawedge.api.SOFT_SCAN_TRIGGER", "STOP_SCANNING");
                sendBroadcast(dwIntent);
            }
        }
        return true;
    }

    void FillListBonCommande() {
        try {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = baseUrlListBc;
            progressBar.setVisibility(View.VISIBLE);

            StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            progressBar.setVisibility(View.GONE);
                            
                            ListBonCommandAchat data = new ListBonCommandAchat();
                            JSONObject obj = null;
                            try {
                                obj = new JSONObject(response);
                                
                                JSONArray array = new JSONArray(obj.getString("value"));

                                JSONObject jsonList = new JSONObject().put("value", array);


                                Gson gson = new Gson();
                                data = gson.fromJson(jsonList.toString(), ListBonCommandAchat.class);
                                final ListBonCommandAchat finalData = data;


                                Log.d("tag****", finalData.toString());
                                final BaseAdapter baseAdapter = new BaseAdapter() {
                                    @Override
                                    public int getCount() {
                                        return finalData.getValue().size();
                                    }

                                    @Override
                                    public Object getItem(int position) {
                                        return null;
                                    }

                                    @Override
                                    public long getItemId(int position) {
                                        return 0;
                                    }


                                    @Override
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        final LayoutInflater layoutInflater = LayoutInflater.from(co);
                                        convertView = layoutInflater.inflate(R.layout.item_bc, null);
                                        final TextView txt_code = (TextView) convertView.findViewById(R.id.txt_code);
                                        final TextView txt_nom_client = (TextView) convertView.findViewById(R.id.tx_nom_client);
                                        final Button btplus = (Button) convertView.findViewById(R.id.btplus);
                                        final Button btdelete = (Button) convertView.findViewById(R.id.btdelete);
                                       
                                        final Value val = finalData.getValue().get(position);
                                        if (helper.testExistBonCommandReception(val.getNoDoc())) {
                                            btdelete.setVisibility(View.VISIBLE);
                                            btplus.setVisibility(View.GONE);
                                        } else {
                                            btdelete.setVisibility(View.GONE);
                                            btplus.setVisibility(View.VISIBLE);
                                        }

                                        btplus.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                helper.AddBonCommandeReception(val);
                                                btdelete.setVisibility(View.VISIBLE);
                                                btplus.setVisibility(View.GONE);
                                            }
                                        });
                                        btdelete.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                helper.RemoveBonCommandReception(val);
                                                btdelete.setVisibility(View.GONE);
                                                btplus.setVisibility(View.VISIBLE);
                                            }
                                        });

                                        txt_code.setText(val.getNoDoc() + "");
                                        txt_nom_client.setText(val.getClient() + "");


                                        return convertView;
                                    }
                                };
                                grid_reception.setAdapter(baseAdapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.GONE);
                            // TODO Auto-generated method stub
                            Log.d("ERROR", "error => " + error.toString());
                            Toast.makeText(getApplicationContext(), "eror vollety" + error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", getResources().getString(R.string.key_autorisation));
                    params.put("Content-Type", "application/json");
                    params.put("Company", getResources().getString(R.string.company_name));

                    return params;
                }

            };
            queue.add(getRequest);
        } catch (Exception e) {
            //  Toast.makeText(getApplicationContext(), "eror exception" + e.toString(), Toast.LENGTH_SHORT).show();
            Log.e("error", e.toString());

        }
    }

    void FillLigneBonCommande() {
        try {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = baseUrlLigneBC;
            progressBar.setVisibility(View.VISIBLE);
            JSONArray arrayJson = new JSONArray();
            Cursor cr = helper.getListCommandReception();
            if (cr.moveToFirst()) {
                do {
                    JSONObject obj = new JSONObject().put("NoDoc", cr.getString(cr.getColumnIndex("Code")));
                    arrayJson.put(obj);
                    Log.d("***inputgetListCommandreception", obj.toString());
                } while (cr.moveToNext());
            }
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("inputJson", arrayJson.toString());
            Log.d("***inputFillLigneBonCommande", jsonBody.toString());

            final String mRequestBody = jsonBody.toString();
            StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            progressBar.setVisibility(View.GONE);
                            Log.d("tag****ResponseFillLigneBonCommande", response);

                            bt_valid_list.setVisibility(View.GONE);
                            bt_scan.setVisibility(View.VISIBLE);
                            JSONObject obj = null;
                            ListLigneBcReception data = new ListLigneBcReception();
                            try {
                                obj = new JSONObject(response);
                                
                                JSONArray array = new JSONArray(obj.getString("value"));

                                JSONObject jsonList = new JSONObject().put("value", array);


                                Gson gson = new Gson();
                                data = gson.fromJson(jsonList.toString(), ListLigneBcReception.class);
                                final ListLigneBcReception finalData = data;
                                for (int i = 0; i < finalData.value.size(); i++) {
                                    LigneBcReception ligne = finalData.value.get(i);
                                    ligne.setQuantiteScan("0");
                                    helper.AddLigneBonCommandeReception(ligne);
                                }


                            } catch (JSONException e) {
                                Log.e("errorlistlignebc", e.toString());
                            }


                            FillListLigneReception fillListLigneReception = new FillListLigneReception();
                            fillListLigneReception.execute("");

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.GONE);
                            // TODO Auto-generated method stub
                            Log.d("ERROR", "error => " + error.toString());
                            Toast.makeText(getApplicationContext(), "eror vollety" + error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", getResources().getString(R.string.key_autorisation));
                    params.put("Content-Type", "application/json");
                    params.put("Company", getResources().getString(R.string.company_name));

                    return params;
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        Log.e("ERROR", "errorgetbody => " + uee.toString());
                        return null;
                    }
                }
            };
            queue.add(getRequest);
        } catch (Exception e) {
            // Toast.makeText(getApplicationContext(), "eror exception" + e.toString(), Toast.LENGTH_SHORT).show();
            Log.e("error", e.toString());

        }
    }

    public class FillListLigneReception extends AsyncTask<String, String, String> {
        String z = "";

        List<Map<String, String>> prolist = new ArrayList<Map<String, String>>();
        ArrayList<String> list;
        Cursor cr;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String r) {


            progressBar.setVisibility(View.GONE);

            BaseAdapter baseAdapter = new BaseAdapter() {
                @Override
                public int getCount() {
                    return cr.getCount();
                }

                @Override
                public Object getItem(int position) {
                    return null;
                }

                @Override
                public long getItemId(int position) {
                    return 0;
                }

                @Override
                public View getView(final int pos, View convertView, ViewGroup parent) {
                    final LayoutInflater layoutInflater = LayoutInflater.from(co);

                    convertView = layoutInflater.inflate(R.layout.item_reception, null);
                    final TextView txt_noDoc = (TextView) convertView.findViewById(R.id.txt_noDoc);
                    final TextView txt_ean = (TextView) convertView.findViewById(R.id.txt_ean);
                    final TextView txt_code_article = (TextView) convertView.findViewById(R.id.txt_code_article);
                    final TextView txt_piece = (TextView) convertView.findViewById(R.id.txt_piece);
                    final TextView txt_qt = (TextView) convertView.findViewById(R.id.txt_qt);
                    final TextView txt_nb_couche = (TextView) convertView.findViewById(R.id.txt_nb_couche);
                    final TextView txt_nb_paquet = (TextView) convertView.findViewById(R.id.txt_nb_paquet);
                    final EditText txt_epaisseur = (EditText) convertView.findViewById(R.id.txt_epaisseur);
                    final EditText txt_qt_total = (EditText) convertView.findViewById(R.id.qt_total);
                    final TextView edt_poids = (TextView) convertView.findViewById(R.id.txt_poids);
                    final EditText edt_qt_scan = (EditText) convertView.findViewById(R.id.edt_qt_scan);
                    final Button btmoin = (Button) convertView.findViewById(R.id.btmoin);
                    final Button btplus = (Button) convertView.findViewById(R.id.btplus);
                    //noDoc TEXT  ,Code TEXT, Quantite INTEGER, QuantiteScan INTEGER
                    cr = helper.getListLigneCommandReception();
                    if (cr.move(pos + 1)) {
//    public String NbrExemplairePaquet;
//    public String NbrPaquetCouche;
//    public String Epaisseur;
                        txt_code_article.setText(cr.getString(cr.getColumnIndex("Article")));
                        txt_noDoc.setText(cr.getString(cr.getColumnIndex("noDoc")));
                        txt_qt.setText(cr.getString(cr.getColumnIndex("Quantite")));
                        txt_piece.setText(cr.getString(cr.getColumnIndex("Piece")));
                        txt_ean.setText(cr.getString(cr.getColumnIndex("EAN")));
                        txt_nb_couche.setText(cr.getString(cr.getColumnIndex("NbrPaquetCouche")));
                        txt_epaisseur.setText(cr.getString(cr.getColumnIndex("Epaisseur")));
                        txt_nb_paquet.setText(cr.getString(cr.getColumnIndex("NbrExemplairePaquet")));
                        edt_qt_scan.setText(cr.getString(cr.getColumnIndex("QuantiteScan")));
                        edt_poids.setText(cr.getString(cr.getColumnIndex("Poids")));
                        txt_qt_total.setText( cr.getString(cr.getColumnIndex("QuantiteTotal")));
                    }

                    txt_qt_total.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View view, int i, KeyEvent keyEvent) {

                            String s = txt_qt_total.getText().toString();
                            if (i != KEYCODE_DEL) {
                                if (!s.equals("")) {

                                    helper.UpdateLigneBonCommandeReception(new LigneBcReception(txt_noDoc.getText().toString(),
                                            txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(),
                                            txt_qt.getText().toString()
                                            ,  edt_qt_scan.getText().toString(),txt_nb_paquet.getText().toString(),""+txt_nb_couche.getText().toString(),""
                                            +txt_epaisseur.getText().toString(), edt_poids.getText().toString(),""+s));
                                    // helper.UpdateLigneBonCommandeReception(new LigneBcReception(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(), txt_qt.getText().toString(), "" + qt));

                                }
                            }
                            return false;
                        }
                    });

                    txt_epaisseur.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View view, int i, KeyEvent keyEvent) {

                            String s = txt_epaisseur.getText().toString();
                            if (i != KEYCODE_DEL) {
                                if (!s.equals("")) {

                                    helper.UpdateLigneBonCommandeReception(new LigneBcReception(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(),
                                            txt_piece.getText().toString(), txt_qt.getText().toString()
                                            , txt_qt_total.getText().toString(),txt_nb_paquet.getText().toString(),""+txt_nb_couche.getText().toString(),""
                                            +s, edt_poids.getText().toString(),txt_qt_total.getText().toString()));
                                    // helper.UpdateLigneBonCommandeReception(new LigneBcReception(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(), txt_qt.getText().toString(), "" + qt));

                                }
                            }
                            return false;
                        }
                    });
                    edt_poids.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View view, int i, KeyEvent keyEvent) {

                            String s = edt_poids.getText().toString();
                            if (i != KEYCODE_DEL) {
                                if (!s.equals("")) {

                                    helper.UpdateLigneBonCommandeReception(new LigneBcReception(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(), txt_qt.getText().toString()
                                            , txt_qt_total.getText().toString(),txt_nb_paquet.getText().toString(),""+txt_nb_couche.getText().toString(),""
                                            +txt_epaisseur.getText().toString(), s,txt_qt_total.getText().toString()));
                                    // helper.UpdateLigneBonCommandeReception(new LigneBcReception(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(), txt_qt.getText().toString(), "" + qt));

                                }
                            }
                            return false;
                        }
                    });

                    edt_qt_scan.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View view, int i, KeyEvent keyEvent) {

                            String s = edt_qt_scan.getText().toString();
                            if (i != KEYCODE_DEL) {
                                if (!s.equals("")) {
                                        int qt =1;
                            if(!edt_qt_scan.getText().toString().equals(""))
                            {qt = Integer.parseInt(edt_qt_scan.getText().toString());}
                                    int qtNbrExemplairePaquet=Integer.parseInt(txt_nb_paquet.getText().toString());
                                    int qtNbrPaquetCouche=Integer.parseInt(txt_nb_couche.getText().toString());
                                    int qtTotal=qt*qtNbrExemplairePaquet*qtNbrPaquetCouche;
                                    txt_qt_total.setText(""+qtTotal);
                                    helper.UpdateLigneBonCommandeReception(new LigneBcReception(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(), txt_qt.getText().toString()
                                            , "" + qt,txt_nb_paquet.getText().toString(),""+txt_nb_couche.getText().toString(),""
                                            +txt_epaisseur.getText().toString(), edt_poids.getText().toString(),txt_qt_total.getText().toString()));
                                }
                            }
                            return false;
                        }
                    });
                    btmoin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int qt =1;
                            if(!edt_qt_scan.getText().toString().equals(""))
                            {qt = Integer.parseInt(edt_qt_scan.getText().toString());qt--;}

                            if(qt<0)
                            { qt=0;}
                            edt_qt_scan.setText("" + qt);
                            int qtNbrExemplairePaquet=Integer.parseInt(txt_nb_paquet.getText().toString());
                            int qtNbrPaquetCouche=Integer.parseInt(txt_nb_couche.getText().toString());
                            int qtTotal=qt*qtNbrExemplairePaquet*qtNbrPaquetCouche;
                            txt_qt_total.setText(""+qtTotal);
                            helper.UpdateLigneBonCommandeReception(new LigneBcReception(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(), txt_qt.getText().toString()
                                    , "" + qt,txt_nb_paquet.getText().toString(),""+txt_nb_couche.getText().toString(),""
                                    +txt_epaisseur.getText().toString(), edt_poids.getText().toString(),txt_qt_total.getText().toString()));


                        }
                    });
                    btplus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                int qt =1;
                            if(!edt_qt_scan.getText().toString().equals(""))
                            {qt = Integer.parseInt(edt_qt_scan.getText().toString());qt++;}

                            int qtNbrExemplairePaquet=Integer.parseInt(txt_nb_paquet.getText().toString());
                            int qtNbrPaquetCouche=Integer.parseInt(txt_nb_couche.getText().toString());
                            int qtTotal=qt*qtNbrExemplairePaquet*qtNbrPaquetCouche;
                            txt_qt_total.setText(""+qtTotal);


                            edt_qt_scan.setText("" + qt);
                          //  helper.UpdateLigneBonCommandeReception(new LigneBcReception(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(), txt_qt.getText().toString(), "" + qt));
                            helper.UpdateLigneBonCommandeReception(new LigneBcReception(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(), txt_qt.getText().toString()
                                    , "" + qt,txt_nb_paquet.getText().toString(),""+txt_nb_couche.getText().toString(),""
                                    +txt_epaisseur.getText().toString(), edt_poids.getText().toString(),txt_qt_total.getText().toString()));
                        }
                    });


                    return convertView;
                }
            };
            grid_reception.setAdapter(baseAdapter);


        }

        @Override
        protected String doInBackground(String... params) {

            try {
                cr = helper.getListLigneCommandReception();


                if (cr.moveToFirst()) {
                    do {
                        Log.e("cursor", cr.getString(cr.getColumnIndex("Article")));
                    } while (cr.moveToNext());
                }


            } catch (Exception ex) {
                z = "list" + ex.toString();

            }
            return z;
        }
    }

    public class FillListBcSelect extends AsyncTask<String, String, String> {
        String z = "";

        List<Map<String, String>> prolist = new ArrayList<Map<String, String>>();
        ArrayList<String> list;
        Cursor cr;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected void onPostExecute(String r) {

            progressBar.setVisibility(View.GONE);

            BaseAdapter baseAdapter = new BaseAdapter() {
                @Override
                public int getCount() {
                    return cr.getCount();
                }

                @Override
                public Object getItem(int position) {
                    return null;
                }

                @Override
                public long getItemId(int position) {
                    return 0;
                }

                @Override
                public View getView(final int pos, View convertView, ViewGroup parent) {
                    final LayoutInflater layoutInflater = LayoutInflater.from(co);
                    convertView = layoutInflater.inflate(R.layout.item_bc, null);
                    final TextView code = (TextView) convertView.findViewById(R.id.txt_code);
                    final TextView designation = (TextView) convertView.findViewById(R.id.tx_nom_client);
                    final Button btdelete = (Button) convertView.findViewById(R.id.btdelete);
                    final Button btplus = (Button) convertView.findViewById(R.id.btplus);
                    btdelete.setVisibility(View.GONE);
                    btplus.setVisibility(View.GONE);
                    cr = helper.getListCommandReception();
                    if (cr.move(pos + 1)) {

                        code.setText(cr.getString(cr.getColumnIndex("Code")));
                        designation.setText(cr.getString(cr.getColumnIndex("ClientName")));


                    }


                    final LinearLayout layout = convertView.findViewById(R.id.layouttop);

                    return convertView;
                }
            };
            grid_reception.setAdapter(baseAdapter);


        }

        @Override
        protected String doInBackground(String... params) {

            try {
                cr = helper.getListCommandReception();
                Log.e("back", cr.getColumnIndex("EAN") + "");

                if (cr.moveToFirst()) {
                    do {
                        Log.e("cursor", cr.getString(cr.getColumnIndex("EAN")));
                    } while (cr.moveToNext());
                }


            } catch (Exception ex) {
                z = "list" + ex.toString();

            }
            return z;
        }
    }

    public class FillListLigneReceptionSearch extends AsyncTask<String, String, String> {
        String z = "";

        List<Map<String, String>> prolist = new ArrayList<Map<String, String>>();
        ArrayList<String> list;
        Cursor cr;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);

            helper.UpdateLigneReceptionByScan(searchScan);
        }

        @Override
        protected void onPostExecute(String r) {


            progressBar.setVisibility(View.GONE);

            BaseAdapter baseAdapter = new BaseAdapter() {
                @Override
                public int getCount() {
                    return cr.getCount();
                }

                @Override
                public Object getItem(int position) {
                    return null;
                }

                @Override
                public long getItemId(int position) {
                    return 0;
                }

                @Override
                public View getView(final int pos, View convertView, ViewGroup parent) {
                    final LayoutInflater layoutInflater = LayoutInflater.from(co);

                    convertView = layoutInflater.inflate(R.layout.item_reception, null);
                    final TextView txt_noDoc = (TextView) convertView.findViewById(R.id.txt_noDoc);
                    final TextView txt_ean = (TextView) convertView.findViewById(R.id.txt_ean);
                    final TextView txt_code_article = (TextView) convertView.findViewById(R.id.txt_code_article);
                    final TextView txt_piece = (TextView) convertView.findViewById(R.id.txt_piece);
                    final TextView txt_qt = (TextView) convertView.findViewById(R.id.txt_qt);
                    final TextView txt_nb_couche = (TextView) convertView.findViewById(R.id.txt_nb_couche);
                    final TextView txt_nb_paquet = (TextView) convertView.findViewById(R.id.txt_nb_paquet);
                    final EditText txt_epaisseur = (EditText) convertView.findViewById(R.id.txt_epaisseur);
                    final EditText txt_qt_total = (EditText) convertView.findViewById(R.id.qt_total);
                    final EditText edt_qt_scan = (EditText) convertView.findViewById(R.id.edt_qt_scan);
                    final EditText edt_poids = (EditText) convertView.findViewById(R.id.txt_poids);
                    final Button btmoin = (Button) convertView.findViewById(R.id.btmoin);
                    final Button btplus = (Button) convertView.findViewById(R.id.btplus);
                    //noDoc TEXT  ,Code TEXT, Quantite INTEGER, QuantiteScan INTEGER

                    cr = helper.getLigneReceptionArticle(searchScan);
                    if (cr.move(pos + 1)) {
                        txt_code_article.setText(cr.getString(cr.getColumnIndex("Article")));
                        txt_noDoc.setText(cr.getString(cr.getColumnIndex("noDoc")));
                        txt_qt.setText(cr.getString(cr.getColumnIndex("Quantite")));
                             txt_piece.setText(cr.getString(cr.getColumnIndex("Piece")));
                        txt_ean.setText(cr.getString(cr.getColumnIndex("EAN")));
                        //NbrExemplairePaquet TEXT,NbrPaquetCouche TEXT,Epaisseur TEXT
                        txt_nb_couche.setText(cr.getString(cr.getColumnIndex("NbrPaquetCouche")));
                        txt_nb_paquet.setText(cr.getString(cr.getColumnIndex("NbrExemplairePaquet")));
                        txt_epaisseur.setText(cr.getString(cr.getColumnIndex("Epaisseur")));
                        edt_qt_scan.setText(cr.getString(cr.getColumnIndex("QuantiteScan")));
                        edt_poids.setText(cr.getString(cr.getColumnIndex("Poids")));
                        txt_qt_total.setText( cr.getString(cr.getColumnIndex("QuantiteTotal")));




                    }


                    txt_qt_total.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View view, int i, KeyEvent keyEvent) {

                            String s = txt_qt_total.getText().toString();
                            if (i != KEYCODE_DEL) {
                                if (!s.equals("")) {

                                    helper.UpdateLigneBonCommandeReception(new LigneBcReception(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(), txt_qt.getText().toString()
                                            ,  edt_qt_scan.getText().toString(),txt_nb_paquet.getText().toString(),""+txt_nb_couche.getText().toString(),""
                                            +txt_epaisseur.getText().toString(), edt_poids.getText().toString(),s));
                                    // helper.UpdateLigneBonCommandeReception(new LigneBcReception(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(), txt_qt.getText().toString(), "" + qt));

                                }
                            }
                            return false;
                        }
                    });

                    txt_epaisseur.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View view, int i, KeyEvent keyEvent) {

                            String s = txt_epaisseur.getText().toString();
                            if (i != KEYCODE_DEL) {
                                if (!s.equals("")) {

                                    helper.UpdateLigneBonCommandeReception(new LigneBcReception(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(),
                                            txt_piece.getText().toString(), txt_qt.getText().toString()
                                            , txt_qt_total.getText().toString(),txt_nb_paquet.getText().toString(),""+txt_nb_couche.getText().toString(),""
                                            +s, edt_poids.getText().toString(),txt_qt_total.getText().toString()));
                                    // helper.UpdateLigneBonCommandeReception(new LigneBcReception(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(), txt_qt.getText().toString(), "" + qt));

                                }
                            }
                            return false;
                        }
                    });
                    edt_poids.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View view, int i, KeyEvent keyEvent) {

                            String s = edt_poids.getText().toString();
                            if (i != KEYCODE_DEL) {
                                if (!s.equals("")) {

                                    helper.UpdateLigneBonCommandeReception(new LigneBcReception(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(), txt_qt.getText().toString()
                                            , txt_qt_total.getText().toString(),txt_nb_paquet.getText().toString(),""+txt_nb_couche.getText().toString(),""
                                            +txt_epaisseur.getText().toString(), s,txt_qt_total.getText().toString()));
                                    // helper.UpdateLigneBonCommandeReception(new LigneBcReception(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(), txt_qt.getText().toString(), "" + qt));

                                }
                            }
                            return false;
                        }
                    });

                    edt_qt_scan.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View view, int i, KeyEvent keyEvent) {

                            String s = edt_qt_scan.getText().toString();
                            if (i != KEYCODE_DEL) {
                                if (!s.equals("")) {
                                        int qt =1;
                            if(!edt_qt_scan.getText().toString().equals(""))
                            {qt = Integer.parseInt(edt_qt_scan.getText().toString());}
                                    int qtNbrExemplairePaquet=Integer.parseInt(txt_nb_paquet.getText().toString());
                                    int qtNbrPaquetCouche=Integer.parseInt(txt_nb_couche.getText().toString());
                                    int qtTotal=qt*qtNbrExemplairePaquet*qtNbrPaquetCouche;
                                    txt_qt_total.setText(""+qtTotal);
                                    helper.UpdateLigneBonCommandeReception(new LigneBcReception(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(), txt_qt.getText().toString()
                                            , "" + qt,txt_nb_paquet.getText().toString(),""+txt_nb_couche.getText().toString(),""
                                            +txt_epaisseur.getText().toString(), edt_poids.getText().toString(),txt_qt_total.getText().toString()));
                                   // helper.UpdateLigneBonCommandeReception(new LigneBcReception(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(), txt_qt.getText().toString(), "" + qt));

                                }
                            }
                            return false;
                        }
                    });

                    btmoin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                                int qt =1;
                            if(!edt_qt_scan.getText().toString().equals(""))
                            {qt = Integer.parseInt(edt_qt_scan.getText().toString());qt--;}

                            if(qt<0)
                            { qt=0;}
                            edt_qt_scan.setText("" + qt);
                            int qtNbrExemplairePaquet=Integer.parseInt(txt_nb_paquet.getText().toString());
                            int qtNbrPaquetCouche=Integer.parseInt(txt_nb_couche.getText().toString());
                            int qtTotal=qt*qtNbrExemplairePaquet*qtNbrPaquetCouche;
                            txt_qt_total.setText(""+qtTotal);
                            //helper.UpdateLigneBonCommandeReception(new LigneBcReception(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(), txt_qt.getText().toString(), "" + qt));
                            helper.UpdateLigneBonCommandeReception(new LigneBcReception(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(), txt_qt.getText().toString()
                                    , "" + qt,txt_nb_paquet.getText().toString(),""+txt_nb_couche.getText().toString(),""
                                    +txt_epaisseur.getText().toString(), edt_poids.getText().toString(),txt_qt_total.getText().toString()));


                        }
                    });
                    btplus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                int qt =1;
                            if(!edt_qt_scan.getText().toString().equals(""))
                            {qt = Integer.parseInt(edt_qt_scan.getText().toString()); qt++;}

                            int qtNbrExemplairePaquet=Integer.parseInt(txt_nb_paquet.getText().toString());
                            int qtNbrPaquetCouche=Integer.parseInt(txt_nb_couche.getText().toString());
                            int qtTotal=qt*qtNbrExemplairePaquet*qtNbrPaquetCouche;
                            txt_qt_total.setText(""+qtTotal);

                            edt_qt_scan.setText("" + qt);
                            //helper.UpdateLigneBonCommandeReception(new LigneBcReception(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(), txt_qt.getText().toString(), "" + qt));
                            helper.UpdateLigneBonCommandeReception(new LigneBcReception(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(), txt_qt.getText().toString()
                                    , "" + qt,txt_nb_paquet.getText().toString(),""+txt_nb_couche.getText().toString(),""
                                    +txt_epaisseur.getText().toString(), edt_poids.getText().toString(),txt_qt_total.getText().toString()));

                        }
                    });


                    return convertView;
                }
            };
            grid_reception.setAdapter(baseAdapter);


        }

        @Override
        protected String doInBackground(String... params) {

            try {
                cr = helper.getLigneReceptionArticle(searchScan);
                Log.e("back", cr.getColumnIndex("Code") + "");

                if (cr.moveToFirst()) {
                    do {
                        Log.e("cursor", cr.getString(cr.getColumnIndex("EAN")));
                    } while (cr.moveToNext());
                }


            } catch (Exception ex) {
                z = "list" + ex.toString();

            }
            return z;
        }
    }

    void CreateReception() {
        try {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = baseUrlCreateReception;
            progressBar.setVisibility(View.VISIBLE);
            JSONArray arrayJson = new JSONArray();
            Cursor cr = helper.getListLigneCommandReception();
            if (cr.moveToFirst()) {
                do {
                    //       cv.put("Article", c.getArticle());
                    //        cv.put("Quantite", c.getQuantite());
                    //        cv.put("QuantiteScan", c.getQuantiteScan());
                    //        cv.put("noDoc", c.getNoDoc());
                    //        cv.put("Piece", c.getPiece());
                    //        cv.put("EAN", c.getEAN());
                    // "inputJson" : "[{"NoDoc":"101021","Article":"1896-S","Quantite":"8"},{"NoDoc":"101023","Article":"1896-S","Quantite":"6"}]"


                    JSONObject obj = new JSONObject().put("NoDoc", cr.getString(cr.getColumnIndex("noDoc")))
                            .put("Quantite", cr.getString(cr.getColumnIndex("QuantiteTotal")))
                            .put("Epaisseur", cr.getString(cr.getColumnIndex("Epaisseur")))
                            .put("Poids", cr.getString(cr.getColumnIndex("Poids")))
                            .put("Article", cr.getString(cr.getColumnIndex("Article")));

                    arrayJson.put(obj);

                } while (cr.moveToNext());
            }
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("inputJson", arrayJson.toString());
            Log.d("****create  ", jsonBody.toString());

            final String mRequestBody = jsonBody.toString();
            StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            progressBar.setVisibility(View.GONE);
                            

                            helper.DeleteListBonCommandReception();
                            helper.DeleteLigneBonCommandReception();
                            helper.DeleteValideBonCommandReception();
                            layout_valider.setVisibility(View.GONE);
                            bt_view.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "reception crée avec succés", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                            startActivity(intent);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.GONE);
                            // TODO Auto-generated method stub
                            
                            Log.e("****ERROR", "error => " + error.toString());
                            Toast.makeText(getApplicationContext(), "error api" + error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", getResources().getString(R.string.key_autorisation));
                    params.put("Content-Type", "application/json");
                    params.put("Company", getResources().getString(R.string.company_name));

                    return params;
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mRequestBody, "utf-8");
                        Log.e("ERROR", "errorgetbody => " + uee.toString());
                        return null;
                    }
                }
            };
            queue.add(getRequest);
        } catch (Exception e) {
            Log.e("error", e.toString());
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}