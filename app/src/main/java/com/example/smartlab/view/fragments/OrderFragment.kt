package com.example.smartlab.view.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.smartlab.R
import com.example.smartlab.databinding.AddressBottomSheetBinding
import com.example.smartlab.databinding.FragmentOrderBinding
import com.example.smartlab.viewmodel.OrderViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

class OrderFragment : Fragment() {


    private lateinit var binding: FragmentOrderBinding
    private val calendar = Calendar.getInstance()

    private val viewModel: OrderViewModel by viewModels()

    private var currentLocation: Location? = null
    private lateinit var locationManager: LocationManager
    private var hasGps by Delegates.notNull<Boolean>()

    private val gpsLocationListener: LocationListener =
        LocationListener {
            viewModel.currentLocation.value = it
            Log.d(TAG, "showSelectAddressBottomSheetDialog: ${viewModel.currentLocation.value}")
        }

    private var selectAddressDialogBinding: AddressBottomSheetBinding? = null

    private val locationPermissionRequestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}
    private val TAG = this::class.java.simpleName
    val dateSetListener: DatePickerDialog.OnDateSetListener by lazy {
        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.YEAR, year)
            updateDateTime()
            TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    val myHour = if (hourOfDay >= 10) hourOfDay else "0$hourOfDay"
                    val myMinute = if (minute >= 10) minute else "0$minute"
                    binding.tvDate.text = " ${binding.tvDate.text} $myHour:$myMinute"
                },
                calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View? {
        locationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        binding = FragmentOrderBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: $hasGps")
        if (hasGps) {
            if (isLocationPermissionGranted()) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000,
                    0f,
                    gpsLocationListener
                )
            }
        } else {
            Toast.makeText(
                requireContext(),
                "Включите gps на смартфоне, а затем перезапустите приложение",
                Toast.LENGTH_LONG
            ).show()
        }
        if (!isLocationPermissionGranted()) {
            locationPermissionRequestLauncher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
        Locale.setDefault(Locale("ru"))
        setListeners()
    }

    private fun setListeners() {
        binding.ivBack.setOnClickListener { findNavController().popBackStack() }
        binding.etAddress.setOnClickListener {
            showSelectAddressBottomSheetDialog()
        }
    }

    private fun updateDateTime() {
        var prefix = ""
        val myFormat =
            if (calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
                prefix = if (calendar.get(Calendar.DAY_OF_MONTH) == Calendar.getInstance()
                        .get(Calendar.DAY_OF_MONTH)
                ) {
                    "Сегодня, "
                } else if (calendar.get(Calendar.DAY_OF_MONTH) == Calendar.getInstance()
                        .get(Calendar.DAY_OF_MONTH) + 1
                ) {
                    "Завтра, "
                } else {
                    ""
                }
                "dd MMMM"
            } else {
                "dd MMMM yyyy"
            }
        val sdf = SimpleDateFormat(myFormat, Locale("ru"))
        binding.tvDate.text = "$prefix${sdf.format(calendar.time)}"
    }

    private fun showSelectAddressBottomSheetDialog() {
        val selectAddressDialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheet)
        selectAddressDialogBinding = AddressBottomSheetBinding.inflate(layoutInflater)
        selectAddressDialog.setContentView(selectAddressDialogBinding!!.root)
        selectAddressDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        selectAddressDialog.show()
    }

    private fun isLocationPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}