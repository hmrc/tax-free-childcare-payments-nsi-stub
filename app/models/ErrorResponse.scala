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
    case class CodeVal(status: Int, errorCode: String, errorDescription: String) extends super.Val

    val E0000: CodeVal        = CodeVal(400, "E0000", "Invalid Input Data")
    val E0001Link: CodeVal    = CodeVal(404, "E0001", "childAccountPaymentRef is missing")
    val E0001Balance: CodeVal = CodeVal(404, "E0001", "childAccountPaymentRef is missing")
    val E0001Payment: CodeVal = CodeVal(400, "E0001", "childAccountPaymentRef is missing")
    val E0002: CodeVal        = CodeVal(500, "E0002", "--missing-error-description--")
    val E0003: CodeVal        = CodeVal(500, "E0003", "--missing-error-description--")
    val E0004: CodeVal        = CodeVal(500, "E0004", "--missing-error-description--")
    val E0005: CodeVal        = CodeVal(500, "E0005", "--missing-error-description--")
    val E0006: CodeVal        = CodeVal(500, "E0006", "--missing-error-description--")
    val E0007: CodeVal        = CodeVal(500, "E0007", "--missing-error-description--")
    val E0008: CodeVal        = CodeVal(500, "E0008", "--missing-error-description--")

    val E0020: CodeVal = CodeVal(502, "E0020", "--missing-error-description--")
    val E0021: CodeVal = CodeVal(500, "E0021", "--missing-error-description--")
    val E0022: CodeVal = CodeVal(500, "E0022", "--missing-error-description--")
    val E0023: CodeVal = CodeVal(500, "E0023", "--missing-error-description--")
    val E0024: CodeVal = CodeVal(400, "E0024", "--missing-error-description--")
    val E0025: CodeVal = CodeVal(400, "E0025", "--missing-error-description--")
    val E0026: CodeVal = CodeVal(400, "E0026", "--missing-error-description--")

    val E0401: CodeVal = CodeVal(500, "E0401", "--missing-error-description--")

    val E0030: CodeVal = CodeVal(400, "E0030", "--missing-error-description--")
    val E0031: CodeVal = CodeVal(400, "E0031", "--missing-error-description--")
    val E0032: CodeVal = CodeVal(400, "E0032", "--missing-error-description--")
    val E0033: CodeVal = CodeVal(400, "E0033", "--missing-error-description--")
    val E0034: CodeVal = CodeVal(503, "E0034", "--missing-error-description--")
    val E0035: CodeVal = CodeVal(400, "E0035", "--missing-error-description--")
    val E0040: CodeVal = CodeVal(400, "E0040", "--missing-error-description--")
    val E0041: CodeVal = CodeVal(400, "E0041", "--missing-error-description--")
    val E0042: CodeVal = CodeVal(400, "E0042", "--missing-error-description--")
    val E0043: CodeVal = CodeVal(400, "E0043", "--missing-error-description--")

    val E9000: CodeVal = CodeVal(503, "E9000", "--missing-error-description--")
    val E9999: CodeVal = CodeVal(503, "E9999", "--missing-error-description--")

    val E8000: CodeVal = CodeVal(503, "E8000", "--missing-error-description--")
    val E8001: CodeVal = CodeVal(503, "E8001", "--missing-error-description--")

    val UNKNOWN: CodeVal = CodeVal(400, "UNKNOWN", "--missing-error-description--")

    implicit val writes: Writes[CodeVal] = {
      case CodeVal(_, errorCode, errorDesc) => Json.obj(
          "errorCode"        -> errorCode,
          "errorDescription" -> errorDesc
        )
    }
  }

  implicit val writes: Writes[ErrorResponse] = Json.writes
}
