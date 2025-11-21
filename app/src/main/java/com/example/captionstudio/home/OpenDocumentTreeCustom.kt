package com.example.captionstudio.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts

class OpenDocumentTreeCustom : ActivityResultContracts.OpenDocumentTree() {

    override fun createIntent(context: Context, input: Uri?): Intent {
        val intent = super.createIntent(context, input)

        return intent
    }
}