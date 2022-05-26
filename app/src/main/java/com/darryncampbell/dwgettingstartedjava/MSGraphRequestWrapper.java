// Copyright (c) Microsoft Corporation.
// All rights reserved.
//
// This code is licensed under the MIT License.
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files(the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions :
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

package com.darryncampbell.dwgettingstartedjava;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.brotli.BrotliInterceptor;

public class MSGraphRequestWrapper {
    private static final String TAG = MSGraphRequestWrapper.class.getSimpleName();

    // See: https://docs.microsoft.com/en-us/graph/deployments#microsoft-graph-and-graph-explorer-service-root-endpoints
    //public static final String MS_GRAPH_ROOT_ENDPOINT = "https://api.businesscentral.dynamics.com/v2.0/ec61defb-c499-4bae-afbe-e96c06b43964/vagademo/ODataV4/Company(%27CRONUS%20FR%27)/Power_BI_Purchase_Hdr_Vendor?$select=No,Item_No,Quantity,Base_Unit_of_Measure,Description,Inventory,Qty_on_Purch_Order,Unit_Price,Vendor_No,Name,Balance,Country_Region_Code";
   // public static final String MS_GRAPH_ROOT_ENDPOINT = "https://api.businesscentral.dynamics.com/v2.0/ec61defb-c499-4bae-afbe-e96c06b43964/vagademo/ODataV4/Power_BI_Purchase_Hdr_Vendor?$select=No,Item_No,Quantity,Base_Unit_of_Measure,Description,Inventory,Qty_on_Purch_Order,Unit_Price,Vendor_No,Name,Balance,Country_Region_Code";
    public static final String MS_GRAPH_ROOT_ENDPOINT = "https://api.businesscentral.dynamics.com/v2.0/ec61defb-c499-4bae-afbe-e96c06b43964/vagademo/ODataV4/InvoiceDocument";
    public static final OkHttpClient client;

    static {
        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(BrotliInterceptor.INSTANCE)
                .build();
    }

    public static void callGraphAPIUsingOkHttp(@NonNull final Context context,
                                               @NonNull final String graphResourceUrl,
                                               @NonNull final String accessToken) {

        /* Make sure we have a token to send to graph */
        if (accessToken == null || accessToken.length() == 0) {
            return;
        }
        System.err.println("access token: " + accessToken);
        RequestBody formBody = new FormBody.Builder()
//                .add("username", "test")
//                .add("password", "test")
                .build();
        Request request = new Request.Builder()
                .url(graphResourceUrl)
                .get()
//                .post(formBody)
                .header("Authorization", "Bearer " + accessToken)
                .header("Accept", "application/json")
                .header("Company", "CRONUS FR" +
                        "")
                .build();
        Log.e("header", request.toString());
        String result = null;

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.e(TAG, "***Response: " + response.body().string());

            }
        });

    }


}
