package com.rrobledo.minesweeper.utils

import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success, Try}

object Retry extends LazyLogging {

  def retry[T](delays: Seq[FiniteDuration], stringFunc: () => String)
              (f: => T): T = {
    Try(f) match {
      case Failure(ex) =>
        val logString = makeLogString(ex, stringFunc)

        if (delays.isEmpty) {
          logger.error(logString)
          throw ex
        } else {
          logger.warn(logString)
          val delay = withJitter(delays.head.toMillis, 0.2)
          logger.warn(s"Retrying[${stringFunc()}] (sleeping: $delay millis)")
          Thread.sleep(delay)
          retry(delays.tail, stringFunc)(f)
        }

      case Success(r) => r
    }
  }

  private def withJitter(value: Long, percentage: Double): Long = {
    val jitter = 1 - percentage + (2 * percentage * Math.random())
    (jitter * value).toLong
  }

  private def makeLogString(ex: Throwable, stringFunc: () => String): String = {
    s"${stringFunc()}]\n[${ex.getClass.getSimpleName} => ${ex.getMessage}\n"
  }
}
