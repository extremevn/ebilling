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

/**
 * Billing response codes, codes >= 10000 are defined by this library.
 * See [In-App Billing](https://developer.android.com/reference/com/android/billingclient/api/BillingClient.BillingResponseCode) docs
 * for more information.
 */
class ResponseCodes private constructor() {
    companion object {
        /**
         * Success
         */
        const val OK = 0

        /**
         * User pressed back or canceled a dialog
         */
        const val USER_CANCELED = 1

        /**
         * Account error, for example, user is not logged in
         */
        const val ACCOUNT_ERROR = 2

        /**
         * This billing API version is not supported for the type requested
         */
        const val BILLING_UNAVAILABLE = 3

        /**
         * Requested SKU is not available for purchase
         */
        const val ITEM_UNAVAILABLE = 4

        /**
         * Invalid arguments provided to the API
         */
        const val DEVELOPER_ERROR = 5

        /**
         * Fatal error during the API action
         */
        const val ERROR = 6

        /**
         * Failure to purchase since item is already owned
         */
        const val ITEM_ALREADY_OWNED = 7

        /**
         * Failure to consume since item is not owned
         */
        const val ITEM_NOT_OWNED = 8

        /**
         * Billing service can't be connected, [android.content.Context.bindService] returned
         * `false`.
         *
         * @see android.content.Context.bindService
         */
        const val SERVICE_NOT_CONNECTED = 10000

        /**
         * Exception occurred during executing the request
         */
        const val EXCEPTION = 10001

        /**
         * Purchase has a wrong signature
         */
        const val WRONG_SIGNATURE = 10002

        /**
         * @return a name of the given response code
         */
        fun toString(code: Int): String {
            return when (code) {
                OK -> "OK"
                USER_CANCELED -> "USER_CANCELED"
                ACCOUNT_ERROR -> "ACCOUNT_ERROR"
                BILLING_UNAVAILABLE -> "BILLING_UNAVAILABLE"
                ITEM_UNAVAILABLE -> "ITEM_UNAVAILABLE"
                DEVELOPER_ERROR -> "DEVELOPER_ERROR"
                ERROR -> "ERROR"
                ITEM_ALREADY_OWNED -> "ITEM_ALREADY_OWNED"
                ITEM_NOT_OWNED -> "ITEM_NOT_OWNED"
                SERVICE_NOT_CONNECTED -> "SERVICE_NOT_CONNECTED"
                EXCEPTION -> "EXCEPTION"
                WRONG_SIGNATURE -> "WRONG_SIGNATURE"
                else -> "UNKNOWN"
            }
        }
    }

    init {
        throw AssertionError()
    }
}
