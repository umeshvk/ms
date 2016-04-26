package com.cloudphysics.training

import akka.actor._
import akka.pattern.pipe
import akka.util.Timeout
import com.cloudphysics.training.model.api.AccountProtocol._
import com.cloudphysics.training.model.api.CustomerProtocol._
import com.cloudphysics.training.model.api.MoneyTransferProtocol._

import spray.http.StatusCodes
import spray.httpx.SprayJsonSupport._
import spray.routing._

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Failure

class RestInterface extends HttpServiceActor
  with RestApi {

  def receive = runRoute(routes)
}

trait RestApi extends HttpService with ActorLogging { actor: Actor =>

  implicit val timeout = Timeout(10 seconds)
  

  val customerManager = new CustomerManager
  val accountManager = new AccountManager
  val moneyTransferManager = new MoneyTransferManager


  def routes: Route =
    pathPrefix("customer") {
      pathEnd {
        post {
          entity(as[Customer]) { customer => requestContext =>
            val responder = createResponder(requestContext)
            customerManager.createCustomer(customer).pipeTo(responder)
          }
        }
      } ~
      path("list") {
        get {
          requestContext =>
            val responder = createResponder(requestContext)
            val result = customerManager.listCustomerEntity()
            result.pipeTo(responder)
        }
      }
    }~
      pathPrefix("account") {
        pathEnd {
          post {
            entity(as[Account]) { account => requestContext =>
              val responder = createResponder(requestContext)
              accountManager.createAccount(account).pipeTo(responder)
            }
          }
        } ~
          path("list") {
            get {
              requestContext =>
                val responder = createResponder(requestContext)
                val result = accountManager.listAccountEntity()
                result.pipeTo(responder)
            }
          }
      }~
      pathPrefix("moneyTransfer") {
        pathEnd {
          post {
            entity(as[MoneyTransfer]) { moneyTransfer => requestContext =>
              val responder = createResponder(requestContext)
              moneyTransferManager.createMoneyTransfer(moneyTransfer).pipeTo(responder)
            }
          }
        } ~
          path("list") {
            get {
              requestContext =>
                val responder = createResponder(requestContext)
                val result = moneyTransferManager.listMoneyTransfer()
                result.pipeTo(responder)
            }
          }
      }
  
  private def createResponder(requestContext: RequestContext) =
    context.actorOf(Props(new Responder(requestContext)))
}

class Responder(requestContext:RequestContext) extends Actor with ActorLogging {
  import model.api.CustomerProtocol._


  def receive = {

    case MoneyTransferCreated(id) =>
      requestContext.complete(StatusCodes.Created, id)
      killYourself
    case moneyTransferList : MoneyTransferList=>
      requestContext.complete(StatusCodes.OK, moneyTransferList)
      killYourself
    case CustomerCreated(id) =>
      requestContext.complete(StatusCodes.Created, id)
      killYourself
    case customerList : CustomerList=>
      requestContext.complete(StatusCodes.OK, customerList)
      killYourself
    case AccountCreated(id) =>
      requestContext.complete(StatusCodes.Created, id)
      killYourself
    case accountList : AccountList=>
      requestContext.complete(StatusCodes.OK, accountList)
      killYourself
    case a:  Failure[_] =>
      println("Result:" + a.asInstanceOf[Failure[_]].exception)
      requestContext.complete(StatusCodes.OK)
      killYourself
    case a:  Any =>
      println("Result Any:" + a)
      requestContext.complete(StatusCodes.OK)
      killYourself
  }

  private def killYourself = self ! PoisonPill
  
}
