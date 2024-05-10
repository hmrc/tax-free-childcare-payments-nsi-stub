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

import play.api.libs.json.{Json, Reads}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import java.time.LocalDate
import javax.inject.{Inject, Singleton}
import scala.util.Random

final case class EnrichedLinkRequest(
    epp_unique_customer_id: String,
    epp_reg_reference: String,
    outbound_child_payment_ref: String,
    child_date_of_birth: String,
    nino: String
  )

object EnrichedLinkRequest {
  implicit val reads: Reads[EnrichedLinkRequest] = Json.reads
}

@Singleton
class NsiController @Inject() (
    cc: ControllerComponents,
    correlate: CorrelatedAction
  ) extends BackendController(cc) {

  def link(): Action[EnrichedLinkRequest] = correlate(parse.json[EnrichedLinkRequest]) { request =>
    Ok(Json.obj(
      "child_full_name" -> testData(request.body.nino.last)
    ))
  }

  def balance(): Action[AnyContent] = correlate {
    Ok(Json.obj(
      "tfc_account_status" -> "active",
      "paid_in_by_you"     -> randomSumOfMoney,
      "government_top_up"  -> randomSumOfMoney,
      "total_balance"      -> randomSumOfMoney,
      "cleared_funds"      -> randomSumOfMoney,
      "top_up_allowance"   -> randomSumOfMoney
    ))
  }

  def payment(): Action[AnyContent] = correlate {
    Ok(Json.obj(
      "payment_reference"      -> randomPaymentRef,
      "estimated_payment_date" -> randomDate
    ))
  }

  private def randomSumOfMoney = BigDecimal(Random.nextInt(MAX_SUM_OF_MONEY_PENCE)).setScale(2) / 100
  private def randomDate       = LocalDate.now() plusDays randomPaymentDelayDays
  private def randomPaymentRef = Array.fill(PAYMENT_REF_LENGTH)(randomDigit).mkString

  @inline private def randomPaymentDelayDays = Random.nextInt(30)
  @inline private def randomDigit            = Random.nextInt(10)

  private val MAX_SUM_OF_MONEY_PENCE = 100000
  private val PAYMENT_REF_LENGTH     = 16

  private val testData = Map(
    'A' -> "Peter Pan",
    'B' -> "Benjamin Button",
    'C' -> "Christopher Columbus",
    'D' -> "Donald Duck"
  )
}
