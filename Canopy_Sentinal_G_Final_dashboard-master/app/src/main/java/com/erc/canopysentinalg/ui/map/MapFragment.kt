package com.erc.canopysentinalg.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.erc.canopysentinalg.R
import com.erc.canopysentinalg.data.model.DeforestationAlert
import com.erc.canopysentinalg.ui.viewmodel.ForestViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {
    private val viewModel: ForestViewModel by viewModels({ requireActivity() })
    private var mMap: GoogleMap? = null
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
        
        observeViewModel()
    }
    
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
        mMap?.uiSettings?.isZoomControlsEnabled = true
        mMap?.uiSettings?.isCompassEnabled = true
        
        // Center on Amazon rainforest
        val amazon = LatLng(-3.4653, -62.2159)
        mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(amazon, 5f))
        
        updateMapMarkers()
    }
    
    private fun observeViewModel() {
        viewModel.alerts.observe(viewLifecycleOwner) { alerts ->
            updateMapMarkers()
        }
        
        viewModel.selectedCountry.observe(viewLifecycleOwner) { country ->
            country?.let {
                val location = LatLng(it.latitude, it.longitude)
                mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 6f))
            }
        }
    }
    
    private fun updateMapMarkers() {
        mMap?.clear()
        val alerts = viewModel.alerts.value ?: return
        
        alerts.forEach { alert ->
            val position = LatLng(alert.latitude, alert.longitude)
            mMap?.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(alert.region)
                    .snippet("Deforested: ${String.format("%.1f", alert.area)} ha")
            )
        }
    }
}
