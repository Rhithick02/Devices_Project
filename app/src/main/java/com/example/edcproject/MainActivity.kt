package com.example.edcproject

import android.Manifest
import android.app.Application
import android.content.ContentProviderClient
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : FragmentActivity(), OnMapReadyCallback {

    lateinit var currentLocation : Location
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    val REQUEST_CODE: Int = 101



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLastLocation()
    }

    private fun fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            , REQUEST_CODE)
            return
        }
        var task : Task<Location> = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener(OnSuccessListener<Location>(){
            if(it != null){
                currentLocation = it;
                Toast.makeText(applicationContext, "$currentLocation.latitude $currentLocation.longitude", Toast.LENGTH_SHORT).show()
                var supportMapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
                supportMapFragment.getMapAsync(this)
            }
        })

    }

    override fun onMapReady(gmap: GoogleMap?) {
        var latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        var markerOptions = MarkerOptions().position(latLng).title("I am here")
        if (gmap != null) {
            gmap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            gmap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5F))
            gmap.addMarker(markerOptions)

        }



    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode){
            REQUEST_CODE -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    fetchLastLocation()
            }
        }
    }


}