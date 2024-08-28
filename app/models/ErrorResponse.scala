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

import play.api.libs.json.{Json, Writes}

object ErrorResponse extends Enumeration {
  case class CodeVal(status: Int, errorCode: String, errorDescription: String) extends super.Val

  val E0000: CodeVal = CodeVal(400, "E0000", "Invalid input data")

  val E0001Link: CodeVal    = CodeVal(404, "E0001", "childAccountPaymentRef is missing")
  val E0001Balance: CodeVal = CodeVal(404, "E0001", "childAccountPaymentRef is missing")
  val E0001Payment: CodeVal = CodeVal(400, "E0001", "childAccountPaymentRef is missing")

  val E0002: CodeVal = CodeVal(400, "E0002", "eppURN is missing")
  val E0003: CodeVal = CodeVal(400, "E0003", "ccpURN is missing")
  val E0004: CodeVal = CodeVal(400, "E0004", "eppAccount is missing")

  val E0005: CodeVal = CodeVal(400, "E0005", "parentNino is missing")
  val E0006: CodeVal = CodeVal(400, "E0006", "childDob is missing")
  val E0007: CodeVal = CodeVal(400, "E0007", "payeeType is missing")
  val E0008: CodeVal = CodeVal(400, "E0008", "amount is missing")
  val E0009: CodeVal = CodeVal(400, "E0009", "ccpPostcode is missing")

  val E0020: CodeVal = CodeVal(400, "E0020", "parentNino does not match expected format (AANNNNNNA)")
  val E0021: CodeVal = CodeVal(400, "E0021", "childDob does not match expected format (YYYY-MM-DD)")
  val E0022: CodeVal = CodeVal(400, "E0022", "payeeType value should be one of ['CCP','EPP']")
  val E0023: CodeVal = CodeVal(400, "E0023", "amount most be a number")
  val E0024: CodeVal = CodeVal(400, "E0024", "eppAccount does not correlate with the provided eppURN")
  val E0025: CodeVal = CodeVal(400, "E0025", "childDob does not correlate with the provided childAccountPaymentRef")
  val E0026: CodeVal = CodeVal(400, "E0026", "childAccountPaymentRef is not related to parentNino")
  val E0027: CodeVal = CodeVal(400, "E0027", "CCP not linked to Child Account")

  val E0401: CodeVal = CodeVal(401, "E0401", "Authentication information is missing or invalid")

  val E0030: CodeVal = CodeVal(403, "E0030", "EPP is not Active")
  val E0031: CodeVal = CodeVal(403, "E0031", "CCP is not Active")
  val E0032: CodeVal = CodeVal(403, "E0032", "EPP is not linked to Child Account")
  val E0033: CodeVal = CodeVal(403, "E0033", "Insufficient funds")
  val E0034: CodeVal = CodeVal(403, "E0034", "Error returned from banking services")
  val E0035: CodeVal = CodeVal(403, "E0035", "Payments from this TFC account are blocked")
  val E0036: CodeVal = CodeVal(403, "E0036", "Check Payee Bank Details")

  val E0041: CodeVal = CodeVal(404, "E0041", "eppURN not found")
  val E0042: CodeVal = CodeVal(404, "E0042", "ccpURN not found")
  val E0043: CodeVal = CodeVal(404, "E0043", "parentNino not found")

  val E9000: CodeVal = CodeVal(500, "E9000", "Internal server error")
  val E9999: CodeVal = CodeVal(500, "E9999", "Error during execution")

  val E8000: CodeVal = CodeVal(503, "E8000", "Service not available")
  val E8001: CodeVal = CodeVal(503, "E8001", "Service not available due to lack of connection to provider")

  val UNKNOWN: CodeVal = CodeVal(400, "UNKNOWN", "--missing-error-description--")

  implicit val writes: Writes[CodeVal] = {
    case CodeVal(_, errorCode, errorDesc) => Json.obj(
        "errorCode"        -> errorCode,
        "errorDescription" -> errorDesc
      )
  }
}
