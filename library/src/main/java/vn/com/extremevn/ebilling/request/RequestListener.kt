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

/**
 * Listener associated with a request. Its [.onSuccess] method is called when the
 * result is ready and [.onError] in case of any error.
 * Listener methods might be called either on a background thread or on the main application
 * thread. See [Billing] for more information.
 *
 *
 * **Note**: if a listener references an activity/context the associated request should
 * be cancelled through [Billing.cancel] or [Billing.cancelAll] methods when
 * the references activity/context is destroyed. Otherwise, the request will continue holding the
 * reference and the activity/context will leak.
 */
interface RequestListener<R> {
    /**
     * Called when the request has finished successfully.
     *
     * @param result request result
     */
    fun onSuccess(result: R)

    /**
     * Called when the request has finished with an error (for example, exception was raised).
     *
     * @param response response code
     * @param e        raised exception
     */
    fun onError(response: Int, e: Exception)
}
