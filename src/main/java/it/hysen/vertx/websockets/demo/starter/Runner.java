package it.hysen.vertx.websockets.demo.starter;

import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class Runner {
	
	public static final Logger _logger = LoggerFactory.getLogger(Runner.class);
	
	public static void main(String[] args) {
		final Vertx vertx = Vertx.vertx();
		vertx.deployVerticle(AuctionServiceVerticle.class.getName(), resultHandler -> {
			if (resultHandler.succeeded()) {
				_logger.debug("Server started.");
			}
			else {
				_logger.error(resultHandler.cause());
			}
		});
	}
	
}
