package io.ingenieux.lambada;

import com.amazonaws.services.lambda.runtime.events.SNSEvent;

import io.ingenieux.lambada.runtime.LambadaFunction;

public class SNSEventHandler {
  @LambadaFunction(name="lb_handleSns", alias="lb_handleSns_latest", description="Handle SNS Events")
  public void handleSNSEvent(SNSEvent snsEvent) throws Exception {
    System.out.println("snsEvent: " + snsEvent);
  }
}
