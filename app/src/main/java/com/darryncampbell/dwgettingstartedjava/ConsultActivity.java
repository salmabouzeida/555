package com.darryncampbell.dwgettingstartedjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
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
import com.darryncampbell.dwgettingstartedjava.Model.ConsultArticle;
import com.darryncampbell.dwgettingstartedjava.Model.Value;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ConsultActivity extends AppCompatActivity implements View.OnTouchListener {
    final Context co = this;
    GridView grid_consult;
    ProgressBar progressBar;
    TextView output;
    //  String baseUrlConsult = getResources().getString(R.string.key_autorisation)+"WmsApp_ConsultItem?$format=application/json;odata.metadata=none";
    String baseUrlConsult = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consult);
        baseUrlConsult = getResources().getString(R.string.base_url) + "WmsApp_ConsultItem?$format=application/json;odata.metadata=none";

        grid_consult = (GridView) findViewById(R.id.grid_consult);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        DWUtilities.CreateDWProfile(co, "com.consultation.action");
        Button btnScan = findViewById(R.id.btnScan);
        output = findViewById(R.id.txtOutput);
        btnScan.setOnTouchListener(this);
        Intent intent = getIntent();
        if (intent.getStringExtra("EAN") != null) {

            output.setText(intent.getStringExtra("EAN"));
            FillListConsult(intent.getStringExtra("EAN"));
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
                                        convertView = layoutInflater.inflate(R.layout.item_consult_article, null);
                                        final TextView txt_item_num = (TextView) convertView.findViewById(R.id.txt_item_num);
                                        final TextView txt_title_100 = (TextView) convertView.findViewById(R.id.txt_title_100);
                                        final TextView txt_capacite = (TextView) convertView.findViewById(R.id.txt_capacite);
                                        final TextView txt_location_code = (TextView) convertView.findViewById(R.id.txt_location_code);
                                        final TextView txt_sum_qty_base = (TextView) convertView.findViewById(R.id.txt_sum_qty_base);
                                        final TextView txt_bin_code = (TextView) convertView.findViewById(R.id.txt_bin_code);
                                        final Button bt_transfert = (Button) convertView.findViewById(R.id.bt_transfert);
                                        final TextView txt_poids = (TextView) convertView.findViewById(R.id.txt_poids);
                                        final Value val = finalData.getValue().get(position);
                                        txt_item_num.setText(val.getArticle());
                                        txt_location_code.setText(val.getPiece());
                                        txt_sum_qty_base.setText(val.getQuantite() + "");
                                        txt_bin_code.setText(val.getEntrepot());
                                        txt_capacite.setText(val.getCapacite());
                                        txt_title_100.setText(val.getTitre());
                                        txt_poids.setText(val.getPoids());
                                        if (val.getCapacite().equals("") || val.getCapacite().equals("0")) {
                                            bt_transfert.setVisibility(View.INVISIBLE);


                                        } else {
                                            if (Float.parseFloat(val.getPoids()) / Float.parseFloat(val.getQuantite()) < Float.parseFloat(val.getCapacite())) {
                                                bt_transfert.setVisibility(View.VISIBLE);
                                            } else {
                                                bt_transfert.setVisibility(View.INVISIBLE);
                                            }

                                        }
                                        bt_transfert.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(getApplicationContext(), TransfertActivity.class);
                                                intent.putExtra("EAN", output.getText().toString());
                                                intent.putExtra("toBin", val.getPiece());
                                                startActivity(intent);
                                            }
                                        });


                                        return convertView;
                                    }
                                };
                                grid_consult.setAdapter(baseAdapter);
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
        String scan = decodedData + " [" + decodedLabelType + "]\n\n";
        final TextView output = findViewById(R.id.txtOutput);
        output.setText(decodedData);
        FillListConsult(decodedData);
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