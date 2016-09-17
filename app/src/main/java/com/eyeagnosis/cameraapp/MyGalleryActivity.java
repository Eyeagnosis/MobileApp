package com.eyeagnosis.cameraapp;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.eyeagnosis.cameraapp.adapter.GalleryAdapter;
import com.eyeagnosis.cameraapp.model.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyGalleryActivity extends AppCompatActivity {

    private String TAG = MyGalleryActivity.class.getSimpleName();
    private static final String galleryURL = "http://xxxxxxxxxxxxx.com/xxxx/gallery.php";
    private ArrayList<Image> images;
    private ProgressDialog pDialog;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_gallery);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        if (null != toolbar) {
//            toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
//        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        pDialog = new ProgressDialog(this);
        images = new ArrayList<>();
        mAdapter = new GalleryAdapter(getApplicationContext(), images);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SlideShowDialogFragment newFragment = SlideShowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        fetchImages();
    }

    private void fetchImages() {

        pDialog.setMessage("Fetching Gallery...");
        pDialog.show();

        JsonArrayRequest req = new JsonArrayRequest(galleryURL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        pDialog.hide();

                        images.clear();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                Image image = new Image();
                                image.setPicture(object.getString("image"));

//                                JSONObject url = object.getJSONObject("url");
//                                image.setSmall(url.getString("small"));
//                                image.setMedium(object.getString("medium"));
//                                image.setLarge(object.getString("large"));
//                                image.setTimestamp(object.getString("timestamp"));

                                images.add(image);

                            } catch (JSONException e) {
                                Log.e(TAG, "Json parsing error: " + e.getMessage());
                            }
                        }

                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                pDialog.hide();
                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content), "PLEASE CHECK YOUR INTERNET", Snackbar.LENGTH_LONG)
                        .setAction("DISMISS", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        });
                // Changing snackbar background
                snackbar.getView().setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

                // Changing message text color
                snackbar.setActionTextColor(Color.YELLOW);

                // Changing action button text color
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);

                snackbar.show();
            }
        });

        // Adding request to request queue
        MyAppController.getInstance().addToRequestQueue(req);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.tsg_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_refresh) {
//            images = new ArrayList<>();
//            mAdapter = new GalleryAdapter(getApplicationContext(), images);
//            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
//            recyclerView.setLayoutManager(mLayoutManager);
//            recyclerView.setItemAnimator(new DefaultItemAnimator());
//            recyclerView.setAdapter(mAdapter);
//            fetchImages();
//            return true;
//        } else if (id == R.id.action_rate) {
//            AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(
//                    MyGalleryActivity.this);
//
//            // Setting Dialog Title
//            alertDialog2.setTitle("Rate Sol Poetry");
//
//            // Setting Dialog Message
//            alertDialog2
//                    .setMessage("If you enjoyed the app, please rate on Play Store, thank you for your support!");
//
//            // Setting Icon to Dialog
//            alertDialog2.setIcon(R.drawable.ic_thumb_up);
//
//            // Setting Positive "Yes" Btn
//            alertDialog2.setPositiveButton("Yes!",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            // Write your code here to execute after dialog
//                            LaunchMarket();
//                        }
//                    });
//            // Setting Negative "NO" Btn
//            alertDialog2.setNegativeButton("No",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            // Write your code here to execute after dialog
//                            dialog.cancel();
//                        }
//                    });
//
//            // Showing Alert Dialog
//            alertDialog2.show();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    private void LaunchMarket() {
//        // TODO Auto-generated method stub
//        Uri uri = Uri.parse("market://details?id=" + getPackageName());
//        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
//        try {
//            startActivity(myAppLinkToMarket);
//        } catch (ActivityNotFoundException e) {
//            Toast.makeText(this, " unable to find market app",
//                    Toast.LENGTH_LONG).show();
//        }
//    }

}