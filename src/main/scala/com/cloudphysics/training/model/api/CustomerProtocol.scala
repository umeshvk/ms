package com.cloudphysics.training.model.api

import com.cloudphysics.training.model.persistence.CustomerEntity


object CustomerProtocol {

  import spray.json._

  //Case class that describes the customer object
  case class Customer(id: String, name: String, company: String)


  //Actor messages
  case class CustomerCreated(id: String)
  case class CustomerList(customerList: List[Customer])

  /* json (un)marshalling */
  object Customer extends DefaultJsonProtocol {
    implicit val format = jsonFormat3(Customer.apply)
  }

  object CustomerList extends DefaultJsonProtocol {
    import Customer._
    implicit val customerListFormat = jsonFormat1(CustomerList.apply)
  }

  implicit def toCustomer(customerEntity: CustomerEntity): Customer =
    Customer(id = customerEntity.id.stringify, name = customerEntity.name, customerEntity.company)

}
