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
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.should
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.http.Status
import play.api.test.WsTestClient

class NsiControllerISpec
    extends AnyWordSpec
    with should.Matchers
    with ScalaFutures
    with IntegrationPatience
    with GuiceOneServerPerSuite
    with WsTestClient
    with Status
    with TableDrivenPropertyChecks
    with JsonGenerators
    with ScalaCheckPropertyChecks {
  private val contextRoot = "/tax-free-childcare-payments-nsi-stub"
  private val baseUrl     = s"http://localhost:$port$contextRoot"

  private val CORRELATION_ID = "correlationId"

  private val errorScenarios = Table(
    ("Expected Error Code", "Expected Status Code", "Outbound Child Payment Ref"),
    ("E0000",               BAD_REQUEST,            "EEAA00000TFC"),
    ("E0001",               NOT_FOUND,              "EEBL00000TFC"),
    ("E0001",               NOT_FOUND,              "EEBB00000TFC"),
    ("E0001",               BAD_REQUEST,            "EEBP00000TFC"),
    ("E0002",               BAD_REQUEST,            "EECC00000TFC"),
    ("E0003",               BAD_REQUEST,            "EEDD00000TFC"),
    ("E0004",               BAD_REQUEST,            "EEEE00000TFC"),
    ("E0005",               BAD_REQUEST,            "EEFF00000TFC"),
    ("E0006",               BAD_REQUEST,            "EEGG00000TFC"),
    ("E0007",               BAD_REQUEST,            "EEHH00000TFC"),
    ("E0008",               BAD_REQUEST,            "EEII00000TFC"),
    ("E0009",               BAD_REQUEST,            "EEIJ00000TFC"),
    ("E0020",               BAD_REQUEST,            "EELL00000TFC"),
    ("E0021",               BAD_REQUEST,            "EEMM00000TFC"),
    ("E0022",               BAD_REQUEST,            "EENN00000TFC"),
    ("E0023",               BAD_REQUEST,            "EEOO00000TFC"),
    ("E0024",               BAD_REQUEST,            "EEPP00000TFC"),
    ("E0025",               BAD_REQUEST,            "EEQQ00000TFC"),
    ("E0026",               BAD_REQUEST,            "EERR00000TFC"),
    ("E0027",               BAD_REQUEST,            "EERS00000TFC"),
    ("E0401",               UNAUTHORIZED,           "EESS00000TFC"),
    ("E0030",               FORBIDDEN,              "EETT00000TFC"),
    ("E0031",               FORBIDDEN,              "EEUU00000TFC"),
    ("E0032",               FORBIDDEN,              "EEVV00000TFC"),
    ("E0033",               FORBIDDEN,              "EEWW00000TFC"),
    ("E0034",               FORBIDDEN,              "EEXX00000TFC"),
    ("E0035",               FORBIDDEN,              "EEYY00000TFC"),
    ("E0036",               FORBIDDEN,              "EEYZ00000TFC"),
    ("E0040",               NOT_FOUND,              "EEZZ00000TFC"),
    ("E0041",               NOT_FOUND,              "EEBA00000TFC"),
    ("E0042",               NOT_FOUND,              "EEBC00000TFC"),
    ("E0043",               NOT_FOUND,              "EEBD00000TFC"),
    ("E9000",               INTERNAL_SERVER_ERROR,  "EEBE00000TFC"),
    ("E9999",               INTERNAL_SERVER_ERROR,  "EEBF00000TFC"),
    ("E8000",               SERVICE_UNAVAILABLE,    "EEBG00000TFC"),
    ("E8001",               SERVICE_UNAVAILABLE,    "EEBH00000TFC")
  )

  val link_url = "/account/v1/accounts/link-to-epp"
  s"POST $link_url/:ref" should {
    s"respond $OK and echo the correlation ID in the response header" when {
      "request contains a valid correlation ID header and expected JSON fields are present and account ref starts with AAAA, AABB, AACC, or AADD" in
        withClient { ws =>
          forAll(LinkAccountsScenario.random) { scenario =>
            val response = ws
              .url(s"$baseUrl$link_url/${scenario.account_ref}?${scenario.queryString}")
              .withHttpHeaders(CORRELATION_ID -> scenario.correlation_id.toString)
              .get()
              .futureValue

            response.status shouldBe CREATED

            val jsonResult = response.json validate LinkAccountsScenario.expectedResponseFormat
            assert(jsonResult.isSuccess)
          }
        }
    }

    forAll(errorScenarios) { (expectedErrorCode, expectedStatusCode, accountRef) =>
      s"respond with status code $expectedStatusCode" when {
        s"given child payment ref $accountRef" in withClient { ws =>
          forAll(LinkAccountsScenario withFixedAccountRef accountRef) { scenario =>
            val response = ws
              .url(s"$baseUrl$link_url/${scenario.account_ref}?${scenario.queryString}")
              .withHttpHeaders(CORRELATION_ID -> scenario.correlation_id.toString)
              .get()
              .futureValue

            val actualErrorCode = (response.json \ "errorCode").as[String]

            response.status shouldBe expectedStatusCode
            actualErrorCode shouldBe expectedErrorCode
          }
        }
      }
    }

    "respond with status 400 and errorCode E0000" when {
      "account ref is outside pre-baked scenarios" in withClient { ws =>
        forAll(LinkAccountsScenario withFixedAccountRef "aaaa") { scenario =>
          val response = ws
            .url(s"$baseUrl$link_url/${scenario.account_ref}?${scenario.queryString}")
            .withHttpHeaders(CORRELATION_ID -> scenario.correlation_id.toString)
            .get()
            .futureValue

          val actualErrorCode = (response.json \ "errorCode").as[String]

          response.status shouldBe BAD_REQUEST
          actualErrorCode shouldBe "E0000"
        }
      }
    }
  }

  val balance_url = "/account/v1/accounts/balance"
  s"GET $balance_url" should {
    s"respond $OK and echo the correlation ID in the response header" when {
      "request contains valid correlation ID header and account ref starts with AAAA, AABB, AACC, or AADD" in withClient { ws =>
        forAll(CheckBalanceScenario.random) { scenario =>
          val response = ws
            .url(s"$baseUrl$balance_url/${scenario.account_ref}?${scenario.queryString}")
            .withHttpHeaders(CORRELATION_ID -> scenario.correlation_id.toString)
            .get()
            .futureValue

          response.status shouldBe OK

          val jsonResult = response.json validate CheckBalanceScenario.expectedResponseFormat
          assert(jsonResult.isSuccess)
        }
      }
    }

    forAll(errorScenarios) { (expectedErrorCode, expectedStatusCode, accountRef) =>
      s"respond with status code $expectedStatusCode" when {
        s"given child payment ref $accountRef" in withClient { ws =>
          forAll(CheckBalanceScenario withFixedAccountRef accountRef) { scenario =>
            val response =
              ws
                .url(s"$baseUrl$balance_url/${scenario.account_ref}?${scenario.queryString}")
                .withHttpHeaders(CORRELATION_ID -> scenario.correlation_id.toString)
                .get()
                .futureValue

            val actualErrorCode = (response.json \ "errorCode").as[String]

            response.status shouldBe expectedStatusCode
            actualErrorCode shouldBe expectedErrorCode
          }
        }
      }
    }

    "respond with status 400 and errorCode E0000" when {
      "account ref is outside pre-baked scenarios" in withClient { ws =>
        forAll(CheckBalanceScenario withFixedAccountRef "aaaa") { scenario =>
          val response = ws
            .url(s"$baseUrl$balance_url/${scenario.account_ref}?${scenario.queryString}")
            .withHttpHeaders(CORRELATION_ID -> scenario.correlation_id.toString)
            .get()
            .futureValue

          val actualErrorCode = (response.json \ "errorCode").as[String]

          response.status shouldBe BAD_REQUEST
          actualErrorCode shouldBe "E0000"
        }
      }
    }
  }

  val payment_url = "/payment/v1/payments/pay-childcare"
  s"POST $payment_url" should {
    s"respond $CREATED and echo the correlation ID in the response header" when {
      "request contains valid correlation ID header" in withClient { ws =>
        forAll(MakePaymentScenario.random) { scenario =>
          val response = ws
            .url(s"$baseUrl$payment_url")
            .withHttpHeaders(CORRELATION_ID -> scenario.correlation_id.toString)
            .post(scenario.requestBody)
            .futureValue

          response.status shouldBe CREATED

          val jsonResult = response.json validate MakePaymentScenario.expectedResponseFormat
          assert(jsonResult.isSuccess)
        }
      }
    }

    forAll(errorScenarios) { (expectedErrorCode, expectedStatusCode, accountRef) =>
      s"respond with status code $expectedStatusCode" when {
        s"given child payment ref $accountRef" in withClient { ws =>
          forAll(MakePaymentScenario withFixedAccountRef accountRef) { scenario =>
            val response = ws
              .url(s"$baseUrl$payment_url")
              .withHttpHeaders(CORRELATION_ID -> scenario.correlation_id.toString)
              .post(scenario.requestBody)
              .futureValue

            val actualErrorCode = (response.json \ "errorCode").as[String]

            response.status shouldBe expectedStatusCode
            actualErrorCode shouldBe expectedErrorCode
          }
        }
      }
    }

    "respond with status 400 and errorCode E0000" when {
      "account ref is outside pre-baked scenarios" in withClient { ws =>
        forAll(MakePaymentScenario withFixedAccountRef "aaaa") { scenario =>
          val response = ws
            .url(s"$baseUrl$payment_url")
            .withHttpHeaders(CORRELATION_ID -> scenario.correlation_id.toString)
            .post(scenario.requestBody)
            .futureValue

          val actualErrorCode = (response.json \ "errorCode").as[String]

          response.status shouldBe BAD_REQUEST
          actualErrorCode shouldBe "E0000"
        }
      }
    }
  }
}
