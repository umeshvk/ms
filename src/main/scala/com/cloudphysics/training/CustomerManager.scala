package com.cloudphysics.training

import com.cloudphysics.training.dao.CustomerDao
import com.cloudphysics.training.dao.CustomerDao
import com.cloudphysics.training.model.persistence.CustomerEntity



class CustomerManager extends CustomerDao {

  def createCustomer(customerEntity: CustomerEntity) = save(customerEntity)

  //def deleteCustomerEntity(id: String) = deleteById(id)

  def listCustomerEntity() = listCustomer()

}

