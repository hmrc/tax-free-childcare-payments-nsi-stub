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
import models.response.{CheckBalanceResponse, MakePaymentResponse}
import models.response.CheckBalanceResponse.AccountStatus
import org.scalacheck.Gen
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.should
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.http.Status
import play.api.libs.json.{JsDefined, JsString, Json}
import play.api.test.WsTestClient

import java.time.LocalDate
import java.util.UUID

class NsiControllerISpec
    extends AnyWordSpec
    with should.Matchers
    with OptionValues
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
    ("Outbound Child Payment Ref", "Expected Status Code", "Expected Error Code", "Expected Error Description"),
    ("EEAA00000TFC",               BAD_REQUEST,            "E0000",               "Invalid input data"),
    ("EEBL00000TFC",               NOT_FOUND,              "E0001",               "childAccountPaymentRef is missing"),
    ("EEBB00000TFC",               NOT_FOUND,              "E0001",               "childAccountPaymentRef is missing"),
    ("EEBP00000TFC",               BAD_REQUEST,            "E0001",               "childAccountPaymentRef is missing"),
    ("EECC00000TFC",               BAD_REQUEST,            "E0002",               "eppURN is missing"),
    ("EEDD00000TFC",               BAD_REQUEST,            "E0003",               "ccpURN is missing"),
    ("EEEE00000TFC",               BAD_REQUEST,            "E0004",               "eppAccount is missing"),
    ("EEFF00000TFC",               BAD_REQUEST,            "E0005",               "parentNino is missing"),
    ("EEGG00000TFC",               BAD_REQUEST,            "E0006",               "childDob is missing"),
    ("EEHH00000TFC",               BAD_REQUEST,            "E0007",               "payeeType is missing"),
    ("EEII00000TFC",               BAD_REQUEST,            "E0008",               "amount is missing"),
    ("EEIJ00000TFC",               BAD_REQUEST,            "E0009",               "ccpPostcode is missing"),
    ("EELL00000TFC",               BAD_REQUEST,            "E0020",               "parentNino does not match expected format (AANNNNNNA)"),
    ("EEMM00000TFC",               BAD_REQUEST,            "E0021",               "childDob does not match expected format (YYYY-MM-DD)"),
    ("EENN00000TFC",               BAD_REQUEST,            "E0022",               "payeeType value should be one of ['CCP','EPP']"),
    ("EEOO00000TFC",               BAD_REQUEST,            "E0023",               "amount most be a number"),
    ("EEPP00000TFC",               BAD_REQUEST,            "E0024",               "eppAccount does not correlate with the provided eppURN"),
    ("EEQQ00000TFC",               BAD_REQUEST,            "E0025",               "childDob does not correlate with the provided childAccountPaymentRef"),
    ("EERR00000TFC",               BAD_REQUEST,            "E0026",               "childAccountPaymentRef is not related to parentNino"),
    ("EERS00000TFC",               BAD_REQUEST,            "E0027",               "CCP not linked to Child Account"),
    ("EESS00000TFC",               UNAUTHORIZED,           "E0401",               "Authentication information is missing or invalid"),
    ("EETT00000TFC",               FORBIDDEN,              "E0030",               "EPP is not Active"),
    ("EEUU00000TFC",               FORBIDDEN,              "E0031",               "CCP is not Active"),
    ("EEVV00000TFC",               FORBIDDEN,              "E0032",               "EPP is not linked to Child Account"),
    ("EEWW00000TFC",               FORBIDDEN,              "E0033",               "Insufficient funds"),
    ("EEXX00000TFC",               FORBIDDEN,              "E0034",               "Error returned from banking services"),
    ("EEYY00000TFC",               FORBIDDEN,              "E0035",               "Payments from this TFC account are blocked"),
    ("EEYZ00000TFC",               FORBIDDEN,              "E0036",               "Check Payee Bank Details"),
    ("EEBA00000TFC",               NOT_FOUND,              "E0041",               "eppURN not found"),
    ("EEBC00000TFC",               NOT_FOUND,              "E0042",               "ccpURN not found"),
    ("EEBD00000TFC",               NOT_FOUND,              "E0043",               "parentNino not found"),
    ("EEBE00000TFC",               INTERNAL_SERVER_ERROR,  "E9000",               "Internal server error"),
    ("EEBF00000TFC",               INTERNAL_SERVER_ERROR,  "E9999",               "Error during execution"),
    ("EEBG00000TFC",               SERVICE_UNAVAILABLE,    "E8000",               "Service not available"),
    ("EEBH00000TFC",               SERVICE_UNAVAILABLE,    "E8001",               "Service not available due to lack of connection to provider")
  )

  /** "EEZZ" is the corresponding account ref for E0040. This generator helps check it has been removed. */
  private val invalidAccountRefs = Gen.oneOf(
    Gen.stringOfN(4, Gen.numChar),
    Gen.stringOfN(4, Gen.alphaLowerChar),
    Gen const "EEZZ"
  )

  val link_url = "/account/v1/accounts/link-to-epp"
  s"POST $link_url/:ref" should {
    s"respond $OK and echo the correlation ID in the response header" when {
      "request contains a valid correlation ID header and expected JSON fields are present and account ref starts with AAAA, AABB, AACC, or AADD" in
        forAll(LinkAccountsScenario.random) { scenario =>
          withClient { ws =>
            val response = ws
              .url(s"$baseUrl$link_url/${scenario.account_ref}?${scenario.queryString}")
              .withHttpHeaders(CORRELATION_ID -> scenario.correlation_id.toString)
              .get()
              .futureValue

            response.status shouldBe OK

            val jsonResult = response.json validate LinkAccountsScenario.expectedResponseFormat
            assert(jsonResult.isSuccess)
          }
        }
    }

    forAll(errorScenarios) { (accountRef, expectedStatusCode, expectedErrorCode, expectedErrorDesc) =>
      s"respond with status code $expectedStatusCode" when {
        s"given child payment ref $accountRef" in
          forAll(LinkAccountsScenario withFixedAccountRef accountRef) { scenario =>
            withClient { ws =>
              val response = ws
                .url(s"$baseUrl$link_url/${scenario.account_ref}?${scenario.queryString}")
                .withHttpHeaders(CORRELATION_ID -> scenario.correlation_id.toString)
                .get()
                .futureValue

              response.status                      shouldBe expectedStatusCode
              (response.json \ "errorCode")        shouldBe JsDefined(JsString(expectedErrorCode))
              (response.json \ "errorDescription") shouldBe JsDefined(JsString(expectedErrorDesc))
            }
          }
      }
    }

    "respond with status 400 and errorCode E0000" when {
      "account ref is outside pre-baked scenarios" in
        forAll(invalidAccountRefs flatMap LinkAccountsScenario.withFixedAccountRef) { scenario =>
          withClient { ws =>
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
    s"respond 200, echo correlation ID, and return expected response body" when {
      val happyScenarios = Table(
        ("Given Account Ref", "Expected Response Body"),
        ("AAAA",              CheckBalanceResponse(AccountStatus.ACTIVE, 31415, 65, 66, 67, 68)),
        ("AABB",              CheckBalanceResponse(AccountStatus.BLOCKED, 92653, 69, 70, 71, 72)),
        ("AACC",              CheckBalanceResponse(AccountStatus.ACTIVE, 58979, 73, 74, 75, 76)),
        ("AADD",              CheckBalanceResponse(AccountStatus.ACTIVE, 32384, 77, 78, 79, 80)),
        ("AAEE",              CheckBalanceResponse(AccountStatus.UNKNOWN, 62643, 81, 82, 83, 84)),
        ("AAFF",              CheckBalanceResponse(AccountStatus.ACTIVE, 38327, 85, 86, 87, 88))
      )

      "given one of the expected account refs" in
        forAll(happyScenarios) { (givenAccountRef, expectedResponseBody) =>
          val queryString   = "parentNino=AA123456A&eppURN=1234&eppAccount=1234"
          val correlationID = UUID.randomUUID().toString

          withClient { ws =>
            val response = ws
              .url(s"$baseUrl$balance_url/$givenAccountRef?$queryString")
              .withHttpHeaders(CORRELATION_ID -> correlationID)
              .get()
              .futureValue

            response.status                       shouldBe OK
            response.header(CORRELATION_ID).value shouldBe correlationID
            response.json                         shouldBe Json.toJson(expectedResponseBody)
          }
        }
    }

    forAll(errorScenarios) { (accountRef, expectedStatusCode, expectedErrorCode, expectedErrorDesc) =>
      s"respond with status code $expectedStatusCode" when {
        s"given child payment ref $accountRef" in
          forAll(CheckBalanceScenario withFixedAccountRef accountRef) { scenario =>
            withClient { ws =>
              val response =
                ws
                  .url(s"$baseUrl$balance_url/${scenario.account_ref}?${scenario.queryString}")
                  .withHttpHeaders(CORRELATION_ID -> scenario.correlation_id.toString)
                  .get()
                  .futureValue

              response.status                      shouldBe expectedStatusCode
              (response.json \ "errorCode")        shouldBe JsDefined(JsString(expectedErrorCode))
              (response.json \ "errorDescription") shouldBe JsDefined(JsString(expectedErrorDesc))
            }
          }
      }
    }

    "respond with status 400 and errorCode E0000" when {
      "account ref is outside pre-baked scenarios" in
        forAll(invalidAccountRefs flatMap CheckBalanceScenario.withFixedAccountRef) { scenario =>
          withClient { ws =>
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
    s"respond 201, echo the correlation ID, and return the expected payload" when {
      val happyScenarios = Table(
        ("Given Account Ref", "Expected Response Body"),
        ("AAAA",              MakePaymentResponse("1234568787654321", LocalDate parse "2024-10-01")),
        ("AABB",              MakePaymentResponse("1234568787654322", LocalDate parse "2024-10-02")),
        ("AACC",              MakePaymentResponse("1234568787654323", LocalDate parse "2024-10-03")),
        ("AADD",              MakePaymentResponse("1234568787654324", LocalDate parse "2024-10-04")),
        ("AAEE",              MakePaymentResponse("1234568787654325", LocalDate parse "2024-10-05")),
        ("AAFF",              MakePaymentResponse("1234568787654326", LocalDate parse "2024-10-06"))
      )

      "given one of the expected account refs" in
        forAll(happyScenarios) { (givenAccountRef, expectedResponseBody) =>
          val requestBody   = Json.obj(
            "childAccountPaymentRef" -> givenAccountRef,
            "parentNino"             -> "AA123456A",
            "eppURN"                 -> "1234",
            "eppAccount"             -> "1234",
            "payeeType"              -> "CCP",
            "ccpURN"                 -> "1234",
            "ccpPostcode"            -> "AA1 1AA",
            "amount"                 -> 100
          )
          val correlationID = UUID.randomUUID().toString

          withClient { ws =>
            val response = ws
              .url(s"$baseUrl$payment_url")
              .withHttpHeaders(CORRELATION_ID -> correlationID)
              .post(requestBody)
              .futureValue

            response.status                       shouldBe CREATED
            response.header(CORRELATION_ID).value shouldBe correlationID
            response.json                         shouldBe Json.toJson(expectedResponseBody)
          }
        }
    }

    forAll(errorScenarios) { (accountRef, expectedStatusCode, expectedErrorCode, expectedErrorDesc) =>
      s"respond with status code $expectedStatusCode" when {
        s"given child payment ref $accountRef" in
          forAll(MakePaymentScenario withFixedAccountRef accountRef) { scenario =>
            withClient { ws =>
              val response = ws
                .url(s"$baseUrl$payment_url")
                .withHttpHeaders(CORRELATION_ID -> scenario.correlation_id.toString)
                .post(scenario.requestBody)
                .futureValue

              response.status                      shouldBe expectedStatusCode
              (response.json \ "errorCode")        shouldBe JsDefined(JsString(expectedErrorCode))
              (response.json \ "errorDescription") shouldBe JsDefined(JsString(expectedErrorDesc))
            }
          }
      }
    }

    "respond with status 400 and errorCode E0000" when {
      "account ref is outside pre-baked scenarios" in
        forAll(invalidAccountRefs flatMap MakePaymentScenario.withFixedAccountRef) { scenario =>
          withClient { ws =>
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
