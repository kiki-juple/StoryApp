package com.juple.storyapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.juple.storyapp.R
import com.juple.storyapp.databinding.ActivityMapsBinding
import com.juple.storyapp.viewmodel.MapsViewModel


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val viewModel by viewModels<MapsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Story Maps"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        showLoading(true)
        addNewStory()

        viewModel.responseCode.observe(this) {
            showLoading(false)
            if (it != 200) {
                viewModel.snackText.observe(this) { text ->
                    Snackbar.make(binding.root, text, Snackbar.LENGTH_INDEFINITE)
                        .show()
                }
            }
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getMyLocation()

        viewModel.lisStory.observe(this) {
            it.forEach { story ->

                val marker = LatLng(story.lat, story.lon)
                mMap.addMarker(
                    MarkerOptions()
                        .position(marker)
                        .title(story.name.trim())
                        .snippet(story.description.trim())
                )
            }
        }
    }

    private fun addNewStory() {
        binding.fabAdd.setOnClickListener {
            Intent(this, UploadActivity::class.java).run {
                startActivity(this)
            }
        }
    }

    private fun showLoading(state: Boolean) {
        binding.loadingPanel.visibility = if (state) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}