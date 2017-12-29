/*
 * Copyright 2017 HM Revenue & Customs
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

import javax.inject.{Inject, Singleton}

import play.api.mvc._
import uk.gov.hmrc.apinotificationpull.validators.HeaderValidator
import uk.gov.hmrc.play.bootstrap.controller.BaseController

import scala.concurrent.Future

@Singleton
class NotificationsController @Inject()(headerValidator: HeaderValidator) extends BaseController {

  def delete(notificationId: String): Action[AnyContent] =
    (headerValidator.validateAcceptHeader andThen headerValidator.validateXClientIdHeader).async
  {
    Future.successful(NotFound)
  }

  def getAllByClientId(clientId: String) = Action.async { implicit request =>
    Future.failed(new NotImplementedError)
  }

}
