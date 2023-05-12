package com.verygoodsecurity.vgscollect.widget

import android.content.Context
import android.util.AttributeSet
import com.verygoodsecurity.vgscollect.view.date.VGSDate
import com.verygoodsecurity.vgscollect.widget.core.DateEditText

class RangeDateEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : DateEditText(context, attrs, defStyleAttr) {

    /**
     * Set minimum date allowed
     */
    fun setMinDate(date: VGSDate) {
        setMinDate(date.timeInMillis)
    }

    fun setMaxDate(date: VGSDate) {
        setMaxDate(date.timeInMillis)
    }
}