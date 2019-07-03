package com.aliyanaresorts.aliyanahotelresorts.activity.account;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.aliyanaresorts.aliyanahotelresorts.R;
import com.aliyanaresorts.aliyanahotelresorts.service.database.AppController;
import com.aliyanaresorts.aliyanahotelresorts.service.SPData;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static com.aliyanaresorts.aliyanahotelresorts.service.Helper.getPermissions;
import static com.aliyanaresorts.aliyanahotelresorts.service.database.API.KEY_GET_USER;
import static com.aliyanaresorts.aliyanahotelresorts.SplashActivity.MY_PERMISSIONS_REQUEST_GET_ACCESS;

public class FotoProfilAccountActivity extends AppCompatActivity {

    private ImageView imageView;
    private Bitmap decoded;

    private static final String TAG = FotoProfilAccountActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foto_profil_account);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(SPData.getInstance(this).getKeyNama());
        toolbar.setTitleTextColor(getResources().getColor(R.color.putih));
        setSupportActionBar(toolbar);

        getPermissions(FotoProfilAccountActivity.this);

        imageView = findViewById(R.id.imageView);

//        if (!SPData.getInstance(this).getKeyFoto().equals("http://aliyanaresorts.com/app/user/foto/")){
//            Glide.with(this).load(SPData.getInstance(this).getKeyFoto())
//                    .transition(withCrossFade())
//                    .placeholder(R.drawable.image_slider_1)
//                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
////                    .skipMemoryCache(true)
//                    .fitCenter()
//                    .into(imageView);
//        }

    }

    private void uploadImage(final View view) {

        //menampilkan progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, KEY_GET_USER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e(" Foto : ", String.valueOf(decoded));
                        Log.e(TAG, "Response: " + response);

                        try {
                            final JSONObject jObj = new JSONObject(response);
                            int success = jObj.getInt("success");
                            if (success == 1) {
//                                SPData.getInstance(getApplicationContext())
//                                        .updateFoto(jObj.getString("foto"));
                                Snackbar.make(view, R.string.bupdate, Snackbar.LENGTH_SHORT).show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        recreate();
                                    }
                                }, 1500);
                            } else if (success == 2) {
                                Snackbar.make(view, R.string.iusr, Snackbar.LENGTH_SHORT).show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 2000);
                            }else{
                                Snackbar.make(view, R.string.xupdate, Snackbar.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        //menghilangkan progress dialog
                        loading.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //menghilangkan progress dialog
                        loading.dismiss();

                        //menampilkan toast
                        Toast.makeText(FotoProfilAccountActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e(TAG, error.getMessage());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //membuat parameters
                Map<String, String> params = new HashMap<>();

                //menambah parameter yang di kirim ke web servis
//                params.put("uid", SPData.getInstance(getBaseContext()).getKeyUid());
                params.put("foto",getStringImage(decoded));
                return params;
            }
        };

        String tag_json_obj = "json_obj_req";
        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

    private String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private void setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));

        //menampilkan gambar yang dipilih dari camera/gallery ke ImageView
        imageView.setImageBitmap(decoded);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profil_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.btnUpload)
            pickImage();
        return true;
    }

    private void pickImage() {
        CropImage.startPickImageActivity(this);
    }

    //CROP REQUEST JAVA
    private void croprequest(Uri imageUri) {
        CropImage.activity(imageUri)
                .setAspectRatio(1,1)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(false)
                .start(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //RESULT FROM SELECTED IMAGE
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            croprequest(imageUri);
        }

        //RESULT FROM CROPING ACTIVITY
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());
                    setToImageView(getResizedBitmap(bitmap));
                    Log.e(" Foto : ", getStringImage(decoded));
                    uploadImage(getWindow().getDecorView().getRootView());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Bitmap getResizedBitmap(Bitmap image) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = 1024;
            height = (int) (width / bitmapRatio);
        } else {
            height = 1024;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (requestCode == MY_PERMISSIONS_REQUEST_GET_ACCESS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Kosong
            } else {
                Toast.makeText(FotoProfilAccountActivity.this, "Ijin Di Tolak!", Toast.LENGTH_SHORT).show();

            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}