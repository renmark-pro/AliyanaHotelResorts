package com.aliyanaresorts.aliyanahotelresorts;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aliyanaresorts.aliyanahotelresorts.dialog.LoadingDialog;
import com.aliyanaresorts.aliyanahotelresorts.dialog.NoInetDialog;
import com.aliyanaresorts.aliyanahotelresorts.network.AppController;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import technolifestyle.com.imageslider.FlipperLayout;
import technolifestyle.com.imageslider.FlipperView;

import static com.aliyanaresorts.aliyanahotelresorts.network.API.keyDaftar;
import static com.aliyanaresorts.aliyanahotelresorts.network.API.keyDomainSistem;
import static com.aliyanaresorts.aliyanahotelresorts.network.API.keySlideHome;
import static com.aliyanaresorts.aliyanahotelresorts.tools.Helper.closeKeyboard;
import static com.aliyanaresorts.aliyanahotelresorts.tools.Helper.getPhoneWithoutZeroFirst;
import static com.aliyanaresorts.aliyanahotelresorts.tools.Helper.isNetworkNotAvailable;
import static com.aliyanaresorts.aliyanahotelresorts.tools.Helper.isValidMail;
import static com.aliyanaresorts.aliyanahotelresorts.tools.Helper.isValidMobile;
import static com.aliyanaresorts.aliyanahotelresorts.tools.Style.setStyleStatusBarGoldTrans;

public class DaftarActivity extends AppCompatActivity {

    private FlipperLayout flipper;
    private EditText mnama, memail, mtelepon,mpassword, mcpassword;
    private ArrayList<HashMap<String, String>> list_dataS;
    private LoadingDialog loadingDialog;
    private CountryCodePicker codePicker;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);
        activity = this;
        loadingDialog = new LoadingDialog(this);
        setStyleStatusBarGoldTrans(this);
        mnama = findViewById(R.id.nama);
        memail = findViewById(R.id.email);
        codePicker = findViewById(R.id.ccp);
        mtelepon = findViewById(R.id.telpon);
        mpassword = findViewById(R.id.password);
        mcpassword = findViewById(R.id.kpassword);
        Button masuk = findViewById(R.id.btnMasuk);
        Button daftar = findViewById(R.id.btnDaftar);
        flipper = findViewById(R.id.flipper);
        final NoInetDialog noInetDialog = new NoInetDialog(this);

        mtelepon.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 1 && s.toString().startsWith("0")) {
                    s.clear();
                }
            }
        });

        setSlide();
        masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard(activity);
                String nama = mnama.getText().toString();
                String email = memail.getText().toString();
                String telepon = mtelepon.getText().toString();
                String password = mpassword.getText().toString();
                String cpassword = mcpassword.getText().toString();

                if (nama.isEmpty()){
                    mnama.requestFocus();
                    mnama.setError(getResources().getString(R.string.isi));
                }else if(email.isEmpty()){
                    memail.requestFocus();
                    memail.setError(getResources().getString(R.string.isi));
                }else if(!isValidMail(email)){
                    memail.requestFocus();
                    memail.setError(getResources().getString(R.string.imail));
                }else if(telepon.isEmpty()){
                    mtelepon.requestFocus();
                    mtelepon.setError(getResources().getString(R.string.isi));
                }else if(!isValidMobile("0"+getPhoneWithoutZeroFirst(telepon))){
                    mtelepon.requestFocus();
                    mtelepon.setError(getResources().getString(R.string.ihp));
                }else if(password.isEmpty()){
                    mpassword.requestFocus();
                    mpassword.setError(getResources().getString(R.string.isi));
                }else if(password.length()<6){
                    mpassword.requestFocus();
                    mpassword.setError( getResources().getString(R.string.ipw));
                }else if(cpassword.isEmpty()){
                    mcpassword.requestFocus();
                    mcpassword.setError(getResources().getString(R.string.isi));
                }else if (!cpassword.equals(password)){
                    Snackbar.make(v, R.string.passalah, Snackbar.LENGTH_SHORT).show();
                }else if(isNetworkNotAvailable(getBaseContext())){
                    noInetDialog.bukaDialog();
                }else {
                    closeKeyboard(activity);
                    checkRegister(nama, email, getPhoneWithoutZeroFirst(telepon), password, cpassword, v);
                }
            }
        });
    }

    private void checkRegister( final String nama, final String email, final String telepon,
                                final String password, final String cpassword, final View view) {
        loadingDialog.bukaDialog();
        StringRequest strReq = new StringRequest(Request.Method.POST, keyDaftar, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                loadingDialog.tutupDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    if (jObj.getString("msg").equals("Registrasi berhasil!")) {
                        Snackbar.make(view, R.string.bdaftar, Snackbar.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 2000);
                    }else {
                        if (jObj.getString("msg").equals("Email atau No Telepon telah terdaftar")) {
                            Snackbar.make(view, R.string.emailada, Snackbar.LENGTH_SHORT).show();
                        } else{
                            Snackbar.make(view, R.string.xdaftar, Snackbar.LENGTH_SHORT).show();
                        }
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
            public Map<String, String> getHeaders(){
                Map<String, String> params = new HashMap<>();
                params.put("Accept", "application/json; charset=UTF-8");
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nama", nama);
                params.put("email", email);
                params.put("kd_negara", codePicker.getSelectedCountryCode());
                params.put("no_telepon", telepon);
                params.put("password", password);
                params.put("password_confirmation", cpassword);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, "json_obj_req");
    }

    private void setSlide() {
        RequestQueue requestQueueS = Volley.newRequestQueue(this);
        list_dataS = new ArrayList<>();
        StringRequest stringRequestS = new StringRequest(Request.Method.GET, keySlideHome,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("slideshow");
                            for (int a = 0; a < jsonArray.length(); a++) {
                                JSONObject json = jsonArray.getJSONObject(a);
                                HashMap<String, String> map = new HashMap<>();
                                map.put("id", json.getString("id"));
                                map.put("foto", json.getString("foto"));
                                map.put("judul", json.getString("judul"));
                                list_dataS.add(map);
                                FlipperView view = new FlipperView(getBaseContext());
                                view.setImageUrl(keyDomainSistem+list_dataS.get(a).get("foto"))
                                        .setDescription(list_dataS.get(a).get("judul"))
                                        .setDescriptionBackgroundColor(getResources()
                                                .getColor(R.color.hitamtrans));
                                flipper.setCircularIndicatorLayoutParams(0,0);
                                flipper.addFlipperView(view);
                                final int finalA = a;
                                view.setOnFlipperClickListener(new FlipperView.OnFlipperClickListener() {
                                    @Override
                                    public void onFlipperClick(FlipperView flipperView) {
                                        Snackbar.make(flipper, Objects.requireNonNull(list_dataS.get(finalA).get("judul")), Snackbar.LENGTH_LONG).show();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueueS
                .add(stringRequestS)
                .setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }
}
