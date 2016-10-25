/*
 * Copyright (c) 2016 Olivier Mélois
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package pipo

import cats.free.{ Free, Inject }

object Main extends App {

  // INSTRUCTIONS SET

  sealed trait KVStore[A]
  case class Put[T](key: String, value: T) extends KVStore[Unit]
  case class Get[T](key: String)           extends KVStore[Option[T]]
  case class Delete(key: String)           extends KVStore[Unit]

  // FREE CONSTRUCTORS

  def put[T, G[_]](key: String, value: T)(
      implicit I: Inject[KVStore, G]
  ): Free[G, Unit] = Free.inject[KVStore, G](Put(key, value))

  def get[T, G[_]](key: String)(
      implicit I: Inject[KVStore, G]
  ): Free[G, Option[T]] = Free.inject[KVStore, G](Get(key))

  def delete[T, G[_]](key: String)(
      implicit I: Inject[KVStore, G]
  ): Free[G, Unit] = Free.inject[KVStore, G](Delete(key))

  // PROGRAM

  /**
   * Now here's the symptom : KVStore is being injected into itself,
   * and with there is an ambiguity with implicit values :
   * - catsFreeReflexiveInjectInstance
   * - catsFreeLeftInjectInstance
   *
   *  Note that the problem doesn't appear if "-Ypartial-unification" is
   *  disabled in the build (project/Build.scala)
   *
   */
  val program: Free[KVStore, Option[String]] = for {
    _ <- put("key", "value")
    v <- get[String, KVStore]("key")
  } yield v

}
