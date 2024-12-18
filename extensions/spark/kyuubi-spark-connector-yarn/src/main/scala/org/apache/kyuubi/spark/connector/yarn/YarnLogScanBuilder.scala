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

import org.apache.spark.sql.connector.read._
import org.apache.spark.sql.sources.Filter
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.util.CaseInsensitiveStringMap

case class YarnLogScanBuilder(options: CaseInsensitiveStringMap, schema: StructType)
  extends BasicScanBuilder {

  override def build(): Scan = {
    YarnLogScan(options, schema, pushed)
  }

  override def pushFilters(filters: Array[Filter]): Array[Filter] = {
    val (supportedFilter, unsupportedFilter) = filters.partition {
      case _: org.apache.spark.sql.sources.EqualTo => true
      case _ => false
    }
    pushed = supportedFilter
    unsupportedFilter
  }
}