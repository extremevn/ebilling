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

package vn.com.extremevn.ebilling.billing

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchaseHistoryRecord
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchaseHistoryParams
import com.android.billingclient.api.QueryPurchasesParams
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import timber.log.Timber
import vn.com.extremevn.ebilling.billing.request.ConsumePurchaseRequest
import vn.com.extremevn.ebilling.billing.request.GeProductsRequest
import vn.com.extremevn.ebilling.billing.request.GetPurchaseHistoryRequest
import vn.com.extremevn.ebilling.billing.request.GetPurchasesRequest
import vn.com.extremevn.ebilling.billing.request.LaunchBillingFlowRequest
import vn.com.extremevn.ebilling.request.OnBillingConnectedRunnableRequest
import vn.com.extremevn.ebilling.request.PendingRequests
import vn.com.extremevn.ebilling.request.Request
import vn.com.extremevn.ebilling.request.RequestListener

/**
 * This Billing Processor for managing and doing requests from app
 */
open class BillingProcessor internal constructor(context: Context) {

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            synchronized(processingRequests) {
                val iterator = processingRequests.iterator()
                while (iterator.hasNext()) {
                    val request = iterator.next()
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        request.onSuccess(purchases)
                    } else {
                        request.onError(billingResult.responseCode)
                    }
                    iterator.remove()
                }
            }
        }

    private var billingClient = BillingClient.newBuilder(context.applicationContext)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()

    private var connectionState = BillingConnectionState.INITIAL

    private val processingRequests = mutableListOf<Request<*, List<Purchase>?>>()

    private val pendingRequests: PendingRequests = PendingRequests()

    private val backgroundExecutor: Executor = Executors.newSingleThreadExecutor { r ->
        Thread(r, "BillingRequestThread")
    }

    init {
        Timber.plant(Timber.DebugTree())
    }

    /**
     * Start billing connection so that requests can be processed after connection established
     */
    fun start() {
        connectBillingService()
    }

    /**
     * End billing connection also cancel all requests
     */
    fun stop() {
        disconnectBillingService()
    }

    @Synchronized
    private fun connectBillingService() {
        Timber.tag("${Billing.TAG} CONNECTION").i("Connection state $connectionState")
        if (connectionState == BillingConnectionState.CONNECTED) {
            executePendingRequests()
            return
        } else if (connectionState == BillingConnectionState.CONNECTING) {
            return
        }
        connectionState = BillingConnectionState.CONNECTING
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    connectionState = BillingConnectionState.CONNECTED
                    Timber.tag("${Billing.TAG} CONNECTION").i("startConnection ok")
                    executePendingRequests()
                } else {
                    pendingRequests.onConnectionFailed()
                    Timber.tag("${Billing.TAG} CONNECTION").i("startConnection fail")
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                Timber.tag("${Billing.TAG} CONNECTION").i("startConnection onBillingServiceDisconnected")
                connectionState = BillingConnectionState.DISCONNECTED
                pendingRequests.cancelAll()
            }
        })
    }

    @Synchronized
    private fun disconnectBillingService() {
        if (connectionState == BillingConnectionState.DISCONNECTED || connectionState == BillingConnectionState.DISCONNECTING || connectionState == BillingConnectionState.INITIAL) {
            return
        }
        if (connectionState == BillingConnectionState.CONNECTED) {
            connectionState = BillingConnectionState.DISCONNECTING
            billingClient.endConnection()
        } else {
            // if we're still CONNECTING - skip DISCONNECTING state
            connectionState = BillingConnectionState.DISCONNECTED
        }
        // requests should be cancelled only when Billing#disconnect() is called explicitly as
        // it's only then we know for sure that no more work should be done
        // requests should be cancelled only when Billing#disconnect() is called explicitly as
        // it's only then we know for sure that no more work should be done
        pendingRequests.cancelAll()
    }

    private fun executePendingRequests() {
        Timber.tag(Billing.TAG).i("REQUEST: executePendingRequests: $pendingRequests")
        backgroundExecutor.execute(pendingRequests)
    }

    /**
     * Get billing client instance for other processing such as: purchase updates, acknowledge purchase etc
     **/
    @Suppress("unused")
    fun getBillingClient(): BillingClient = billingClient

    /**
     * Initiates the billing flow for an in-app purchase or subscription.
     * @param activity activity for launching purchase flow
     * @param billingFlowParams Parameters to consume a purchase @see 	com.android.billingclient.api.BillingFlowParams.
     * @param productType The type of SKU, either "inapp" or "subs" as in @see com.android.billingclient.api.BillingClient.ProductType.
     * @param listener The listener for the result of the query returned asynchronously through the succeed callback with the purchases @see com.android.billingclient.api.Purchase list or error callback with error code if failed
     */
    open fun launchBillingFlow(
        activity: AppCompatActivity,
        billingFlowParams: BillingFlowParams,
        productType: String,
        listener: RequestListener<List<Purchase>?>
    ) {
        val billingFlowRequest =
            LaunchBillingFlowRequest(activity, billingFlowParams, productType, listener)
        pendingRequests.add(
            OnBillingConnectedRunnableRequest(
                billingClient,
                billingFlowRequest,
                ::connectBillingService
            )
        )
        synchronized(processingRequests) {
            processingRequests.add(billingFlowRequest)
        }
        connectBillingService()
    }

    /**
     * Performs a network query to get SKU details and return the result asynchronously.
     * @param queryProductDetailsParams Parameters to consume a purchase @see 	com.android.billingclient.api.QueryProductDetailsParams.
     * @param productType The type of product which is being query
     * @param listener The listener for the result of the query returned asynchronously through the succeed callback with the skeu detail @see com.android.billingclient.api.SkuDetails list or error callback with error code if failed
     */
    fun getProductDetails(
        queryProductDetailsParams: QueryProductDetailsParams,
        productType: String,
        listener: RequestListener<List<ProductDetails>>
    ) {
        val geProductsRequest = GeProductsRequest(queryProductDetailsParams, listener, productType)
        pendingRequests.add(
            OnBillingConnectedRunnableRequest(
                billingClient,
                geProductsRequest,
                ::connectBillingService
            )
        )
        connectBillingService()
    }

    /**
     * Returns purchases details for currently owned items bought within app.
     * Only active subscriptions and non-consumed one-time purchases are returned.
     * This method uses a cache of Google Play Store app without initiating a network request.
     * @param productType The type of Product, either "inapp" or "subs" as in @see com.android.billingclient.api.BillingClient.ProductType.
     * @param listener The listener for the result of the query returned asynchronously through the succeed callback with the purchases @see com.android.billingclient.api.Purchase list or error callback with error code if failed
     */
    fun getPurchases(
        productType: String,
        listener: RequestListener<List<Purchase>>
    ) {
        val queryPurchasesParams = QueryPurchasesParams.newBuilder().setProductType(productType).build()
        val getPurchasesRequest = GetPurchasesRequest(queryPurchasesParams, productType, listener)
        pendingRequests.add(
            OnBillingConnectedRunnableRequest(
                billingClient,
                getPurchasesRequest,
                ::connectBillingService
            )
        )
        connectBillingService()
    }

    /**
     * Returns the most recent purchase made by the user for each SKU, even if that purchase is expired, canceled, or consumed
     * @param productType The type of Product, either "inapp" or "subs" as in @see com.android.billingclient.api.BillingClient.ProductType.
     * @param listener The listener for the result of the query returned asynchronously through the succeed callback with the purchases @see com.android.billingclient.api.PurchaseHistoryRecord list or error callback with error code if failed
     */
    fun getPurchaseHistory(
        productType: String,
        listener: RequestListener<List<PurchaseHistoryRecord>?>
    ) {
        val queryPurchaseHistoryParams = QueryPurchaseHistoryParams.newBuilder().setProductType(productType).build()
        val getPurchasesRequest = GetPurchaseHistoryRequest(queryPurchaseHistoryParams, productType, listener)
        pendingRequests.add(
            OnBillingConnectedRunnableRequest(
                billingClient,
                getPurchasesRequest,
                ::connectBillingService
            )
        )
        connectBillingService()
    }

    /**
     * Consumes a given in-app product. Consuming can only be done on an item that's owned, and as a result of consumption, the user will no longer own it.
     * @param productType The type of Product, either "inapp" or "subs" as in @see com.android.billingclient.api.BillingClient.ProductType.
     * @param consumeParams Parameters to consume a purchase @see 	com.android.billingclient.api.ConsumeParams.
     * @param listener The listener for the result of the query returned asynchronously through the succeed callback with the purchase token or error callback with error code if failed
     */
    fun consumePurchase(
        productType: String,
        consumeParams: ConsumeParams,
        listener: RequestListener<String>
    ) {
        val getPurchasesRequest = ConsumePurchaseRequest(consumeParams, productType, listener)
        pendingRequests.add(
            OnBillingConnectedRunnableRequest(
                billingClient,
                getPurchasesRequest,
                ::connectBillingService
            )
        )
        connectBillingService()
    }
}
