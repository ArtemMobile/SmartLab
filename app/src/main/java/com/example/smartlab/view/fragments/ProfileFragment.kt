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
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.smartlab.R
import com.example.smartlab.databinding.FragmentCreatePatientCardBinding
import com.example.smartlab.databinding.FragmentProfileBinding
import com.example.smartlab.model.api.callModels.ProfileRequest
import com.example.smartlab.viewmodel.ProfileViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*

class ProfileFragment : Fragment() {

    private var editProfileBinding: FragmentProfileBinding? = null
    private var createProfileBinding: FragmentCreatePatientCardBinding? = null
    private val viewModel: ProfileViewModel by viewModels()
    private lateinit var uri: Uri
    private var showPhoto = true

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
        setObservers()
        //setEditors()
        setUpSpinner()
        setListeners()
    }

    private fun setObservers() {
        viewModel.patientCard.observe(viewLifecycleOwner) {
            it?.let { patientCard: ProfileRequest ->
                setPatientCardData(patientCard)
                setEditors()
            }
        }

        viewModel.imageFileName.observe(viewLifecycleOwner) { uri ->
            editProfileBinding?.let {
                if (uri.toString().isNotBlank()) {
                    it.ivAvatar.setImageURI(uri)
                }
            }
        }
        viewModel.updatedProfile.observe(viewLifecycleOwner) { updatedProfile ->
            editProfileBinding?.let {
                updatedProfile.image?.let { image ->
                    if (image.isNotBlank()) {
                        Glide.with(requireContext()).load(image).into(it.ivAvatar)
                    }
                }
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
        }
    }

    private fun setPatientCardData(patientCard: ProfileRequest) {
        val binding = if (viewModel.isEditMode) editProfileBinding else createProfileBinding
        if (binding is FragmentProfileBinding) {
            with(editProfileBinding!!) {
                if (patientCard.image != "") {
                    loadPhoto(patientCard.image)
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
                etGender.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int, ) {

                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int, ) {

                    }

                    override fun afterTextChanged(s: Editable?) {
                        applyEditButton()
                    }
                })

                mainContainer.iterator().forEach { view ->
                    etDateOfBirth.setBackgroundResource(R.drawable.single_digit_editor_filled)
                    if (view is TextView) {
                        view.setBackgroundResource(R.drawable.single_digit_editor_filled)
                        view.addTextChangedListener(object : TextWatcher {
                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int, ) {
                            }

                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int,
                            ) {
                                if (s!!.isNotBlank()) {
                                    view.setBackgroundResource(R.drawable.single_digit_editor_filled)
                                } else {
                                    view.setBackgroundResource(R.drawable.single_digit_editor)
                                }
                                applyEditButton()
                            }

                            override fun afterTextChanged(s: Editable?) {}
                        })
                    }
                }
            }
        } else {
            with(createProfileBinding!!) {
                etGender.addTextChangedListener(object: TextWatcher{
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int, ) {

                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int, ) {

                    }

                    override fun afterTextChanged(s: Editable?) {
                        applyCreateButton()
                    }

                })
                editorLayout.iterator().forEach { view ->
                    if (view is TextView) {
                        if(view.text.isNotBlank())
                            view.setBackgroundResource(R.drawable.single_digit_editor_filled)
                        view.addTextChangedListener(object : TextWatcher {
                            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int, ) {

                            }

                            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int, ) {
                                if (s!!.isNotBlank()) {
                                    view.setBackgroundResource(R.drawable.single_digit_editor_filled)
                                } else {
                                    view.setBackgroundResource(R.drawable.single_digit_editor)
                                }
                                applyCreateButton()
                            }

                            override fun afterTextChanged(s: Editable?) {

                            }
                        })
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
                btnCreateCard.setBackgroundColor(resources.getColor(com.example.smartlab.R.color.blue_button, null))
            } else {
                btnCreateCard.isEnabled = false
                btnCreateCard.setBackgroundColor(resources.getColor(com.example.smartlab.R.color.inactive_button, null))
            }
        }
    }

    private fun savePhoto(uri: Uri) {
        if(viewModel.avatarError.value == null){
            val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
            val dir = requireContext().getDir("my_images", AppCompatActivity.MODE_PRIVATE)
            val file = File(dir, "image_${LocalTime.now().nano}.jpg")
            viewModel.saveImageToPrefs(uri)
            val fo = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fo)
            fo.flush()
            fo.close()
            editProfileBinding!!.ivAvatar.setImageBitmap(bitmap)
            val requestFile = file.asRequestBody("file".toMediaTypeOrNull())
            viewModel.updateAvatar(MultipartBody.Part.createFormData("file", file.name, requestFile))
            showPhoto = false
        }

        viewModel.avatarError.observe(viewLifecycleOwner){
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
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

    private fun loadPhoto(photo: String) {
        viewModel.getImageName()
        if (showPhoto) {
        Glide.with(requireContext())
            .load(photo)
            .placeholder(R.drawable.photo_placeholder)
            .into(editProfileBinding!!.ivAvatar)
            showPhoto = false
        } else {
            viewModel.imageFileName.observe(viewLifecycleOwner) {
                editProfileBinding?.let {
                    if (uri.toString().isNotBlank()) {
                        it.ivAvatar.setImageURI(uri)
                    }
                }
            }
        }
    }
}