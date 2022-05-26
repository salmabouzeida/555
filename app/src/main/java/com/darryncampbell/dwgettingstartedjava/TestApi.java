package com.darryncampbell.dwgettingstartedjava;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import com.darryncampbell.dwgettingstartedjava.Model.prelevement.ListeBonCommande;
import com.darryncampbell.dwgettingstartedjava.Model.prelevement.Value;
import com.google.gson.Gson;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.SilentAuthenticationCallback;
import com.microsoft.identity.client.exception.MsalClientException;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.identity.client.exception.MsalServiceException;
import com.microsoft.identity.client.exception.MsalUiRequiredException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class TestApi extends AppCompatActivity {
    /* Azure AD Variables */
    private ISingleAccountPublicClientApplication mSingleAccountApp;
    private IAccount mAccount;
   String urlAPi = "https://api.businesscentral.dynamics.com/v2.0/ec61defb-c499-4bae-afbe-e96c06b43964/vagademo/ODataV4/Company(%27CRONUS%20FR%27)/Power_BI_Purchase_Hdr_Vendor?$select=No,Item_No,Quantity,Base_Unit_of_Measure,Description,Inventory,Qty_on_Purch_Order,Unit_Price,Vendor_No,Name,Balance,Country_Region_Code";
 //String urlAPi =  "https://api.businesscentral.dynamics.com/v2.0/b2ba6f8e-2ab7-4428-98f5-8a73929fd0f9/DEV/ODataV4/WmsApp_ReturnPickingLot?$format=application/json;odata.metadata=none&=";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_api);
        // Creates a PublicClientApplication object with res/raw/auth_config_single_account.json
        PublicClientApplication.createSingleAccountPublicClientApplication(getApplicationContext(),
                R.raw.auth_config_single_account,
                new IPublicClientApplication.ISingleAccountApplicationCreatedListener() {
                    @Override
                    public void onCreated(ISingleAccountPublicClientApplication application) {
                        /**
                         * This test app assumes that the app is only going to support one account.
                         * This requires "account_mode" : "SINGLE" in the config json file.
                         **/
                        mSingleAccountApp = application;
                        Log.e("***p", mSingleAccountApp.toString());
                        loadAccount();

                    }

                    @Override
                    public void onError(MsalException exception) {
                        Log.e("**error", exception.toString());
                    }
                });

        Button bt_test = (Button) findViewById(R.id.bt_test);

        bt_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                init();
            }
        });

    }

    private SilentAuthenticationCallback getAuthSilentCallback(final int idFunction) {
        return new SilentAuthenticationCallback() {

            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                Log.d("***test", "Successfully authenticated" + authenticationResult.getAccessToken());

                /* Successfully got a token, use it to call a protected resource - MSGraph */
                if (idFunction == 1)
                    callApiTest(authenticationResult);
            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                Log.d("***test", "Authentication failed: " + exception.toString());


                if (exception instanceof MsalClientException) {
                    /* Exception inside MSAL, more info inside MsalError.java */
                } else if (exception instanceof MsalServiceException) {
                    /* Exception when communicating with the STS, likely config issue */
                } else if (exception instanceof MsalUiRequiredException) {
                    /* Tokens expired or no session, retry with interactive */
                }
            }
        };
    }

    void init() {
        if (mSingleAccountApp == null) {
            Log.e("error", "smlnull");
            return;
        }

        /**
         * Once you've signed the user in,
         * you can perform acquireTokenSilent to obtain resources without interrupting the user.
         */
        mSingleAccountApp.acquireTokenSilentAsync("https://api.businesscentral.dynamics.com/.default".toLowerCase().split(" "), mAccount.getAuthority(), getAuthSilentCallback(1));


    }

    /**
     * Make an HTTP request to obtain MSGraph data
     */
    private void callApiTest(final IAuthenticationResult authenticationResult) {
//        MSGraphRequestWrapper.callGraphAPIUsingOkHttp(
//                new TestApi(),
//                urlAPi,
//                authenticationResult.getAccessToken());
        try {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());


            StringRequest getRequest = new StringRequest(Request.Method.GET, urlAPi,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // response
                            Toast.makeText(getApplicationContext(),"succes", Toast.LENGTH_SHORT).show();


                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            Log.e("***ERROR", "error => " + error.toString());
                            Toast.makeText(getApplicationContext(),"echec", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getApplicationContext(), "eror vollety" + error.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", "Bearer " + authenticationResult.getAccessToken());
                    params.put("Content-Type", "application/json");
                   // params.put("Company", "RDD Sprint 2");


                    return params;
                }

            };
            queue.add(getRequest);
        } catch (Exception e) {
            //  Toast.makeText(getApplicationContext(), "eror exception" + e.toString(), Toast.LENGTH_SHORT).show();
            Log.e("***error", e.toString());

        }

    }


    /**
     * Load the currently signed-in account, if there's any.
     */
    private void loadAccount() {
        Log.e("***pss", mSingleAccountApp.toString());
        if (mSingleAccountApp == null) {
            return;
        }

        mSingleAccountApp.getCurrentAccountAsync(new ISingleAccountPublicClientApplication.CurrentAccountCallback() {
            @Override
            public void onAccountLoaded(@Nullable IAccount activeAccount) {
                // You can use the account data to update your UI or your app database.
                mAccount = activeAccount;

                Log.e("***p", mAccount.toString());
            }

            @Override
            public void onAccountChanged(@Nullable IAccount priorAccount, @Nullable IAccount currentAccount) {
                if (currentAccount == null) {
                    // Perform a cleanup task as the signed-in account changed.
                    Log.e("***p", "account null");
                }
            }

            @Override
            public void onError(@NonNull MsalException exception) {
                Log.e("error", exception.toString());
            }
        });
    }
}