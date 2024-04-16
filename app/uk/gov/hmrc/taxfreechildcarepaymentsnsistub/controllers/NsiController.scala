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

package uk.gov.hmrc.taxfreechildcarepaymentsnsistub.controllers

import play.api.libs.json.{Json, OFormat}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

final case class LinkResponse(correlationId: String, child_full_name: String)

object LinkResponse {
  implicit lazy val format: OFormat[LinkResponse] = Json.format
}
final case class EnrichedLinkRequest(
                                      correlationId: String,
                                      epp_unique_customer_id: String,
                                      epp_reg_reference: String,
                                      outbound_child_payment_ref: String,
                                      child_date_of_birth: String,
                                      nino: String
                                    )

object EnrichedLinkRequest {
  implicit lazy val format: OFormat[EnrichedLinkRequest] = Json.format
}

@Singleton()
class NsiController @Inject() (cc: ControllerComponents)(implicit ec: ExecutionContext)
    extends BackendController(cc) {

  def link(): Action[AnyContent] = Action.async { implicit request =>
    val va = request.body.asJson
    println(va.get("nino"))
    va match {
      case Some(x)  => println(x)
      case None     => println("dont know what to do")
    }
    val incomingRequest = parse.json[EnrichedLinkRequest]
    println(incomingRequest.toString())
    incomingRequest.map(f => {
      println(f.nino)
      println(f.correlationId)
//      Future.successful(Ok(Json.toJson(LinkResponse(f.correlationId, "Peter Pan"))))
    })
    Future.successful(Ok(Json.toJson(LinkResponse(va.get("correlationId").toString(), "Peter Pan"))))
  }

  def balance(): Action[AnyContent] = Action.async {
    Future.successful(Ok("balance  is wip"))
  }

  def payment(): Action[AnyContent] = Action.async {
    Future.successful(Ok("payment is wip"))
  }
}
