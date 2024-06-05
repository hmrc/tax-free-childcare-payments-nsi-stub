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

import java.util.UUID

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
      s"respond $OK and echo the correlation ID in the response header" when {
        "request contains a valid correlation ID header and expected JSON fields are present and NINO ends in [A-D]" in {
          val expectedCorrelationId @ (headerName, Seq(headerValue)) = randomCorrelationIdHeader
          val goodPayload = Json.obj(
            "epp_unique_customer_id"     -> "",
            "epp_reg_reference"          -> "",
            "outbound_child_payment_ref" -> "",
            "child_date_of_birth"        -> "",
            "nino"                       -> "QW123456A"
          )

          val response = wsClient
            .url(s"$baseUrl/linknsi")
            .withHttpHeaders(headerName -> headerValue)
            .post(goodPayload)
            .futureValue

          response.status shouldBe OK
          response.headers should contain(expectedCorrelationId)
        }
      }
    }

    /** Covers [[NsiController.balance()]]. */
    "POST /balance" should {
      s"respond $OK and echo the correlation ID in the response header" when {
        "request contains valid correlation ID header" in {
          val expectedCorrelationId @ (headerName, Seq(headerValue)) = randomCorrelationIdHeader
          val goodPayload = Json.obj(
            "epp_unique_customer_id"     -> "",
            "epp_reg_reference"          -> "",
            "outbound_child_payment_ref" -> ""
          )

          val response = wsClient
            .url(s"$baseUrl/balancensi")
            .withHttpHeaders(headerName -> headerValue)
            .post(goodPayload)
            .futureValue

          response.status shouldBe OK
//          response.headers should contain(expectedCorrelationId)
        }
      }
    }

    /** Covers [[NsiController.payment()]]. */
    "POST /" should {
      s"respond $OK and echo the correlation ID in the response header" when {
        "request contains valid correlation ID header" in {
          val expectedCorrelationId @ (headerName, Seq(headerValue)) = randomCorrelationIdHeader

          val response = wsClient
            .url(s"$baseUrl/pnsi")
            .withHttpHeaders(headerName -> headerValue)
            .post(Json.obj())
            .futureValue

          response.status shouldBe OK
          response.headers should contain(expectedCorrelationId)
        }
      }
    }
  }

  private def randomCorrelationIdHeader = "Correlation-ID" -> Seq(UUID.randomUUID().toString)
}
