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

package unit.connectors

import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.{any, eq => meq}
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.Eventually
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatestplus.mockito.MockitoSugar
import play.api.test.Helpers
import uk.gov.hmrc.apinotificationpull.connectors.EnhancedApiNotificationQueueConnector
import uk.gov.hmrc.apinotificationpull.model.NotificationStatus._
import uk.gov.hmrc.apinotificationpull.model.{Notification, Notifications}
import uk.gov.hmrc.http._
import uk.gov.hmrc.http.client.{HttpClientV2, RequestBuilder}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import unit.util.RequestHeaders.{ClientId, X_CLIENT_ID_HEADER, X_CLIENT_ID_HEADER_NAME}
import unit.util.StubNotificationLogger
import util.UnitSpec

import java.net.URI
import java.util.UUID
import java.util.UUID.fromString
import scala.concurrent.{ExecutionContext, Future}

class EnhancedApiNotificationQueueConnectorSpec extends UnitSpec with MockitoSugar with BeforeAndAfterEach with Eventually {

  trait Setup {

    implicit val hc: HeaderCarrier = HeaderCarrier().withExtraHeaders(X_CLIENT_ID_HEADER)

    implicit val ec: ExecutionContext = Helpers.stubControllerComponents().executionContext
    val mockServicesConfig: ServicesConfig = mock[ServicesConfig]
    val mockHttpClient: HttpClientV2 = mock[HttpClientV2]
    val mockHttpResponse: HttpResponse = mock[HttpResponse]
    val notifications = Notifications(List("notification-1", "notification-2"))

    val notificationId = "some-notification-id"
    val conversationId = UUID.fromString("0ad764d1-ba29-4bfb-b7f7-25adbede0002")
    val headers = Map(X_CLIENT_ID_HEADER_NAME -> Seq(ClientId))
    val notification = Notification(notificationId, headers.map(h => h._1 -> h._2.head), "notification-payload")
    val stubLogger = new StubNotificationLogger(mock[ServicesConfig])
    val enhancedApiNotificationQueueConnector = new EnhancedApiNotificationQueueConnector(mockServicesConfig, mockHttpClient, stubLogger)
    val mockRequestBuilder = mock[RequestBuilder]

    when(mockServicesConfig.baseUrl("api-notification-queue")).thenReturn("http://api-notification-queue.url")
    when(mockHttpResponse.headers).thenReturn(headers)
    when(mockHttpResponse.body).thenReturn("notification-payload")

  }

