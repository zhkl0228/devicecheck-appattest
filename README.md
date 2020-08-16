# Apple App Attest Validation

Proof of concept for validating the authenticity of Apple App Attest statements, written in Kotlin.

The implementation follows the steps outlined in the article ["Validating Apps That Connect to Your Server"](https://developer.apple.com/documentation/devicecheck/validating_apps_that_connect_to_your_server) at Apple Developer.

It relies on:

- [Jackson](https://github.com/FasterXML/jackson-dataformats-binary/tree/master/cbor) for parsing the CBOR-encoded `apple-attest` attestation statement.
- [Bouncy Castle](https://www.bouncycastle.org/) for ASN.1 parsing of Apple's X509 extension containing the _nonce_.
- [WebAuth4J](https://github.com/webauthn4j/webauthn4j) for parsing the WebAuthn _authenticator data_ of the `apple-attest` attestation statement. 

## Usage Example

```kotlin
// Create an Attestation instance specific to a given iOS app and development team
val attestationValidator = AttestationValidator(
    appleTeamIdentifier = "6MURL8TA57",
    appCfBundleIdentifier = "de.vincent-haupert.AppleAppAttestPoc",
    appleAppAttestEnvironment = AppleAppAttestEnvironment.DEVELOPMENT
)

// Validate a single attestation object. Throws an AttestationException if a validation error occurs.
val (publicKey, receipt) = attestationValidator.validate(
    // See `iOS14-attestation-response-base64.cbor` for full attestation response
    attestationObjectBase64 = "o2NmbXRvYXBwbGUtYXBwYXR0ZXN0Z2F0dFN0bXSiY3g1Y4JZAvYwggLyM ...",
    keyIdBase64 = "XGr5wqmUab/9M4b5vxa6KkPOigfeEWDaw7tuK02aJ6c=",
    serverChallenge = "wurzelpfropf".toByteArray()
)

// If the method call returns, the validation has passed and you can now trust the returned public key and receipt.
```

## Building

Just clone this respository

	git clone https://github.com/veehaitch/devicecheck-appattest.git
	
and build using Gradle

	./gradlew build
	
## License

[Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html)
