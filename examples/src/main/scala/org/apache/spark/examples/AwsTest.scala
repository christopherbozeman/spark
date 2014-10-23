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

package org.apache.spark.examples

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.SparkContext._

object AwsTest {
  def main(args: Array[String]) {
    val (ak, sk, in, out) = args match {
      case Array(ak, sk, in, out) ? (ak, sk, in, out)
      case _ => sys.error("Usage: AwsTest AWS_ACCESS_KEY_ID AWS_SECRET_ACCESS_KEY s3://INPUT s3://OUTPUT")
    }

    val sparkConf = new SparkConf().setAppName("AwsTest")
    val sc = new SparkContext(sparkConf)

    /*
     * Example setup that closely resembles Elastic MapReduce configuration
     */
    sc.hadoopConfiguration.set("fs.s3.awsAccessKeyId", ak)
    sc.hadoopConfiguration.set("fs.s3.awsSecretAccessKey", sk)
    //sc.hadoopConfiguration.set("mapred.output.committer.class", "org.apache.hadoop.mapred.DirectFileOutputCommitter")
    sc.hadoopConfiguration.set("fs.s3.impl", "org.apache.hadoop.fs.s3native.NativeS3FileSystem")

    val file = sc.textFile(in)
    val counts = file.flatMap { line =>
      line.split("\\s")
    }.map {
      word => (word, 1)
    }.reduceByKey(_ + _)

    counts.saveAsTextFile(out)

    sc.stop()
  }
}
