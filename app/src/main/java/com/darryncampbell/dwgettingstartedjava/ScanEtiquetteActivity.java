
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
import com.darryncampbell.dwgettingstartedjava.Model.Etiquette.Etiquette;
import com.darryncampbell.dwgettingstartedjava.Model.Etiquette.LigneEtiquette;
import com.darryncampbell.dwgettingstartedjava.Model.Value;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ScanEtiquetteActivity extends AppCompatActivity implements View.OnTouchListener {
    final Context co = this;
    GridView grid_consult;
    ProgressBar progressBar;
    TextView output;
    String baseUrlConsult = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_etiquette);
          baseUrlConsult = getResources().getString(R.string.base_url)+"WmsApp_GetSSCCFromBarcode?$format=application/json;odata.metadata=none";

        grid_consult = (GridView) findViewById(R.id.grid_consult);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        DWUtilities.CreateDWProfile(co, "com.etiquette.action");
        Button btnScan = findViewById(R.id.btnScan);
        output = findViewById(R.id.txtOutput);
        btnScan.setOnTouchListener(this);
        Button bt_test=(Button) findViewById(R.id.bt_test);
        bt_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FillListConsult("036660301032260048");
            }
        });



    }

    void FillListConsult(String scan) {
        try {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = baseUrlConsult;
            progressBar.setVisibility(View.VISIBLE);
            JSONObject jsonEAN = new JSONObject().put("SSCC", scan);
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


                                Etiquette data = new Etiquette();
                                Gson gson = new Gson();
                                data = gson.fromJson(jsonList.toString(), Etiquette.class);
                                final Etiquette finalData = data;
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
                                        convertView = layoutInflater.inflate(R.layout.item_etiquette, null);
                                        final TextView txt_item_num = (TextView) convertView.findViewById(R.id.txt_item_num);

                                        final TextView txt_quantite = (TextView) convertView.findViewById(R.id.txt_quantite);
                                        final TextView txt_article = (TextView) convertView.findViewById(R.id.txt_article);

                                        LigneEtiquette val = finalData.getValue().get(position);

                                        txt_item_num.setText(val.getColis());

                                        txt_quantite.setText(val.getQuantite() + "");
                                        txt_article.setText(val.getArticle());


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