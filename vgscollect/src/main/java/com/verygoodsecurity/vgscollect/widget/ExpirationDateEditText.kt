package com.verygoodsecurity.vgscollect.widget

import android.content.Context
import android.util.AttributeSet
import com.verygoodsecurity.vgscollect.widget.core.DateEditText

/**
 * Provides a user interface element for date input. The range of dates supported by this field is not configurable.
 *
 * @since 1.0.7
 */
class ExpirationDateEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : DateEditText(context, attrs, defStyleAttr) {

    /**
     * Interface definition for a callback to be invoked when the DatePicker Dialog changes
     * visibility.
     */
    interface OnDatePickerVisibilityChangeListener {
        /**
         * Called when the DatePicker Dialog was shown.
         */
        fun onShow()

        /**
         * Called when the DatePicker Dialog was dismissed.
         */
        fun onDismiss()
    }
}