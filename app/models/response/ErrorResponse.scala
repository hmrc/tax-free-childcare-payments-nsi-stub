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

package models.response

import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.mvc.Results.Status

case class ErrorResponse(status: Status, code: String, desc: String) {

  def toResult: Result = status(
    Json.obj(
      "errorCode"        -> code,
      "errorDescription" -> desc
    )
  )
}

object ErrorResponse {

  def parse(string: String): Option[ErrorResponse] =
    string.split("\\|").map(_.trim).toList match {
      case status :: code :: desc :: _ => Some(apply(Status(status.toInt), code, desc))
      case _                           => None
    }
}
