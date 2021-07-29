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
 * An exception that is passed in [RequestListener.onError] if any error
 * occur. A response error code can be obtained through [.getResponse] method.
 */
class BillingException internal constructor(
    /**
     * @return error code for which this exception was created
     * @see ResponseCodes
     */
    val response: Int
) : Exception(
    "An error occurred while performing billing request: " + ResponseCodes.toString(
        response
    )
)
