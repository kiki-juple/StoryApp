package com.juple.storyapp.ui

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.KeyEvent
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.juple.storyapp.R

fun EditText.filterMinLength(min: Int) {
    onFocusChangeListener = OnFocusChangeListener { _, b ->
        if (!b) setLengthError(min)
    }

    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            setLengthError(min)
        }
        false
    }

    setOnKeyListener { _, actionId, event ->
        if (event.action == KeyEvent.ACTION_DOWN && actionId == KeyEvent.KEYCODE_ENTER) {
            setLengthError(min)
            true
        } else {
            false
        }
    }
}

private fun EditText.setLengthError(min: Int) {
    error = try {
        val value = text.toString().trim()
        if (value.length < min) {
            resources.getString(R.string.error)
        } else {
            context.hideSoftKeyboard(this)
            null
        }
    } catch (e: Exception) {
        resources.getString(R.string.error)
    }
}

private fun Context.hideSoftKeyboard(editText: EditText) {
    (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).apply {
        hideSoftInputFromWindow(editText.windowToken, 0)
    }
}
