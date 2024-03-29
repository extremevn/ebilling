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

class Billing {
    companion object {
        /**
         * Create billing instance live in activity lifecycle: start when activity created and stop when activity destroyed
         * Only billing instance for activity can make purchase
         */
        const val TAG = "Billing"
        fun createFor(context: Context): BillingProcessor =
            if (context is AppCompatActivity) {
                BillingActivity(context)
            } else {
                BillingProcessor(context)
            }
    }
}
