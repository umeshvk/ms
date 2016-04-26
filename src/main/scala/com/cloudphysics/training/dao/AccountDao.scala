package com.cloudphysics.training.dao

import com.cloudphysics.training.model.api.{AccountProtocol, CustomerProtocol}
import com.cloudphysics.training.model.persistence.AccountEntity
import reactivemongo.api.QueryOpts
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import reactivemongo.core.commands.Count

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

trait AccountDao extends MongoDao {
  
  import AccountProtocol._
  
  val collection = db[BSONCollection]("accounts")

  def save(accountEntity: AccountEntity) = collection.save(accountEntity)
    .map(_ => AccountCreated(accountEntity.id.stringify))
  
  def findById(id: String) =
    collection.find(queryById(id)).one[AccountEntity]
  
  def findOne = {
    val futureCount = db.command(Count(collection.name))
    val result = futureCount.flatMap { count =>
      val skip = Random.nextInt(count)
      val ce = collection.find(emptyQuery).options(QueryOpts(skipN = 0)).one[AccountEntity]
      println("ae:" + ce)
      ce
    }
    println("findOne for ae:" + result)
    result
  }
  
  //def deleteById(id: String) = collection.remove(queryById(id)).map(_ => AccountDeleted)




  def readAccountEntity(doc: BSONDocument): Account =
      AccountEntity(
        id = doc.getAs[BSONObjectID]("_id").get,
        customerId = doc.getAs[String]("customerId").get,
        amount = doc.getAs[Int]("amount").get
      )

  def listAccount() = {

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
            readAccountEntity(bdoc)
          }
      }
    }.map((list: List[Account]) => AccountList(list))

    println("lc:" + futureRes)
    futureRes


  }


  private def queryById(id: String) = BSONDocument("_id" -> BSONObjectID(id))

  private def emptyQuery = BSONDocument()
}
