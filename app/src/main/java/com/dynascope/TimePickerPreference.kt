package com.dynascope

import android.content.Context
import android.util.AttributeSet
import androidx.preference.DialogPreference

class TimePickerPreference(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
    DialogPreference(context, attrs, defStyleAttr, defStyleRes) {

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, R.attr.dialogPreferenceStyle)
    /** This constructor is called from the library */
    constructor(context: Context) : this(context, null)

    override fun getDialogLayoutResource(): Int {
        return R.layout.time_picker
    }
}
