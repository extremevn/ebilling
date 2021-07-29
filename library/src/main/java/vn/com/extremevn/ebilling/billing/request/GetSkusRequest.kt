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

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import vn.com.extremevn.ebilling.request.Request
import vn.com.extremevn.ebilling.request.RequestListener

class GetSkusRequest(
    skuDetailsParams: SkuDetailsParams,
    requestListener: RequestListener<List<SkuDetails>>
) :
    Request<SkuDetailsParams, List<SkuDetails>>(skuDetailsParams, skuDetailsParams.skuType, requestListener) {

    override fun startWhenReady(client: BillingClient) {
        if (checkSubFeature(client).not())
            return
        client.querySkuDetailsAsync(
            param as SkuDetailsParams
        ) { billingResult, skuDetails ->
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK ->
                    onSuccess(skuDetails ?: mutableListOf())
                else -> handleError(billingResult.responseCode)
            }
        }
    }
}
