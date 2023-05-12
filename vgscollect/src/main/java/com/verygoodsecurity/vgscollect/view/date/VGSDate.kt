package com.verygoodsecurity.vgscollect.view.date

import java.util.Calendar

data class VGSDate constructor(
    val day: Int,
    val month: Int,
    val year: Int,
) {

    val timeInMillis: Long = Calendar.getInstance().apply {
        set(Calendar.MILLISECOND, getActualMinimum(Calendar.MILLISECOND))
        set(Calendar.SECOND, getActualMinimum(Calendar.SECOND))
        set(Calendar.MINUTE, getActualMinimum(Calendar.MINUTE))
        set(Calendar.HOUR_OF_DAY, getActualMinimum(Calendar.HOUR_OF_DAY))
        set(Calendar.DAY_OF_MONTH, day)
        set(Calendar.MONTH, month)
        set(Calendar.YEAR, year)
    }.timeInMillis
}