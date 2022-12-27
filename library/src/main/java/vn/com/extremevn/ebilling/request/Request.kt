/*
 * Copyright (c) [2021] Extreme Viet Nam
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vn.com.extremevn.ebilling.request

import com.android.billingclient.api.BillingClient
import timber.log.Timber
import vn.com.extremevn.ebilling.billing.Billing
import vn.com.extremevn.ebilling.billing.BillingException
import vn.com.extremevn.ebilling.billing.ResponseCodes

/**
 * Asynchronous operation which is done with connected billing service
 */
abstract class Request<P : Any, R>(
    val param: Any? = null,
    private val productType: String,
    var requestListener: RequestListener<R>?
) {

    private var isListenerCalled = false

    // Start sending request by using [client] @see com.android.billingclient.api.BillingClient
    internal fun start(client: BillingClient) {
        if (checkSubFeature(client).not() || client.isReady.not()) {
            return
        }
        startWhenReady(client)
    }

    // Start sending request by using [client] @see com.android.billingclient.api.BillingClient
    internal abstract fun startWhenReady(client: BillingClient)

    /**
     * Cancels this request, after this method is called request listener method will not be called
     */
    open fun cancel() {
        synchronized(this) {
            Timber.tag(Billing.TAG).i("REQUEST: cancel request $this")
            requestListener = null
        }
    }

    /**
     * @return true if request is cancelled
     */
    internal val isCancelled: Boolean
        get() {
            synchronized(this) { return requestListener == null }
        }

    internal fun onSuccess(result: R) {
        Timber.tag(Billing.TAG).i("REQUEST: request succeed")
        if (requestListener != null) {
            if (checkListenerCalled()) return
            Timber.tag(Billing.TAG).i("REQUEST: request succeed raise listener")
            requestListener!!.onSuccess(result)
        }
    }

    private fun checkListenerCalled(): Boolean {
        synchronized(this) {
            if (isListenerCalled) {
                return true
            }
            isListenerCalled = true
        }
        return false
    }

    internal fun onError(response: Int) {
        val message = ResponseCodes.toString(response)
        Timber.tag(Billing.TAG).e("REQUEST request error response: $message in $this request")
        onError(response, BillingException(response))
    }

    internal fun onError(e: Exception) {
        Timber.tag("${Billing.TAG} REQUEST").e(e, "request exception in $this request: ")
        onError(ResponseCodes.EXCEPTION, e)
    }

    private fun onError(response: Int, e: Exception) {
        if (requestListener != null) {
            if (checkListenerCalled()) return
            requestListener!!.onError(response, e)
        }
    }

    protected fun handleError(response: Int): Boolean {
        if (response != ResponseCodes.OK) {
            onError(response)
            return true
        }
        return false
    }

    protected fun checkSubFeature(service: BillingClient): Boolean {
        if (productType == BillingClient.ProductType.SUBS) {
            val billingResult = service.isFeatureSupported(BillingClient.ProductType.SUBS)
            if (billingResult.responseCode == BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED) {
                handleError(billingResult.responseCode)
                return false
            }
        }
        return true
    }

    override fun toString(): String {
        return super.toString() + " $param, $isCancelled, $requestListener"
    }
}
