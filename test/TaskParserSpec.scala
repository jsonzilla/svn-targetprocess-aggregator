/*
 * Copyright (c) 2018, Jeison Cardoso. All Rights Reserved
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU AFFERO GENERAL PUBLIC LICENSE as published by
 * the Free Software Foundation; either version 3, or (at your option)
 * any later version.
 *
 *
 */

import org.specs2.mutable.Specification
import org.specs2.matcher.{MatchResult, Matchers}
import tasks.TaskParserOctothorpe

class TaskParserSpec extends Specification with Matchers {
  val patternParser = "(#\\d)\\d+"
  val patternSplit = "#"
  val separator = ""

  val defaultParser: TaskParserOctothorpe = TaskParserOctothorpe(patternParser, patternSplit, separator)

  s2"Convert message #123 Commit Message should return id 123 $e1"
  def e1: MatchResult[Seq[Long]] =  defaultParser.convert(Some("#123 Commit Message")) must beEqualTo[Seq[Int]](Seq(123))

  s2"Convert message #123 #145 Commit Message should return ids 123 145 $e2"
  def e2: MatchResult[Seq[Long]] = defaultParser.convert(Some("#123 #145 Commit Message"))  must beEqualTo(Seq(123, 145))

  s2"Convert message #123#145#67 Commit Message should return ids 123, 145, 67 $e3"
  def e3: MatchResult[Seq[Long]] = defaultParser.convert(Some("#123#145#67 Commit Message"))  must beEqualTo(Seq(123, 145, 67))

  s2"Convert message #Commit Message should no return id $e4"
  def e4: MatchResult[Seq[Long]] = defaultParser.convert(Some("#Commit Message"))  must beEqualTo(Nil)

  s2"Convert no message should no return id $e5"
  def e5: MatchResult[Seq[Long]] = defaultParser.convert(None)  must beEqualTo(Nil)

}
