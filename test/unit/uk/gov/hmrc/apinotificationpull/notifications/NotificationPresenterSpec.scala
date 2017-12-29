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

package uk.gov.hmrc.apinotificationpull.notifications

import akka.stream.Materializer
import play.api.http.Status._
import play.api.http.HeaderNames.CONTENT_TYPE
import uk.gov.hmrc.apinotificationpull.model.Notification
import uk.gov.hmrc.play.test.{UnitSpec, WithFakeApplication}
import views.txt

class NotificationPresenterSpec extends UnitSpec with WithFakeApplication {
  private implicit val materializer: Materializer = fakeApplication.materializer

   "present" when {
     "no notification" should {
       "return NOT_FOUND" in {
         val presenter = new NotificationPresenter

         val result = presenter.present("NotificationId", None)

         status(result) shouldBe NOT_FOUND
       }
     }

     "notificationExists" should {
       "return OK" in {
         val presenter = new NotificationPresenter
         val notificationId = "notificationId"

         val result = presenter.present(notificationId, Some(Notification(notificationId, Map(CONTENT_TYPE -> "application/xml"), "Notification")))

         status(result) shouldBe OK
       }

       "return notification payload in body" in {
         val presenter = new NotificationPresenter
         val notificationId = "NotificationId"
         val notification = Notification(notificationId, Map(CONTENT_TYPE -> "application/xml"), "Notification")

         val result = await(presenter.present(notificationId, Some(notification)))

         bodyOf(result) shouldBe notification.payload
       }
     }
   }
}
