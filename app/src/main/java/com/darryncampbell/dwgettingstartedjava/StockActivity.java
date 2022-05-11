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
import com.darryncampbell.dwgettingstartedjava.Model.Article.Article;
import com.darryncampbell.dwgettingstartedjava.Model.ConsultArticle;
import com.darryncampbell.dwgettingstartedjava.Model.Transfert.LigneReturn;
import com.darryncampbell.dwgettingstartedjava.Model.Value;
import com.darryncampbell.dwgettingstartedjava.Model.reception.LigneBcReception;
import com.darryncampbell.dwgettingstartedjava.Model.stock.LigneStock;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StockActivity extends AppCompatActivity implements View.OnTouchListener {
    Button btScan;
    Button btValid;
    Button btCancel;
    Button btAjustement;
    TextView txtCodeClient, txtNomClient;
    GridView gridReturn;
    final Context co = this;

    String baseUrlConsultArticle = "";
    String baseUrlCreateReturn = "";
    String baseUrlConsult = "";
    ProgressBar progressBar;
    Helper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        baseUrlConsult = getResources().getString(R.string.base_url) + "WmsApp_ConsultItem?$format=application/json;odata.metadata=none";
        baseUrlCreateReturn = getResources().getString(R.string.base_url) + "WmsApp_AdjustItemQty?$format=application/json;odata.metadata=none";
        btScan = (Button) findViewById(R.id.btnScan);
        btCancel = (Button) findViewById(R.id.bt_cancel);
        txtCodeClient = (TextView) findViewById(R.id.txt_code_client);
        txtNomClient = (TextView) findViewById(R.id.txt_nom_client);
        gridReturn = (GridView) findViewById(R.id.grid_stock);
        baseUrlConsultArticle = getResources().getString(R.string.base_url) + "WmsApp_GetItemFromBarcode?$format=application/json;odata.metadata=none";
        baseUrlCreateReturn = getResources().getString(R.string.base_url) + "WmsApp_AdjustItemQty?$format=application/json;odata.metadata=none";
        DWUtilities.CreateDWProfile(co, "com.stock.action");
        btScan.setOnTouchListener(this);
        helper = new Helper(getApplicationContext());




        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alt = new AlertDialog.Builder(co);
                alt.setIcon(R.drawable.icon_return);
                alt.setTitle("Annuler");
                alt.setMessage("Voulez-vous vraiment annuler l'ajustement?");
                alt.setNegativeButton("oui",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface di, int i) {

                                helper.DeleteLigneStock();

                                finish();
                                startActivity(getIntent());
                            }
                        })
                        . setPositiveButton("non",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface di, int i) {
                                        //FillListConsult("9782129822910");
                                        di.cancel();
                                    }
                                });

                final AlertDialog d = alt.create();
                d.show();

            }
        });

    }

    public class FillListLigneStock extends AsyncTask<String, String, String> {
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

                    convertView = layoutInflater.inflate(R.layout.item_article, null);

                    final TextView txtCode = (TextView) convertView.findViewById(R.id.txt_code_article);
                    final TextView txtDesignation = (TextView) convertView.findViewById(R.id.txt_designation);
                    final TextView txt_from = (TextView) convertView.findViewById(R.id.txt_from);
                    final EditText edt_qt_scan = (EditText) convertView.findViewById(R.id.edt_qt_scan);
                    final Button btmoin = (Button) convertView.findViewById(R.id.btmoin);
                    final Button btplus = (Button) convertView.findViewById(R.id.btplus);
                    cr = helper.getListLigneStock();
                    if (cr.move(pos + 1)) {

                        txtCode.setText(cr.getString(cr.getColumnIndex("Article")));
                        edt_qt_scan.setText(cr.getString(cr.getColumnIndex("Quantite")));
                        txt_from.setText(cr.getString(cr.getColumnIndex("FromBin")));
                        txtDesignation.setText("");


                    }

                    edt_qt_scan.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View view, int i, KeyEvent keyEvent) {

                            String s = edt_qt_scan.getText().toString();
                            if (i != KEYCODE_DEL) {
                                if (!s.equals("")) {
                                    int qt = Integer.parseInt(edt_qt_scan.getText().toString());
                                    helper.UpdateLigneStock(new LigneStock(txt_from.getText().toString(), txtCode.getText().toString(), "" + qt));

                                }
                            }
                            return false;
                        }
                    });

                    btmoin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int qt = Integer.parseInt(edt_qt_scan.getText().toString());
                            qt--;
                            if (qt < 0) {
                                qt = 0;
                            }
                            edt_qt_scan.setText("" + qt);
                            helper.UpdateLigneStock(new LigneStock(txt_from.getText().toString(), txtCode.getText().toString(), "" + qt));


                        }
                    });
                    btplus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int qt = Integer.parseInt(edt_qt_scan.getText().toString());
                            qt++;


                            edt_qt_scan.setText("" + qt);
                            helper.UpdateLigneStock(new LigneStock(txt_from.getText().toString(), txtCode.getText().toString(), "" + qt));

                        }
                    });

                    return convertView;
                }
            };
            gridReturn.setAdapter(baseAdapter);


        }

        @Override
        protected String doInBackground(String... params) {

            try {
                cr = helper.getListLigneStock();


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


    void FillListConsult(final String scan) {
        try {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = baseUrlConsult;
            progressBar.setVisibility(View.VISIBLE);
            JSONObject jsonEAN = new JSONObject().put("EAN", scan);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("inputJson", jsonEAN.toString());
            

            final String mRequestBody = jsonBody.toString();
            StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            progressBar.setVisibility(View.GONE);
                            
                            JSONObject obj = null;

                            try {
                                obj = new JSONObject(response);

                                
                                JSONArray array = new JSONArray(obj.getString("value"));

                                JSONObject jsonList = new JSONObject().put("value", array);


                                ConsultArticle data = new ConsultArticle();
                                Gson gson = new Gson();
                                data = gson.fromJson(jsonList.toString(), ConsultArticle.class);
                                final ConsultArticle finalData = data;
                                Log.d("tag****", finalData.toString());
                                // Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
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
                                        convertView = layoutInflater.inflate(R.layout.item_stock, null);
                                        final TextView txt_code_article = (TextView) convertView.findViewById(R.id.txt_code_article);
                                        final TextView txt_designation = (TextView) convertView.findViewById(R.id.txt_designation);

                                        final EditText edt_qt = (EditText) convertView.findViewById(R.id.edt_qt_scan);
                                        final LinearLayout layout_item = (LinearLayout) convertView.findViewById(R.id.layout_item);

                                        final TextView txt_bin_code = (TextView) convertView.findViewById(R.id.txt_bin_code);
                                        final Button bt_valide_ligne = (Button) convertView.findViewById(R.id.bt_valide_ligne);

                                       
                                        final Value val = finalData.getValue().get(position);
                                        
                                        txt_code_article.setText(val.getArticle());
                                        edt_qt.setText(val.getQuantite());
                                        txt_bin_code.setText(val.getPiece());
                                        txt_designation.setText(val.getTitre());
                                        edt_qt.setOnKeyListener(new View.OnKeyListener() {
                                            @Override
                                            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                                                String s = edt_qt.getText().toString();
                                                if (i != KEYCODE_DEL) {
                                                    if (!s.equals("")) {
                                                       helper.UpdateLigneStock(new LigneStock( txt_bin_code.getText().toString(),txt_code_article.getText().toString(),s));
                                                    }
                                                }
                                                return false;
                                            }
                                        });
                                        if (helper.getLigneStock(txt_code_article.getText().toString(), txt_bin_code.getText().toString()).getCount() > 0)
                                        {layout_item.setVisibility(View.GONE);}


                                        bt_valide_ligne.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                               LigneStock ligne= new LigneStock(txt_bin_code.getText().toString(), txt_code_article.getText().toString(), edt_qt.getText().toString());
                                               CreateAjustementStock(ligne , scan);

                                            }
                                        });


                                        return convertView;
                                    }
                                };
                                gridReturn.setAdapter(baseAdapter);
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
                            
                            Log.d("ERROR", "error => " + error.getMessage());
                            Toast.makeText(getApplicationContext(), " error api : " + error.toString(), Toast.LENGTH_SHORT).show();
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
    void CreateAjustementStock(LigneStock ligne, final String scan) {
        try {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = baseUrlCreateReturn;
            progressBar.setVisibility(View.VISIBLE);
            JSONObject obj = new JSONObject().put("FromBin", ligne.getFromBin())
                    .put("Quantite",ligne.getQuantite() )
                    .put("Article",  ligne.getArticle());
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("inputJson", obj.toString());
            Log.d("***create  ", jsonBody.toString());

            final String mRequestBody = jsonBody.toString();
            StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            progressBar.setVisibility(View.GONE);
                            

                            helper.DeleteLigneStock();



                            Toast.makeText(getApplicationContext(), "Ajustement est affectué avec succés", Toast.LENGTH_SHORT).show();
                           FillListConsult(scan);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.GONE);
                            // TODO Auto-generated method stub
                            Log.d("**ERROR", "error => " + error.getLocalizedMessage());
                            Log.d("**ERROR", "error => " + error.getMessage());
                            Toast.makeText(getApplicationContext(), " error api : " + error.toString(), Toast.LENGTH_SHORT).show();
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
            Log.e("***error", e.toString());
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
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
        String scan = decodedData;

        FillListConsult(scan);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (view.getId() == R.id.btnScan) {
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

}