/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.apinotificationpull.controllers

import play.api.mvc.Request
import uk.gov.hmrc.apinotificationpull.model.SeqOfHeader
import uk.gov.hmrc.http.{HeaderCarrier,HeaderNames}

object CustomHeaderNames {

  val X_CLIENT_ID_HEADER_NAME = "X-Client-ID"

  val ACCEPT_HEADER_VALUE = "application/vnd.hmrc.1.0+xml"

  val X_CONVERSATION_ID_HEADER_NAME = "x-conversation-id"

  val X_CLIENT_AUTHORIZATION_TOKEN = "x-client-authorization-token"

  implicit def getHeadersFromHeaderCarrier[A](implicit hc: HeaderCarrier): SeqOfHeader = {
    val headerNames = HeaderNames.explicitlyIncludedHeaders
    hc.headers(headerNames) ++ hc.extraHeaders
  }

    implicit def getHeadersFromRequest[A](implicit r: Request[A]): SeqOfHeader = {
      r.headers.headers
    }
}
