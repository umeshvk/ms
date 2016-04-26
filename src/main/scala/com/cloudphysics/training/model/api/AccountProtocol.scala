package com.cloudphysics.training.model.api

import com.cloudphysics.training.model.persistence.AccountEntity


object AccountProtocol {

  import spray.json._

  //Case class that describes the customer object
  case class Account(id: String, customerId: String, amount: Int)


  //Actor messages
  case class AccountCreated(id: String)
  case class AccountList(accountList: List[Account])

  /* json (un)marshalling */
  object Account extends DefaultJsonProtocol {
    implicit val format = jsonFormat3(Account.apply)
  }

  object AccountList extends DefaultJsonProtocol {
    import Account._
    implicit val customerListFormat = jsonFormat1(AccountList.apply)
  }

  implicit def toAccount(accountEntity: AccountEntity): Account =
    Account(id = accountEntity.id.stringify, customerId = accountEntity.customerId, amount = accountEntity.amount)

}
