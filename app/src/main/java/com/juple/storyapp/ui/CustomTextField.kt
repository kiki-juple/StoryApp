package com.juple.storyapp.ui

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatEditText

class CustomTextField : AppCompatEditText, View.OnClickListener {

    constructor(context: Context) : super(context) {
        6.filterMinLength()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        6.filterMinLength()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        6.filterMinLength()
    }

    override fun onClick(p0: View?) {}

    private fun Int.filterMinLength() {
        onFocusChangeListener = OnFocusChangeListener { _, b ->
            if (!b) {
                setLengthError(this)
            }
        }

        setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                setLengthError(this)
            }
            false
        }

        setOnKeyListener { _, actionId, event ->
            if (event.action == KeyEvent.ACTION_DOWN && actionId == KeyEvent.KEYCODE_ENTER) {
                setLengthError(this)
                true
            } else {
                false
            }
        }
    }

    private fun setLengthError(min: Int) {
        error = try {
            val value = text.toString().trim()
            if (value.length < min) {
                "Minimum password length is $min."
            } else {
                context.hideSoftKeyboard(this)
                null
            }
        } catch (e: Exception) {
            "Minimum password length is $min."
        }
    }

    private fun Context.hideSoftKeyboard(editText: AppCompatEditText) {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
            hideSoftInputFromWindow(editText.windowToken, 0)
        }
    }
}