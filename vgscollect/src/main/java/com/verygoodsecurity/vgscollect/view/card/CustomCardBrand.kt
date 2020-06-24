package com.verygoodsecurity.vgscollect.view.card

/**
 * The data class definition for represent custom card brand.
 * It may be useful to add new brands in addition to already defined brands or override existing ones.
 *
 * @since 1.0.1
 */
data class CustomCardBrand(

    /** The regex rules for detection card brand. */
    val regex:String,

    /** The name of current card brand. This name may be visible for users. */
    val cardBrandName:String,

    /** The drawable resource represents credit card logo. */
    val drawableResId:Int = 0,


    val params:BrandParams = BrandParams()
)