package com.darryncampbell.dwgettingstartedjava;

import static android.view.KeyEvent.KEYCODE_DEL;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
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
import com.darryncampbell.dwgettingstartedjava.Model.Client.Client;
import com.darryncampbell.dwgettingstartedjava.Model.Return.LigneEcartReturn;
import com.darryncampbell.dwgettingstartedjava.Model.Return.LigneSelectReturn;
import com.darryncampbell.dwgettingstartedjava.Model.Return.ListEcartReturn;
import com.darryncampbell.dwgettingstartedjava.Model.Return.ListLigneSelectReturn;
import com.darryncampbell.dwgettingstartedjava.Model.Return.ListSelectReturn;
import com.darryncampbell.dwgettingstartedjava.Model.Return.SelectReturn;
import com.darryncampbell.dwgettingstartedjava.Model.Transfert.LigneReturn;
import com.darryncampbell.dwgettingstartedjava.Model.Value;
import com.darryncampbell.dwgettingstartedjava.Model.prelevement.LigneBcPrelevement;
import com.darryncampbell.dwgettingstartedjava.Model.prelevement.ListeBonCommande;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReturnActivity extends AppCompatActivity implements View.OnTouchListener {
    Button btClient;
    Button btScan;
    Button btValid;
    Button btCancel;
    Button btSearch;
    Button bt_ecart;
    Button btList;
    TextView txtCodeClient, txtNomClient, txtCodePostalClient, txtCityClient, txtAdressClient, txtNoDoc;
    GridView gridReturn;
    final Context co = this;
    String baseUrlListClient = "";
    String baseUrlConsultArticle = "";
    String baseUrlCreateReturn = "";
    String baseUrlValidReturn = "";
    String baseUrlSelectReturn = "";
    String baseUrlLigneSelectReturn = "";
    String baseUrlEcartReturn = "";
    ProgressBar progressBar;
    Helper helper;


    androidx.appcompat.app.AlertDialog.Builder altGlobal;
    View px;
    EditText edtQt;
    EditText edtGln;
    EditText edtReference;
    androidx.appcompat.app.AlertDialog dialog;
    LinearLayout layoutClient, layoutInfo, layout_search;
    Boolean isNewReturn = false;
    Boolean isEcart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        btClient = (Button) findViewById(R.id.bt_client);
        btScan = (Button) findViewById(R.id.btnScan);
        btValid = (Button) findViewById(R.id.bt_valid);
        btCancel = (Button) findViewById(R.id.bt_cancel);
        btSearch = (Button) findViewById(R.id.bt_search);
        bt_ecart = (Button) findViewById(R.id.bt_ecart);
        btList = (Button) findViewById(R.id.bt_list);
        txtAdressClient = (TextView) findViewById(R.id.txt_address);
        txtCodePostalClient = (TextView) findViewById(R.id.txt_code_postal);
        txtCityClient = (TextView) findViewById(R.id.txt_city);
        txtCodeClient = (TextView) findViewById(R.id.txt_code_client);
        txtNoDoc = (TextView) findViewById(R.id.txt_no_doc);
        txtNomClient = (TextView) findViewById(R.id.txt_nom_client);
        gridReturn = (GridView) findViewById(R.id.grid_return);
        edtGln = (EditText) findViewById(R.id.edt_gln);
        edtReference = (EditText) findViewById(R.id.edt_reference);
        layoutClient = (LinearLayout) findViewById(R.id.layout_client);
        layoutInfo = (LinearLayout) findViewById(R.id.layout);
        layout_search = (LinearLayout) findViewById(R.id.layout_search);
        baseUrlListClient = getResources().getString(R.string.base_url) + "WmsApp_GetCustomerFromGLN?$format=application/json;odata.metadata=none";
        baseUrlConsultArticle = getResources().getString(R.string.base_url) + "WmsApp_GetReturnItem?$format=application/json;odata.metadata=none";
        baseUrlCreateReturn = getResources().getString(R.string.base_url) + "WmsApp_CreateReturn?$format=application/json;odata.metadata=none";
        baseUrlValidReturn = getResources().getString(R.string.base_url) + "WmsApp_ValidateReturn?$format=application/json;odata.metadata=none";
        baseUrlSelectReturn = getResources().getString(R.string.base_url) + "WmsApp_SalesReturnOrderList?$format=application/json;odata.metadata=none";
        baseUrlLigneSelectReturn = getResources().getString(R.string.base_url) + "WmsApp_SelectedSalesReturnLigne?$format=application/json;odata.metadata=none";
        baseUrlEcartReturn = getResources().getString(R.string.base_url) + "WmsApp_EcartRetour?$format=application/json;odata.metadata=none";
        DWUtilities.CreateDWProfile(co, "com.retour.action");
        btScan.setOnTouchListener(this);
        helper = new Helper(getApplicationContext());
        btClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (helper.getListLigneReturn().getCount() > 0) {
                    final AlertDialog.Builder alt = new AlertDialog.Builder(co);
                    alt.setIcon(R.drawable.icon_return);
                    alt.setTitle("Annuler");
                    alt.setMessage("Voulez-vous vraiment annuler le bon de retour ? ");
                    alt.setNegativeButton("oui",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface di, int i) {

                                    helper.DeleteClientReturn();
                                    helper.DeleteLigneReturn();
                                    txtCodeClient.setText("");
                                    txtCityClient.setText("");
                                    txtCodePostalClient.setText("");
                                    txtAdressClient.setText("");
                                    txtNomClient.setText("");
                                    edtGln.setText("");
                                    layout_search.setVisibility(View.VISIBLE);
                                    FillListLigneReturn fillListLigneReturn = new FillListLigneReturn("");
                                    fillListLigneReturn.execute("");
                                }
                            })
                            .setPositiveButton("non",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface di, int i) {
                                            di.cancel();
                                        }
                                    });

                    final AlertDialog d = alt.create();
                    d.show();
                } else {
                    helper.DeleteClientReturn();
                    txtCodeClient.setText("");
                    txtCityClient.setText("");
                    txtCodePostalClient.setText("");
                    txtAdressClient.setText("");
                    txtNomClient.setText("");
                    edtGln.setText("");
                    layout_search.setVisibility(View.VISIBLE);

                }


            }
        });
        btValid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (helper.getListLigneReturn().getCount() > 0) {
                    //choices
                    LayoutInflater li = LayoutInflater.from(co);
                    View px = li.inflate(R.layout.item_action_return, null);
                    final androidx.appcompat.app.AlertDialog.Builder alt = new androidx.appcompat.app.AlertDialog.Builder(co);
                    alt.setIcon(R.drawable.icon_article);
                    alt.setTitle("Valider ");
                    alt.setView(px);

                    // connectionClass = new ConnectionClass();

                    final Button option1 = (Button) px.findViewById(R.id.bt_option1);
                    final Button option2 = (Button) px.findViewById(R.id.bt_option2);
                    final Button option3 = (Button) px.findViewById(R.id.bt_option3);
                    final Button option4 = (Button) px.findViewById(R.id.bt_option4);
                    option1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ValidReturn("1");
                        }
                    });

                    option2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ValidReturn("2");
                        }
                    });
                    option3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ValidReturn("3");
                        }
                    });
                    option4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ValidReturn("4");
                        }
                    });
                    alt.setCancelable(false);
                    alt.setNegativeButton("Annuler",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface di, int i) {
                                    di.cancel();
                                }
                            });

                    final androidx.appcompat.app.AlertDialog d = alt.create();


                    d.show();
                } else {
                    Toast.makeText(getApplicationContext(), "Entrer des articles d'abord", Toast.LENGTH_LONG).show();
                }
                //   FillListConsultListClient("3025591324608");

                // FillListConsultArticle("9782129822910");


            }
        });
        bt_ecart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (helper.getListLigneReturn().getCount() > 0) {

                    if (edtReference.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "vous devez saisir la référence du bon ", Toast.LENGTH_SHORT).show();
                    } else {
                        FillLigneEcartReturn(txtNoDoc.getText().toString());
                    }


                } else {
                    Toast.makeText(getApplicationContext(), "Entrer des articles d'abord", Toast.LENGTH_LONG).show();
                }
                //   FillListConsultListClient("3025591324608");

                // FillListConsultArticle("9782129822910");


            }
        });
        btCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alt = new AlertDialog.Builder(co);
                alt.setIcon(R.drawable.icon_return);
                alt.setTitle("Annuler");
                alt.setMessage("Voulez-vous vraiment annuler le bon de retour? ");
                alt.setNegativeButton("oui",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface di, int i) {

                                helper.DeleteLigneReturn();
                                helper.DeleteClientReturn();
                                finish();
                                startActivity(getIntent());
                            }
                        })
                        .setPositiveButton("non",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface di, int i) {
actionScan("3294680002776");
                                        di.cancel();


                                    }
                                });

                final AlertDialog d = alt.create();
                d.show();

            }
        });


        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FillListConsultListClient(edtGln.getText().toString());
            }
        });
        layoutClient.setVisibility(View.GONE);
        btClient.setVisibility(View.GONE);
        btList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initInfo();

            }
        });
        initInfo();

    }

    void initNewReturn() {
        Log.d("VI", "initNewReturn");
        layoutClient.setVisibility(View.VISIBLE);
        layoutInfo.setVisibility(View.VISIBLE);
        btClient.setVisibility(View.VISIBLE);
        isNewReturn = true;
    }

    void visibleListScan() {
        Log.d("VI", "visibleListScan");

        layoutInfo.setVisibility(View.VISIBLE);
        btCancel.setVisibility(View.VISIBLE);

        bt_ecart.setVisibility(View.VISIBLE);
        edtReference.setVisibility(View.VISIBLE);
        layout_search.setVisibility(View.GONE);
    }

    void hideListScan() {
        Log.d("VI", "hideListScan");
        layoutInfo.setVisibility(View.GONE);
        btCancel.setVisibility(View.GONE);
        btValid.setVisibility(View.GONE);
        bt_ecart.setVisibility(View.GONE);
        edtReference.setVisibility(View.GONE);
        layout_search.setVisibility(View.VISIBLE);
    }

    void initInfo() {
        btValid.setVisibility(View.GONE);
        Cursor cr = helper.getClientReturn();
        Log.d("getClient", cr.getCount() + "");
        if (cr.getCount() > 0) {
            if (cr.move(1)) {
                txtCodeClient.setText(cr.getString(cr.getColumnIndex("NoClient")));
                txtNomClient.setText(cr.getString(cr.getColumnIndex("Designation")));
                txtAdressClient.setText(cr.getString(cr.getColumnIndex("Address")));
                txtCityClient.setText(cr.getString(cr.getColumnIndex("City")));
                txtCodePostalClient.setText(cr.getString(cr.getColumnIndex("PostCode")));
                txtNoDoc.setText(cr.getString(cr.getColumnIndex("NoDoc")));

//                isNewReturn = cr.getString(cr.getColumnIndex("NoDoc")).equals("");
//                if(isNewReturn)
//                {
//                    initNewReturn();
//                }

                FillListLigneReturn fillListLigneReturn = new FillListLigneReturn("");
                fillListLigneReturn.execute("");
                visibleListScan();

            }
        } else {
            FillListSelectReturn();
            hideListScan();
        }

    }

    void FillListConsultArticle(final String scan) {
        try {
            visibleListScan();
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = baseUrlConsultArticle;
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

                                
                                if (obj.getString("value").equals("Article Introuvable")) {
                                    mediaPlayerStart(false);

                                    Toast.makeText(getApplicationContext(), "Article Introuvable", Toast.LENGTH_SHORT).show();
                                } else {


                                    Article data = new Article();
                                    Gson gson = new Gson();
                                    data = gson.fromJson(obj.getString("value"), Article.class);
                                    final Article article = data;
                                    Log.d("tag****", article.toString());
                                    Cursor cr = helper.getLigneReturnByEAN(scan);
                                    if (cr.getCount() > 0) {
                                        cr.move(1);
                                        float qt = Float.parseFloat(cr.getString(cr.getColumnIndex("QuantiteScan"))) + 1;
                                        helper.UpdateLigneReturn(new LigneSelectReturn(cr.getString(cr.getColumnIndex("NoDoc")),
                                                cr.getString(cr.getColumnIndex("EAN")),
                                                cr.getString(cr.getColumnIndex("Article")),
                                                cr.getString(cr.getColumnIndex("Quantite")), "" + qt,
                                                cr.getInt(cr.getColumnIndex("Rejection")) > 0,
                                                cr.getInt(cr.getColumnIndex("Damaged")) > 0
                                        ));
                                        if (cr.getInt(cr.getColumnIndex("Rejection")) > 0) {
                                            mediaPlayerStart(false);

                                        }
                                        FillListLigneReturn fillListLigneReturn = new FillListLigneReturn(scan);
                                        fillListLigneReturn.execute("");


                                    } else {
                                        helper.AddLigneReturn(new LigneSelectReturn("", scan, article.getArticle(), "0",
                                                "1", article.getRejection(), false));
                                        FillListLigneReturn fillListLigneReturn = new FillListLigneReturn(scan);
                                        fillListLigneReturn.execute("");

                                    }

                                }


                            } catch (JSONException e) {
                                e.printStackTrace();

                            } catch (Exception e) {
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
                            Toast.makeText(getApplicationContext(), " error api article : " + error.toString(), Toast.LENGTH_SHORT).show();
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

    void FillListConsultListClient(String scan) {
        try {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = baseUrlListClient;
            progressBar.setVisibility(View.VISIBLE);
            JSONObject jsonEAN = new JSONObject().put("GLN", scan);
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

                                

                                Client data = new Client();
                                Gson gson = new Gson();
                                if (obj.getString("value").equals("Client Introuvable")) {
                                    Toast.makeText(getApplicationContext(), "Client Introuvable", Toast.LENGTH_SHORT).show();
                                } else {
                                    data = gson.fromJson(obj.getString("value"), Client.class);
                                    helper.AddClient(data);
                                    txtAdressClient.setText(data.getAddress());
                                    txtCityClient.setText(data.getCity());
                                    txtCodePostalClient.setText(data.getPostCode());
                                    txtCodeClient.setText(data.getClient());
                                    txtNomClient.setText(data.getDescription());
                                }


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


    public class FillListLigneReturn extends AsyncTask<String, String, String> {
        String z = "";
        String EAN = "";

        List<Map<String, String>> prolist = new ArrayList<Map<String, String>>();
        ArrayList<String> list;
        Cursor cr;

        public FillListLigneReturn(String Scan) {
            super();
            EAN = Scan;
        }

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
                    convertView = layoutInflater.inflate(R.layout.item_ligne_return, null);
                    final TextView txt_doc = (TextView) convertView.findViewById(R.id.txt_no_doc);
                    final TextView txt_ean = (TextView) convertView.findViewById(R.id.txt_ean);
                    final TextView txt_code_article = (TextView) convertView.findViewById(R.id.txt_code_article);
                    final TextView txt_qt = (TextView) convertView.findViewById(R.id.txt_qt);
                    final EditText edt_qt_scan = (EditText) convertView.findViewById(R.id.edt_qt_scan);
                    final RadioButton radio_rejected = (RadioButton) convertView.findViewById(R.id.radio_rejected);
                    final CheckBox rd_damaged = (CheckBox) convertView.findViewById(R.id.rd_damaged);
                    if (isNewReturn) {
                        txt_qt.setVisibility(View.GONE);
                    }
                    rd_damaged.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            helper.UpdateLigneReturn(new LigneSelectReturn(txt_doc.getText().toString(), txt_ean.getText().toString(),
                                    txt_code_article.getText().toString(), txt_qt.getText().toString(), edt_qt_scan.getText().toString(),
                                    radio_rejected.isChecked(), rd_damaged.isChecked()));
                        }
                    });
                    cr = helper.getListLigneReturn();
                    if (cr.move(pos + 1)) {

                        txt_ean.setText(cr.getString(cr.getColumnIndex("EAN")));
                        edt_qt_scan.setText(cr.getString(cr.getColumnIndex("QuantiteScan")));
                        txt_qt.setText(cr.getString(cr.getColumnIndex("Quantite")));
                        txt_doc.setText(cr.getString(cr.getColumnIndex("NoDoc")));
                        txt_code_article.setText(cr.getString(cr.getColumnIndex("Article")));
                        radio_rejected.setChecked(cr.getInt(cr.getColumnIndex("Rejection")) > 0);
                        rd_damaged.setChecked(cr.getInt(cr.getColumnIndex("Damaged")) > 0);

                    }


                    edt_qt_scan.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View view, int i, KeyEvent keyEvent) {

                            String s = edt_qt_scan.getText().toString();
                            if (i != KEYCODE_DEL) {
                                if (!s.equals("")) {
                                    float qt = Float.valueOf(edt_qt_scan.getText().toString());
                                    float qtPrevu = Float.valueOf(txt_qt.getText().toString());
                                    if (qt > qtPrevu) {
                                        mediaPlayerStart(true);
                                        edt_qt_scan.setText(qtPrevu + "");
                                        Toast.makeText(getApplicationContext(), "Quantite dépassé", Toast.LENGTH_SHORT).show();

                                    } else {
                                        helper.UpdateLigneReturn(new LigneSelectReturn(txt_doc.getText().toString(), txt_ean.getText().toString(),
                                                txt_code_article.getText().toString(), txt_qt.getText().toString(), "" + qt,
                                                radio_rejected.isChecked(), rd_damaged.isChecked()));

                                    }
                                }
                            }
                            return false;
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
                if (EAN.equals("")) {
                    cr = helper.getListLigneReturn();
                } else {
                    cr = helper.getLigneReturnByEAN(EAN);
                }


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
        actionScan(scan);

    }

    public void actionScan(String scan) {
        if (scan != null) {
            //Toast.makeText(getApplicationContext(),scan,Toast.LENGTH_SHORT).show();
            if (txtCodeClient.getText().toString().equals("")) {
                FillListConsultListClient(scan);
            } else {
                if (isNewReturn) {

                    FillListConsultArticle(scan);


                } else {
                    Cursor cr = helper.getLigneReturnByEAN(scan);
                    if (cr.getCount() > 0) {
                        cr.move(1);
                        float qt = Float.parseFloat(cr.getString(cr.getColumnIndex("QuantiteScan"))) + 1;
                        float qtPrevu = Float.parseFloat(cr.getString(cr.getColumnIndex("Quantite")));

                        if (qt > qtPrevu) {
                            mediaPlayerStart(true);
                            Toast.makeText(getApplicationContext(), "Quantite dépassé", Toast.LENGTH_SHORT).show();

                        } else {
                            helper.UpdateLigneReturn(new LigneSelectReturn(cr.getString(cr.getColumnIndex("NoDoc")),
                                    cr.getString(cr.getColumnIndex("EAN")),
                                    cr.getString(cr.getColumnIndex("Article")),
                                    cr.getString(cr.getColumnIndex("Quantite")), "" + qt,
                                    cr.getInt(cr.getColumnIndex("Rejection")) > 0,
                                    cr.getInt(cr.getColumnIndex("Damaged")) > 0
                            ));
                            if (cr.getInt(cr.getColumnIndex("Rejection")) > 0) {
                                mediaPlayerStart(false);

                            }

                        }
                        FillListLigneReturn fillListLigneReturn = new FillListLigneReturn(scan);
                        fillListLigneReturn.execute("");
                    } else {
                        mediaPlayerStart(true);
                        Toast.makeText(getApplicationContext(), "Cet article n'existe pas dans le bon de retour Num:" + txtNoDoc.getText().toString(), Toast.LENGTH_SHORT).show();
                    }


                }
            }
        }
    }

    public void mediaPlayerStart(Boolean isError) {
        final MediaPlayer mMediaPlayer;
        if (isError)
            mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alerte_cancel);
        else
            mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alert_notif);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                mMediaPlayer.stop();
            }
        }, 1500);

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

    void CreateReturn() {
        try {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = baseUrlCreateReturn;
            progressBar.setVisibility(View.VISIBLE);
            JSONArray arrayJson = new JSONArray();
            Cursor cr = helper.getListLigneReturn();
            if (cr.moveToFirst()) {
                do {
                    JSONObject obj = new JSONObject().put("NoClient", cr.getString(cr.getColumnIndex("NoClient")))
                            .put("Quantite", cr.getString(cr.getColumnIndex("Quantite")))
                            .put("Article", cr.getString(cr.getColumnIndex("Article")))
                            .put("Reference", edtReference.getText().toString());

                    arrayJson.put(obj);

                } while (cr.moveToNext());
            }
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("inputJson", arrayJson.toString());
            Log.d("*** create  ", jsonBody.toString());

            final String mRequestBody = jsonBody.toString();
            StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            progressBar.setVisibility(View.GONE);
                            

                            helper.DeleteClientReturn();
                            helper.DeleteLigneReturn();


                            Toast.makeText(getApplicationContext(), "Retour créé avec succès", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                            startActivity(intent);

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

    void ValidReturn(String option) {
        try {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = baseUrlValidReturn;
            progressBar.setVisibility(View.VISIBLE);
            JSONArray arrayJson = new JSONArray();
            Cursor cr = helper.getListLigneReturn();
            if (cr.moveToFirst()) {
                do {
                    JSONObject obj = new JSONObject().put("NoDoc", txtNoDoc.getText().toString())
                            .put("Quantite", cr.getString(cr.getColumnIndex("QuantiteScan")))
                            .put("Article", cr.getString(cr.getColumnIndex("Article")))
                            .put("Damaged", cr.getInt(cr.getColumnIndex("Damaged")) > 0)
                            .put("Option", option);

                    arrayJson.put(obj);

                } while (cr.moveToNext());
            }
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("inputJson", arrayJson.toString());
            Log.d("*** create  ", jsonBody.toString());

            final String mRequestBody = jsonBody.toString();
            StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            progressBar.setVisibility(View.GONE);
                            

                            helper.DeleteClientReturn();
                            helper.DeleteLigneReturn();


                            Toast.makeText(getApplicationContext(), "Retour créé avec succès", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                            startActivity(intent);

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

    void FillListSelectReturn() {
        try {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = baseUrlSelectReturn;
            Log.d("****url", url);
            progressBar.setVisibility(View.VISIBLE);
            visibleListScan();
            StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            progressBar.setVisibility(View.GONE);
                            
                            ListSelectReturn data = new ListSelectReturn();
                            JSONObject obj = null;
                            try {
                                obj = new JSONObject(response);
                                
                                JSONArray array = new JSONArray(obj.getString("value"));

                                JSONObject jsonList = new JSONObject().put("value", array);


                                Gson gson = new Gson();
                                data = gson.fromJson(jsonList.toString(), ListSelectReturn.class);
                                final ListSelectReturn finalData = data;
                                if (finalData.getValue().size() == 0) {
                                    initNewReturn();
                                } else {
                                    isNewReturn = false;
                                    layoutClient.setVisibility(View.GONE);
                                    btClient.setVisibility(View.GONE);
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
                                            convertView = layoutInflater.inflate(R.layout.item_select_client, null);
                                            final TextView txt_doc = (TextView) convertView.findViewById(R.id.txt_title);
                                            final TextView txt_client = (TextView) convertView.findViewById(R.id.txt_description);
                                            final TextView txt_rs = (TextView) convertView.findViewById(R.id.txt_rs);
                                            final Button bt_select = (Button) convertView.findViewById(R.id.bt_select);
                                            final LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.layout);

                                            final SelectReturn val = finalData.getValue().get(position);
                                            txt_doc.setText(val.getNoDoc());
                                            txt_client.setText(val.getNoClient());
                                            txt_rs.setText(val.getNomClient());

                                            bt_select.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Client client = new Client();
                                                    client.setNoDoc(txt_doc.getText().toString());
                                                    client.setClient(txt_client.getText().toString());
                                                    client.setDescription(txt_rs.getText().toString());
                                                    txtCodeClient.setText(txt_client.getText().toString());
                                                    txtNoDoc.setText(txt_doc.getText().toString());
                                                    txtNomClient.setText(txt_rs.getText().toString());

                                                    helper.AddClient(client);

                                                    FillLigneSelectReturn(txt_doc.getText().toString());
                                                }
                                            });


                                            return convertView;
                                        }
                                    };
                                    gridReturn.setAdapter(baseAdapter);
                                }


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

    void FillLigneSelectReturn(String codeDoc) {
        try {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = baseUrlLigneSelectReturn;
            progressBar.setVisibility(View.VISIBLE);
            visibleListScan();
            JSONObject jsonEAN = new JSONObject().put("NoDoc", codeDoc);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("inputJson", jsonEAN.toString());
            

            final String mRequestBody = jsonBody.toString();
            StringRequest getRequest = new StringRequest(Request.Method.POST, url,

                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            progressBar.setVisibility(View.GONE);
                            
                            ListLigneSelectReturn data = new ListLigneSelectReturn();
                            JSONObject obj = null;
                            try {
                                obj = new JSONObject(response);
                                
                                JSONArray array = new JSONArray(obj.getString("value"));

                                JSONObject jsonList = new JSONObject().put("value", array);


                                Gson gson = new Gson();
                                data = gson.fromJson(jsonList.toString(), ListLigneSelectReturn.class);
                                final ListLigneSelectReturn finalData = data;

                                for (int i = 0; i < finalData.getValue().size(); i++) {
                                    LigneSelectReturn ligne = finalData.getValue().get(i);
                                    ligne.setQuantiteScan("0");
                                    helper.AddLigneReturn(ligne);
                                    FillListLigneReturn fillListLigneReturn = new FillListLigneReturn("");
                                    fillListLigneReturn.execute("");
                                }


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
            //  Toast.makeText(getApplicationContext(), "eror exception" + e.toString(), Toast.LENGTH_SHORT).show();
            Log.e("error", e.toString());

        }
    }

    void FillLigneEcartReturn(String codeDoc) {
        try {
            visibleListScan();

            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = baseUrlEcartReturn;
            progressBar.setVisibility(View.VISIBLE);

            JSONArray arrayJson = new JSONArray();
            Cursor cr = helper.getListLigneReturn();
            if (cr.moveToFirst()) {
                do {
                    JSONObject obj = new JSONObject().put("NoClient", txtCodeClient.getText().toString())
                            .put("Quantite", cr.getString(cr.getColumnIndex("QuantiteScan")))
                            .put("Article", cr.getString(cr.getColumnIndex("Article")))
                            .put("Damaged", cr.getInt(cr.getColumnIndex("Damaged")) > 0)
                            .put("NoDoc", cr.getString(cr.getColumnIndex("NoDoc")))
                            .put("Reference", edtReference.getText().toString());


                    arrayJson.put(obj);

                } while (cr.moveToNext());
            }
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("inputJson", arrayJson.toString());
            Log.d("*** ecart  ", jsonBody.toString());

            final String mRequestBody = jsonBody.toString();
            StringRequest getRequest = new StringRequest(Request.Method.POST, url,

                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            progressBar.setVisibility(View.GONE);
                            btValid.setVisibility(View.VISIBLE);
                            ListEcartReturn data = new ListEcartReturn();
                            JSONObject obj = null;
                            try {
                                obj = new JSONObject(response);
                                
                                JSONArray array = new JSONArray(obj.getString("value"));

                                JSONObject jsonList = new JSONObject().put("value", array);

                                isEcart = false;

                                Gson gson = new Gson();
                                data = gson.fromJson(jsonList.toString(), ListEcartReturn.class);
                                final ListEcartReturn finalData = data;
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
                                        convertView = layoutInflater.inflate(R.layout.item_ecart_return, null);
                                        final TextView txt_article = (TextView) convertView.findViewById(R.id.txt_article);
                                        final TextView txt_ean = (TextView) convertView.findViewById(R.id.txt_ean);
                                        final TextView txt_qt_rejected = (TextView) convertView.findViewById(R.id.txt_qt_rejected);
                                        final TextView txt_qt_scan = (TextView) convertView.findViewById(R.id.txt_qt_scan);
                                        final TextView txt_preview = (TextView) convertView.findViewById(R.id.txt_preview);

                                       
                                        final LigneEcartReturn val = finalData.getValue().get(position);

                                        txt_article.setText(val.getArticle());
                                        txt_ean.setText(val.getEAN());

                                        txt_preview.setText(val.getQuantitePrevue() + "");
                                        txt_qt_rejected.setText(val.getQuantiteRejected() + "");
                                        txt_qt_scan.setText(val.getQuantiteScan() + "");


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
                            Log.d("ERROR", "error => " + error.toString());
                            Log.e("ERROR", "error => " + mRequestBody);
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
            //  Toast.makeText(getApplicationContext(), "eror exception" + e.toString(), Toast.LENGTH_SHORT).show();
            Log.e("error", e.toString());

        }
    }

}