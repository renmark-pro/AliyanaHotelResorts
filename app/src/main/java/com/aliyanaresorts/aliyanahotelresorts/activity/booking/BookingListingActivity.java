package com.aliyanaresorts.aliyanahotelresorts.activity.booking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aliyanaresorts.aliyanahotelresorts.R;
import com.aliyanaresorts.aliyanahotelresorts.activity.MasukActivity;
import com.aliyanaresorts.aliyanahotelresorts.service.mInterface.RecyclerTouchListener;
import com.aliyanaresorts.aliyanahotelresorts.service.SPData;
import com.aliyanaresorts.aliyanahotelresorts.service.database.AppController;
import com.aliyanaresorts.aliyanahotelresorts.service.database.models.BookList;
import com.aliyanaresorts.aliyanahotelresorts.service.database.viewHolders.BookListingAdapter;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.aliyanaresorts.aliyanahotelresorts.service.Helper.getIntentData;
import static com.aliyanaresorts.aliyanahotelresorts.service.Style.setTemaAplikasi;
import static com.aliyanaresorts.aliyanahotelresorts.service.database.API.KEY_ADD_ROOM_COUNT;
import static com.aliyanaresorts.aliyanahotelresorts.service.database.API.KEY_CEK_KAMAR;

public class BookingListingActivity extends AppCompatActivity {

    private ArrayList<BookList> arrayList;
    private RecyclerView.Adapter adapter;
    private ProgressDialog pDialog, aDialog;
    private TextView hitungan;
    private LinearLayout proses;
    int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_listing);
        setTemaAplikasi(this, 1);

        proses = findViewById(R.id.layoutProses);
        hitungan=findViewById(R.id.txtCount);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        @SuppressLint("PrivateResource") final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
        Objects.requireNonNull(upArrow).setColorFilter(ContextCompat.getColor(this, R.color.goldtua), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        arrayList = new ArrayList<>();
        final RecyclerView recyclerView = findViewById(R.id.roomList);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        getData(getIntentData(this,"ci"), getIntentData(this,"co"),
                getIntentData(this,"or"), getIntentData(this,"id"));
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        adapter = new BookListingAdapter(arrayList, this, BookingListingActivity.this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),
                recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, final int position) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addCount();
                    }
                }, 1000L);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        proses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(hitungan.getText().toString().isEmpty()){
                    Snackbar.make(v, R.string.blmpesan, Snackbar.LENGTH_SHORT).show();
                }else {
                    Bundle bundle = new Bundle();
                    bundle.putString("ci",getIntentData(BookingListingActivity.this,"ci"));
                    bundle.putString("co", getIntentData(BookingListingActivity.this,"co"));
                    Intent i = new Intent(BookingListingActivity.this, PreviewBookingActivity.class);
                    i.putExtras(bundle);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

    private void addCount() {
        aDialog = new ProgressDialog(this);
        aDialog.setCancelable(false);
        aDialog.setMessage(this.getResources().getString(R.string.tunggu));
        aDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.GET, KEY_ADD_ROOM_COUNT, new Response.Listener<String>() {

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(String response) {
                Log.e(MasukActivity.class.getSimpleName(), "Login Response: " + response);
                aDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    hitungan.setText(" "+jsonObject.getString("jumlah")+" ");
                    counter = Integer.parseInt(jsonObject.getString("jumlah"));
                    if (counter!=0) {
                        proses.setBackground(getResources().getDrawable(R.drawable.bt_book));
                    }else {
                        proses.setBackground(getResources().getDrawable(R.drawable.bt_cek));
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(MasukActivity.class.getSimpleName(), "Login Error: " + error.getMessage());
                Toast.makeText(BookingListingActivity.this,
                        error.getMessage(), Toast.LENGTH_LONG).show();
                aDialog.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", SPData.getInstance(BookingListingActivity.this).getKeyToken() );
                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(strReq, "json_obj_req");
    }

    private void getData(final String cekin, final String cekout, final String tamu, final String id){
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage(getResources().getString(R.string.tunggu));
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, KEY_CEK_KAMAR, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray result = jsonObject.getJSONArray("kamar");
                    for(int i =0;i<result.length(); i++) {
                        JSONObject productObject = result.getJSONObject(i);
                        arrayList.add(new BookList(
                                productObject.getString("id"),
                                productObject.getString("no_room"),
                                productObject.getString("foto"),
                                productObject.getString("id_tipe"),
                                productObject.getString("tipe"),
                                productObject.getString("kapasitas"),
                                productObject.getString("harga")
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
                Log.e(MasukActivity.class.getSimpleName(), "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                pDialog.dismiss();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization", SPData.getInstance(BookingListingActivity.this).getKeyToken() );
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("tgl_checkin", cekin);
                params.put("tgl_checkout",cekout);
                params.put("jml_tamu", tamu);
                params.put("tipe", id);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, "json_obj_req");
    }
}