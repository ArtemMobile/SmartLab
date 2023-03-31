package com.example.smartlab.view.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.speech.RecognizerIntent
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
import com.example.smartlab.databinding.DateTimeBottomSheetBinding
import com.example.smartlab.databinding.FragmentOrderBinding
import com.example.smartlab.viewmodel.OrderViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round
import kotlin.properties.Delegates

class OrderFragment : Fragment() {

    private lateinit var binding: FragmentOrderBinding
    private val calendar = Calendar.getInstance()

    private val viewModel: OrderViewModel by viewModels()

    private lateinit var locationManager: LocationManager
    private var hasGps by Delegates.notNull<Boolean>()

    private var speechStartTime by Delegates.notNull<Long>()
    private var speechEndTime by Delegates.notNull<Long>()

    var selectTimeDialogBinding: DateTimeBottomSheetBinding? = null

    private val getAudio =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.data != null) {
                speechEndTime = System.currentTimeMillis()
                val result = it.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                binding.etComment.setText(result.toString().trim('[', ']'))
                if (speechEndTime - speechStartTime > 20000) {
                    Toast.makeText(requireContext(), "Вы превысили 20 секунд", Toast.LENGTH_LONG).show()
                }
            }
        }

    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU")
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Говорите сейчас!")
        try {
            getAudio.launch(intent)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Ошибка: " + e.message, Toast.LENGTH_LONG).show()
        }
    }

    private val gpsLocationListener =
        LocationListener { location ->
            viewModel.currentLocation.value = location
            selectAddressDialogBinding?.let {
                it.etLatitude.setText(round(location.latitude).toInt().toString())
                it.etLongitude.setText(round(location.longitude).toInt().toString())
                it.etHeight.setText(round(location.altitude).toInt().toString())
                viewModel.reverseGeocoding(lat = location.latitude, lon = location.longitude)
            }
            Log.d(
                TAG,
                "showSelectAddressBottomSheetDialog: ${viewModel.currentLocation.value}"
            )
            Log.d(
                TAG,
                "showSelectAddressBottomSheetDialog: ${viewModel.currentLocation.value?.latitude}"
            )
            Log.d(
                TAG,
                "showSelectAddressBottomSheetDialog: ${viewModel.currentLocation.value?.longitude}"
            )
        }

    private var selectAddressDialogBinding: AddressBottomSheetBinding? = null

    private val locationPermissionRequestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}
    private val TAG = this::class.java.simpleName
    private val dateSetListener: DatePickerDialog.OnDateSetListener by lazy {
        DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.YEAR, year)
            updateDateTime()
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
        setObservers()
        if (hasGps) {
            if (isLocationPermissionGranted()) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000,
                    0f,
                    gpsLocationListener
                )
            }
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

    @SuppressLint("MissingPermission")
    private fun setListeners() {
        binding.ivBack.setOnClickListener { findNavController().popBackStack() }
        binding.etAddress.setOnClickListener {
            if (hasGps) {
                if (isLocationPermissionGranted()) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1000,
                        0f,
                        gpsLocationListener
                    )
                }
            }
//            } else {
//                Toast.makeText(
//                    requireContext(),
//                    "Включите gps на смартфоне, а затем перезапустите приложение",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
            showSelectAddressBottomSheetDialog()
        }
        binding.ivMic.setOnClickListener {
            startVoiceInput()
            speechStartTime = System.currentTimeMillis()
        }
        binding.tvDate.setOnClickListener {
            showSelectTimeBottomSheetDialog()
        }
    }

    private fun setObservers() {
        viewModel.reversedGeocoding.observe(viewLifecycleOwner) { reversedGeocoging ->
            selectAddressDialogBinding?.etAddress?.setText("${reversedGeocoging.features[0].properties.address.road} ${reversedGeocoging.features[0].properties.address.house_number}")
        }
    }

    private fun updateDateTime() {
        val myFormat =
            if (calendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
                "dd MMMM"
            } else {
                "dd MMMM yyyy"
            }
        val sdf = SimpleDateFormat(myFormat, Locale("ru"))
        selectTimeDialogBinding?.let {
            it.tvDateTime.text = sdf.format(calendar.time)
        }
    }

    private fun showSelectAddressBottomSheetDialog() {
        val selectAddressDialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheet)
        selectAddressDialogBinding = AddressBottomSheetBinding.inflate(layoutInflater)
        selectAddressDialog.setContentView(selectAddressDialogBinding!!.root)
        selectAddressDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        selectAddressDialogBinding!!.btnConfirm.setOnClickListener {
            if (selectAddressDialogBinding!!.etAddress.text.isNotEmpty() && selectAddressDialogBinding!!.etFlat.text.isNotEmpty()) {
                val resultAddress =
                    "${selectAddressDialogBinding!!.etAddress.text}, кв. ${selectAddressDialogBinding!!.etFlat.text}"
                binding.etAddress.text = resultAddress
                selectAddressDialog.cancel()
            } else {
                Toast.makeText(requireContext(), "Введите адрес!", Toast.LENGTH_SHORT).show()
            }
        }
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
    private fun showSelectTimeBottomSheetDialog() {
        val selectTimeDialog = BottomSheetDialog(requireContext(), R.style.AppBottomSheet)
        selectTimeDialogBinding = DateTimeBottomSheetBinding.inflate(layoutInflater)
        selectTimeDialogBinding!!.ivClose.setOnClickListener {
            selectTimeDialog.cancel()
        }
        selectTimeDialogBinding!!.btnConfirm.setOnClickListener {
            if (selectTimeDialogBinding!!.group.checkedChipId == -1) {
                Toast.makeText(requireContext(), "Выберите время заказа!", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (selectTimeDialogBinding!!.tvDateTime.text.isBlank()) {
                Toast.makeText(requireContext(), "Выберите дату заказа!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val prefix = if (calendar.get(Calendar.DAY_OF_MONTH) == Calendar.getInstance()
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
            val checkedChip =
                selectTimeDialogBinding!!.group.findViewById<Chip>(selectTimeDialogBinding!!.group.checkedChipId)
            binding.tvDate.text =
                "$prefix ${selectTimeDialogBinding!!.tvDateTime.text} ${checkedChip.text}"
            selectTimeDialog.cancel()
        }
        selectTimeDialogBinding!!.tvDateTime.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).apply {
                datePicker.minDate = System.currentTimeMillis()
            }.show()
        }
        selectTimeDialog.setContentView(selectTimeDialogBinding!!.root)
        selectTimeDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        selectTimeDialog.show()
    }
}