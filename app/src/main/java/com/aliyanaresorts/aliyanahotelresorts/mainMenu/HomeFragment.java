package com.aliyanaresorts.aliyanahotelresorts.mainMenu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.aliyanaresorts.aliyanahotelresorts.R;
import com.aliyanaresorts.aliyanahotelresorts.dialog.NoInetDialog;
import com.aliyanaresorts.aliyanahotelresorts.mainMenu.home.about.AboutActivity;
import com.aliyanaresorts.aliyanahotelresorts.mainMenu.home.booking.BookingActivity;
import com.aliyanaresorts.aliyanahotelresorts.mainMenu.home.fasilitas.FasilitasActivity;
import com.aliyanaresorts.aliyanahotelresorts.mainMenu.home.info.InfoHotelActivity;
import com.aliyanaresorts.aliyanahotelresorts.mainMenu.home.promo.PromoActivity;
import com.aliyanaresorts.aliyanahotelresorts.mainMenu.home.rooms.RoomActivity;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import technolifestyle.com.imageslider.FlipperLayout;
import technolifestyle.com.imageslider.FlipperView;

import static com.aliyanaresorts.aliyanahotelresorts.network.API.keyDomainSistem;
import static com.aliyanaresorts.aliyanahotelresorts.network.API.keySlideHome;
import static com.aliyanaresorts.aliyanahotelresorts.tools.Helper.isNetworkNotAvailable;
import static com.aliyanaresorts.aliyanahotelresorts.tools.Style.setTemaAplikasi;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private FlipperLayout flipper;

    private ArrayList<HashMap<String, String>> list_dataS;

    private TextView selamat;
    private NoInetDialog noInetDialog;
    private Activity activity;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setTemaAplikasi(getActivity(),0);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        activity=getActivity();
        ImageView rooms = view.findViewById(R.id.bt_rooms);
        ImageView book = view.findViewById(R.id.bt_book);
        ImageView about = view.findViewById(R.id.bt_about);
        ImageView promo = view.findViewById(R.id.bt_promo);
        ImageView galeri = view.findViewById(R.id.bt_galleries);
        ImageView fasilitas = view.findViewById(R.id.bt_facilities);
        flipper = view.findViewById(R.id.flipper);
        selamat = view.findViewById(R.id.textWelcome);
        flipper.setCircularIndicatorLayoutParams(0, 0);
        noInetDialog = new NoInetDialog(activity);

        Typeface face = Typeface.createFromAsset(Objects.requireNonNull(activity).getAssets(),
                "JosefinSans-Regular.ttf");
        selamat.setTypeface(face);

        final Animation animation1 =  AnimationUtils.loadAnimation(activity,R.anim.coba);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                selamat.setAnimation(animation1);
                selamat.setVisibility(View.VISIBLE);
            }
        }, 1500);

        rooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkNotAvailable(Objects.requireNonNull(getContext()))){
                    noInetDialog.bukaDialog();
                }else {
                    Intent intent = new Intent(activity, RoomActivity.class);
                    startActivity(intent);
                }
            }
        });

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkNotAvailable(Objects.requireNonNull(getContext()))){
                    noInetDialog.bukaDialog();
                }else {
                    Intent intent = new Intent(activity, BookingActivity.class);
                    startActivity(intent);
                }
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, AboutActivity.class);
                startActivity(intent);
            }
        });

        promo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkNotAvailable(Objects.requireNonNull(getContext()))){
                    noInetDialog.bukaDialog();
                }else {
                    Intent intent = new Intent(activity, PromoActivity.class);
                    startActivity(intent);
                }
            }
        });

        galeri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkNotAvailable(Objects.requireNonNull(getContext()))){
                    noInetDialog.bukaDialog();
                }else {
                    Intent intent = new Intent(activity, InfoHotelActivity.class);
                    startActivity(intent);
                }
            }
        });

        fasilitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkNotAvailable(Objects.requireNonNull(getContext()))){
                    noInetDialog.bukaDialog();
                }else {
                    Intent intent = new Intent(activity, FasilitasActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void setSlide() {
        RequestQueue requestQueueS = Volley.newRequestQueue(Objects.requireNonNull(activity));

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
                                map.put("judul", json.getString("judul"));
                                map.put("foto", json.getString("foto"));
                                list_dataS.add(map);
                                @NonNull
                                FlipperView view = new FlipperView(Objects.requireNonNull(activity).getBaseContext());
                                view.setImageUrl(keyDomainSistem+list_dataS.get(a).get("foto"))
                                        .setDescription(list_dataS.get(a).get("judul"))
                                        .setDescriptionBackgroundColor(getResources()
                                                .getColor(R.color.hitamtrans));
                                if (!Objects.requireNonNull(list_dataS.get(a).get("foto")).isEmpty()) {
                                    flipper.addFlipperView(view);
//                                    final int finalA = a;
//                                    view.setOnFlipperClickListener(new FlipperView.OnFlipperClickListener() {
//                                        @Override
//                                        public void onFlipperClick(FlipperView flipperView) {
//                                            Bundle bundle = new Bundle();
//                                            bundle.putString("posisi", list_dataS.get(finalA).get("id"));
//                                            Intent intent = new Intent(activity, RoomDetailActivity.class);
//                                            intent.putExtras(bundle);
//                                            startActivity(intent);
//                                        }
//                                    });
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Objects.requireNonNull(activity).recreate();
            }
        });
        requestQueueS
                .add(stringRequestS)
                .setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isNetworkNotAvailable(Objects.requireNonNull(getContext()))){
            noInetDialog.bukaDialog();
            FlipperView a = new FlipperView(Objects.requireNonNull(activity).getBaseContext());
            a.setImageDrawable(R.drawable.image_slider_1)
                    .setDescription(getResources().getString(R.string.noinet))
                    .setDescriptionBackgroundColor(getResources()
                            .getColor(R.color.hitamtrans));
        }else {
            setSlide();
        }
    }
}
