package vn.com.extremevn.sample

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchaseHistoryRecord
import com.android.billingclient.api.QueryProductDetailsParams
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
                QueryProductDetailsParams.newBuilder()
                    .setProductList(
                        mutableListOf(
                            QueryProductDetailsParams.Product.newBuilder()
                                .setProductId("your.product.id")
                                .setProductType(BillingClient.ProductType.INAPP).build()
                        )
                    )
                    .build()
            for (i in 0..100) {
                billingProcessor.getProductDetails(
                    params,
                    BillingClient.ProductType.INAPP,
                    object : RequestListener<List<ProductDetails>> {
                        override fun onSuccess(result: List<ProductDetails>) {
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
                QueryProductDetailsParams.newBuilder()
                    .setProductList(
                        mutableListOf(
                            QueryProductDetailsParams.Product.newBuilder()
                                .setProductId("your.product.id")
                                .setProductType(BillingClient.ProductType.INAPP).build()
                        )
                    ).build()
            billingProcessor.getProductDetails(
                params,
                BillingClient.ProductType.INAPP,
                object : RequestListener<List<ProductDetails>> {
                    override fun onSuccess(result: List<ProductDetails>) {
                        Timber.tag("PURCHASE").i("$result")
                        result.firstOrNull()?.run {
                            billingProcessor.launchBillingFlow(this@MainActivity,
                                BillingFlowParams.newBuilder().setProductDetailsParamsList(
                                    listOf(
                                        BillingFlowParams.ProductDetailsParams.newBuilder()
                                            .setProductDetails(this)
                                            .build()
                                    )
                                ).build(),
                                BillingClient.ProductType.INAPP,
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
                    BillingClient.ProductType.INAPP,
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
                    BillingClient.ProductType.INAPP,
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
                BillingClient.ProductType.INAPP,
                object : RequestListener<List<Purchase>> {
                    override fun onSuccess(result: List<Purchase>) {
                        Timber.tag("GET_PURCHASE").i("$result")
                        result.firstOrNull()?.run {
                            billingProcessor.consumePurchase(
                                BillingClient.ProductType.INAPP,
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
