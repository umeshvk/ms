package com.cloudphysics.training

import com.cloudphysics.training.dao.MoneyTransferDao
import com.cloudphysics.training.model.persistence.MoneyTransferEntity


class MoneyTransferManager extends MoneyTransferDao {

  def createMoneyTransfer(moneyTransferEntity: MoneyTransferEntity) = save(moneyTransferEntity)

  //def deleteCustomerEntity(id: String) = deleteById(id)

  def listMoneyTransferEntity() = listMoneyTransfer()

}

