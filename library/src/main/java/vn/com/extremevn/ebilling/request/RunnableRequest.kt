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
 * Runnable which executes a billing request.
 */
internal interface RunnableRequest {
    /**
     * Cancels request.
     * Note: nothing happens if request has already been executed
     */
    fun cancel()

    /**
     * @return associated request, null if request was cancelled
     */
    val request: Request<*, *>?

    /**
     * Note that implementation of this method should always check if the request was cancelled.
     *
     * @return true if request was successfully executed, false if request was not executed (and
     * should be rerun)
     */
    fun run(): Boolean
}
