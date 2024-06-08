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

package models

import models.ErrorResponse.Code

import play.api.libs.json.{Json, Writes}

final case class ErrorResponse(
    errorCode: Code.Value,
    errorDescription: String
  )

object ErrorResponse {

  object Code extends Enumeration {
    case class CodeVal(statusCode: Int) extends super.Val

    val E0000: CodeVal = CodeVal(400)
    val E0001: CodeVal = CodeVal(400)
    val E0002: CodeVal = CodeVal(400)
    val E0003: CodeVal = CodeVal(400)
    val E0004: CodeVal = CodeVal(400)
    val E0005: CodeVal = CodeVal(400)
    val E0006: CodeVal = CodeVal(400)
    val E0007: CodeVal = CodeVal(400)
    val E0008: CodeVal = CodeVal(400)
    val E0009: CodeVal = CodeVal(400)
    val E0010: CodeVal = CodeVal(400)
    val E0020: CodeVal = CodeVal(400)
    val E0021: CodeVal = CodeVal(400)
    val E0022: CodeVal = CodeVal(400)
    val E0024: CodeVal = CodeVal(400)

    val E9000: CodeVal = CodeVal(500)
    val E9999: CodeVal = CodeVal(500)

    val E8000: CodeVal = CodeVal(503)
    val E8001: CodeVal = CodeVal(503)

    implicit val writes: Writes[Code.Value] = Json.formatEnum(Code)
  }

  implicit val writes: Writes[ErrorResponse] = Json.writes
}
