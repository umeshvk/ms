package com.cloudphysics.training

import java.util

import akka.actor._
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.github.sstone.amqp.Amqp.{DeclareQueue, QueueParameters}
import com.github.sstone.amqp.{ChannelOwner, ConnectionOwner}
import com.rabbitmq.client.{AMQP, ConnectionFactory, DefaultConsumer, Envelope}
import com.typesafe.config.ConfigFactory
import spray.can.Http
import spray.http.CacheDirectives.public

import scala.concurrent.duration._

object RabbitMqMain extends App {
  val config = ConfigFactory.load()
  val host = config.getString("http.host")
  val port = config.getInt("http.port")

  implicit val system = ActorSystem("bank-service")
/*
  // create an AMQP connection
  val connFactory = new ConnectionFactory()
  connFactory.setUri("amqp://guest:guest@192.168.99.100:5672/%2F")
  val conn = system.actorOf(ConnectionOwner.props(connFactory, 1 second))
  val channel = ConnectionOwner.createChildActor(conn, ChannelOwner.props())

  channel ! DeclareQueue(
    QueueParameters("my_queue", passive = false, durable = true, exclusive = false, autodelete = false))
*/

  import com.rabbitmq.client.Connection;
  import com.rabbitmq.client.Channel;


  val factory = new ConnectionFactory();
  factory.setUsername("guest");
  factory.setPassword("guest");
  factory.setVirtualHost("/");
  factory.setHost("192.168.99.100");
  factory.setPort(5672);
  val conn = factory.newConnection();

  val channel = conn.createChannel();

  val exchangeName = "training-exchange-topic-2"
  val queueName = "OrderQueue";
  val routingKey = "OrderCreatedEvent"
  channel.exchangeDeclare(exchangeName, "topic", true);
  val queueStatus = channel.queueDeclare(queueName, true, false, false, new util.HashMap()).getQueue();
  channel.queueBind(queueName, exchangeName, routingKey);


  val messageBodyBytes = "Hello, world!".getBytes();
  channel.basicPublish(exchangeName, routingKey, null, messageBodyBytes);

  var autoAck = false;
  channel.basicConsume(queueName, autoAck, "myConsumerTag",
    new DefaultConsumer(channel) {
      override def handleDelivery(consumerTag: String,
                         envelope : Envelope,
                         properties: AMQP.BasicProperties,
                         body: Array[Byte])
        {
          println("Got a message from the queue:" + new String(body, "UTF-8"))
          val routingKey = envelope.getRoutingKey();
          val contentType = properties.getContentType();
          val deliveryTag = envelope.getDeliveryTag();
          // (process the message components here ...)
          channel.basicAck(deliveryTag, false);
        }
    });

  Thread.sleep(1000000000)
}
