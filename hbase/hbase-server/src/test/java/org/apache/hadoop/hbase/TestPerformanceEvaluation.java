/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.PerformanceEvaluation.RandomReadTest;
import org.apache.hadoop.hbase.PerformanceEvaluation.TestOptions;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.yammer.metrics.core.Histogram;
import com.yammer.metrics.stats.Snapshot;
import com.yammer.metrics.stats.UniformSample;

@Category({MiscTests.class, SmallTests.class})
public class TestPerformanceEvaluation {
  private static final HBaseTestingUtility HTU = new HBaseTestingUtility();

  @Test
  public void testSerialization()
  throws JsonGenerationException, JsonMappingException, IOException {
    PerformanceEvaluation.TestOptions options = new PerformanceEvaluation.TestOptions();
    assertTrue(!options.isAutoFlush());
    options.setAutoFlush(true);
    ObjectMapper mapper = new ObjectMapper();
    String optionsString = mapper.writeValueAsString(options);
    PerformanceEvaluation.TestOptions optionsDeserialized =
        mapper.readValue(optionsString, PerformanceEvaluation.TestOptions.class);
    assertTrue(optionsDeserialized.isAutoFlush());
  }

  /**
   * Exercise the mr spec writing.  Simple assertions to make sure it is basically working.
   * @throws IOException
   */
  @Ignore @Test
  public void testWriteInputFile() throws IOException {
    TestOptions opts = new PerformanceEvaluation.TestOptions();
    final int clients = 10;
    opts.setNumClientThreads(clients);
    opts.setPerClientRunRows(10);
    Path dir =
      PerformanceEvaluation.writeInputFile(HTU.getConfiguration(), opts, HTU.getDataTestDir());
    FileSystem fs = FileSystem.get(HTU.getConfiguration());
    Path p = new Path(dir, PerformanceEvaluation.JOB_INPUT_FILENAME);
    long len = fs.getFileStatus(p).getLen();
    assertTrue(len > 0);
    byte [] content = new byte[(int)len];
    FSDataInputStream dis = fs.open(p);
    try {
      dis.readFully(content);
      BufferedReader br =
        new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content)));
      int count = 0;
      while (br.readLine() != null) {
        count++;
      }
      assertEquals(clients * PerformanceEvaluation.TASKS_PER_CLIENT, count);
    } finally {
      dis.close();
    }
  }

  @Test
  public void testSizeCalculation() {
    TestOptions opts = new PerformanceEvaluation.TestOptions();
    opts = PerformanceEvaluation.calculateRowsAndSize(opts);
    int rows = opts.getPerClientRunRows();
    // Default row count
    final int defaultPerClientRunRows = 1024 * 1024;
    assertEquals(defaultPerClientRunRows, rows);
    // If size is 2G, then twice the row count.
    opts.setSize(2.0f);
    opts = PerformanceEvaluation.calculateRowsAndSize(opts);
    assertEquals(defaultPerClientRunRows * 2, opts.getPerClientRunRows());
    // If two clients, then they get half the rows each.
    opts.setNumClientThreads(2);
    opts = PerformanceEvaluation.calculateRowsAndSize(opts);
    assertEquals(defaultPerClientRunRows, opts.getPerClientRunRows());
    // What if valueSize is 'random'? Then half of the valueSize so twice the rows.
    opts.valueRandom = true;
    opts = PerformanceEvaluation.calculateRowsAndSize(opts);
    assertEquals(defaultPerClientRunRows * 2, opts.getPerClientRunRows());
  }

  @Test
  public void testZipfian()
  throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException {
    TestOptions opts = new PerformanceEvaluation.TestOptions();
    opts.setValueZipf(true);
    final int valueSize = 1024;
    opts.setValueSize(valueSize);
    RandomReadTest rrt = new RandomReadTest(null, opts, null);
    Constructor<?> ctor =
      Histogram.class.getDeclaredConstructor(com.yammer.metrics.stats.Sample.class);
    ctor.setAccessible(true);
    Histogram histogram = (Histogram)ctor.newInstance(new UniformSample(1024 * 500));
    for (int i = 0; i < 100; i++) {
      histogram.update(rrt.getValueLength(null));
    }
    double stddev = histogram.stdDev();
    assertTrue(stddev != 0 && stddev != 1.0);
    assertTrue(histogram.stdDev() != 0);
    Snapshot snapshot = histogram.getSnapshot();
    double median = snapshot.getMedian();
    assertTrue(median != 0 && median != 1 && median != valueSize);
  }
}