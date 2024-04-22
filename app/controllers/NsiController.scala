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

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

import play.api.libs.json.{JsValue, Json, OFormat}
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

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

  def link(): Action[JsValue] = Action(parse.json) { request =>
    (request.body \ "correlationId").asOpt[String].map { value =>
      Ok(Json.toJson(
        Map("correlationId" -> value, "child_full_name" -> "Peter Pan")
      ))
    }.getOrElse {
      BadRequest(Json.toJson(
        Map("message" -> "Error - Missing parameter [nino]")
      ))
    }
  }

  def balance(): Action[AnyContent] = Action.async {
    Future.successful(Ok("balance  is wip"))
  }

  def payment(): Action[AnyContent] = Action.async {
    Future.successful(Ok("payment is wip"))
  }
}
