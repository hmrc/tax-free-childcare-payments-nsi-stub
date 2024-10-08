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
import controllers.ControllerWithConfigOverriddenISpec._
import org.scalacheck.Gen
import play.api.libs.json.{JsDefined, JsString}

class ControllerWithConfigOverriddenISpec extends BaseISpec(
      s"data.accounts.$ACCOUNT_REF_1"       -> "",
      s"data.errorResponses.$ACCOUNT_REF_2" -> ""
    ) {

  private val disabledAccountRefs = Gen.oneOf(ACCOUNT_REF_1, ACCOUNT_REF_2)

  s"Get $link_url/:ref"    should {
    val scenarios = disabledAccountRefs flatMap LinkAccountsScenario.withFixedAccountRef

    "respond with 200 and a body in the expected format" when {
      "account ref is not enabled to return error" in
        forAll(scenarios) { scenario =>
          withClient { ws =>
            val response = ws
              .url(s"$baseUrl$link_url/${scenario.account_ref}?${scenario.queryString}")
              .get()
              .futureValue

            response.status                              shouldBe OK
            (response.json \ "childFullName").as[String] shouldBe "Peter Pan"
          }
        }
    }
  }

  s"GET $balance_url/:ref" should {
    val scenarios = disabledAccountRefs flatMap CheckBalanceScenario.withFixedAccountRef

    "respond with 200 and a body in the expected format" when {
      "account ref is not enabled to return error" in
        forAll(scenarios) { scenario =>
          withClient { ws =>
            val response = ws
              .url(s"$baseUrl$balance_url/${scenario.account_ref}?${scenario.queryString}")
              .get()
              .futureValue

            response.status                            shouldBe OK
            (response.json \ "topUpAvailable").as[Int] shouldBe 4500
          }
        }
    }
  }

  s"POST $payment_url"     should {
    val scenarios = disabledAccountRefs flatMap MakePaymentScenario.withFixedAccountRef

    s"respond with 201 and a body in the expected format" when {
      "account ref is not enabled to return error" in
        forAll(scenarios) { scenario =>
          withClient { ws =>
            val response = ws
              .url(s"$baseUrl$payment_url")
              .post(scenario.requestBody)
              .futureValue

            response.status                                 shouldBe CREATED
            (response.json \ "paymentReference").as[String] shouldBe "1234567887654321"
          }
        }
    }
  }
}

object ControllerWithConfigOverriddenISpec {

  private val ACCOUNT_REF_1 = "AABB"
  private val ACCOUNT_REF_2 = "EEAA"
}
