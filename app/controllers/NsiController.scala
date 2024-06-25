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

import models.ErrorResponse.Code._
import models.request.LinkAccountsRequest
import models.response.LinkAccountsResponse
import models.{AuthenticationData, ErrorResponse}
import play.api.Logging
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.time.LocalDate
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import scala.util.Random

@Singleton
class NsiController @Inject() (
    cc: ControllerComponents,
    correlate: CorrelationIdAction
  ) extends BackendController(cc) with Logging {
  import NsiController._

  def link(accountRef: String): Action[JsValue] = correlate(parse.json).async { implicit req =>
    withJsonBody { body: LinkAccountsRequest =>
      Future.successful(
        Created(
          Json.toJson(
            LinkAccountsResponse(
              testChildren(body.parent_nino.last)
            )
          )
        )
      )
    }
  }

  def balance(accountRef: String, authData: AuthenticationData): Action[AnyContent] = correlate {
    Ok(Json.obj(
      "tfc_account_status" -> "active",
      "paid_in_by_you"     -> randomSumOfMoney,
      "government_top_up"  -> randomSumOfMoney,
      "total_balance"      -> randomSumOfMoney,
      "cleared_funds"      -> randomSumOfMoney,
      "top_up_allowance"   -> randomSumOfMoney
    ))
  }

  def payment(): Action[JsValue] = correlate(parse.json) { _ =>
    Ok(Json.obj(
      "payment_reference"      -> randomPaymentRef,
      "estimated_payment_date" -> randomDate
    ))
  }

  private def withNsiErrorScenarios(authData: AuthenticationData)(block: AuthenticationData => Result) =
    Future.successful {
      testErrorScenarios get authData.parent_nino match {
        case Some(nsiErrorCode) =>
          new Status(nsiErrorCode.statusCode)(
            Json.toJson(
              ErrorResponse(nsiErrorCode, "asdf")
            )
          )
        case None               => block(authData)
      }
    }
}

object NsiController {
  private def randomSumOfMoney = BigDecimal(Random.nextInt(MAX_SUM_OF_MONEY_PENCE)).setScale(2) / 100
  private def randomDate       = LocalDate.now() plusDays randomPaymentDelayDays
  private def randomPaymentRef = Array.fill(PAYMENT_REF_LENGTH)(randomDigit).mkString

  @inline private def randomPaymentDelayDays = Random.nextInt(30)
  @inline private def randomDigit            = Random.nextInt(10)

  private val MAX_SUM_OF_MONEY_PENCE = 100000
  private val PAYMENT_REF_LENGTH     = 16

  private val testChildren = Map(
    'A' -> "Peter Pan",
    'B' -> "Benjamin Button",
    'C' -> "Christopher Columbus",
    'D' -> "Donald Duck"
  )

  private val testErrorScenarios = Map(
    "AA110000A" -> E0000,
    "AA110001A" -> E0001,
    "AA110002A" -> E0002,
    "AA110003A" -> E0003,
    "AA110004A" -> E0004,
    "AA110005A" -> E0005,
    "AA110006A" -> E0006,
    "AA110007A" -> E0007,
    "AA110008A" -> E0008,
    "AA110009A" -> E0009,
    "AA110010A" -> E0010,
    "AA110020A" -> E0020,
    "AA110021A" -> E0021,
    "AA110022A" -> E0022,
    "AA110024A" -> E0024,
    "AA119000A" -> E9000,
    "AA119999A" -> E9999,
    "AA118000A" -> E8000,
    "AA118001A" -> E8001
  )
}
