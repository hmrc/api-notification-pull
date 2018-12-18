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

package unit.services

import org.mockito.Mockito._
import org.scalatest.concurrent.Eventually
import org.scalatest.mockito.MockitoSugar
import uk.gov.hmrc.apinotificationpull.connectors.EnhancedApiNotificationQueueConnector
import uk.gov.hmrc.apinotificationpull.model.Notification
import uk.gov.hmrc.apinotificationpull.model.NotificationStatus._
import uk.gov.hmrc.apinotificationpull.services.EnhancedApiNotificationQueueService
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.test.UnitSpec

import scala.concurrent.Future

class EnhancedApiNotificationQueueServiceSpec extends UnitSpec with MockitoSugar with Eventually {

  private val hc = HeaderCarrier()

  val X_CLIENT_ID_HEADER_NAME = "X-Client-ID"
  val clientId = "client-id"
  val notificationId = "some-notification-id"

  trait Setup {
    val mockEnhancedApiNotificationQueueConnector = mock[EnhancedApiNotificationQueueConnector]
    val enhancedApiNotificationQueueService = new EnhancedApiNotificationQueueService(mockEnhancedApiNotificationQueueConnector)
  }

  "EnhancedApiNotificationQueueService" should {

    "pass the unpulled notification id to the connector" in new Setup {

      val headers = Map(X_CLIENT_ID_HEADER_NAME -> Seq(clientId))
      val notification = Notification(notificationId, headers.map(h => h._1 -> h._2.head), "notification-payload")

      when(mockEnhancedApiNotificationQueueConnector.getNotificationBy(notificationId, Unpulled)(hc)).thenReturn(Future.successful(Right(notification)))

      val result = await(enhancedApiNotificationQueueService.getNotificationBy(notificationId, Unpulled)(hc))

      result shouldBe Right(notification)
    }

    "pass the pulled notification id to the connector" in new Setup {

      val headers = Map(X_CLIENT_ID_HEADER_NAME -> Seq(clientId))
      val notification = Notification(notificationId, headers.map(h => h._1 -> h._2.head), "notification-payload")

      when(mockEnhancedApiNotificationQueueConnector.getNotificationBy(notificationId, Pulled)(hc)).thenReturn(Future.successful(Right(notification)))

      val result = await(enhancedApiNotificationQueueService.getNotificationBy(notificationId, Pulled)(hc))

      result shouldBe Right(notification)
    }
  }
}
