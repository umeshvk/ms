package com.cloudphysics.training.model.persistence

import com.cloudphysics.training.model.api.AccountProtocol.Account
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONObjectID}

case class AccountEntity(id: BSONObjectID = BSONObjectID.generate,
                         customerId: String,
                         amount: Int)

object AccountEntity {

  implicit def toAccountEntity(account: Account) = AccountEntity(customerId = account.customerId, amount = account.amount)

  implicit object AccountEntityBSONReader extends BSONDocumentReader[AccountEntity] {

    def read(doc: BSONDocument): AccountEntity =
      AccountEntity(
        id = doc.getAs[BSONObjectID]("_id").get,
        customerId = doc.getAs[String]("customerId").get,
        amount = doc.getAs[Int]("amount").get
      )
  }

  implicit object AccountEntityBSONWriter extends BSONDocumentWriter[AccountEntity] {
    def write(accountEntity: AccountEntity): BSONDocument =
      BSONDocument(
        "_id" -> accountEntity.id,
        "customerId" -> accountEntity.customerId,
        "amount" -> accountEntity.amount
      )
  }
}
