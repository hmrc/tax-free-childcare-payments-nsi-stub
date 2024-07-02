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
      ("E0000", 500, "EEAA00000TFC"),
      ("E0001", 500, "EEBB00000TFC"),
      ("E0002", 500, "EECC00000TFC"),
      ("E0003", 500, "EEDD00000TFC"),
      ("E0004", 500, "EEEE00000TFC"),
      ("E0005", 500, "EEFF00000TFC"),
      ("E0006", 500, "EEGG00000TFC"),
      ("E0007", 500, "EEHH00000TFC"),
      ("E0008", 500, "EEII00000TFC"),

      ("E0020", 502, "EELL00000TFC"),
      ("E0021", 500, "EEMM00000TFC"),
      ("E0022", 500, "EENN00000TFC"),
      ("E0023", 500, "EEOO00000TFC"),
      ("E0024", 400, "EEPP00000TFC"),
      ("E0025", 400, "EEQQ00000TFC"),
      ("E0026", 400, "EERR00000TFC"),

      ("E0401", 500, "EESS00000TFC"),

      ("E0030", 400, "EETT00000TFC"),
      ("E0031", 400, "EEUU00000TFC"),
      ("E0032", 400, "EEVV00000TFC"),
      ("E0033", 400, "EEWW00000TFC"),
      ("E0034", 503, "EEXX00000TFC"),
      ("E0035", 400, "EEYY00000TFC"),

      ("E0040", 400, "EEZZ00000TFC"),
      ("E0041", 400, "EEBA00000TFC"),
      ("E0042", 400, "EEBC00000TFC"),
      ("E0043", 400, "EEBD00000TFC"),

      ("E9000", 503, "EEBE00000TFC"),
      ("E9999", 503, "EEBF00000TFC"),
      ("E8000", 503, "EEBG00000TFC"),
      ("E8001", 503, "EEBH00000TFC")
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
