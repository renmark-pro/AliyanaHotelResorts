package com.aliyanaresorts.aliyanahotelresorts.mainMenu.home.booking.previewBooking;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyanaresorts.aliyanahotelresorts.R;
import com.aliyanaresorts.aliyanahotelresorts.dialog.LoadingDialog;
import com.aliyanaresorts.aliyanahotelresorts.dialog.NoInetDialog;
import com.aliyanaresorts.aliyanahotelresorts.network.AppController;
import com.aliyanaresorts.aliyanahotelresorts.tools.SPData;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.aliyanaresorts.aliyanahotelresorts.network.API.keyBookProses;
import static com.aliyanaresorts.aliyanahotelresorts.network.API.keyBookRoom;
import static com.aliyanaresorts.aliyanahotelresorts.tools.Helper.formatingRupiah;
import static com.aliyanaresorts.aliyanahotelresorts.tools.Helper.getIntentData;
import static com.aliyanaresorts.aliyanahotelresorts.tools.Helper.isNetworkNotAvailable;
import static com.aliyanaresorts.aliyanahotelresorts.tools.Style.setTemaAplikasi;

public class PreviewBookingActivity extends AppCompatActivity {

    private ArrayList<PreviewList> arrayList;
    private RecyclerView.Adapter adapter;
    private LoadingDialog loadingDialog;
    private TextView kode, total;
    private String kdb, harga;
    private Activity activity;
    private SPData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_booking);

        activity = this;
        data = SPData.getInstance(activity);
        setTemaAplikasi(this, 1);

        Button konfirm = findViewById(R.id.btKonfirmasi);
        kode = findViewById(R.id.kodeBook);
        total = findViewById(R.id.total);
        final NoInetDialog noInetDialog = new NoInetDialog(this);

        arrayList = new ArrayList<>();
        RecyclerView recyclerView = findViewById(R.id.previewList);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        getData();
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        adapter = new PreviewAdapter(arrayList, PreviewBookingActivity.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        konfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkNotAvailable(getBaseContext())){
                    noInetDialog.bukaDialog();
                }else {
                    konfirmBooking(view);
                }
            }
        });

    }

    private void konfirmBooking(final View view) {
        loadingDialog = new LoadingDialog(this);
        loadingDialog.bukaDialog();

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest strReq = new StringRequest(Request.Method.POST, keyBookProses, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                loadingDialog.tutupDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    Log.e("res", response);
                    if (jObj.getString("msg").equals("Berhasil")) {
                        Snackbar.make(view, R.string.suksesbook, Snackbar.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 2000L);
                    }else{
                        Snackbar.make(view, R.string.gagal, Snackbar.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                loadingDialog.tutupDialog();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Accept", "application/json; charset=UTF-8");
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization", data.getKeyToken() );
                return params;
            }

            @Override
            protected Map<String, String> getParams()  {
                Map<String, String> params = new HashMap<>();
                params.put("kode_booking", kdb);
                params.put("tgl_checkin", getIntentData(activity,"ci"));
                params.put("tgl_checkout", getIntentData(activity,"co"));
                params.put("total", harga);
                for (int i=0;i< arrayList.size();i++){
                    params.put("id_tipe["+i+"]", arrayList.get(i).getId_tipe());
                    params.put("jml_kamar["+i+"]", arrayList.get(i).getJml_kamar());
                }
                return params;
            }
        };
        requestQueue
                .add(strReq)
                .setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void getData() {
        loadingDialog = new LoadingDialog(this);
        loadingDialog.bukaDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET, keyBookRoom, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingDialog.tutupDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray child = jsonObject.getJSONArray("temp");
                    kdb = jsonObject.getString("kd_booking");
                    kode.setText(kdb);
                    harga = jsonObject.getString("total");
                    total.setText(formatingRupiah(harga));
                    for (int i =0; i<child.length();i++){
                        JSONObject childObj = child.getJSONObject(i);
                        arrayList.add(new PreviewList(
                                childObj.getString("id"),
                                childObj.getString("id_tipe"),
                                childObj.getString("subtotal"),
                                childObj.getString("kapasitas"),
                                childObj.getString("harga"),
                                childObj.getString("tipe"),
                                childObj.getString("jml_kamar")
                        ));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                loadingDialog.tutupDialog();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", data.getKeyToken() );
                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(strReq, "json_obj_req");
    }

}
