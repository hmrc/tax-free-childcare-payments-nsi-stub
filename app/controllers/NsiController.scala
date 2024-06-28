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

import java.time.LocalDate
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future
import scala.util.Random
import models.ErrorResponse.Code._
import models.{ErrorResponse, SharedRequestData}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

@Singleton
class NsiController @Inject() (
    cc: ControllerComponents,
    correlate: CorrelationIdAction
  ) extends BackendController(cc) {
  import NsiController._

  def link(): Action[JsValue] = withNsiErrorScenarios { sharedRequestData =>
    Ok(Json.obj(
      "child_full_name" -> testChildren(sharedRequestData.outbound_child_payment_ref.take(SUPPORTED_PATTERN))
    ))
  }

  def balance(): Action[JsValue] = withNsiErrorScenarios { _ =>
    Ok(Json.obj(
      "tfc_account_status" -> "active",
      "paid_in_by_you"     -> randomSumOfMoney,
      "government_top_up"  -> randomSumOfMoney,
      "total_balance"      -> randomSumOfMoney,
      "cleared_funds"      -> randomSumOfMoney,
      "top_up_allowance"   -> randomSumOfMoney
    ))
  }

  def payment(): Action[JsValue] = withNsiErrorScenarios { _ =>
    Ok(Json.obj(
      "payment_reference"      -> randomPaymentRef,
      "estimated_payment_date" -> randomDate
    ))
  }

  private def withNsiErrorScenarios(block: SharedRequestData => Result) =
    correlate.async(parse.json) { implicit request =>
      withJsonBody[SharedRequestData] { sharedRequestData =>
        Future.successful(
          testErrorScenarios get sharedRequestData.outbound_child_payment_ref.take(SUPPORTED_PATTERN) match {
            case Some(nsiErrorCode) =>
              new Status(nsiErrorCode.statusCode)(
                Json.toJson(
                  ErrorResponse(nsiErrorCode, "asdf")
                )
              )
            case None               => block(sharedRequestData)
          }
        )
      }
    }
}

object NsiController {
  private def randomSumOfMoney = BigDecimal(Random.nextInt(MAX_SUM_OF_MONEY_PENCE)).setScale(2) / 100
  private def randomDate       = LocalDate.now() plusDays randomPaymentDelayDays
  private def randomPaymentRef = Array.fill(PAYMENT_REF_LENGTH)(randomDigit).mkString

  private val SUPPORTED_PATTERN = 4

  @inline private def randomPaymentDelayDays = Random.nextInt(30)
  @inline private def randomDigit            = Random.nextInt(10)

  private val MAX_SUM_OF_MONEY_PENCE = 100000
  private val PAYMENT_REF_LENGTH     = 16

  private val testChildren = Map(
    "AAAA" -> "Peter Pan",
    "AABB" -> "Benjamin Button",
    "AACC" -> "Christopher Columbus",
    "AADD" -> "Donald Duck"
  )

  private val testErrorScenarios = Map(
    "EEAA" -> E0000,
    "EEBB" -> E0001,
    "EECC" -> E0002,
    "EEDD" -> E0003,
    "EEEE" -> E0004,
    "EEFF" -> E0005,
    "EEGG" -> E0006,
    "EEHH" -> E0007,
    "EEII" -> E0008,
    "EEJJ" -> E0009,
    "EEKK" -> E0010,
    "EELL" -> E0020,
    "EEMM" -> E0021,
    "EENN" -> E0022,
    "EEOO" -> E0024,
    "EEPP" -> E9000,
    "EEQQ" -> E9999,
    "EERR" -> E8000,
    "EESS" -> E8001
  )
}
