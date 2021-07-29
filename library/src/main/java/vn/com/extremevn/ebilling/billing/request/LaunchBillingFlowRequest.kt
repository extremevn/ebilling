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

package vn.com.extremevn.ebilling.billing.request

import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.Purchase
import vn.com.extremevn.ebilling.request.Request
import vn.com.extremevn.ebilling.request.RequestListener

class LaunchBillingFlowRequest(
    private val activity: AppCompatActivity,
    billingFlowParams: BillingFlowParams,
    skuType: String,
    requestListener: RequestListener<List<Purchase>?>
) :
    Request<BillingFlowParams, List<Purchase>?>(billingFlowParams, skuType, requestListener) {

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    override fun startWhenReady(client: BillingClient) {
        mainThreadHandler.post {
            client.launchBillingFlow(
                activity,
                param as BillingFlowParams
            )
        }
    }

    override fun cancel() {
        mainThreadHandler.removeCallbacksAndMessages(null)
        super.cancel()
    }
}
