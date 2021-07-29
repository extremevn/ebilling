package vn.com.extremevn.sample

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchaseHistoryRecord
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import timber.log.Timber
import vn.com.extremevn.ebilling.billing.Billing
import vn.com.extremevn.ebilling.billing.BillingProcessor
import vn.com.extremevn.ebilling.request.RequestListener

class MainActivity : AppCompatActivity() {
    lateinit var billingProcessor: BillingProcessor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        billingProcessor = Billing.createFor(this)
        setContentView(R.layout.activity_main)
        setUpGetSkuDetail()
        setUpPurchase()
        setUpGetPurchase()
        setUpGetPurchaseHistory()
        setUpConsumePurchase()
    }

    private fun setUpGetSkuDetail() {
        findViewById<Button>(R.id.bt_sku_details).setOnClickListener {
            val params =
                SkuDetailsParams.newBuilder()
                    .setType(BillingClient.SkuType.INAPP)
                    .setSkusList(
                        listOf("your.product.id")
                    ).build()
            for (i in 0..100) {
                billingProcessor.getSkuDetails(
                    params,
                    object : RequestListener<List<SkuDetails>> {
                        override fun onSuccess(result: List<SkuDetails>) {
                            Timber.tag("GET_SKU").i("$result")
                        }

                        override fun onError(response: Int, e: Exception) {
                            Timber.tag("GET_SKU").e(e, "code: $response")
                        }
                    }
                )
            }
        }
    }

    private fun setUpPurchase() {
        findViewById<Button>(R.id.bt_purchase).setOnClickListener {
            val params =
                SkuDetailsParams.newBuilder()
                    .setType(BillingClient.SkuType.INAPP)
                    .setSkusList(
                        listOf("your.product.id")
                    ).build()
            billingProcessor.getSkuDetails(
                params,
                object : RequestListener<List<SkuDetails>> {
                    override fun onSuccess(result: List<SkuDetails>) {
                        Timber.tag("PURCHASE").i("$result")
                        result.firstOrNull()?.run {
                            billingProcessor.launchBillingFlow(
                                BillingFlowParams.newBuilder().setSkuDetails(this).build(),
                                type,
                                object : RequestListener<List<Purchase>?> {
                                    override fun onSuccess(result: List<Purchase>?) {
                                        Timber.tag("PURCHASE").i("Result: $result")
                                    }

                                    override fun onError(response: Int, e: Exception) {
                                        Timber.tag("PURCHASE").e(e, "Result: $response")
                                    }
                                }
                            )
                        }
                    }

                    override fun onError(response: Int, e: Exception) {
                        Timber.tag("PURCHASE").e(e, "code: $response")
                    }
                }
            )
        }
    }

    private fun setUpGetPurchase() {
        findViewById<Button>(R.id.bt_get_purchase).setOnClickListener {
            for (i in 0..100) {
                billingProcessor.getPurchases(
                    BillingClient.SkuType.INAPP,
                    object : RequestListener<List<Purchase>> {
                        override fun onSuccess(result: List<Purchase>) {
                            Timber.tag("GET_PURCHASE").i("$result")
                        }

                        override fun onError(response: Int, e: Exception) {
                            Timber.tag("GET_PURCHASE").e(e, "code: $response")
                        }
                    }
                )
            }
        }
    }

    private fun setUpGetPurchaseHistory() {
        findViewById<Button>(R.id.bt_get_purchase_history).setOnClickListener {
            for (i in 0..100) {
                billingProcessor.getPurchaseHistory(
                    BillingClient.SkuType.INAPP,
                    object : RequestListener<List<PurchaseHistoryRecord>?> {
                        override fun onSuccess(result: List<PurchaseHistoryRecord>?) {
                            Timber.tag("GET_PURCHASE_HISTORY").i("$result")
                        }

                        override fun onError(response: Int, e: Exception) {
                            Timber.tag("GET_PURCHASE_HISTORY").e(e, "code: $response")
                        }
                    }
                )
            }
        }
    }

    private fun setUpConsumePurchase() {
        findViewById<Button>(R.id.bt_consume_purchase).setOnClickListener {
            billingProcessor.getPurchases(
                BillingClient.SkuType.INAPP,
                object : RequestListener<List<Purchase>> {
                    override fun onSuccess(result: List<Purchase>) {
                        Timber.tag("GET_PURCHASE").i("$result")
                        result.firstOrNull()?.run {
                            billingProcessor.consumePurchase(
                                BillingClient.SkuType.INAPP,
                                ConsumeParams.newBuilder().setPurchaseToken(purchaseToken).build(),
                                object : RequestListener<String> {
                                    override fun onSuccess(result: String) {
                                        Timber.tag("CONSUME_PURCHASE").i(result)
                                    }

                                    override fun onError(response: Int, e: Exception) {
                                        Timber.tag("CONSUME_PURCHASE").e(e, "code: $response")
                                    }
                                }
                            )
                        }
                    }

                    override fun onError(response: Int, e: Exception) {
                        Timber.tag("CONSUME_PURCHASE").e(e, "code: $response")
                    }
                }
            )
        }
    }
}
