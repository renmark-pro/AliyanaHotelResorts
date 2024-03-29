package com.aliyanaresorts.aliyanahotelresorts.mainMenu.status.menu.proses;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyanaresorts.aliyanahotelresorts.R;
import com.aliyanaresorts.aliyanahotelresorts.dialog.LoadingDialog;
import com.aliyanaresorts.aliyanahotelresorts.mainMenu.status.MyBookingList;
import com.aliyanaresorts.aliyanahotelresorts.network.AppController;
import com.aliyanaresorts.aliyanahotelresorts.tools.SPData;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.aliyanaresorts.aliyanahotelresorts.network.API.keyProsesList;
import static com.aliyanaresorts.aliyanahotelresorts.tools.Helper.setViewStatus;

public class ProsesStatusFragment extends Fragment {

    private ArrayList<MyBookingList> arrayList;
    private RecyclerView.Adapter adapter;
    private LoadingDialog loadingDialog;
    private NestedScrollView nestedLayout;
    private RelativeLayout kosongLayout;
    private Activity activity;
    private SPData data;

    public ProsesStatusFragment() {
        ///Construct
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_proses, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        data= SPData.getInstance(activity);
        loadingDialog = new LoadingDialog(activity);
        nestedLayout = view.findViewById(R.id.nestedLayout);
        kosongLayout = view.findViewById(R.id.layKosong);
        setViewStatus(0, nestedLayout, kosongLayout);
        arrayList = new ArrayList<>();
        RecyclerView recyclerView = view.findViewById(R.id.prosesList);
        recyclerView.setHasFixedSize(true);
        getDetail();
        LinearLayoutManager layoutManager= new LinearLayoutManager(getContext());
        adapter = new ProsesStatusListAdapter(arrayList, getActivity(), getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void getDetail() {
        loadingDialog.bukaDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET, keyProsesList, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingDialog.tutupDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.e("masalah :", String.valueOf(jsonObject));
                    JSONArray result = jsonObject.getJSONArray("bookings");
                    if (result.length()>0){
                        for(int i =0;i<result.length(); i++) {
                            JSONObject productObject = result.getJSONObject(i);
                            arrayList.add(new MyBookingList(
                                    productObject.getString("id"),
                                    productObject.getString("kode_booking"),
                                    productObject.getString("tipe"),
                                    productObject.getString("jml_kamar"),
                                    productObject.getString("tgl_checkin"),
                                    productObject.getString("tgl_checkout"),
                                    productObject.getString("total_tagihan"),
                                    productObject.getString("terbayarkan"),
                                    productObject.getString("kekurangan"),
                                    productObject.getString("status")
                            ));
                        }
                    }
                    setViewStatus(result.length(), nestedLayout, kosongLayout);
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
        }){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("Authorization", data.getKeyToken());
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(strReq, "json_obj_req");
    }

}
