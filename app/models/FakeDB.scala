package models

import api.Page
import java.text.SimpleDateFormat

import scala.collection.mutable

/*
* A fake DB to store and load all the data
*/
object FakeDB {
  val dt = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss")

  // API KEYS
  val apiKeys = FakeTable(
    1L -> ApiKey(apiKey = "AbCdEfGhIjK1", name = "ios-app", active = true),
    2L -> ApiKey(apiKey = "AbCdEfGhIjK2", name = "android-app", active = true))

  // TOKENS
  val tokens: FakeTable[ApiToken] = FakeTable[ApiToken]()

  // API REQUEST LOG
  val logs: FakeTable[ApiLog] = FakeTable[ApiLog]()

  // USERS
  val users = FakeTable(
    1L -> User("user1@mail.com", "123456", "User 1", emailConfirmed = true, active = true, 1L),
    2L -> User("user2@mail.com", "123456", "User 2", emailConfirmed = true, active = true, 2L),
    3L -> User("user3@mail.com", "123456", "User 3", emailConfirmed = true, active = true, 3L))

  // FOLDERS
  val folders = FakeTable(
    1L -> Phase(1L, 0, "Personal", 1L),
    2L -> Phase(1L, 1, "Work", 2L),
    3L -> Phase(1L, 2, "Home", 3L))

  // TASKS
  val tasks = FakeTable(
    1L -> Term(1L, 0, "Shirts on dry cleaner", java.sql.Date.valueOf("2015-09-06 10:11:00"), Some(java.sql.Date.valueOf("2015-09-08 17:00:00")), done = true, 1L),
    2L -> Term(1L, 1, "Gift for Mum's birthday", java.sql.Date.valueOf("2015-09-05 12:24:32"), Some(java.sql.Date.valueOf("2015-10-22 00:00:00")), done = false, 2L),
    3L -> Term(1L, 2, "Plan the Barcelona's trip", java.sql.Date.valueOf("2015-09-06 14:41:11"), None, done = false, 3L),
    4L -> Term(2L, 0, "Check monday's report", java.sql.Date.valueOf("2015-09-06 09:21:00"), Some(java.sql.Date.valueOf("2015-09-08 18:00:00")), done = false, 4L),
    5L -> Term(2L, 1, "Call conference with Jonh", java.sql.Date.valueOf("2015-09-06 11:37:00"), Some(java.sql.Date.valueOf("2015-09-07 18:00:00")), done = false, 5L),
    6L -> Term(3L, 0, "Fix the lamp", java.sql.Date.valueOf("2015-08-16 21:22:00"), None, done = false, 6L),
    7L -> Term(3L, 1, "Buy coffee", java.sql.Date.valueOf("2015-09-05 08:12:00"), None, done = false, 7L))

  /*
	* Fake table that emulates a SQL table with an auto-increment index
	*/
  case class FakeTable[A](var table: mutable.Map[Long, A], var incr: Long) {
    def nextId: Long = {
      if (!table.contains(incr))
        incr
      else {
        incr += 1
        nextId
      }
    }
    def get(id: Long): Option[A] = table.get(id)
    def find(p: A => Boolean): Option[A] = table.values.find(p)
    def insert(a: Long => A): (Long, A) = {
      val id = nextId
      val tuple = id -> a(id)
      table += tuple
      incr += 1
      tuple
    }
    def update(id: Long)(f: A => A): Boolean = get(id).exists { a =>
      table += (id -> f(a))
      true
    }
    def delete(id: Long): Unit = table -= id
    def delete(p: A => Boolean): Unit = table = table.filterNot { case (_, a) => p(a) }

    def values: List[A] = table.values.toList
    def map[B](f: A => B): List[B] = values.map(f)
    def filter(p: A => Boolean): List[A] = values.filter(p)
    def exists(p: A => Boolean): Boolean = values.exists(p)
    def count(p: A => Boolean): Int = values.count(p)
    def size: Int = table.size
    def isEmpty: Boolean = size == 0

    def page(p: Int, s: Int)(filterFunc: A => Boolean)(sortFuncs: (A, A) => Boolean*): Page[A] = {
      val items = filter(filterFunc)
      val sorted = sortFuncs.foldRight(items)((f, items) => items.sortWith(f))
      Page(
        items = sorted.slice((p - 1) * s, (p - 1) * s + s),
        page = p,
        size = s,
        total = sorted.size)
    }
  }

  object FakeTable {
    def apply[A](elements: (Long, A)*): FakeTable[A] = apply(mutable.Map(elements: _*), elements.size + 1)
  }

}