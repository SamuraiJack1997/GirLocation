package gir.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class gData {
    public static boolean allPermissionsAllowed = false;
    public static boolean flagLokacija;
    public static int udaljenost;
    public static String StringTrenutnaGeografskaDuzina;
    public static String StringTrenutnaGeografskaSirina;
    public static double trenutnaGeografskaDuzina;
    public static double trenutnaGeografskaSirina;
    public static Activity activityGlobal;
    public static Context contextGlobal;
}
