package com.cloudphysics.training.dao

import com.cloudphysics.training.model.api.CustomerProtocol
import com.cloudphysics.training.model.persistence.CustomerEntity
import reactivemongo.api.QueryOpts
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import reactivemongo.core.commands.Count

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Random, Success}

trait CustomerDao extends MongoDao {
  
  import CustomerProtocol._

  val collection = db[BSONCollection]("customers")

  def save(customerEntity: CustomerEntity) = collection.save(customerEntity)
    .map(_ => CustomerCreated(customerEntity.id.stringify))
  
  def findById(id: String) =
    collection.find(queryById(id)).one[CustomerEntity]
  
  def findOne = {
    val futureCount = db.command(Count(collection.name))
    val result = futureCount.flatMap { count =>
      val skip = Random.nextInt(count)
      val ce = collection.find(emptyQuery).options(QueryOpts(skipN = 0)).one[CustomerEntity]
      println("ce:" + ce)
      ce
    }
    println("findOne:" + result)
    result
  }
  
  //def deleteById(id: String) = collection.remove(queryById(id)).map(_ => CustomerDeleted)



  import spray.json._
  import DefaultJsonProtocol._

  import CustomerProtocol._

    def readCustomerEntity(doc: BSONDocument): Customer =
      CustomerEntity(
        id = doc.getAs[BSONObjectID]("_id").get,
        name = doc.getAs[String]("name").get,
        company = doc.getAs[String]("company").get
      )

  def listCustomer() = {

    val query = BSONDocument()
    // select only the fields 'lastName' and '_id'
    val filter = BSONDocument()

    // Or, the same with getting a list
    val futureList: Future[List[BSONDocument]] =
      collection.
        find(query).
        cursor[BSONDocument].
        collect[List]()
    //val result = collection.find(emptyQuery).collection

    val futureRes = futureList.map {
      (listDoc : List[BSONDocument]) => for ( bdoc <- listDoc)yield {readCustomerEntity(bdoc) }
    }.map((list: List[Customer]) => CustomerList(list))

    println("lc:" + futureRes)
    futureRes


  }


  private def queryById(id: String) = BSONDocument("_id" -> BSONObjectID(id))

  private def emptyQuery = BSONDocument()
}
