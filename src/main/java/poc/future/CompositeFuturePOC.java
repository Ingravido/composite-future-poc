package poc.future;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;
import io.vertx.ext.web.codec.BodyCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class CompositeFuturePOC extends AbstractVerticle {

  private final Logger logger = LoggerFactory.getLogger(poc.future.CompositeFuturePOC.class);
  private WebClient webClient;

  @Override
  public void start(Promise<Void> promise) {
    webClient = WebClient.create(vertx);

    printDateTime("Starting");

    CompositeFuture.all(
      doAsyncCall(5), //Each of this makes a vertex http request that takes 5seconds to complete
      doAsyncCall(5),
      doAsyncCall(5),
      doAsyncCall(5),
      doAsyncCall(5)
    )
    .onSuccess(data -> {
      printDateTime("ALL DONE");
      logger.info(data.toString());
    })
    .onFailure(err -> {
      logger.error("Something went wrong", err);
    });
  }

  private Future<JsonObject> doAsyncCall(int delay) {
    String hostName = "35.169.55.235"; // https://httpbin.org/ Using domain name there is a real nasty bug in vertex with DNS resolve in OSX which was making the main thread to block! so using IP of httpbin.org
    String path = "/delay/" + delay; // Delays the response x seconds
    String hostAndPath = hostName + path;
    printDateTime("Calling " + hostAndPath);

    return webClient
      .get(80, hostName, path)
      .expect(ResponsePredicate.SC_SUCCESS)
      .as(BodyCodec.jsonObject())
      .send()
      .onSuccess(data -> {
        printDateTime("CALL to "+ hostAndPath + " IS DONE " );
      })
      .map(HttpResponse::body);
  }

  private void printDateTime(String msg) {
    LocalDateTime localDate = LocalDateTime.now();
    logger.info(msg + " at: " + localDate);
  }
}
