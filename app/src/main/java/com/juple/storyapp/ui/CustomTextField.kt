package com.juple.storyapp.ui

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText

class CustomTextField : AppCompatEditText {

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

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setLengthError(this@filterMinLength)
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun setLengthError(min: Int) {
        error = try {
            val value = text.toString().trim()
            if (value.length < min) "Minimum password length is $min" else null
        } catch (e: Exception) {
            "Minimum password length is $min"
        }
    }
}