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
import com.darryncampbell.dwgettingstartedjava.Model.prelevement.LigneBcPrelevement;
import com.darryncampbell.dwgettingstartedjava.Model.prelevement.ListLigneBcPrelevement;
import com.darryncampbell.dwgettingstartedjava.Model.prelevement.ListeBonCommande;
import com.darryncampbell.dwgettingstartedjava.Model.prelevement.Value;
import com.darryncampbell.dwgettingstartedjava.Model.prelevementLot.ListFacture;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrelevementLotActivity extends AppCompatActivity implements View.OnTouchListener {
    final Context co = this;
    GridView grid_prelevement;
    ProgressBar progressBar;
    Helper helper;
    Button bt_scan, bt_view_ligne, btnDelete, bt_create, bt_ecart;
    String searchScan = "";
    TextView output;
    String baseUrlLigneBC = "";
    String baseUrlCreatePrelevement = "";
    String baseUrlCreatePrelevementFacture = "";
    String baseUrlListBC = "";
    LinearLayout layout_scan, layout_action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prelevement_lot);
        baseUrlLigneBC = getResources().getString(R.string.base_url) + "WmsApp_ReturnPickingLine?$format=application/json;odata.metadata=none";
        baseUrlCreatePrelevement = getResources().getString(R.string.base_url) + "WmsApp_RegisterPickLot?$format=application/json;odata.metadata=none";
        baseUrlCreatePrelevementFacture = getResources().getString(R.string.base_url) + "WmsApp_InvoicePickingDocs?$format=application/json;odata.metadata=none";
        // baseUrlListBC = getResources().getString(R.string.base_url) + "WmsApp_ReturnPickingLot?$format=application/json;odata.metadata=none";
        baseUrlListBC = "https://api.businesscentral.dynamics.com/v2.0/b2ba6f8e-2ab7-4428-98f5-8a73929fd0f9/Bc365_Check/ODataV4/InvoiceDocument";
        grid_prelevement = (GridView) findViewById(R.id.grid_prelevement);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        layout_scan = (LinearLayout) findViewById(R.id.layout_scan);
        layout_action = (LinearLayout) findViewById(R.id.layout_action);
        output = findViewById(R.id.txtOutput);
        helper = new Helper(getApplicationContext());

        bt_view_ligne = (Button) findViewById(R.id.bt_view_ligne);
        bt_create = (Button) findViewById(R.id.btn_create);
        bt_ecart = (Button) findViewById(R.id.bt_ecart);
        bt_scan = (Button) findViewById(R.id.btnScanprev);

        bt_view_ligne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FillListLignePrelevement fillList = new FillListLignePrelevement();
                fillList.execute("");


            }
        });
        bt_ecart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FillEcartPrelevement fillList = new FillEcartPrelevement();
                fillList.execute("");


            }
        });

        DWUtilities.CreateDWProfile(co, "com.prelevementlot.action");
        Button btnScan = findViewById(R.id.btnScanprev);
        btnScan.setOnTouchListener(this);
        Cursor c = helper.getListLigneCommandPrelevementLot();
        if (c.getCount() > 0) {
            FillListLignePrelevement fillListLignePrelevement = new FillListLignePrelevement();
            fillListLignePrelevement.execute("");
            layout_scan.setVisibility(View.VISIBLE);
            layout_action.setVisibility(View.VISIBLE);

        } else {
            //FillListBonCommande();
            layout_scan.setVisibility(View.GONE);
            layout_action.setVisibility(View.GONE);
        }
        test();
        btnDelete = (Button) findViewById(R.id.bt_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final AlertDialog.Builder alt = new AlertDialog.Builder(co);
                alt.setIcon(R.drawable.ison_prelevement);
                alt.setTitle("Prélèvement");
                alt.setMessage("Voulez-vous vraiment annuler ce prélèvement ?");
                alt.setNegativeButton("oui",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface di, int i) {

                                helper.DeleteListBonCommandPrelevementLot();
                                helper.DeleteLigneBonCommandPrelevementLot();
                                helper.DeleteValideBonCommandPrelevementLot();
                                finish();
                                startActivity(getIntent());
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

            }
        });
        bt_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Cursor c = helper.getListLigneCommandPrelevementLot();
                if (c.getCount() > 0) {
                    if (testEcart()) {
                        Toast.makeText(getApplicationContext(), "il reste des articles non prélevés", Toast.LENGTH_SHORT).show();

                    } else {
                        CreatePrelevement();
                    }


                } else {
                    Toast.makeText(getApplicationContext(), "scanner des article d'abord", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    void test() {
        try {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = "https://api.businesscentral.dynamics.com/v2.0/b2ba6f8e-2ab7-4428-98f5-8a73929fd0f9/Bc365_Check/ODataV4/InvoiceDocument";
            progressBar.setVisibility(View.VISIBLE);

            StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            progressBar.setVisibility(View.GONE);

                            Log.e("****sss", "ssss");

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
                    params.put("Authorization", "Basic SEVMTUkuR1VJREFSQTprMlc3V0NZM09vMjRSYnJnRjQxYU5mcGY0WDJSU1VNbmhuRndjb2czMndjPQ==");
                    //params.put("Content-Type", "application/json");
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

    void FillListBonCommande() {
        try {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            final String url = baseUrlListBC;
            progressBar.setVisibility(View.VISIBLE);

            StringRequest getRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            progressBar.setVisibility(View.GONE);

                            ListeBonCommande data = new ListeBonCommande();
                            JSONObject obj = null;
                            try {
                                obj = new JSONObject(response);

                                JSONArray array = new JSONArray(obj.getString("value"));

                                JSONObject jsonList = new JSONObject().put("value", array);


                                Gson gson = new Gson();
                                data = gson.fromJson(jsonList.toString(), ListeBonCommande.class);
                                final ListeBonCommande finalData = data;


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
                                        if (helper.testExistBonCommandPrelevementLot(val.getNo_())) {
                                            btdelete.setVisibility(View.VISIBLE);
                                            btplus.setVisibility(View.GONE);
                                        } else {
                                            btdelete.setVisibility(View.GONE);
                                            btplus.setVisibility(View.VISIBLE);
                                        }

                                        btplus.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                helper.AddBonCommandePrelevementLot(val);
                                                FillLigneBonCommande();
                                                layout_scan.setVisibility(View.VISIBLE);
                                                layout_action.setVisibility(View.VISIBLE);
                                                btdelete.setVisibility(View.VISIBLE);
                                                btplus.setVisibility(View.GONE);
                                            }
                                        });
                                        btdelete.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                helper.RemoveBonCommandPrelevementLot(val);
                                                btdelete.setVisibility(View.GONE);
                                                btplus.setVisibility(View.VISIBLE);
                                            }
                                        });

                                        txt_code.setText(val.getNo_() + "");
                                        txt_nom_client.setText("");


                                        return convertView;
                                    }
                                };
                                grid_prelevement.setAdapter(baseAdapter);
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
                            Log.e("***ERROR", url);
                            Log.e("***ERROR", "error => " + error.toString());
                            Log.e("***ERROR", "error => " + getResources().getString(R.string.key_autorisation));
                            Log.e("***ERROR", "error => " + getResources().getString(R.string.company_name));
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
            Log.e("***error", e.toString());

        }
    }

    void FillLigneBonCommande() {
        try {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = baseUrlLigneBC;
            progressBar.setVisibility(View.VISIBLE);

            Cursor cr = helper.getListCommandPrelevementLot();
            cr.moveToFirst();
            final String noDoc = cr.getString(cr.getColumnIndex("Code"));
            JSONObject obj = new JSONObject().put("NoDoc", noDoc);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("inputJson", obj.toString());
            Log.d("***inputFillLigneBonCommande", jsonBody.toString());

            final String mRequestBody = jsonBody.toString();
            StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            progressBar.setVisibility(View.GONE);
                            Log.d("tag****ResponseFillLigneBonCommande", response);


                            bt_scan.setVisibility(View.VISIBLE);
                            JSONObject obj = null;
                            ListLigneBcPrelevement data = new ListLigneBcPrelevement();
                            try {
                                obj = new JSONObject(response);

                                JSONArray array = new JSONArray(obj.getString("value"));

                                JSONObject jsonList = new JSONObject().put("value", array);


                                Gson gson = new Gson();
                                data = gson.fromJson(jsonList.toString(), ListLigneBcPrelevement.class);
                                final ListLigneBcPrelevement finalData = data;
                                for (int i = 0; i < finalData.value.size(); i++) {
                                    LigneBcPrelevement ligne = finalData.value.get(i);
                                    ligne.setQuantiteScan("0");
                                    ligne.setNoDoc(noDoc);
                                    helper.AddLigneBonCommandePrelevementLot(ligne);
                                }


                            } catch (JSONException e) {
                                Log.e("errorlistlignebc", e.toString());
                            }


                            //helper.AddLigneBonCommandePrelevementLot(new LigneBcPrelevement("codedoc1", "3268840001008", "555", "8", "0"));
                            FillListLignePrelevement fillListLignePrelevement = new FillListLignePrelevement();
                            fillListLignePrelevement.execute("");

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.GONE);
                            // TODO Auto-generated method stub
                            Log.d("***ERROR", "error => " + error.toString());
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
            Log.e("***error", e.toString());

        }
    }

    void CreatePrelevement() {
        try {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = baseUrlCreatePrelevement;
            progressBar.setVisibility(View.VISIBLE);
            Cursor cr = helper.getListCommandPrelevementLot();
            cr.moveToFirst();
            final String noDoc = cr.getString(cr.getColumnIndex("Code"));
            JSONObject obj = new JSONObject().put("NoDoc", noDoc);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("inputJson", obj.toString());
            Log.d("***create", jsonBody.toString());

            final String mRequestBody = jsonBody.toString();
            StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            progressBar.setVisibility(View.GONE);
                            Log.d("****Response", response);

                            CreateFacturePrelevement();


//

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.GONE);
                            // TODO Auto-generated method stub


                            Log.e("**ERROR", "error => " + error.networkResponse.statusCode);
                            Log.e("**ERROR", "error => " + error.networkResponse.data);
                            Log.e("**ERROR", "error => " + error.networkResponse.toString());

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

    void CreateFacturePrelevement() {
        try {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = baseUrlCreatePrelevementFacture;
            progressBar.setVisibility(View.VISIBLE);
            Cursor cr = helper.getListCommandPrelevementLot();
            cr.moveToFirst();
            final String noDoc = cr.getString(cr.getColumnIndex("Code"));
            JSONObject obj = new JSONObject().put("NoDoc", noDoc);
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("inputJson", obj.toString());
            Log.d("***create", jsonBody.toString());

            final String mRequestBody = jsonBody.toString();
            StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            progressBar.setVisibility(View.GONE);
                            Log.d("****Response", response);

                            helper.DeleteListBonCommandPrelevementLot();
                            helper.DeleteLigneBonCommandPrelevementLot();
                            helper.DeleteValideBonCommandPrelevementLot();

                            Toast.makeText(getApplicationContext(), "prélevement crée avec succés", Toast.LENGTH_SHORT).show();

                            ListFacture data = new ListFacture();
                            JSONObject obj = null;
                            try {
                                obj = new JSONObject(response);

                                JSONArray array = new JSONArray(obj.getString("value"));

                                JSONObject jsonList = new JSONObject().put("value", array);


                                Gson gson = new Gson();
                                data = gson.fromJson(jsonList.toString(), ListFacture.class);
                                final ListFacture finalData = data;


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
                                        convertView = layoutInflater.inflate(R.layout.item_simple, null);
                                        final TextView txt_title = (TextView) convertView.findViewById(R.id.txt_title);
                                        final TextView txt_description = (TextView) convertView.findViewById(R.id.txt_description);

                                        final com.darryncampbell.dwgettingstartedjava.Model.prelevementLot.Value val = finalData.getValue().get(position);
                                        txt_title.setText(val.getNoPrelevement() + "");
                                        txt_description.setText(val.getNoFacture());


                                        return convertView;
                                    }
                                };
                                grid_prelevement.setAdapter(baseAdapter);
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


                            Log.e("**ERROR", "error => " + error.networkResponse.statusCode);
                            Log.e("**ERROR", "error => " + error.networkResponse.data);
                            Log.e("**ERROR", "error => " + error.networkResponse.toString());

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
        // String scan = decodedData + " [" + decodedLabelType + "]\n\n";
        String scan = decodedData;
        searchScan = scan;

        output.setText(scan);

        FillListLignePrelevementSearch fillListLignePrelevementSearch = new FillListLignePrelevementSearch();
        fillListLignePrelevementSearch.execute("");

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
                    cr = helper.getListCommandPrelevementLot();
                    if (cr.move(pos + 1)) {

                        code.setText(cr.getString(cr.getColumnIndex("Code")));
                        designation.setText(cr.getString(cr.getColumnIndex("ClientName")));


                    }


                    final LinearLayout layout = convertView.findViewById(R.id.layouttop);

                    return convertView;
                }
            };
            grid_prelevement.setAdapter(baseAdapter);


        }

        @Override
        protected String doInBackground(String... params) {

            try {
                cr = helper.getListCommandPrelevementLot();
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

    public class FillListLignePrelevement extends AsyncTask<String, String, String> {
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

                    convertView = layoutInflater.inflate(R.layout.item_prelevement, null);
                    final TextView txt_noDoc = (TextView) convertView.findViewById(R.id.txt_noDoc);
                    final TextView txt_ean = (TextView) convertView.findViewById(R.id.txt_ean);
                    final TextView txt_code_article = (TextView) convertView.findViewById(R.id.txt_code_article);
                    final TextView txt_piece = (TextView) convertView.findViewById(R.id.txt_piece);
                    final TextView txt_qt = (TextView) convertView.findViewById(R.id.txt_qt);
                    final EditText edt_qt_scan = (EditText) convertView.findViewById(R.id.edt_qt_scan);
                    final Button btmoin = (Button) convertView.findViewById(R.id.btmoin);
                    final Button btplus = (Button) convertView.findViewById(R.id.btplus);
                    final Button btplusall = (Button) convertView.findViewById(R.id.btplusall);
                    //noDoc TEXT  ,Code TEXT, Quantite INTEGER, QuantiteScan INTEGER
                    btplusall.setVisibility(View.VISIBLE);
                    cr = helper.getListLigneCommandPrelevementLot();
                    if (cr.move(pos + 1)) {

                        txt_code_article.setText(cr.getString(cr.getColumnIndex("Article")));
                        txt_noDoc.setText(cr.getString(cr.getColumnIndex("noDoc")));
                        txt_qt.setText(cr.getString(cr.getColumnIndex("Quantite")));
                        txt_piece.setText(cr.getString(cr.getColumnIndex("Piece")));
                        txt_ean.setText(cr.getString(cr.getColumnIndex("EAN")));
                        edt_qt_scan.setText(cr.getString(cr.getColumnIndex("QuantiteScan")));

                        if (Integer.parseInt(cr.getString(cr.getColumnIndex("QuantiteScan"))) >
                                Integer.parseInt(cr.getString(cr.getColumnIndex("Quantite")))) {
                            mediaPlayerStart();
                            Toast.makeText(getApplicationContext(), "depassement quantité", Toast.LENGTH_SHORT).show();
                            helper.UpdateLigneBonCommandePrelevementLot(new LigneBcPrelevement(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(), txt_qt.getText().toString(), cr.getString(cr.getColumnIndex("Quantite"))));

                        } else {
                            //Toast.makeText(getApplicationContext(), "pas quantité", Toast.LENGTH_SHORT).show();

                        }
                    }


                    btmoin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int qt = Integer.parseInt(edt_qt_scan.getText().toString());
                            qt--;
                            if (qt < 0) {
                                qt = 0;
                            }

                            if (qt > Integer.parseInt(txt_qt.getText().toString())) {
                                mediaPlayerStart();
                                Toast.makeText(getApplicationContext(), "Dépassement quantité", Toast.LENGTH_SHORT).show();
                            } else {
                                edt_qt_scan.setText("" + qt);
                                helper.UpdateLigneBonCommandePrelevementLot(new LigneBcPrelevement(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(), txt_qt.getText().toString(), "" + qt));
                            }

                        }
                    });
                    btplus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int qt = Integer.parseInt(edt_qt_scan.getText().toString());
                            qt++;

                            if (qt > Integer.parseInt(txt_qt.getText().toString())) {
                                Toast.makeText(getApplicationContext(), "Dépassement quantité", Toast.LENGTH_SHORT).show();
                                mediaPlayerStart();
                            } else {
                                edt_qt_scan.setText("" + qt);
                                helper.UpdateLigneBonCommandePrelevementLot(new LigneBcPrelevement(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(), txt_qt.getText().toString(), "" + qt));
                            }
                        }
                    });

                    btplusall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String qt = txt_qt.getText().toString();
                            edt_qt_scan.setText(qt);
                            helper.UpdateLigneBonCommandePrelevementLot(new LigneBcPrelevement(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(), txt_qt.getText().toString(), "" + qt));

                        }
                    });

                    edt_qt_scan.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View view, int i, KeyEvent keyEvent) {

                            String s = edt_qt_scan.getText().toString();
                            if (i != KEYCODE_DEL) {
                                if (!s.equals("")) {
                                    int qt = Integer.parseInt(edt_qt_scan.getText().toString());
                                    if (qt > Integer.parseInt(txt_qt.getText().toString())) {
                                        Toast.makeText(getApplicationContext(), "Dépassement quantité", Toast.LENGTH_SHORT).show();
                                        mediaPlayerStart();
                                    } else {
                                        helper.UpdateLigneBonCommandePrelevementLot(new LigneBcPrelevement(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(), txt_qt.getText().toString(), "" + qt));
                                    }
                                }
                            }
                            return false;
                        }
                    });
                    return convertView;
                }
            };
            grid_prelevement.setAdapter(baseAdapter);


        }

        @Override
        protected String doInBackground(String... params) {

            try {
                cr = helper.getListLigneCommandPrelevementLot();


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


    public boolean testEcart() {
        boolean test = false;
        Cursor cr;
        try {
            cr = helper.getListLigneCommandPrelevementLot();


            if (cr.moveToFirst()) {
                do {
                    float ecart = Integer.parseInt(cr.getString(cr.getColumnIndex("Quantite"))) - Integer.parseInt(cr.getString(cr.getColumnIndex("QuantiteScan")));

                    if (ecart != 0)
                        test = true;
                } while (cr.moveToNext());
            }


        } catch (Exception ex) {


        }
        return test;
    }

    public class FillEcartPrelevement extends AsyncTask<String, String, String> {
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


                    convertView = layoutInflater.inflate(R.layout.item_ecart_prelevement, null);
                    final TextView txt_article = (TextView) convertView.findViewById(R.id.txt_article);

                    final TextView txt_piece = (TextView) convertView.findViewById(R.id.txt_piece);
                    final TextView txt_qt = (TextView) convertView.findViewById(R.id.txt_qt);
                    final TextView txt_ecart = (TextView) convertView.findViewById(R.id.txt_ecart);
                    final TextView edt_qt_scan = (TextView) convertView.findViewById(R.id.txt_qt_scan);
                    //noDoc TEXT  ,Code TEXT, Quantite INTEGER, QuantiteScan INTEGER
                    cr = helper.getListLigneCommandPrelevementLot();
                    if (cr.move(pos + 1)) {

                        txt_article.setText(cr.getString(cr.getColumnIndex("Article")));

                        txt_qt.setText(cr.getString(cr.getColumnIndex("Quantite")));
                        txt_piece.setText(cr.getString(cr.getColumnIndex("Piece")));

                        edt_qt_scan.setText(cr.getString(cr.getColumnIndex("QuantiteScan")));
                        float ecart = Integer.parseInt(cr.getString(cr.getColumnIndex("Quantite"))) - Integer.parseInt(cr.getString(cr.getColumnIndex("QuantiteScan")));

                        txt_ecart.setText("" + ecart);

                    }


                    return convertView;
                }
            };
            grid_prelevement.setAdapter(baseAdapter);


        }

        @Override
        protected String doInBackground(String... params) {

            try {
                cr = helper.getListLigneCommandPrelevementLot();


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

    public void mediaPlayerStart() {
        final MediaPlayer mMediaPlayer;

        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alerte_cancel);

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

    public class FillListLignePrelevementSearch extends AsyncTask<String, String, String> {
        String z = "";

        List<Map<String, String>> prolist = new ArrayList<Map<String, String>>();
        ArrayList<String> list;
        Cursor cr;

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);


            helper.UpdateLignePrelevementLotByScan(searchScan);
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


                    convertView = layoutInflater.inflate(R.layout.item_prelevement, null);
                    final TextView txt_noDoc = (TextView) convertView.findViewById(R.id.txt_noDoc);
                    final TextView txt_ean = (TextView) convertView.findViewById(R.id.txt_ean);
                    final TextView txt_code_article = (TextView) convertView.findViewById(R.id.txt_code_article);
                    final TextView txt_piece = (TextView) convertView.findViewById(R.id.txt_piece);
                    final TextView txt_qt = (TextView) convertView.findViewById(R.id.txt_qt);
                    final EditText edt_qt_scan = (EditText) convertView.findViewById(R.id.edt_qt_scan);
                    final Button btmoin = (Button) convertView.findViewById(R.id.btmoin);
                    final Button btplus = (Button) convertView.findViewById(R.id.btplus);
                    //noDoc TEXT  ,Code TEXT, Quantite INTEGER, QuantiteScan INTEGER
                    final Button btplusall = (Button) convertView.findViewById(R.id.btplusall);
                    //noDoc TEXT  ,Code TEXT, Quantite INTEGER, QuantiteScan INTEGER
                    btplusall.setVisibility(View.VISIBLE);
                    btplusall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String qt = txt_qt.getText().toString();
                            edt_qt_scan.setText(qt);
                            helper.UpdateLigneBonCommandePrelevementLot(new LigneBcPrelevement(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(), txt_qt.getText().toString(), "" + qt));

                        }
                    });
                    cr = helper.getLignePrelevementLotArticle(searchScan);
                    if (cr.move(pos + 1)) {
                        txt_code_article.setText(cr.getString(cr.getColumnIndex("Article")));
                        txt_noDoc.setText(cr.getString(cr.getColumnIndex("noDoc")));
                        txt_qt.setText(cr.getString(cr.getColumnIndex("Quantite")));
                        txt_piece.setText(cr.getString(cr.getColumnIndex("Piece")));
                        txt_ean.setText(cr.getString(cr.getColumnIndex("EAN")));
                        edt_qt_scan.setText(cr.getString(cr.getColumnIndex("QuantiteScan")));
                        if (Integer.parseInt(cr.getString(cr.getColumnIndex("QuantiteScan"))) >
                                Integer.parseInt(cr.getString(cr.getColumnIndex("Quantite")))) {
                            mediaPlayerStart();
                            Toast.makeText(getApplicationContext(), "Dépassement quantité", Toast.LENGTH_SHORT).show();
                            helper.UpdateLigneBonCommandePrelevementLot(new LigneBcPrelevement(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(), txt_qt.getText().toString(), cr.getString(cr.getColumnIndex("Quantite"))));

                        } else {


                        }

                    }
                    edt_qt_scan.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View view, int i, KeyEvent keyEvent) {

                            String s = edt_qt_scan.getText().toString();
                            if (i != KEYCODE_DEL) {
                                if (!s.equals("")) {
                                    int qt = Integer.parseInt(edt_qt_scan.getText().toString());
                                    if (qt > Integer.parseInt(txt_qt.getText().toString())) {
                                        Toast.makeText(getApplicationContext(), "Dépassement quantité", Toast.LENGTH_SHORT).show();
                                        mediaPlayerStart();
                                    } else {

                                        helper.UpdateLigneBonCommandePrelevementLot(new LigneBcPrelevement(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(), txt_qt.getText().toString(), "" + qt));
                                    }
                                    helper.UpdateLigneBonCommandePrelevementLot(new LigneBcPrelevement(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(), txt_qt.getText().toString(), "" + qt));

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
                            if (qt > Integer.parseInt(txt_qt.getText().toString())) {
                                Toast.makeText(getApplicationContext(), "Dépassement quantité", Toast.LENGTH_SHORT).show();
                                mediaPlayerStart();
                            } else {
                                edt_qt_scan.setText("" + qt);
                                helper.UpdateLigneBonCommandePrelevementLot(new LigneBcPrelevement(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(), txt_qt.getText().toString(), "" + qt));
                            }

                        }
                    });
                    btplus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int qt = Integer.parseInt(edt_qt_scan.getText().toString());
                            qt++;


                            if (qt > Integer.parseInt(txt_qt.getText().toString())) {
                                Toast.makeText(getApplicationContext(), "Dépassement quantité", Toast.LENGTH_SHORT).show();
                                mediaPlayerStart();
                            } else {
                                edt_qt_scan.setText("" + qt);
                                helper.UpdateLigneBonCommandePrelevementLot(new LigneBcPrelevement(txt_noDoc.getText().toString(), txt_ean.getText().toString(), txt_code_article.getText().toString(), txt_piece.getText().toString(), txt_qt.getText().toString(), "" + qt));
                            }
                        }
                    });

                    return convertView;
                }
            };
            grid_prelevement.setAdapter(baseAdapter);


        }

        @Override
        protected String doInBackground(String... params) {

            try {
                cr = helper.getLignePrelevementLotArticle(searchScan);
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
}