package com.cloudphysics.training.dao

import com.cloudphysics.training.model.api.MoneyTransferProtocol
import com.cloudphysics.training.model.persistence.{MoneyTransferEntity}
import reactivemongo.api.QueryOpts
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import reactivemongo.core.commands.Count

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

trait MoneyTransferDao extends MongoDao {
  
  import MoneyTransferProtocol._
  
  val collection = db[BSONCollection]("moneytransfer")

  def save(moneyTransferEntity: MoneyTransferEntity) = collection.save(moneyTransferEntity)
    .map(_ => MoneyTransferCreated(moneyTransferEntity.id.stringify))
  
  def findById(id: String) =
    collection.find(queryById(id)).one[MoneyTransferEntity]
  
  def findOne = {
    val futureCount = db.command(Count(collection.name))
    val result = futureCount.flatMap { count =>
      val skip = Random.nextInt(count)
      val ce = collection.find(emptyQuery).options(QueryOpts(skipN = 0)).one[MoneyTransferEntity]
      println("ae:" + ce)
      ce
    }
    println("findOne for ae:" + result)
    result
  }
  
  //def deleteById(id: String) = collection.remove(queryById(id)).map(_ => MoneyTransferDeleted)
  def readMoneyTransferEntity(doc: BSONDocument): MoneyTransfer =
      MoneyTransferEntity(
        id = doc.getAs[BSONObjectID]("_id").get,
        fromAccountId = doc.getAs[String]("fromAccountId").get,
        toAccountId = doc.getAs[String]("toAccountId").get,
        moneyTransferState = doc.getAs[Int]("moneyTransferState").get,
        amount = doc.getAs[Int]("amount").get
      )

  def listMoneyTransfer() = {

    val query = BSONDocument()
    // select only the fields 'lastName' and '_id'
    val filter = BSONDocument()

    // Or, the same with getting a list
    val futureList: Future[List[BSONDocument]] =
      collection.
        find(query).
        cursor[BSONDocument].
        collect[List]()

    val futureRes = futureList.map {
      (listDoc : List[BSONDocument]) => {
        for ( bdoc <- listDoc)
          yield {
            readMoneyTransferEntity(bdoc)
          }
      }
    }.map((list: List[MoneyTransfer]) => MoneyTransferList(list))

    println("lc:" + futureRes)
    futureRes


  }


  private def queryById(id: String) = BSONDocument("_id" -> BSONObjectID(id))

  private def emptyQuery = BSONDocument()
}
