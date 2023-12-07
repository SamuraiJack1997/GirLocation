package gir.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,OnMapReadyCallback {

    TextView ime,prezime,provera;
    Button prijava;
    Button refresh;
    private Spinner spinner;
    private GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!Permissions.hasPermissions(this, Permissions.PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, Permissions.PERMISSIONS, Permissions.PERMISSION_ALL);
        }

        //iskljucivanje night mod-a
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setLogo(R.drawable.gir_logo);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        //Dodeljivanje id-jeva
        spinner=findViewById(R.id.spinner);
        ime=findViewById(R.id.ime);
        prezime=findViewById(R.id.prezime);
        provera=findViewById(R.id.provera);
        prijava=findViewById(R.id.prijava);
        refresh=findViewById(R.id.refresh);
        ime.setText(Html.fromHtml("<font color='#000000'><b>Ime:</b><br></font>"+User.ime));
        prezime.setText(Html.fromHtml("<font color='#000000'><b>Prezime:</b><br></font>"+User.prezime));
        provera.setText(Html.fromHtml("<font color='#000000'><b>Status:</b><br>"));

        String pomocniNizLokacija[]=new String[nizLokacija.nizLokacija.length];
        for(int i=0;i<nizLokacija.nizLokacija.length;i++){
            if(!nizLokacija.nizLokacija[i].equals(null)){
                pomocniNizLokacija[i]= nizLokacija.nizLokacija[i].imeLokacije;
            }
        }

        //Dodeljivanje spinner-a
        ArrayAdapter<CharSequence> adapter= new ArrayAdapter(this,
                android.R.layout.simple_spinner_item,pomocniNizLokacija);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        //Funkcija za proveru statusa lokacije koja poziva getLocation na klik dugmeta
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation(izabranaLokacija.izabranaLokacija);
            }
        });
    }

    ///Funkcija za prijavu na lokaciji
    private void prijaviSe(){

        prijava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog=new AlertDialog.Builder(MainActivity.this);
                alertDialog.setMessage(Html.fromHtml("Da li želite da se prijavite na lokaciju <b>"+izabranaLokacija.imeLokacije+"</b>?")).setCancelable(false).setPositiveButton(Html.fromHtml("<font color='#000000'>Da</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        User.prijavljen=true;
                        writeToFile("true",MainActivity.this);
                        //EVIDENCIJA PRIJAVE NA SISTEM//

                        insertEvidencijaPrijava();

                        startActivity(new Intent(MainActivity.this,ActivityPrijava.class));
                    }
                }).setNegativeButton(Html.fromHtml("<font color='#000000'>Ne</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                alertDialog.setTitle("Prijava na sistem");
                alertDialog.show();
            }
        });
    }

    //Funkcija za selektovanje item-a u dropdown listi
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selectedItem=(String)adapterView.getItemAtPosition(i);


        for(i=0;i<nizLokacija.nizLokacija.length;i++){
            if (!nizLokacija.nizLokacija[i].equals(null)) {
                if(selectedItem.equals(nizLokacija.nizLokacija[i].imeLokacije)){
                    izabranaLokacija.idLokacije=nizLokacija.nizLokacija[i].idLokacije;
                    izabranaLokacija.imeLokacije=nizLokacija.nizLokacija[i].imeLokacije;
                    izabranaLokacija.geografskaSirina=nizLokacija.nizLokacija[i].geografskaSirina;
                    izabranaLokacija.geografskaDuzina=nizLokacija.nizLokacija[i].geografskaDuzina;
                    izabranaLokacija.dozvoljenaDistanca=nizLokacija.nizLokacija[i].dozvoljenaDistanca;
                    izabranaLokacija.izabranaLokacija=new Lokacija(nizLokacija.nizLokacija[i].idLokacije,nizLokacija.nizLokacija[i].geografskaSirina,nizLokacija.nizLokacija[i].geografskaDuzina,nizLokacija.nizLokacija[i].dozvoljenaDistanca,nizLokacija.nizLokacija[i].imeLokacije);
                    //Pozivanje funkcije za lokaciju//
                    //TODO getLocation prebacivanje
                    getLocation(nizLokacija.nizLokacija[i]);
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    //TODO getLocation prebacivanje
    @SuppressLint("MissingPermission")
    public void getLocation(Lokacija trazenaLokacija) {
        //Provajder lokacije
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        //Provera permisije za lokaciju
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            //Uzimanje trenutne lokacije uredjaja
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {

                    //Inicijalizacija lokacije
                    Location trenutnaLokacija = task.getResult();

                    if (trenutnaLokacija != null) {
                        //Inicijalizacija Geokodera
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        try {
                            //Inicijalizacija liste adresa
                            List<Address> adrese = geocoder.getFromLocation(
                                    trenutnaLokacija.getLatitude(), trenutnaLokacija.getLongitude(), 1);

                            //Uzimanje flag-a o informaciji da li sam u opsegu lokacije i uzimanje udaljenosti koji se izracunava pomocu Haversajnove formule
                            gData.flagLokacija = trazenaLokacija.daLiSamUBliziniLokacije(trenutnaLokacija.getLatitude(), trenutnaLokacija.getLongitude());
                            gData.udaljenost = trazenaLokacija.distancaDoUseraUMetrima(trenutnaLokacija.getLatitude(), trenutnaLokacija.getLongitude());

                            //Uzimanje trenutne lokacije
                            gData.trenutnaGeografskaDuzina = trenutnaLokacija.getLongitude();
                            gData.trenutnaGeografskaSirina = trenutnaLokacija.getLatitude();
                            gData.StringTrenutnaGeografskaDuzina = trenutnaLokacija.convert(trenutnaLokacija.getLongitude(), trenutnaLokacija.FORMAT_DEGREES);
                            gData.StringTrenutnaGeografskaSirina = trenutnaLokacija.convert(trenutnaLokacija.getLatitude(), trenutnaLokacija.FORMAT_DEGREES);


                            ////////////Inicijalizacija mape
                            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                    .findFragmentById(R.id.map);
                            mapFragment.getMapAsync(MainActivity.this);

                            //Funkcija za proveru statusa lokacije koja poziva getLocation na klik dugmeta
                            refresh.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //TODO getLocation prebacivanje
                                    getLocation(trazenaLokacija);
                                }
                            });
                            //Logika koja menja mogucnost prijave na lokaciju ako je moguce
                            if (gData.flagLokacija) {
                                prijava.setClickable(true);
                                prijava.setBackgroundColor(Color.rgb(69, 114, 62));
                                prijava.setTextColor(Color.BLACK);
                                provera.setText(Html.fromHtml("<font color='#000000'><b>Status:</b><br></font><font color='#000000'>Prijava na sistem je <b>moguća.</b><br>Udaljeni ste <b>" + gData.udaljenost + "m</b> od lokacije za prijavu.<br></font>"));
                                prijaviSe();
                            } else {
                                prijava.setClickable(false);
                                prijava.setBackgroundColor(Color.LTGRAY);
                                prijava.setTextColor(Color.BLACK);
                                provera.setText(Html.fromHtml("<font color='#000000'><b>Status:</b><br></font><font color='#000000'>Prijava na sistem <b>nije moguća</b>.Udaljenost od lokacije za prijavu: <b>" + gData.udaljenost + "m.</b><br></font>"));
                            }
                        } catch (IOException e) {
                            Log.d("Log", "Greska:GEOCODER ERROR!");
                        }
                    }
                }
            });
        } else {
            //Ako permisija nije dodata
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }


    //Funkcija koja podesava mapu
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        mMap.addCircle(new CircleOptions().center(new LatLng(izabranaLokacija.geografskaSirina,izabranaLokacija.geografskaDuzina)).radius(200).strokeColor(Color.rgb(69, 114, 62)).fillColor(Color.argb(127,69, 114, 62)));
        LatLng lokacija = new LatLng(izabranaLokacija.geografskaSirina, izabranaLokacija.geografskaDuzina);
        mMap.addMarker(new MarkerOptions()
                .position(lokacija)
                .title(izabranaLokacija.imeLokacije));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lokacija));

    }

    private void writeToFile(String statusPrijave, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(String.valueOf(statusPrijave));
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public void insertEvidencijaPrijava(){
        String URL="https://test.gir.rs/insertEvidencija.php";
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("false")){
                            Toast.makeText(getApplicationContext(),"Vaša prijava na sistem nije uspešno zabeležena,pokušajte opet.",Toast.LENGTH_SHORT).show();
                            finishAndRemoveTask();
                        }else{
                            Toast.makeText(getApplicationContext(),"Vaša prijava na sistem je uspešno zabeležena.",Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error:getLocationForUser();",Toast.LENGTH_SHORT).show();
                finishAndRemoveTask();
            }
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_zaposlenog", String.valueOf(User.id_user));
                params.put("id_lokacije",String.valueOf(izabranaLokacija.idLokacije));
                params.put("IN_OUT", "'IN'");
                params.put("in_out_geografska_sirina",gData.StringTrenutnaGeografskaSirina);
                params.put("in_out_geografska_duzina",gData.StringTrenutnaGeografskaDuzina);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

}