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

package uk.gov.hmrc.apinotificationpull.config

import play.api.Configuration

import javax.inject.{Inject, Singleton}

@Singleton
class AppContext @Inject()(configuration: Configuration) {
  private val apiScopeConfigKey = "api.definition.scope"
  private val apiContextConfigKey = "api.context"
  private val notificationsLimitConfigKey = "notifications.limit"
  private def apiConfigException(apiConfigKey: String) = new IllegalStateException(s"$apiConfigKey is not configured")
  lazy val apiScopeKey: String = configuration.getOptional[String](apiScopeConfigKey).getOrElse(throw apiConfigException(apiScopeConfigKey))
  lazy val apiContext: String = configuration.getOptional[String](apiContextConfigKey).getOrElse(throw apiConfigException(apiContextConfigKey))
  lazy val notificationsLimit: String = configuration.getOptional[String](notificationsLimitConfigKey).getOrElse(throw apiConfigException(notificationsLimitConfigKey))
}
