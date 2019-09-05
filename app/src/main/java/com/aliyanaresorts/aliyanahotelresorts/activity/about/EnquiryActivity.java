package com.aliyanaresorts.aliyanahotelresorts.activity.about;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.aliyanaresorts.aliyanahotelresorts.R;
import com.aliyanaresorts.aliyanahotelresorts.service.LoadingDialog;
import com.aliyanaresorts.aliyanahotelresorts.service.NoInetDialog;
import com.aliyanaresorts.aliyanahotelresorts.service.database.AppController;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.aliyanaresorts.aliyanahotelresorts.service.Helper.closeKeyboard;
import static com.aliyanaresorts.aliyanahotelresorts.service.Helper.isNetworkAvailable;
import static com.aliyanaresorts.aliyanahotelresorts.service.Helper.isValidMail;
import static com.aliyanaresorts.aliyanahotelresorts.service.Style.setWindowFlag;
import static com.aliyanaresorts.aliyanahotelresorts.service.database.API.KEY_ENQ;

public class EnquiryActivity extends AppCompatActivity {

    private  EditText mNama, mEmail, mIsi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquiry);

        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
        getWindow().setStatusBarColor(getResources().getColor(R.color.goldtrans));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mNama = findViewById(R.id.nama);
        mEmail = findViewById(R.id.email);
        mIsi = findViewById(R.id.isi);
        Button submit = findViewById(R.id.btnKirim);
        final NoInetDialog noInetDialog = new NoInetDialog(this);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard(EnquiryActivity.this);

                String nama = mNama.getText().toString();
                String email = mEmail.getText().toString();
                String isi = mIsi.getText().toString();

                if (nama.isEmpty()){
                    mNama.requestFocus();
                    mNama.setError(getResources().getString(R.string.kolom));
                }else if (email.isEmpty()){
                    mEmail.requestFocus();
                    mEmail.setError(getResources().getString(R.string.kolom));
                }else if (isValidMail(email)){
                    mEmail.requestFocus();
                    mEmail.setError(getResources().getString(R.string.imail));
                }else if (isi.isEmpty()){
                    mIsi.requestFocus();
                    mIsi.setError(getResources().getString(R.string.kolom));
                }else if(isNetworkAvailable(getBaseContext())){
                    noInetDialog.bukaDialog();
                }else {
                    kirim(v, nama, email, isi);
                }
            }
        });

    }

    private void kirim(final View v, final String nama, final String email, final String isi) {
        final LoadingDialog loadingDialog = new LoadingDialog(EnquiryActivity.this);
        loadingDialog.bukaDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, KEY_ENQ,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);
                            int success = jObj.getInt("success");
                            if (success == 1) {
                                Snackbar.make(v, R.string.bisa, Snackbar.LENGTH_SHORT).show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mNama.setText("");
                                        mEmail.setText("");
                                        mIsi.setText("");
                                    }
                                }, 1500);
                            }else{
                                Snackbar.make(v, R.string.gagal, Snackbar.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        loadingDialog.tutupDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingDialog.tutupDialog();
                        Toast.makeText(EnquiryActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
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
                params.put("email",email);
                params.put("isi",isi);
                return params;
            }
        };

        String tag_json_obj = "json_obj_req";
        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }
}
