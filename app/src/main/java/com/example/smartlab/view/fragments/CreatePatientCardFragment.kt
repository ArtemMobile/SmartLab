package com.example.smartlab.view.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.iterator
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.smartlab.R
import com.example.smartlab.databinding.FragmentCreatePatientCardBinding
import com.example.smartlab.model.api.callModels.ProfileCall
import com.example.smartlab.viewmodel.CreatePatientCardViewModel
import java.text.SimpleDateFormat
import java.util.*

class CreatePatientCardFragment : Fragment() {

    private val binding: FragmentCreatePatientCardBinding by lazy {
        FragmentCreatePatientCardBinding.inflate(layoutInflater)
    }

    private val viewModel: CreatePatientCardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applyEditors()
        setUpSpinner()
        applyClicks()
        setUpObservers()
    }

    private fun applyClicks() {
        with(binding) {
            tvSkip.setOnClickListener {
                viewModel.setCreatePatientCardPassed()
                findNavController().navigate(R.id.action_patientCardFragment_to_mainActivity)
            }
            btnNext.setOnClickListener {
                val profileCall = ProfileCall(etBirth.text.toString(),
                    etName.text.toString(),
                    "",
                    etLastname.text.toString(),
                    etPatronymic.text.toString(),
                    etGender.text.toString())
                viewModel.createProfile(profileCall)
            }
            etBirth.setOnClickListener {
                val calendar = Calendar.getInstance()
                val day = calendar.get(Calendar.DAY_OF_MONTH)
                val month = calendar.get(Calendar.MONTH)
                val year = calendar.get(Calendar.YEAR)
                val datePicker = DatePickerDialog(requireContext(), { _, sYear, sMonth, sDay ->
                    calendar.apply {
                        set(Calendar.YEAR, sYear)
                        set(Calendar.MONTH, sMonth)
                        set(Calendar.DAY_OF_MONTH, sDay)
                    }
                    val formatter = SimpleDateFormat("dd MMMM yyyy", Locale("ru"))
                    if (calendar.time <= Calendar.getInstance().time) {
                        (it as TextView).text = formatter.format(calendar.time)
                    } else {
                        Toast.makeText(requireContext(),
                            "Выберите корректуню дату",
                            Toast.LENGTH_SHORT).show()
                    }
                    applyCreateButton()
                }, year, month, day)

                datePicker.apply {
                    setTitle("Выберите дату рождения")
                    show()
                }
            }
        }
    }

    private fun setUpObservers() {
        viewModel.profile.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), "${it.lastname}, ${it.created_at}", Toast.LENGTH_SHORT)
                .show()
            viewModel.setCreatePatientCardPassed()
            findNavController().navigate(R.id.action_patientCardFragment_to_mainActivity)
        }
    }

    private fun setUpSpinner() {
        binding.etGender.setOnClickListener {
            val menu = PopupMenu(requireContext(), it)
            menu.inflate(R.menu.dropdown_menu)
            menu.show()

            menu.setOnMenuItemClickListener { item ->
                (it as TextView).text = item.title.toString()
                applyCreateButton()
                true
            }
        }
    }

    private fun applyEditors() {
        with(binding) {
            applyCreateButton()
            editorLayout.iterator().forEach { view ->
                if (view is EditText) {
                    view.doOnTextChanged { text, _, _, _ ->
                        if (text!!.isNotBlank()) {
                            applyCreateButton()
                            view.setBackgroundResource(R.drawable.single_digit_editor_filled)
                        } else {
                            applyCreateButton()
                            view.setBackgroundResource(R.drawable.single_digit_editor)
                        }
                    }
                }
            }
        }
    }

    private fun applyCreateButton() {
        with(binding) {
            val fieldsNoEmpty = etName.text.toString().isNotBlank() && etPatronymic.text.toString()
                .isNotBlank() && etLastname.text.toString().isNotBlank() && etGender.text.toString()
                .isNotBlank() && etBirth.text.toString().isNotBlank()
            if (fieldsNoEmpty) {
                btnNext.isEnabled = true
                btnNext.setBackgroundColor(resources.getColor(R.color.blue_button, null))
            } else {
                btnNext.isEnabled = false
                btnNext.setBackgroundColor(resources.getColor(R.color.inactive_button, null))
            }
        }
    }
}