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

import base.JsonGenerators
import org.scalatest.Assertion
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.should
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.http.Status
import play.api.libs.ws.WSResponse
import play.api.test.WsTestClient

import java.util.UUID

class NsiControllerISpec
    extends AnyWordSpec
    with should.Matchers
    with ScalaFutures
    with IntegrationPatience
    with GuiceOneServerPerSuite
    with WsTestClient
    with Status
    with TableDrivenPropertyChecks
    with JsonGenerators {
  withClient { wsClient =>
    val contextRoot = "/tax-free-childcare-payments-nsi-stub"
    val baseUrl     = s"http://localhost:$port$contextRoot"

    val errorScenarios = Table(
      ("Expected Error Code", "Expected Status Code", "Outbound Child Payment Ref"),
      ("E0000", 400, "EEAA00000TFC"),
      ("E0001", 400, "EEBB00000TFC"),
      ("E0002", 400, "EECC00000TFC"),
      ("E0003", 400, "EEDD00000TFC"),
      ("E0004", 400, "EEEE00000TFC"),
      ("E0005", 400, "EEFF00000TFC"),
      ("E0006", 400, "EEGG00000TFC"),
      ("E0007", 400, "EEHH00000TFC"),
      ("E0008", 400, "EEII00000TFC"),
      ("E0009", 400, "EEJJ00000TFC"),
      ("E0010", 400, "EEKK00000TFC"),
      ("E0020", 400, "EELL00000TFC"),
      ("E0021", 400, "EEMM00000TFC"),
      ("E0022", 400, "EENN00000TFC"),
      ("E0024", 400, "EEOO00000TFC"),
      ("E9000", 500, "EEPP00000TFC"),
      ("E9999", 500, "EEQQ00000TFC"),
      ("E8000", 503, "EERR00000TFC"),
      ("E8001", 503, "EESS00000TFC")
    )

    def forAllScenariosWithValidRequest(resource: String, expectedCorrelationId: UUID)(check: WSResponse => Assertion) =
      check(
        wsClient
          .url(s"$baseUrl$resource")
          .withHttpHeaders("Correlation-ID" -> expectedCorrelationId.toString)
          .post(sharedRequestData("AAAA00000TFC"))
          .futureValue
      )

    val link_url = "/link"
    s"POST $link_url" should {
      s"respond $OK and echo the correlation ID in the response header" when {
        "request contains a valid correlation ID header and expected JSON fields are present and NINO ends in [A-D]" in {
          val expectedCorrelationId = UUID.randomUUID()

          forAllScenariosWithValidRequest(link_url, expectedCorrelationId) { response =>
            response.status shouldBe OK
          }
        }
      }

      forAll(errorScenarios) { (expectedErrorCode, expectedStatusCode, outboundChildPaymentRef) =>
        s"respond with status code $expectedStatusCode" when {
          s"given outbound child payment reference $outboundChildPaymentRef" in {
            val response =
              wsClient
                .url(s"$baseUrl$link_url")
                .withHttpHeaders("Correlation-ID" -> UUID.randomUUID().toString)
                .post(sharedRequestData(outboundChildPaymentRef))
                .futureValue

            val actualErrorCode = (response.json \ "errorCode").as[String]

            response.status shouldBe expectedStatusCode
            actualErrorCode shouldBe expectedErrorCode
          }
        }
      }
    }

    val balance_url = "/balance"
    s"POST $balance_url" should {
      s"respond $OK and echo the correlation ID in the response header" when {
        "request contains valid correlation ID header" in {
          val expectedCorrelationId = UUID.randomUUID()

          forAllScenariosWithValidRequest(balance_url, expectedCorrelationId) { response =>
            response.status shouldBe OK
          }
        }

        forAll(errorScenarios) { (expectedErrorCode, expectedStatusCode, outboundChildPaymentRef) =>
          s"respond with status code $expectedStatusCode" when {
            s"given outbound child payment reference $outboundChildPaymentRef" in {
              val response =
                wsClient
                  .url(s"$baseUrl$balance_url")
                  .withHttpHeaders("Correlation-ID" -> UUID.randomUUID().toString)
                  .post(sharedRequestData(outboundChildPaymentRef))
                  .futureValue

              val actualErrorCode = (response.json \ "errorCode").as[String]

              response.status shouldBe expectedStatusCode
              actualErrorCode shouldBe expectedErrorCode
            }
          }
        }
      }
    }

    val payment_url = "/"
    s"POST $payment_url" should {
      s"respond $OK and echo the correlation ID in the response header" when {
        "request contains valid correlation ID header" in {
          val expectedCorrelationId = UUID.randomUUID()

          forAllScenariosWithValidRequest(payment_url, expectedCorrelationId) { response =>
            response.status shouldBe OK
          }
        }
      }

      forAll(errorScenarios) { (expectedErrorCode, expectedStatusCode, outboundChildPaymentRef) =>
        s"respond with status code $expectedStatusCode" when {
          s"given outbound child payment reference $outboundChildPaymentRef" in {
            val response =
              wsClient
                .url(s"$baseUrl$payment_url")
                .withHttpHeaders("Correlation-ID" -> UUID.randomUUID().toString)
                .post(sharedRequestData(outboundChildPaymentRef))
                .futureValue

            val actualErrorCode = (response.json \ "errorCode").as[String]

            response.status shouldBe expectedStatusCode
            actualErrorCode shouldBe expectedErrorCode
          }
        }
      }
    }
  }
}
