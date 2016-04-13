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
