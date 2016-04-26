package com.cloudphysics.training.model.persistence

import com.cloudphysics.training.model.api.AccountProtocol.Account
import com.cloudphysics.training.model.api.MoneyTransferProtocol.MoneyTransfer
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONObjectID}

/*
trait MoneyTransferState {
  val state : Int
}
object InitialMoneyTransferState extends MoneyTransferState {
  val state: Int = 1
}
*/
case class MoneyTransferEntity(id: BSONObjectID = BSONObjectID.generate,
                               fromAccountId: String,
                               toAccountId: String,
                               amount: Int,
                               moneyTransferState: Int)

object MoneyTransferEntity {

  implicit def toMoneyTransferEntity(moneyTransfer: MoneyTransfer) =
    MoneyTransferEntity(fromAccountId = moneyTransfer.fromAccountId,
      toAccountId = moneyTransfer.toAccountId,
      amount = moneyTransfer.amount,
      moneyTransferState = moneyTransfer.moneyTransferState)

  implicit object MoneyTransferEntityBSONReader extends BSONDocumentReader[MoneyTransferEntity] {

    def read(doc: BSONDocument): MoneyTransferEntity =
      MoneyTransferEntity(
        id = doc.getAs[BSONObjectID]("_id").get,
        fromAccountId = doc.getAs[String]("fromAccountId").get,
        toAccountId = doc.getAs[String]("toAccountId").get,
        amount = doc.getAs[Int]("amount").get,
        moneyTransferState = doc.getAs[Int]("moneyTransferState").get
      )
  }

  implicit object MoneyTransferEntityBSONWriter extends BSONDocumentWriter[MoneyTransferEntity] {
    def write(moneyTransferEntity: MoneyTransferEntity): BSONDocument =
      BSONDocument(
        "_id" -> moneyTransferEntity.id,
        "fromAccountId" -> moneyTransferEntity.fromAccountId,
        "toAccountId" -> moneyTransferEntity.toAccountId,
        "amount" -> moneyTransferEntity.amount,
        "moneyTransferState" -> moneyTransferEntity.moneyTransferState

      )
  }
}
