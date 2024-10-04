/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers

import base.BaseISpec
import controllers.ControllerWithErrorResponseDisabledISpec.disabledAccountRef

class ControllerWithErrorResponseDisabledISpec extends BaseISpec(
  s"errorResponses.$disabledAccountRef" -> ""
) {
  s"Get $link_url/:ref" should {
    "respond with 200 and a body in the expected format" when {
      "request contains a valid correlation ID header and expected JSON fields are present and account ref is not enabled to return error" in
        forAll(LinkAccountsScenario withFixedAccountRef disabledAccountRef) { scenario =>
          withClient { ws =>
            val response = ws
              .url(s"$baseUrl$link_url/${scenario.account_ref}?${scenario.queryString}")
              .get()
              .futureValue

            response.status shouldBe OK

            val jsonResult = response.json validate LinkAccountsScenario.expectedResponseFormat
            assert(jsonResult.isSuccess)
          }
        }
    }
  }

  s"GET $balance_url/:ref" should {
    "respond with200 and a body in the expected format" when {
      "given one of the expected account refs" in
        forAll(CheckBalanceScenario withFixedAccountRef disabledAccountRef) { scenario =>

          withClient { ws =>
            val response = ws
              .url(s"$baseUrl$balance_url/${scenario.account_ref}?${scenario.queryString}")
              .get()
              .futureValue

            response.status shouldBe OK

            val jsonResult = response.json validate CheckBalanceScenario.expectedResponseFormat
            assert(jsonResult.isSuccess)
          }
        }
    }
  }

  s"POST $payment_url" should {
    s"respond with 201 and a body in the expected format" when {
      "given one of the expected account refs" in
        forAll(MakePaymentScenario withFixedAccountRef disabledAccountRef) { scenario =>

          withClient { ws =>
            val response = ws
              .url(s"$baseUrl$payment_url")
              .post(scenario.requestBody)
              .futureValue

            response.status shouldBe CREATED

            val jsonResult = response.json validate MakePaymentScenario.expectedResponseFormat
            assert(jsonResult.isSuccess)
          }
        }
    }
  }
}
object ControllerWithErrorResponseDisabledISpec {
  
  private val disabledAccountRef = "EEAA"
}