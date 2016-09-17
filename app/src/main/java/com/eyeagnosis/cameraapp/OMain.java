package com.eyeagnosis.cameraapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.ImageBase64;
import com.kosalgeek.android.photoutil.ImageLoader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OMain extends AppCompatActivity {

    private final String TAG = this.getClass().getName();

    ImageView ivCamera, ivUpload, ivImage, ivDelete;
    private String UPLOAD_URL = "http://xxxxxxxxxxxxxxxxxx.com/xxxxxxxxxxx/upload.php";
    CameraPhoto cameraPhoto;
    final int CAMERA_REQUEST = 1022;
    String selectedPhoto;
    private EditText edServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_omain);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ///  granting permission  ////
        if (!checkPermission()) {
            requestAllPermissions();
        }

        cameraPhoto = new CameraPhoto(getApplicationContext());

        ivImage = (ImageView) findViewById(R.id.ivImage);
        ivCamera = (ImageView) findViewById(R.id.ivCamera);
        ivUpload = (ImageView) findViewById(R.id.ivUpload);
        ivDelete = (ImageView) findViewById(R.id.ivDelete);
        edServer = (EditText) findViewById(R.id.edServer);

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Integer> map = new HashMap<String, Integer>();
                map.put("blank", R.drawable.blankbg);
                ivImage.setImageResource(map.get("blank"));
            }
        });

        ivCamera.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            // Checking camera availability
                                            if (!isDeviceSupportCamera()) {
                                                Toast.makeText(getApplicationContext(),
                                                        "Sorry! Your device doesn't support camera",
                                                        Toast.LENGTH_LONG).show();
                                                // will close the app if the device does't have camera
                                            } else {
                                                try {
                                                    startActivityForResult(cameraPhoto.takePhotoIntent(), CAMERA_REQUEST);
                                                    cameraPhoto.addToGallery();
                                                } catch (IOException e) {
                                                    Toast.makeText(getApplicationContext(),
                                                            "Something Wrong while taking photos", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    }

        );

        ivUpload.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            edServer = (EditText) findViewById(R.id.edServer);
                                            String serverURL = edServer.getText().toString(); //get URL from EditText
                                            //if camera has no photo, show no message selected
                                            if (selectedPhoto == null || selectedPhoto.equals("")) {
                                                Toast.makeText(getApplicationContext(), "No Image Selected.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
//if serverURL in EditText is empty, show "Please insert server URL"
                                            if (serverURL.isEmpty()) {
                                                Toast.makeText(getApplicationContext(), "Please insert server URL.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            try {
                                                final Bitmap bitmap = ImageLoader.init().from(selectedPhoto).requestSize(1024, 1024).getBitmap();
                                                final String encodedImage = ImageBase64.encode(bitmap);
                                                Log.d(TAG, encodedImage);

                                                //Showing the progress dialog
                                                final ProgressDialog loading = ProgressDialog.show(OMain.this, "Uploading...", "Please wait...", false, false);
                                                //If you want to add server URL from code and not EditText, insert the server URL at the top like
                                                //    private String UPLOAD_URL = "http://xxxxxxxxxxxxxxxxxx.com/xxxxxxxxxxx/upload.php";
                                                //then change serverURL after the Request.Methid.POST below to UPLOAD_URL
                                                StringRequest stringRequest = new StringRequest(Request.Method.POST, serverURL,
                                                        new Response.Listener<String>() {
                                                            @Override
                                                            public void onResponse(String s) {
                                                                //Disimissing the progress dialog
                                                                loading.dismiss();
                                                                //Showing toast message of the response
                                                                Toast.makeText(OMain.this, s, Toast.LENGTH_LONG).show();
                                                            }
                                                        },
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError volleyError) {
                                                                //Dismissing the progress dialog
                                                                loading.dismiss();

                                                                //Showing toast
                                                                Toast.makeText(OMain.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                                                            }
                                                        }) {
                                                    @Override
                                                    protected Map<String, String> getParams() throws AuthFailureError {

                                                        //Creating parameters
                                                        HashMap<String, String> params = new HashMap<String, String>();

                                                        //Adding parameters
                                                        params.put("image", encodedImage);

                                                        //returning parameters
                                                        return params;
                                                    }
                                                };
                                                stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                                                //Creating a Request Queue
//                                                RequestQueue requestQueue = Volley.newRequestQueue(OMain.this);

                                                //Adding request to the queue
//                                                requestQueue.add(stringRequest);
                                                MyAppController.getInstance().addToRequestQueue(stringRequest);

                                            } catch (FileNotFoundException e) {
                                                Toast.makeText(getApplicationContext(),
                                                        "Something Wrong while encoding photos", Toast.LENGTH_SHORT).show();
                                            }

                                        }
                                    }

        );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                String photoPath = cameraPhoto.getPhotoPath();
                selectedPhoto = photoPath;
                Bitmap bitmap = null;
                try {
                    bitmap = ImageLoader.init().from(photoPath).requestSize(512, 512).getBitmap();
                    ivImage.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    Toast.makeText(getApplicationContext(),
                            "Something Wrong while loading photos", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    private boolean checkPermission() {
        int result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int result2 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED) {

            return true;

        } else {
            //Toast.makeText(this,"You don't have permission to use further features",Toast.LENGTH_LONG).show();
            return false;

        }
    }

    private void requestAllPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) &&
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            Toast.makeText(this, "Application needs permission to use your camera and storage.", Toast.LENGTH_LONG).show();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permissions Granted.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(this, "Permissions Denied.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }

    //    * Checking device has camera hardware or not

    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_omain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_gallery) {
            Intent i = new Intent(getApplicationContext(), MyGalleryActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
