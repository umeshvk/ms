package com.cloudphysics.training

import com.cloudphysics.training.dao.{AccountDao}
import com.cloudphysics.training.model.persistence.{AccountEntity}


class AccountManager extends AccountDao {

  def createAccount(accountEntity: AccountEntity) = save(accountEntity)

  //def deleteCustomerEntity(id: String) = deleteById(id)

  def listAccountEntity() = listAccount()

}

