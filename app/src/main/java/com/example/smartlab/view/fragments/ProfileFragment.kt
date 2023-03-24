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
import com.example.smartlab.R
import com.example.smartlab.databinding.FragmentCreatePatientCardBinding
import com.example.smartlab.databinding.FragmentProfileBinding
import com.example.smartlab.model.api.callModels.ProfileRequest
import com.example.smartlab.model.api.responseModels.ProfileResponse
import com.example.smartlab.viewmodel.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {

    private var editProfileBinding: FragmentProfileBinding? = null
    private var createProfileBinding: FragmentCreatePatientCardBinding? = null
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        viewModel.getPatientCard()
        viewModel.patientCard.value?.let {
            editProfileBinding = FragmentProfileBinding.inflate(inflater, container, false)
            viewModel.isEditMode = true
            return editProfileBinding!!.root
        }
        createProfileBinding = FragmentCreatePatientCardBinding.inflate(inflater, container, false)
            .apply { tvSkip.visibility = View.GONE }
        return createProfileBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setEditors()
        setObservers()
        viewModel.getPatientCard()
        setListeners()

    }

    private fun setObservers() {
        viewModel.patientCard.observe(viewLifecycleOwner) {
            it?.let { patientCard: ProfileResponse ->
                setPatientCardData(patientCard)
            }
        }
    }

    private fun setListeners(){
        if (editProfileBinding != null){
            with(editProfileBinding!!){
                etGender.setOnClickListener{ setUpSpinner(it as TextView) }
                btnSavePatientCard.setOnClickListener{
                    val profileCall = ProfileRequest(etDateOfBirth.text.toString(),
                        etName.text.toString(),
                        "",
                        etSurname.text.toString(),
                        etMiddleName.text.toString(),
                        etGender.text.toString())
                    viewModel.updateProfile(profileCall)
                }
                etDateOfBirth.setOnClickListener{
                    setUpDatePicker(it as TextView)
                }
            }

        } else{
            with(createProfileBinding!!){
                etGender.setOnClickListener{ setUpSpinner(it as TextView) }
                btnCreateCard.setOnClickListener {
                    val profileCall = ProfileRequest(etBirth.text.toString(),
                        etName.text.toString(),
                        "",
                        etLastname.text.toString(),
                        etPatronymic.text.toString(),
                        etGender.text.toString())
                    viewModel.createProfile(profileCall)
                }
                etBirth.setOnClickListener{
                    setUpDatePicker(it as TextView)
                }
            }
        }
    }

    private fun setUpDatePicker(view: TextView){
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
                (view).text = formatter.format(calendar.time)
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

    private fun setUpSpinner(view: TextView) {
        view.setOnClickListener {
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

    private fun setPatientCardData(patientCard: ProfileResponse) {
        val binding = if (viewModel.isEditMode) editProfileBinding else createProfileBinding
        if (binding is FragmentProfileBinding) {
            with(editProfileBinding!!) {
                etName.setText(patientCard.firstname)
                etMiddleName.setText(patientCard.middlename)
                etSurname.setText(patientCard.lastname)
                etDateOfBirth.setText(patientCard.bith)
                etGender.text = patientCard.pol
            }
        }
    }

    private fun setEditors(){
        if(editProfileBinding != null){
            with(editProfileBinding!!) {
                applyEditButton()
                mainContainer.iterator().forEach { view ->
                    if (view is EditText) {
                        view.doOnTextChanged { text, _, _, _ ->
                            if (text!!.isNotBlank()) {
                                view.setBackgroundResource(R.drawable.single_digit_editor_filled)
                            } else {
                                view.setBackgroundResource(R.drawable.single_digit_editor)
                            }
                            applyEditButton()
                        }
                    }
                }
            }
        } else{
            with(createProfileBinding!!) {
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
    }

    private fun applyEditButton(){
        with(editProfileBinding!!) {
            val fieldsNoEmpty = etName.text.toString().isNotBlank() &&
                    etSurname.text.toString().isNotBlank() && etGender.text.toString()
                .isNotBlank() && etMiddleName.text.toString().isNotBlank()
            if (fieldsNoEmpty) {
                btnSavePatientCard.isEnabled = true
                btnSavePatientCard.setBackgroundColor(resources.getColor(com.example.smartlab.R.color.blue_button, null))
            } else {
                btnSavePatientCard.isEnabled = false
                btnSavePatientCard.setBackgroundColor(resources.getColor(com.example.smartlab.R.color.inactive_button, null))
            }
        }
    }

    private fun applyCreateButton() {
        with(createProfileBinding!!) {
            val fieldsNoEmpty = etName.text.toString().isNotBlank() && etPatronymic.text.toString()
                .isNotBlank() && etLastname.text.toString().isNotBlank() && etGender.text.toString()
                .isNotBlank() && etBirth.text.toString().isNotBlank()
            if (fieldsNoEmpty) {
                btnCreateCard.isEnabled = true
                btnCreateCard.setBackgroundColor(resources.getColor(com.example.smartlab.R.color.blue_button, null))
            } else {
                btnCreateCard.isEnabled = false
                btnCreateCard.setBackgroundColor(resources.getColor(com.example.smartlab.R.color.inactive_button, null))
            }
        }
    }
}