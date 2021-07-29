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
 * Service connection state
 */
internal enum class BillingConnectionState {
    /**
     * Service is not connected, no requests can be done, initial state
     */
    INITIAL,

    /**
     * Service is connecting
     */
    CONNECTING,

    /**
     * Service is connected, requests can be executed
     */
    CONNECTED,

    /**
     * Service is disconnecting
     */
    DISCONNECTING,

    /**
     * Service is disconnected
     */
    DISCONNECTED
}
