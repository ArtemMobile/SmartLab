package com.example.smartlab.view.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.iterator
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.smartlab.R
import com.example.smartlab.databinding.FragmentCreatePatientCardBinding
import com.example.smartlab.databinding.FragmentProfileBinding
import com.example.smartlab.model.api.callModels.ProfileRequest
import com.example.smartlab.model.api.responseModels.ProfileResponse
import com.example.smartlab.viewmodel.ProfileViewModel
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*

class ProfileFragment : Fragment() {

    private var editProfileBinding: FragmentProfileBinding? = null
    private var createProfileBinding: FragmentCreatePatientCardBinding? = null
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var uri: Uri

    private val takePhotoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            try {
                if (res.resultCode == RESULT_OK)
                    savePhoto(uri)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "can't", Toast.LENGTH_SHORT).show()
            }
        }
    private val askForCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted)
                Toast.makeText(requireContext(),
                    "Allow camera access to take photo",
                    Toast.LENGTH_SHORT).show()
            else
                capturePhoto()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        viewModel.getPatientCard()
        return if (viewModel.patientCard.value?.firstname != null) {
            editProfileBinding = FragmentProfileBinding.inflate(inflater, container, false)
            viewModel.isEditMode = true
            editProfileBinding!!.root
        } else {
            createProfileBinding =
                FragmentCreatePatientCardBinding.inflate(inflater, container, false)
                    .apply { tvSkip.visibility = View.GONE }
            createProfileBinding!!.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getPatientCard()
        setEditors()
        setUpSpinner()
        setListeners()
        setObservers()
    }

    private fun setObservers() {
        viewModel.patientCard.observe(viewLifecycleOwner) {
            it?.let { patientCard: ProfileResponse ->
                setPatientCardData(patientCard)
                //setEditors()
            }
        }
    }

    private fun setListeners() {
        if (editProfileBinding != null) {
            with(editProfileBinding!!) {
                btnSavePatientCard.setOnClickListener {
                    val profileCall = ProfileRequest(etDateOfBirth.text.toString(),
                        etName.text.toString(),
                        "",
                        etSurname.text.toString(),
                        etMiddleName.text.toString(),
                        etGender.text.toString())
                    viewModel.updateProfile(profileCall)
                }
                etDateOfBirth.setOnClickListener {
                    setUpDatePicker(it as TextView)
                }
                avatarCard.setOnClickListener {
                    if (cameraPermissionGranted())
                        capturePhoto()
                    else
                        askForCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        } else {
            with(createProfileBinding!!) {

                btnCreateCard.setOnClickListener {
                    val profileCall = ProfileRequest(etBirth.text.toString(),
                        etName.text.toString(),
                        "",
                        etLastname.text.toString(),
                        etPatronymic.text.toString(),
                        etGender.text.toString())
                    viewModel.createProfile(profileCall)
                }
                etBirth.setOnClickListener {
                    setUpDatePicker(it as TextView)
                }
            }
        }
    }

    private fun setUpDatePicker(view: TextView) {
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
        }, year, month, day)

        datePicker.apply {
            setTitle("Выберите дату рождения")
            show()
        }
    }

    private fun setUpSpinner() {
        if (editProfileBinding != null) {
            with(editProfileBinding!!) {
                this.etGender.setOnClickListener {
                    val menu = PopupMenu(requireContext(), etGender)
                    menu.inflate(R.menu.dropdown_menu)
                    menu.show()
                    menu.setOnMenuItemClickListener { item ->
                        this.etGender.text = item.title.toString()
                        true
                    }
                }
            }
        } else {
            with(createProfileBinding!!) {
                this.etGender.setOnClickListener{
                    val menu = PopupMenu(requireContext(), etGender)
                    menu.inflate(R.menu.dropdown_menu)
                    menu.show()
                    menu.setOnMenuItemClickListener { item ->
                        this.etGender.text = item.title.toString()
                        true
                    }
                }

            }
        }
    }

    private fun setPatientCardData(patientCard: ProfileResponse) {
        val binding = if (viewModel.isEditMode) editProfileBinding else createProfileBinding
        if (binding is FragmentProfileBinding) {
            with(editProfileBinding!!) {
                viewModel.getImageName()
                if (viewModel.imageFileName.value != "") {
                    loadPhoto()

                }
                etName.setText(patientCard.firstname)
                etMiddleName.setText(patientCard.middlename)
                etSurname.setText(patientCard.lastname)
                etDateOfBirth.text = patientCard.bith
                etGender.text = patientCard.pol
            }
        }
    }

    private fun setEditors() {
        if (editProfileBinding != null) {
            with(editProfileBinding!!) {
                applyEditButton()
                mainContainer.iterator().forEach { view ->
                    etDateOfBirth.setBackgroundResource(R.drawable.single_digit_editor_filled)
                    if (view is EditText) {
                        view.setBackgroundResource(R.drawable.single_digit_editor_filled)
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
        } else {
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

    private fun applyEditButton() {
        with(editProfileBinding!!) {
            val fieldsNoEmpty = etName.text.toString().isNotBlank() &&
                    etSurname.text.toString().isNotBlank() && etGender.text.toString()
                .isNotBlank() && etMiddleName.text.toString().isNotBlank()
            if (fieldsNoEmpty) {
                btnSavePatientCard.isEnabled = true
                btnSavePatientCard.setBackgroundColor(resources.getColor(com.example.smartlab.R.color.blue_button,
                    null))
            } else {
                btnSavePatientCard.isEnabled = false
                btnSavePatientCard.setBackgroundColor(resources.getColor(com.example.smartlab.R.color.inactive_button,
                    null))
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
                btnCreateCard.setBackgroundColor(resources.getColor(com.example.smartlab.R.color.blue_button,
                    null))
            } else {
                btnCreateCard.isEnabled = false
                btnCreateCard.setBackgroundColor(resources.getColor(com.example.smartlab.R.color.inactive_button,
                    null))
            }
        }
    }

    private fun savePhoto(uri: Uri) {
        val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
        val dir = requireContext().getDir("my_images", AppCompatActivity.MODE_PRIVATE)
        val file = File(dir, "image_${LocalTime.now().nano}.jpg")
        viewModel.saveImageToPrefs(file.name)
        val fo = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fo)
        fo.flush()
        fo.close()
    }

    private fun capturePhoto() {
        uri = requireContext().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            ContentValues())!!
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        takePhotoLauncher.launch(intent)
    }

    private fun cameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(),
            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun loadPhoto() {
        val dir = requireContext().getDir("my_images", AppCompatActivity.MODE_PRIVATE)
        viewModel.imageFileName.observe(viewLifecycleOwner) {
            val file = File(dir, it)
            if (file.exists()) {
                FileInputStream(file).use {
                    String(file.readBytes())
                    Glide.with(requireContext())
                        .load(file.toUri())
                        .into(editProfileBinding!!.ivAvatar)
                }
            }
        }
    }
}