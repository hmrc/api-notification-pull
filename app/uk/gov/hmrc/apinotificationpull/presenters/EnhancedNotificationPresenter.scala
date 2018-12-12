/*
 * Copyright 2018 HM Revenue & Customs
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

package uk.gov.hmrc.apinotificationpull.presenters

import akka.util.ByteString
import play.api.http.HeaderNames.CONTENT_TYPE
import play.api.http.HttpEntity
import play.api.http.Status.OK
import play.api.mvc.Results.{BadRequest, InternalServerError, NotFound}
import play.api.mvc.{ResponseHeader, Result}
import uk.gov.hmrc.apinotificationpull.model.Notification
import uk.gov.hmrc.http.{BadRequestException, HttpException, NotFoundException}

class EnhancedNotificationPresenter {

  def present(notification: Either[HttpException, Notification]): Result = {
    notification match {
      case Right(n) => Result(
        header = ResponseHeader(OK),
        body = HttpEntity.Strict(ByteString(n.payload), n.headers.get(CONTENT_TYPE)))
        .withHeaders(n.headers.toSeq: _*)

      case Left(e: NotFoundException) => NotFound
      case Left(e: BadRequestException) => BadRequest
      case Left(_) => InternalServerError
    }
  }
}
