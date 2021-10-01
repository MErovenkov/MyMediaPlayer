package com.example.mymediaplayer.ui.player.selector

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckedTextView
import androidx.core.view.allViews
import androidx.core.view.forEach
import com.example.mymediaplayer.R

@SuppressLint("InflateParams")
abstract class AlertDialogBuilder(context: Context): AlertDialog.Builder(context), DialogBuilder {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private var alertDialog: AlertDialog? = null

    protected lateinit var checkedTextViewGroup: ViewGroup

    init {
        this.setView(layoutInflater.inflate(R.layout.exo_selectior_dialog, null).apply {
            (this as ViewGroup).forEach {
                when (it.id) {
                    R.id.selector_view_group -> checkedTextViewGroup = it as ViewGroup
                }
            }
        })
    }

    protected fun createCheckedTextView(trackType: Int, viewTag: Int, text: String,
                                        selectedTrackMap: MutableMap<Int, Int>
    ): CheckedTextView {

        return (layoutInflater.inflate(R.layout.selector_item, checkedTextViewGroup,
                          false) as CheckedTextView).apply {
            this.text = text
            this.tag = viewTag
            isFocusable = true

            setOnClickListener {
                val viewIndex = it.tag as Int

                if (selectedTrackMap[trackType] != viewIndex) {
                    getCheckedTextViewByTag(selectedTrackMap[trackType]!!).isChecked = false
                    selectedTrackMap[trackType] = viewIndex
                }

                getCheckedTextViewByTag(viewIndex).isChecked = true
                alertDialog?.dismiss()
            }
        }
    }

    protected fun createDialog(titleDialog: String): AlertDialog {
        return this.setTitle(titleDialog).create().also {
            alertDialog = it
        }
    }

    protected fun getCheckedTextViewByTag(tag: Int): CheckedTextView {
        return (checkedTextViewGroup.allViews.first { it.tag == tag } as CheckedTextView)
    }
}