/*
 * Copyright 2019 HM Revenue & Customs
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

package unit.util

import uk.gov.hmrc.apinotificationpull.logging.NotificationLogger
import uk.gov.hmrc.apinotificationpull.model.SeqOfHeader
import uk.gov.hmrc.customs.api.common.logging.CdsLogger

// Use purely to increase coverage
class StubNotificationLogger(logger: CdsLogger) extends NotificationLogger(logger) {

  override def debug(msg: => String, headers: => SeqOfHeader): Unit =
    println(s"msg: $msg, headers: $headers")

  override def info(msg: => String, headers: => SeqOfHeader): Unit =
    println(s"msg: $msg, headers: $headers")

  override def info(msg: => String, headers: => SeqOfHeader, e: => Throwable): Unit =
    println(s"msg: $msg, headers: $headers, error: $e")

  override def warn(msg: => String, headers: => SeqOfHeader): Unit =
    println(s"msg: $msg, headers: $headers")

  override def error(msg: => String, headers: => SeqOfHeader, e: => Throwable): Unit =
    println(s"msg: $msg, headers: $headers, error: $e")

  override def error(msg: => String, headers: => SeqOfHeader): Unit =
    println(s"msg: $msg, headers: $headers")

}


