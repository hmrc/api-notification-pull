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

package uk.gov.hmrc.apinotificationpull.connectors

import javax.inject.Inject

import play.api.libs.json.Json
import play.mvc.Http.RequestHeader
import uk.gov.hmrc.apinotificationpull.config.ServiceConfiguration
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.Future

class ApiNotificationQueueConnector @Inject()(config: ServiceConfiguration, http: HttpClient) {

  private lazy val serviceBaseUrl: String = config.baseUrl("api-notification-queue")

  case class Notifications(notifications: List[String])



  def getQueueNotifications(clientId: String)(implicit hc: HeaderCarrier) = {

    implicit val rds = Json.format[Notifications]
    import scala.concurrent.ExecutionContext.Implicits.global

    // you need to pass the clientId as header (maybe from the controller) .....

//    hc.extraHeaders("")
    val notifications = http.GET[Notifications](s"$serviceBaseUrl/notifications")

    notifications.map { x => println(x) }

    Future.successful(Json.toJson("""{"notifications":[]}"""))
  }

}
