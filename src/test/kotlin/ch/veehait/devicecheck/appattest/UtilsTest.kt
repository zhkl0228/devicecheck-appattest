package ch.veehait.devicecheck.appattest

import ch.veehait.devicecheck.appattest.TestExtensions.readTextResource
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class UtilsTest : StringSpec({
    "readTextResource works and omits comments" {
        javaClass.readTextResource("/iOS14-attestation-receipt-response-base64.der") shouldBe
            "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"
    }
})
