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

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.should
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.http.Status
import play.api.libs.json.Json
import play.api.test.WsTestClient

class NsiControllerISpec
    extends AnyWordSpec
    with should.Matchers
    with ScalaFutures
    with IntegrationPatience
    with GuiceOneServerPerSuite
    with WsTestClient
    with Status {
  withClient { wsClient =>
    val contextRoot = "/individuals/tax-free-childcare/payments"
    val baseUrl     = s"http://localhost:$port$contextRoot"

    /** Covers [[NsiController.link()]]. */
    "POST /link" should {
      s"respond $OK" when {
        "nino is nonempty string ending in [A-D]" in {
          val payload = Json.obj(
            "correlationId"              -> "",
            "epp_unique_customer_id"     -> "",
            "epp_reg_reference"          -> "",
            "outbound_child_payment_ref" -> "",
            "child_date_of_birth"        -> "",
            "nino"                       -> "QW123456A"
          )

          val response = wsClient
            .url(s"$baseUrl/link")
            .post(payload)
            .futureValue

          response.status shouldBe OK
        }
      }
    }
  }
}
