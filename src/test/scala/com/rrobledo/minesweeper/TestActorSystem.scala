package com.rrobledo.minesweeper

import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, Suite}

abstract class TestActorSystem(name: String)
  extends TestKit(ActorSystem(name)) with Suite with BeforeAndAfterAll
{
  override def afterAll(): Unit = {
    super.afterAll()
    TestKit.shutdownActorSystem(system)
  }
}