package com.cloudphysics.training.model.api

import com.cloudphysics.training.model.persistence.{MoneyTransferEntity}


object MoneyTransferProtocol {

  import spray.json._

  //Case class that describes the customer object
  case class MoneyTransfer(id: String,
                           fromAccountId: String,
                           toAccountId: String,
                           amount: Int,
                           moneyTransferState: Int)


  //Actor messages
  case class MoneyTransferCreated(id: String)
  case class MoneyTransferList(moneyTransferList: List[MoneyTransfer])

  /* json (un)marshalling */
  object MoneyTransfer extends DefaultJsonProtocol {
    implicit val format = jsonFormat5(MoneyTransfer.apply)
  }

  object MoneyTransferList extends DefaultJsonProtocol {
    import MoneyTransfer._
    implicit val customerListFormat = jsonFormat1(MoneyTransferList.apply)
  }

  implicit def toMoneyTransfer(moneyTransferEntity: MoneyTransferEntity): MoneyTransfer =
    MoneyTransfer(id = moneyTransferEntity.id.stringify,
      fromAccountId = moneyTransferEntity.fromAccountId,
      toAccountId = moneyTransferEntity.toAccountId,
      amount = moneyTransferEntity.amount,
      moneyTransferState = moneyTransferEntity.moneyTransferState)

}
