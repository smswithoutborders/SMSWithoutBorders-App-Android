package com.example.sw0b_001.Models.GatewayClients

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.sw0b_001.Database.Datastore
import com.example.sw0b_001.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GatewayClientAddModalFragment :
    BottomSheetDialogFragment(R.layout.fragment_gateway_client_add_modal) {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                selectContact()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.read_contacts_permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private val selectContactLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { contactUri ->
                    getPhoneNumberFromContact(contactUri)
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet = view.findViewById<View>(R.id.gateway_client_add_modal)

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isDraggable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        view.findViewById<MaterialButton>(R.id.gateway_client_add_custom_btn)
            .setOnClickListener {
                addGatewayClients(view)
            }

        val textInputLayout =
            view.findViewById<TextInputLayout>(R.id.gateway_client_add_contact_layout)
        textInputLayout.setEndIconOnClickListener { checkAndRequestContactsPermission() }
    }

    private fun addGatewayClients(view: View) {
        val contactTextView = view.findViewById<TextInputEditText>(R.id.gateway_client_add_contact)
        val aliasTextView =
            view.findViewById<TextInputEditText>(R.id.gateway_client_add_contact_alias)

        if (contactTextView.text.isNullOrEmpty()) {
            contactTextView.error =
                getString(R.string.gateway_client_settings_add_custom_empty_error)
            return
        }

        val gatewayClient = GatewayClient()
        gatewayClient.mSISDN = contactTextView.text.toString()
        gatewayClient.alias = aliasTextView.text?.toString()
        gatewayClient.type = GatewayClient.TYPE_CUSTOM

        CoroutineScope(Dispatchers.Default).launch {
            Datastore.getDatastore(context).gatewayClientsDao().insert(gatewayClient)
            dismiss()
        }
    }

    private fun checkAndRequestContactsPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                selectContact()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.request_contact_permission),
                    Toast.LENGTH_SHORT
                ).show()
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
            else -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.grant_contact_permission_from_settings),
                    Toast.LENGTH_SHORT
                ).show()
                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }
    }

    private fun selectContact() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        }
        selectContactLauncher.launch(intent)
    }

    private fun getPhoneNumberFromContact(contactUri: Uri) {
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val cursor =
            requireActivity().contentResolver.query(contactUri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val number = it.getString(numberIndex).replace("\\s".toRegex(), "")
                view?.findViewById<TextInputEditText>(R.id.gateway_client_add_contact)
                    ?.setText(number)
            }
        }
    }
}