[![Maven Central](https://img.shields.io/maven-central/v/vn.com.extremevn/ebilling?label=MavenCentral&logo=apache-maven)](https://search.maven.org/artifact/vn.com.extremevn/ebilling)
# E-Billing
An Android library for new in-app-purchase api

## Getting Started

Check out the app directory for a sample app using this library

##### **Install**
Root build.gradle
```gradle
    allprojects {
		repositories {
			...
			mavenCentral()
		}
	}
```
App build.gradle
```gradle
    dependencies {
        implementation 'vn.com.extremevn:ebilling:1.0.0'
        ...
    }
```
##### **Usage**

For launch purchase flow, must use
```kotlin
        class MainActivity : AppCompatActivity() {
            lateinit var billingProcessor: BillingProcessor
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                billingProcessor = Billing.createFor(this)
                // Other code
            }
        }
```

If don't need to launch purchase flow, can use
```kotlin
        class SampleService : Service() {
            lateinit var billingProcessor: BillingProcessor
            override fun onCreate() {
                super.onCreate()
                billingProcessor = Billing.createFor(this)
                // Other code
            }
        }
```
Then we can use billing api func similar to android in-app-billing api. For example
```kotlin
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
                        Timber.tag("GET_SKU").i("$result")
                    }

                    override fun onError(response: Int, e: Exception) {
                        Timber.tag("GET_SKU").e(e, "code: $response")
                    }
                }
            )
```

## Issues and feedback
If there is specific issues, bugs, or feature requests please report in our [mailing list][mailing list]

## Contributor & Maintainer

- [Justin Lewis](https://github.com/justin-lewis) (Maintainer)
  
## License
[Apache License 2.0](https://gitlab.extremevn.vn/development-mobile-1/library/flutter_amplify_sdk/raw/master/LICENSE)

[mailing list]: https://groups.google.com/g/ebilling-group