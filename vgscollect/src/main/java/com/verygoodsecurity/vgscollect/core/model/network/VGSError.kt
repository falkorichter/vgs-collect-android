package com.verygoodsecurity.vgscollect.core.model.network

import com.verygoodsecurity.vgscollect.R

enum class VGSError(val code:Int, val messageResId:Int) {
    URL_NOT_VALID(1480,
        R.string.error_url_validation
    ),
    NO_INTERNET_PERMISSIONS(1481,
        R.string.error_internet_permission
    ),
    NO_NETWORK_CONNECTIONS(1482,
        R.string.error_internet_connection
    ),
    TIME_OUT(1483,
        R.string.error_time_out
    ),
    INPUT_DATA_NOT_VALID(1001,
        R.string.error_field_validation
    ),
    FILE_NOT_FOUND(1101,
        R.string.error_file_not_fount
    ),
    FILE_NOT_SUPPORT(1102,
        R.string.error_file_not_support
    ),
    FILE_SIZE_OVER_LIMIT(1103,
        R.string.error_file_size_validation
    )
}