/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.kyuubi.spark.connector.yarn

import scala.jdk.CollectionConverters.iterableAsScalaIterableConverter

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.connector.read._
import org.apache.spark.sql.sources.Filter
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.util.CaseInsensitiveStringMap

case class YarnAppScan(options: CaseInsensitiveStringMap, schema: StructType, pushed: Array[Filter])
  extends ScanBuilder
  with Scan with Batch with Serializable {

  override def toBatch: Batch = this

  override def readSchema(): StructType = schema

  override def planInputPartitions(): Array[InputPartition] = {
    // show pushed
    // scalastyle:off println
    println(s"Applying filters: ${pushed.mkString(", ")}")
    // scalastyle:on println
    Array(YarnAppPartition(
      SparkSession.active.sparkContext
        .hadoopConfiguration.asScala.map(kv => (kv.getKey, kv.getValue)).toMap,
      pushed))
  }

  override def createReaderFactory(): PartitionReaderFactory =
    new YarnAppReaderFactory

  override def build(): Scan = {
    this
  }
}
