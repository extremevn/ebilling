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

internal class OnBillingConnectedRunnableRequest(
    private val billingClient: BillingClient,
    override var request: Request<*, *>?,
    private val onRetry: () -> Unit
) : RunnableRequest {
    @Synchronized
    override fun cancel() {
        if (request != null) {
            Timber.i("Cancelling request: $request")
            request!!.cancel()
        }
        request = null
    }

    override fun run(): Boolean {
        if (request == null || request!!.isCancelled) {
            Timber.tag("REQUEST").i("request $request is Cancelled, stop run")
            return true
        }
        if (billingClient.isReady.not()) {
            Timber.tag("REQUEST")
                .i("Billing client is not ready, try to connect and run again later")
            onRetry.invoke()
            return false
        }
        try {
            Timber.tag("REQUEST").i("request $request start")
            request!!.start(billingClient)
        } catch (e: Exception) {
            request!!.onError(RequestException(e))
            Timber.tag("REQUEST").e(e, "exception occured when execute request $request")
        }
        return true
    }
}
