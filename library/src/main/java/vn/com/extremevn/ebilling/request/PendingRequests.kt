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

import timber.log.Timber
import vn.com.extremevn.ebilling.billing.Billing
import vn.com.extremevn.ebilling.billing.ResponseCodes

/**
 * List of the requests to be executed when connection to the billing service is established.
 */
internal class PendingRequests : Runnable {
    private val requests: MutableList<RunnableRequest> = mutableListOf()

    /**
     * Adds <var>runnable</var> to the end of waiting list.
     *
     * @param runnableRequest runnable to be executed when connection is established
     */
    internal fun add(runnableRequest: RunnableRequest) {
        synchronized(requests) {
            Timber.tag(Billing.TAG).d("Adding pending request: $runnableRequest")
            requests.add(runnableRequest)
        }
    }

    /**
     * Method cancels all pending requests
     */
    internal fun cancelAll() {
        synchronized(requests) {
            Timber.tag(Billing.TAG).d("Cancelling all pending requests")
            val iterator: MutableIterator<RunnableRequest> = requests.iterator()
            while (iterator.hasNext()) {
                val request: RunnableRequest = iterator.next()
                request.cancel()
                iterator.remove()
            }
        }
    }

    /**
     * Method removes first element from the waiting list
     *
     * @return first list element or null if waiting list is empty
     */
    private fun pop(): RunnableRequest? {
        synchronized(requests) {
            val runnableRequest: RunnableRequest? =
                if (!requests.isEmpty()) requests.removeAt(0) else null
            if (runnableRequest != null) {
                Timber.tag(Billing.TAG).d("Removing pending request: $runnableRequest")
            }
            return runnableRequest
        }
    }

    /**
     * Method gets first element from the waiting list
     *
     * @return first list element or null if waiting list is empty
     */
    private fun peek(): RunnableRequest? {
        synchronized(requests) { return if (!requests.isEmpty()) requests[0] else null }
    }

    /**
     * Executes all pending runnable.
     * Note: this method must be called only on one thread.
     */
    override fun run() {
        var runnableRequest: RunnableRequest? = peek()
        while (runnableRequest != null) {
            Timber.tag(Billing.TAG).d("Running pending request: $runnableRequest")
            runnableRequest = if (runnableRequest.run()) {
                remove(runnableRequest)
                peek()
            } else {
                // request can't be run because service is not connected => no need to run other requests (they will be
                // executed when service is connected)
                break
            }
        }
    }

    /**
     * Method removes instance of <var>runnable</var> from the waiting list
     *
     * @param runnableRequest runnable to be removed from the waiting list
     */
    private fun remove(runnableRequest: RunnableRequest) {
        synchronized(requests) {
            val iterator: MutableIterator<RunnableRequest> = requests.iterator()
            while (iterator.hasNext()) {
                if (iterator.next() === runnableRequest) {
                    Timber.tag(Billing.TAG).d("Removing pending request: $runnableRequest")
                    iterator.remove()
                    break
                }
            }
        }
    }

    /**
     * Cancels all pending requests with [ResponseCodes.SERVICE_NOT_CONNECTED] error code.
     */
    internal fun onConnectionFailed() {
        var runnableRequest: RunnableRequest? = pop()
        while (runnableRequest != null) {
            val request: Request<*, *>? = runnableRequest.request
            request?.run {
                onError(ResponseCodes.SERVICE_NOT_CONNECTED)
            }
            runnableRequest.cancel()
            runnableRequest = pop()
        }
    }

    override fun toString(): String {
        synchronized(requests) {
            return "requests: $requests"
        }
    }
}