  "EnhancedApiNotificationQueueConnector" should {

    "return a list of unpulled notification" in new Setup {

      val url = s"http://api-notification-queue.url/notifications/unpulled"
      when(mockHttpClient.get(meq(url"$url"))(any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[Notifications](any(), any())).thenReturn(Future.successful(notifications))

      val result: Notifications = enhancedApiNotificationQueueConnector.getAllNotificationsBy(Unpulled).futureValue

      result shouldBe notifications
    }

    "return a list of previously pulled notification" in new Setup {

      val url = s"http://api-notification-queue.url/notifications/pulled"
      when(mockHttpClient.get(meq(url"$url"))(any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[Notifications](any(), any())).thenReturn(Future.successful(notifications))

      val result: Notifications = (enhancedApiNotificationQueueConnector.getAllNotificationsBy(Pulled)).futureValue

      result shouldBe notifications
    }

    "return a list of notifications by conversation id" in new Setup {

      val url = s"http://api-notification-queue.url/notifications/conversationId/0ad764d1-ba29-4bfb-b7f7-25adbede0002"
      when(mockHttpClient.get(meq(url"$url"))(any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[Notifications](any(), any())).thenReturn(Future.successful(notifications))

      val result: Notifications = (enhancedApiNotificationQueueConnector.getAllNotificationsBy(fromString("0ad764d1-ba29-4bfb-b7f7-25adbede0002"))).futureValue

      result shouldBe notifications
    }

    "return the unpulled notification for the specified notification id" in new Setup {

      val url = s"http://api-notification-queue.url/notifications/unpulled/$notificationId"
      when(mockHttpClient.get(meq(url"$url"))(any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[HttpResponse](any(), any())).thenReturn(Future.successful(mockHttpResponse))

      val result: Either[UpstreamErrorResponse, Notification] = (enhancedApiNotificationQueueConnector.getNotificationBy(notificationId, Unpulled)).futureValue

      result shouldBe Right(notification)
    }

    "return the previously pulled notification for the specified notification id" in new Setup {

      val url = s"http://api-notification-queue.url/notifications/pulled/$notificationId"
      when(mockHttpClient.get(meq(url"$url"))(any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[HttpResponse](any(), any())).thenReturn(Future.successful(mockHttpResponse))

      val result: Either[UpstreamErrorResponse, Notification] = (enhancedApiNotificationQueueConnector.getNotificationBy(notificationId, Pulled)).futureValue

      result shouldBe Right(notification)
    }

    "return a not found exception when a downstream system returns a 404" in new Setup {

      private val notFoundException = UpstreamErrorResponse("not found exception",404)

      val url = s"http://api-notification-queue.url/notifications/unpulled/$notificationId"
      when(mockHttpClient.get(meq(url"$url"))(any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[HttpResponse](any(), any())).thenReturn(Future.failed(notFoundException))

      val result: Either[UpstreamErrorResponse, Notification] = (enhancedApiNotificationQueueConnector.getNotificationBy(notificationId, Unpulled)).futureValue

      result shouldBe Left(notFoundException)
    }

    "return a bad request exception when a downstream system returns a 400" in new Setup {

      private val badRequestException = UpstreamErrorResponse("bad request exception", 400)

      val url = s"http://api-notification-queue.url/notifications/unpulled/$notificationId"
      when(mockHttpClient.get(meq(url"$url"))(any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[HttpResponse](any(), any())).thenReturn(Future.failed(badRequestException))

      val result: Either[UpstreamErrorResponse, Notification] = (enhancedApiNotificationQueueConnector.getNotificationBy(notificationId, Unpulled)).futureValue

      result shouldBe Left(badRequestException)
    }

    "return a internal server error exception when a downstream system returns any other error" in new Setup {

      private val unauthorisedException = new UnauthorizedException("unauthorised exception")

      val url = s"http://api-notification-queue.url/notifications/unpulled/$notificationId"
      when(mockHttpClient.get(meq(url"$url"))(any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[HttpResponse](any(), any())).thenReturn(Future.failed(unauthorisedException))

      val result: Either[UpstreamErrorResponse, Notification] = enhancedApiNotificationQueueConnector.getNotificationBy(notificationId, Unpulled).futureValue

      result.swap.getOrElse(null).message shouldBe "unauthorised exception"
      result.swap.getOrElse(null).statusCode shouldBe 500
    }

    "return an empty list of unpulled notifications when the downstream returns an empty list" in new Setup {

      val url = s"http://api-notification-queue.url/notifications/unpulled"
      when(mockHttpClient.get(meq(url"$url"))(any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[Notifications](any(), any())).thenReturn(Future.successful(Notifications(List())))

      val result = enhancedApiNotificationQueueConnector.getAllNotificationsBy(Unpulled).futureValue

      result shouldBe Notifications(List())
    }

    "return an empty list of previously pulled notifications when the downstream returns an empty list" in new Setup {

      val url = s"http://api-notification-queue.url/notifications/pulled"
      when(mockHttpClient.get(meq(url"$url"))(any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[Notifications](any(), any())).thenReturn(Future.successful(Notifications(List())))

      val result = enhancedApiNotificationQueueConnector.getAllNotificationsBy(Pulled).futureValue

      result shouldBe Notifications(List())
    }

    "return an empty list of notifications by conversation id when the downstream returns an empty list" in new Setup {

      val url = s"http://api-notification-queue.url/notifications/conversationId/$conversationId"
      when(mockHttpClient.get(meq(url"$url"))(any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[Notifications](any(), any())).thenReturn(Future.successful(Notifications(List())))

      val result = enhancedApiNotificationQueueConnector.getAllNotificationsBy(conversationId).futureValue

      result shouldBe Notifications(List())
    }

    "return an empty list of unpulled notifications by conversation id when the downstream returns an empty list" in new Setup {

      val url = s"http://api-notification-queue.url/notifications/conversationId/$conversationId/unpulled"
      when(mockHttpClient.get(meq(url"$url"))(any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[Notifications](any(), any())).thenReturn(Future.successful(Notifications(List())))

      val result = enhancedApiNotificationQueueConnector.getAllNotificationsBy(conversationId, Unpulled).futureValue

      result shouldBe Notifications(List())
    }

    "return an empty list of previously pulled notifications by conversation id when the downstream returns an empty list" in new Setup {

      val url = s"http://api-notification-queue.url/notifications/conversationId/$conversationId/pulled"
      when(mockHttpClient.get(meq(url"$url"))(any())).thenReturn(mockRequestBuilder)
      when(mockRequestBuilder.execute[Notifications](any(), any())).thenReturn(Future.successful(Notifications(List())))

      val result = enhancedApiNotificationQueueConnector.getAllNotificationsBy(conversationId, Pulled).futureValue

      result shouldBe Notifications(List())
    }
  }
}
