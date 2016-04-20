/*
 * Copyright (c) 2016 ingenieux Labs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.ingenieux.lambada;

import java.io.InputStream;
import java.util.Properties;

import io.ingenieux.lambada.runtime.LambadaFunction;

public class ExampleConfigurationHandler {

  private final Properties configuration;

  @LambadaFunction(name="lb_showConfiguration")
  public String handleSNSEvent(InputStream is) throws Exception {
    return configuration.toString();
  }

  public ExampleConfigurationHandler() throws Exception {
    Properties p = new Properties();

    p.load(getClass().getResourceAsStream("config.properties"));

    this.configuration = p;
  }
}
