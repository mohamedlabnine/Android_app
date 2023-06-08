package com.example.product

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import com.example.product.R
import android.graphics.drawable.ColorDrawable

class LoadingDialog internal constructor(var activity: Activity) {
    var dialog: Dialog? = null
    fun startAlertDialog() {
        dialog = Dialog(activity)
        dialog!!.setContentView(R.layout.custom_dialog)
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setCancelable(false)
        dialog!!.create()
        dialog!!.show()
    }

    fun dismissDialog() {
        dialog!!.dismiss()
    }
}