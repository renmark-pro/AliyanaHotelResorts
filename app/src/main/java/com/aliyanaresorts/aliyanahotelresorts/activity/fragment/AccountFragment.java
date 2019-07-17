package com.aliyanaresorts.aliyanahotelresorts.activity.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.aliyanaresorts.aliyanahotelresorts.R;
import com.aliyanaresorts.aliyanahotelresorts.activity.account.HelpAccountActivity;
import com.aliyanaresorts.aliyanahotelresorts.activity.account.ProfilDetailActivity;
import com.aliyanaresorts.aliyanahotelresorts.activity.account.PromoAccountActivity;
import com.aliyanaresorts.aliyanahotelresorts.activity.account.VoucherAccountActivity;
import com.aliyanaresorts.aliyanahotelresorts.service.LoadingDialog;
import com.aliyanaresorts.aliyanahotelresorts.service.SPData;
import com.aliyanaresorts.aliyanahotelresorts.service.database.AppController;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.aliyanaresorts.aliyanahotelresorts.SplashActivity.MY_PERMISSIONS_REQUEST_GET_ACCESS;
import static com.aliyanaresorts.aliyanahotelresorts.service.Helper.setFotoUser;
import static com.aliyanaresorts.aliyanahotelresorts.service.Helper.setTextData;
import static com.aliyanaresorts.aliyanahotelresorts.service.Style.setTemaAplikasi;
import static com.aliyanaresorts.aliyanahotelresorts.service.database.API.KEY_GET_USER;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    private LoadingDialog loadingDialog;
    private TextView nama, telpon;
    private ImageView fotoUser;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setTemaAplikasi(getActivity(),1);
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        getPermissions();
        nama = view.findViewById(R.id.nama);
        telpon = view.findViewById(R.id.telpon);
        fotoUser = view.findViewById(R.id.imgView);
        LinearLayout promo = view.findViewById(R.id.promo);
        LinearLayout bantuan = view.findViewById(R.id.bantuan);
        LinearLayout voucher = view.findViewById(R.id.voucher);
        LinearLayout keluar = view.findViewById(R.id.keluar);
        CardView user = view.findViewById(R.id.layoutUser);

        nama.setText(SPData.getInstance(getActivity()).getKeyNama());
        telpon.setText(SPData.getInstance(getActivity()).getKeyTelepon());

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfilDetailActivity.class);
                startActivity(intent);
            }
        });

        promo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PromoAccountActivity.class);
                startActivity(intent);
            }
        });

        bantuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HelpAccountActivity.class);
                startActivity(intent);
            }
        });

        voucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VoucherAccountActivity.class);
                startActivity(intent);
            }
        });

        keluar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPData.getInstance(getActivity()).logout();
                Objects.requireNonNull(getActivity()).finish();
            }
        });
    }

    private void getPermissions() {
        /* Check and Request permission */
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.VIBRATE,
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_GET_ACCESS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (requestCode == MY_PERMISSIONS_REQUEST_GET_ACCESS) {
            if (grantResults.length <= 0
                    || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getActivity(), "Ijin Di Tolak!", Toast.LENGTH_SHORT).show();

                    }
        }
    }

    private void getDetail() {
        loadingDialog = new LoadingDialog(getActivity());
        loadingDialog.bukaDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET, KEY_GET_USER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (fotoUser.getDrawable()!=getResources().getDrawable(R.drawable.image_slider_1)){
                    loadingDialog.tutupDialog();
                }
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject result = jsonObject.getJSONObject("user");
                    nama.setText(setTextData(result.getString("nama")));
                    telpon.setText(setTextData(result.getString("no_telepon")));
                    setFotoUser(result.getString("foto"), getContext(), fotoUser);
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                loadingDialog.tutupDialog();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", SPData.getInstance(getActivity()).getKeyToken() );
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, "json_obj_req");
    }

    @Override
    public void onStart() {
        super.onStart();
        getDetail();
    }

}
