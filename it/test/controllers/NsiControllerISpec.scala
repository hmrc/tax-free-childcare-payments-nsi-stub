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
      ("Expected Error Code", "Expected Status Code", "NI Number"),
      ("E0000", 400, "AA110000A"),
      ("E0001", 400, "AA110001A"),
      ("E0002", 400, "AA110002A"),
      ("E0003", 400, "AA110003A"),
      ("E0004", 400, "AA110004A"),
      ("E0005", 400, "AA110005A"),
      ("E0006", 400, "AA110006A"),
      ("E0007", 400, "AA110007A"),
      ("E0008", 400, "AA110008A"),
      ("E0009", 400, "AA110009A"),
      ("E0010", 400, "AA110010A"),
      ("E0020", 400, "AA110020A"),
      ("E0021", 400, "AA110021A"),
      ("E0022", 400, "AA110022A"),
      ("E0024", 400, "AA110024A"),
      ("E9000", 500, "AA119000A"),
      ("E9999", 500, "AA119999A"),
      ("E8000", 503, "AA118000A"),
      ("E8001", 503, "AA118001A")
    )

    def forAllScenariosWithValidRequest(resource: String, expectedCorrelationId: UUID)(check: WSResponse => Assertion) =
      check(
        wsClient
          .url(s"$baseUrl$resource")
          .withHttpHeaders("Correlation-ID" -> expectedCorrelationId.toString)
          .post(randomSharedRequestDataJsonWithNino("BB001111B"))
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

      forAll(errorScenarios) { (expectedErrorCode, expectedStatusCode, nino) =>
        s"respond with status code $expectedStatusCode" when {
          s"given nino $nino" in {
            val response =
              wsClient
                .url(s"$baseUrl$link_url")
                .withHttpHeaders("Correlation-ID" -> UUID.randomUUID().toString)
                .post(randomSharedRequestDataJsonWithNino(nino))
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

        forAll(errorScenarios) { (expectedErrorCode, expectedStatusCode, nino) =>
          s"respond with status code $expectedStatusCode" when {
            s"given nino $nino" in {
              val response =
                wsClient
                  .url(s"$baseUrl$balance_url")
                  .withHttpHeaders("Correlation-ID" -> UUID.randomUUID().toString)
                  .post(randomSharedRequestDataJsonWithNino(nino))
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

      forAll(errorScenarios) { (expectedErrorCode, expectedStatusCode, nino) =>
        s"respond with status code $expectedStatusCode" when {
          s"given nino $nino" in {
            val response =
              wsClient
                .url(s"$baseUrl$payment_url")
                .withHttpHeaders("Correlation-ID" -> UUID.randomUUID().toString)
                .post(randomSharedRequestDataJsonWithNino(nino))
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
