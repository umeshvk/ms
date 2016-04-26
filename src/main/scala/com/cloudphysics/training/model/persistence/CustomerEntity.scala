package com.cloudphysics.training.model.persistence

import com.cloudphysics.training.model.api.CustomerProtocol.Customer
import reactivemongo.bson.{BSONDocument, BSONDocumentReader, BSONDocumentWriter, BSONObjectID}

case class CustomerEntity(id: BSONObjectID = BSONObjectID.generate,
                          name: String,
                          company: String)

object CustomerEntity {

  implicit def toCustomerEntity(customer: Customer) = CustomerEntity(name = customer.name, company = customer.company)

  implicit object CustomerEntityBSONReader extends BSONDocumentReader[CustomerEntity] {

    def read(doc: BSONDocument): CustomerEntity =
      CustomerEntity(
        id = doc.getAs[BSONObjectID]("_id").get,
        name = doc.getAs[String]("name").get,
        company = doc.getAs[String]("company").get
      )
  }

  implicit object CustomerEntityBSONWriter extends BSONDocumentWriter[CustomerEntity] {
    def write(customerEntity: CustomerEntity): BSONDocument =
      BSONDocument(
        "_id" -> customerEntity.id,
        "name" -> customerEntity.name,
        "company" -> customerEntity.company
      )
  }
}
