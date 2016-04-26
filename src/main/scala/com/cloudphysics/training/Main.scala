package com.cloudphysics.training

import akka.actor._
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.github.sstone.amqp.Amqp.{DeclareQueue, QueueParameters}
import com.github.sstone.amqp.{ChannelOwner, ConnectionOwner}
import com.rabbitmq.client.ConnectionFactory
import com.typesafe.config.ConfigFactory
import spray.can.Http

import scala.concurrent.duration._

object Main extends App {
  val config = ConfigFactory.load()
  val host = config.getString("http.host")
  val port = config.getInt("http.port")

  implicit val system = ActorSystem("bank-service")

  // create an AMQP connection
  val connFactory = new ConnectionFactory()
  connFactory.setUri("amqp://guest:guest@192.168.99.100:5672/%2F")
  val conn = system.actorOf(ConnectionOwner.props(connFactory, 1 second))
  val channel = ConnectionOwner.createChildActor(conn, ChannelOwner.props())

  channel ! DeclareQueue(QueueParameters("my_queue", passive = false, durable = false, exclusive = false, autodelete = true))


  val api = system.actorOf(Props(new RestInterface()), "httpInterface")


  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(10 seconds)

  IO(Http).ask(Http.Bind(listener = api, interface = host, port = port))
    .mapTo[Http.Event]
    .map {
      case Http.Bound(address) =>
        println(s"REST interface bound to $address")
      case Http.CommandFailed(cmd) =>
        println("REST interface could not bind to " +
          s"$host:$port, ${cmd.failureMessage}")
        system.shutdown()
    }
}
