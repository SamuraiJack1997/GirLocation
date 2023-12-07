package gir.location;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.location.LocationManagerCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SplashScreen extends AppCompatActivity {

    LinearLayout vie;
    ImageView logo;
    TextView text;
    Handler handler;
    ProgressDialog progressDialog;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[3]==0 && grantResults[4]==0 && grantResults[5]==0 && grantResults[6]==0){
            if(!isLocationEnabled(getApplicationContext())){
                Toast.makeText(getApplicationContext(), "Lokacija na vašem uredjaju nije uključena! Molimo Vas da uključite lokaciju.", Toast.LENGTH_SHORT).show();
                finish();
            }
            else{
                continueProgram();
            }

        }else{
            Toast.makeText(getApplicationContext(), "Kako biste koristili aplikaciju potrebno je da odobrite dozvole koje aplikacija zahteva!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if (!Permissions.hasPermissions(this, Permissions.PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, Permissions.PERMISSIONS, Permissions.PERMISSION_ALL);
        }
    }
        //end onCreate()

    //IS LOCATION ENABLED
    private boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return LocationManagerCompat.isLocationEnabled(locationManager);
    }

    private void continueProgram(){

        //VARIJABLE
        vie = findViewById(R.id.vie);
        logo = findViewById(R.id.logo);
        text = findViewById(R.id.text);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.slide_in);
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        vie.startAnimation(anim);
        handler = new Handler();
        //VARIJABLE

        // PROVERA STATUSA PRIJAVE IZ FAJLA
        String s = readFromFile(SplashScreen.this);
        if (s.equals("true")) {
            User.prijavljen = true;
        } else {
            User.prijavljen = false;
        }

        //DODELJIVANJE MAC ADRESE I LOKACIJA I PROVERA MAC ADRESE
        try {
            User.macAdresa = MACAddr.getMAC(getApplicationContext());
            Log.d("MAC-ADDRESS", "Mac address:" + User.macAdresa);
        } catch (Exception e) {
            Log.d("MAC-ADDRESS", "MEK ADRESA NIJE UZETA!");
            Toast.makeText(getApplicationContext(), "Neuspešno prikupljanje mac adrese.", Toast.LENGTH_SHORT).show();
            finishAndRemoveTask();
        }

        //KOMUNIKACIJA SA SERVEROM
        checkMacAddress();

        //HANDLER ZA ANIMACIJE
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (User.prijavljen == true) {
                    if (User.macAdresaDodeljenja) {
                        Toast.makeText(getApplicationContext(), "Konekcija sa serverom uspešna.", Toast.LENGTH_SHORT).show();
                        intent = new Intent(SplashScreen.this, ActivityPrijava.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Konekcija sa serverom nije uspešna.", Toast.LENGTH_SHORT).show();
                        finishAndRemoveTask();
                    }
                } else {
                    if (User.macAdresaDodeljenja) {
                        Toast.makeText(getApplicationContext(), "Konekcija sa serverom uspešna.", Toast.LENGTH_SHORT).show();
                        intent = new Intent(SplashScreen.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Konekcija sa serverom nije uspešna.", Toast.LENGTH_SHORT).show();
                        finishAndRemoveTask();
                    }
                }
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }, 3000);
    }

    private String readFromFile(Context context) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput("config.txt");

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public void checkMacAddress(){
        String URL="https://test.gir.rs/checkMac.php";
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("true")){
                            getMacAndUser();
                        }else if(response.equals("false")){
                            insertMac();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error:Nije moguce uzeti mac addresu uredjaja.",Toast.LENGTH_LONG).show();
                finishAndRemoveTask();
            }
        }){
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mac_adresa", User.macAdresa);
                return params;
            };
        };
        requestQueue.add(stringRequest);
    }

    public void getMacAndUser(){
        String URL="https://test.gir.rs/getMacAndUser.php";
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("false")){
                            Toast.makeText(getApplicationContext(),"Vaš uredjaj nije aktiviran. Pokušajte kasnije.",Toast.LENGTH_LONG).show();
                            finishAndRemoveTask();
                        }else{
                        try {
                            JSONArray jsonArray=new JSONArray(response);
                            User.aktivan=jsonArray.getJSONObject(0).getString("aktivan");
                            if(User.aktivan.equals("ON")){
                                User.macAdresaDodeljenja=true;//PROGRAM MOZE DALJE
                                User.id_user=jsonArray.getJSONObject(0).getInt("id_zaposlenog");
                                User.ime=jsonArray.getJSONObject(0).getString("ime");
                                User.prezime=jsonArray.getJSONObject(0).getString("prezime");
                                getLocationForUser();
                            }else{
                                Toast.makeText(getApplicationContext(),"Vaš uredjaj nije aktiviran. Pokušajte kasnije.",Toast.LENGTH_LONG).show();
                                finishAndRemoveTask();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(),"Error:getMacAndUser();",Toast.LENGTH_LONG).show();
                            finishAndRemoveTask();
                        }}
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error:getMacAndUser();",Toast.LENGTH_LONG).show();
                finishAndRemoveTask();
            }
        }){
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mac_adresa", User.macAdresa);
                return params;
            };
        };
        requestQueue.add(stringRequest);
    }

    public void insertMac(){
        String URL="https://test.gir.rs/insertMac.php";
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("true")){
                            Toast.makeText(getApplicationContext(),"Vaš uredjaj je prijavljen. Pokušajte kasnije nakon odobrenja Vašeg uredjaja.",Toast.LENGTH_SHORT).show();
                        }else if(response.equals("false")){
                            Toast.makeText(getApplicationContext(),"Vaš uredjaj nije prijavljen. Pokušajte kasnije nakon odobrenja Vašeg uredjaja.",Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error:insertMac();",Toast.LENGTH_SHORT).show();
                finishAndRemoveTask();
            }
        }){
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mac_adresa", User.macAdresa);
                return params;
            };
        };
        requestQueue.add(stringRequest);
    }

    public void getLocationForUser(){
        String URL="https://test.gir.rs/getLocationForUser.php";
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("false")){
                            Toast.makeText(getApplicationContext(),"Greška prilikom dobavljanja lokacija iz baze.",Toast.LENGTH_SHORT).show();
                            finishAndRemoveTask();
                        }else{
                            try {
                                JSONArray jsonArray=new JSONArray(response);
                                nizLokacija.nizLokacija=new Lokacija[jsonArray.length()];
                                Lokacija pomocnaLokacija;
                                int idLokacije;
                                double geografskaSirina,geografskaDuzina,dozvoljenaDistanca;
                                String nazivLokacije;
                                for(int i=0;i<jsonArray.length();i++){
                                    idLokacije=jsonArray.getJSONObject(i).getInt("id_lokacije");
                                    geografskaSirina=jsonArray.getJSONObject(i).getDouble("geografska_sirina");
                                    geografskaDuzina=jsonArray.getJSONObject(i).getDouble("geografska_duzina");
                                    dozvoljenaDistanca=jsonArray.getJSONObject(i).getDouble("dozvoljena_distanca_u_metrima");
                                    nazivLokacije=jsonArray.getJSONObject(i).getString("naziv");
                                    nizLokacija.nizLokacija[i]=new Lokacija(idLokacije,geografskaSirina,geografskaDuzina,dozvoljenaDistanca,nazivLokacije);
                                }
                            } catch (JSONException e) {
                                Toast.makeText(getApplicationContext(),"Greška prilikom dobavljanja lokacija iz baze.",Toast.LENGTH_SHORT).show();
                                finishAndRemoveTask();
                            }}
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error:getLocationForUser();",Toast.LENGTH_SHORT).show();
                finishAndRemoveTask();
            }
        }){
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_zaposlenog", String.valueOf(User.id_user));
                return params;
            };
        };
        requestQueue.add(stringRequest);
    }

}