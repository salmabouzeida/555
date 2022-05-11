package com.darryncampbell.dwgettingstartedjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

import com.darryncampbell.dwgettingstartedjava.Model.ConsultArticle;
import com.darryncampbell.dwgettingstartedjava.Model.Transfert.LigneDepot;
import com.darryncampbell.dwgettingstartedjava.Model.Transfert.ListDepot;
import com.darryncampbell.dwgettingstartedjava.Model.Value;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class TransfertActivity extends AppCompatActivity implements View.OnTouchListener {
    final Context co = this;
    GridView grid_to, grid_from;
    ProgressBar progressBar;
    Helper helper;

    Button bt_scan, btnDelete, bt_create, btn_list;
    String searchScan = "";
    TextView output, txt_to, txt_from, txt_qt_max;
    EditText edt_transfert;

    String baseUrlListDepot = "";
    String baseUrlCreateTransfert = "";
    String typeTransfert = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfert);

        baseUrlListDepot = getResources().getString(R.string.base_url) + "WmsApp_ReturnBinList?$format=application/json;odata.metadata=none";
        baseUrlCreateTransfert = getResources().getString(R.string.base_url) + "WmsApp_BinTransfer?$format=application/json;odata.metadata=none";
        grid_to = (GridView) findViewById(R.id.grid_to);
        grid_from = (GridView) findViewById(R.id.grid_from);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        output = findViewById(R.id.txtOutput);
        txt_from = (TextView) findViewById(R.id.txt_from);
        txt_to = (TextView) findViewById(R.id.txt_to);
        txt_qt_max = (TextView) findViewById(R.id.txt_qt_max);
        edt_transfert = (EditText) findViewById(R.id.edt_transfert);
        helper = new Helper(getApplicationContext());

        bt_create = (Button) findViewById(R.id.btn_create);
        bt_scan = (Button) findViewById(R.id.btnScanprev);
        btnDelete = (Button) findViewById(R.id.bt_delete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (typeTransfert.equals("0")) {
                    finish();
                    startActivity(getIntent());
                } else {
                    Intent intent=new Intent(getApplicationContext(), ConsultActivity.class);
                    intent.putExtra("EAN",output.getText().toString());
                    startActivity(intent);
                }
                //output.setText("BORANG");

                // searchScanDepot("BORANG");

            }
        });
        Intent intent = getIntent();
        if (intent.getStringExtra("EAN") != null) {
            txt_to.setText(intent.getStringExtra("toBin"));
            output.setText(intent.getStringExtra("EAN"));
            searchScanDepot(intent.getStringExtra("EAN"));
            bt_scan.setVisibility(View.GONE);
            edt_transfert.setVisibility(View.GONE);
            typeTransfert = "1";
        } else {
            initList();
        }


        DWUtilities.CreateDWProfile(co, "com.transfert.action");
        Button btnScan = findViewById(R.id.btnScanprev);
        btnScan.setOnTouchListener(this);

        bt_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (typeTransfert.equals("0")) {
                    if (txt_from.getText().toString().equals(txt_to.getText().toString())) {
                        Toast.makeText(getApplicationContext(), " Dépôt identique au dépôt de sortie ", Toast.LENGTH_SHORT).show();
                    } else {

                        if (edt_transfert.getText().toString().equals("") || txt_from.getText().toString().equals("") || txt_from.getText().toString().equals("") || txt_to.getText().toString().equals(""))
                            Toast.makeText(getApplicationContext(), "champs vide", Toast.LENGTH_SHORT).show();
                        else {
                            float qt = Float.parseFloat(edt_transfert.getText().toString());
                            float qt_max = Float.parseFloat(txt_qt_max.getText().toString());
                            if (qt > qt_max) {
                                Toast.makeText(getApplicationContext(), "Quantité à transferer est supérieur", Toast.LENGTH_SHORT).show();
                                mediaPlayerStart();
                            } else
                                CreateTransfert();


                        }
                    }
                } else {
                    if (!txt_to.getText().toString().equals("")) {
                        CreateTransfert();
                    } else {
                        Toast.makeText(getApplicationContext(), "choisir un depot", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

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

    void initList() {
        try {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = baseUrlListDepot;
            progressBar.setVisibility(View.VISIBLE);
            JSONObject jsonEAN = new JSONObject().put("EAN", "");
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


                                ListDepot data = new ListDepot();
                                Gson gson = new Gson();
                                data = gson.fromJson(jsonList.toString(), ListDepot.class);
                                final ListDepot finalData = data;
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
                                        convertView = layoutInflater.inflate(R.layout.item_transfert, null);
                                        final TextView txt_code = (TextView) convertView.findViewById(R.id.txt_code);
                                        final TextView txt_qt = (TextView) convertView.findViewById(R.id.txt_qt);
                                        final Button btplus = (Button) convertView.findViewById(R.id.btplus);
                                        txt_qt.setVisibility(View.GONE);
                                       
                                        LigneDepot val = finalData.getValue().get(position);
                                        Log.d("tag****", val.getPiece());
                                        txt_code.setText(val.getPiece());
                                        txt_qt.setText("");
                                        btplus.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                if (txt_code.getText().toString().equals(txt_from.getText().toString())) {
                                                    Toast.makeText(getApplicationContext(), " Dépôt identique au dépôt de sortie ", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    txt_to.setText(txt_code.getText().toString());
                                                }

                                            }
                                        });


                                        return convertView;
                                    }
                                };
                                grid_to.setAdapter(baseAdapter);
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

    void searchScanDepot(String codeScan) {
        try {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = baseUrlListDepot;
            progressBar.setVisibility(View.VISIBLE);
            JSONObject jsonEAN = new JSONObject().put("EAN", codeScan);
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


                                ListDepot data = new ListDepot();
                                Gson gson = new Gson();
                                data = gson.fromJson(jsonList.toString(), ListDepot.class);
                                final ListDepot finalData = data;
                                Log.d("tag****", finalData.toString());
                                // Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                                if (finalData.getValue().size() > 0) {
                                    float qt = 0;
                                    if (!edt_transfert.getText().toString().equals("")) {
                                        qt = Float.parseFloat(edt_transfert.getText().toString());
                                        qt++;
                                    }
                                    edt_transfert.setText(qt + "");
                                }
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
                                        convertView = layoutInflater.inflate(R.layout.item_transfert, null);
                                        final TextView txt_code = (TextView) convertView.findViewById(R.id.txt_code);
                                        final TextView txt_qt = (TextView) convertView.findViewById(R.id.txt_qt);
                                        final Button btplus = (Button) convertView.findViewById(R.id.btplus);

                                       
                                        LigneDepot val = finalData.getValue().get(position);
                                        Log.d("tag****", val.getPiece());
                                        txt_code.setText(val.getPiece());
                                        txt_qt.setText(val.getQuantite());
                                        btplus.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if (txt_code.getText().toString().equals(txt_to.getText().toString())) {
                                                    Toast.makeText(getApplicationContext(), " Dépôt identique au dépôt de sortie ", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    txt_from.setText(txt_code.getText().toString());
                                                    txt_qt_max.setText(txt_qt.getText().toString());
                                                }

                                            }
                                        });


                                        return convertView;
                                    }
                                };
                                grid_from.setAdapter(baseAdapter);
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

        searchScanDepot(scan);

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

    void CreateTransfert() {
        try {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = baseUrlCreateTransfert;
            progressBar.setVisibility(View.VISIBLE);
            JSONObject obj = new JSONObject().put("EAN", output.getText().toString())
                    .put("Quantite", edt_transfert.getText().toString())
                    .put("FromBin", txt_from.getText().toString())
                    .put("ToBin", txt_to.getText().toString())
                    .put("Type", typeTransfert);

//"{\"EAN\":\"3268840001008\",\"FromBin\":\"A001\",\"ToBin\":\"A002\",\"Quantite\":\"2\"}"
            final JSONObject jsonBody = new JSONObject();
            jsonBody.put("inputJson", obj.toString());
            Log.d("*** create  ", jsonBody.toString());

            final String mRequestBody = jsonBody.toString();
            StringRequest getRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            progressBar.setVisibility(View.GONE);


                            Toast.makeText(getApplicationContext(), "transfert  crée avec succés", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                            startActivity(intent);


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            progressBar.setVisibility(View.GONE);
                            // TODO Auto-generated method stub
                            Log.e("ERROR", "error => " + error.getLocalizedMessage());
                            Log.e("ERROR", "error => " + error.getMessage());
                            Log.e("ERROR", "error => " + jsonBody.toString());
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

}