package com.verygoodsecurity.vgscollect.core

import android.app.Activity
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.util.Log
import android.webkit.URLUtil
import androidx.core.content.ContextCompat
import com.verygoodsecurity.vgscollect.core.model.SimpleResponse
import com.verygoodsecurity.vgscollect.core.model.mapToEncodedQuery
import com.verygoodsecurity.vgscollect.core.storage.DefaultStorage
import com.verygoodsecurity.vgscollect.widget.VGSEditText
import java.lang.StringBuilder

class VGSCollect(id:String, environment: Environment = Environment.SANDBOX) {

    private val baseURL:String
    private val isURLValid:Boolean

    private val storage = DefaultStorage()
    private val client = ApiClient()
    private val tasks = mutableListOf<AsyncTask<String?, Void, SimpleResponse>>()

    var onResponceListener:VgsCollectResponseListener? = null

    init {
        val builder = StringBuilder("https://")
            .append(id).append(".")
            .append(environment.rawValue).append(".")
            .append("verygoodproxy.com")

        baseURL = builder.toString()
        isURLValid = URLUtil.isValidUrl(baseURL)
    }

    fun bindView(view: VGSEditText?) {
        val listener = storage.performSubscription()
        view?.inputField?.addDataViewStateChangeListener(listener)
    }

    fun onDestroy() {
        tasks.forEach {
            it.cancel(true)
        }
        tasks.clear()
        storage.clear()
    }

    fun submit(mainActivity:Activity) {
        when {
            ContextCompat.checkSelfPermission(mainActivity,android.Manifest.permission.INTERNET)
                    == PackageManager.PERMISSION_DENIED -> Log.e("VGSCollect", "Permission denied (missing INTERNET permission?)")
            !isURLValid -> Log.e("VGSCollect", "URL is not valid")

            else -> {
                val states = storage.getStates()

                val operation = NetworkOperation()
                tasks.add(operation)
                val data = states.mapToEncodedQuery()
                operation.execute(data)
            }
        }
    }

    private inner class NetworkOperation : AsyncTask<String?, Void, SimpleResponse>() {
        override fun doInBackground(vararg arg: String?): SimpleResponse? {
            if(arg.isNotEmpty()) {
                val response = arg[0]?.run {
                    client.callPost(baseURL, this)
                }
                if(response != null) {
                    return response
                }
            }
            return null
        }

        override fun onPostExecute(result: SimpleResponse?) {
            super.onPostExecute(result)
            if(onResponceListener == null) {
                Log.i("VGSCollect", "VgsCollectResponseListener not set")
            } else {
                onResponceListener!!.onResponse(result)
            }

            tasks.remove(this@NetworkOperation)
        }
    }
}