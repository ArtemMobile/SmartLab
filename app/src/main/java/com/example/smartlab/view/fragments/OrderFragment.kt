package com.example.smartlab.view.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.smartlab.R
import com.example.smartlab.databinding.FragmentOrderBinding
import java.text.SimpleDateFormat
import java.util.*

class OrderFragment : Fragment() {

    private val binding: FragmentOrderBinding by lazy{
        FragmentOrderBinding.inflate(layoutInflater)
    }
    private val calendar = Calendar.getInstance()
    val dateSetter: DatePickerDialog.OnDateSetListener by lazy {
        DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.YEAR, year)
            updateDateTime()
            TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    val mHour = if (hourOfDay >= 10) hourOfDay else "0$hourOfDay"
                    val mMinute = if (minute >= 10) minute else "0$minute"
                    binding.tvDate.text = "${binding.tvDate.text} $mHour:$mMinute"
                },
                calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyClicks()
    }

    private fun applyClicks(){
        with(binding){
            ivBack.setOnClickListener{
                findNavController().popBackStack()
            }
            tvDate.setOnClickListener{
                DatePickerDialog(
                    requireContext(),
                    R.style.DatePickerTheme,
                    dateSetter,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).apply {
                    datePicker.minDate = System.currentTimeMillis()
                }.show()
            }
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
}