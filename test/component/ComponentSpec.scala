/*
 * Copyright 2021 HM Revenue & Customs
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

package component

import org.scalatest._
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import util.ExternalServicesConfig.{Host, Port}

abstract class ComponentSpec extends FeatureSpec with GivenWhenThen with Matchers with GuiceOneAppPerTest
  with BeforeAndAfterEach with BeforeAndAfterAll {

  override def newAppForTest(testData: TestData): Application = new GuiceApplicationBuilder().configure(Map(
    "api.context" -> "notifications",
    "microservice.services.api-notification-queue.host" -> Host,
    "microservice.services.api-notification-queue.port" -> Port,
    "auditing.enabled" -> false
  )).build()
}
